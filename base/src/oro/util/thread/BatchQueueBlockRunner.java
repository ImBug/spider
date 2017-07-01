package oro.util.thread;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import oro.util.thread.entity.BatchQueue;

/**
 * 
 * 将单个数据转换成批量处理，得以缓冲
 * @author honghm 
 * Create By 2016年6月19日 下午2:15:45
 */
public abstract class BatchQueueBlockRunner<T> implements Runnable {
	
	protected Log logger = LogFactory.getLog(BatchQueueBlockRunner.class);
	protected int max = 0 ;
	protected BatchQueue<T> queue;
	
	private boolean stop;
	
	
	public BatchQueueBlockRunner(T[] array) {
		super();
		queue = new BatchQueue<T>(array);
	}

	
	public BatchQueueBlockRunner(T[] array,int batchSize, int batchMsss) {
		super();
		queue = new BatchQueue<T>(array,batchSize,batchMsss);
	}


	public boolean hasFinish(){
		return queue.size() < 1;
	}
	
	/**
	 * 如果不是强行终止：
	 * 	如果还有未完成的任务 return false;
	 * @param interupt 是否强行终止
	 * @return
	 */
	public boolean stop(boolean interupt){
		if(!interupt){
			if(!hasFinish())return false;
		}
		stop = true;
		return true;
	}
	
	public abstract void excute(T[] t);
	
	public int getMaxCount(){return max;}
	
	public void offerTask(T t){
		if(t != null){
			try {
				queue.offer(t);
			} catch (InterruptedException e) {
				logger.error("中断",e);
			}
		}
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				if(stop) {
					stop = false;
					break;
				}
				if (queue.size() > max)
					max = queue.size();
					try {
						T[] t = queue.take();
						if (t != null) {
							if (logger.isDebugEnabled())
								logger.warn(String.format("当前执行[%s],队列剩余任务[%s],峰值[%s]", t, queue.size(), max));
							excute(t);
						}
					} catch (InterruptedException e) {
						logger.info("exit");
						stop = false;
						break;
					}
			} catch (Throwable e) {
				logger.error("执行出错",e);
			}
		}
	}

}

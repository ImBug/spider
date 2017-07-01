package oro.util.thread;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * 阻塞队列消费
 * @author honghm 
 * Create By 2016年6月19日 下午2:15:45
 */
public abstract class QueueBlockRunner<T> implements Runnable {
	
	protected Log logger = LogFactory.getLog(QueueBlockRunner.class);
	protected int max = 0 ;
	protected BlockingQueue<T> queue = new LinkedBlockingQueue<T>();
	
	private boolean stop;
	
	public boolean hasFinish(){
		return queue.isEmpty();
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
	
	public abstract void excute(T t);
	
	public int getMaxCount(){return max;}
	
	public void offerTask(List<T> list){
		if(list != null){
			queue.addAll(list);
		}
	}
	public void offerTask(T[] array){
		if(array != null){
			for(T t:array){
				try {
					queue.put(t);
				} catch (InterruptedException e) {
					logger.error("中断",e);
				}
			}
		}
	}
	public void offerTask(T t){
		if(t != null){
			try {
				queue.put(t);
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
						T t = queue.take();
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

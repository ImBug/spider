package oro.util.thread;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import oro.util.thread.entity.CountLatch;


/**
 * 
 * 动态添加任务:
 * 	1.如果不start也能运行，但callback失效
 * @author honghm 
 * Create By 2016年6月15日 下午3:40:44
 */
public abstract class TaskAutoRunner<T> implements Runnable{

protected Log logger = LogFactory.getLog(TaskAutoRunner.class);
	
	protected int dealNumPerThread = Integer.valueOf(System.getProperty("topo.discover.dealNumPerThread","20"));
	
	private final CountLatch counter = new CountLatch(0);
	protected  int maxThread = Integer.valueOf(System.getProperty("topo.discover.maxThread","30"));
	private   ThreadPoolExecutor threadPool = BridgeThreadPoolExecutor.createDefault(maxThread);
	protected LinkedList<T> queue = new LinkedList<T>();
	private boolean stop = false;
	private Integer running = 0;
	
	protected abstract void excuteTask(T t);
	protected abstract void callBack();
	
	/**
	 * 负荷状态
	 * @return
	 */
	public int getWeight(){
		int full = dealNumPerThread * maxThread;
 		if(queue.size() * 2 < full) return 0;
 		if(queue.size()  > full) return 2;
 		return 1;
	}
	
	public boolean hasFinish(){
		return running < 1 && queue.isEmpty();
	}
	
	public void addTask(T t){
		if(t == null) return;
		synchronized (queue) {
			queue.offer(t);
			dispatch();
		}
		synchronized (this) {
			this.notify();
		}
	}
	public void addTask(Collection<? extends T> l){
		if(l == null) return;
		synchronized (queue) {
			for(T t:l){
				queue.offer(t);
			}
			dispatch();
		}
		synchronized (this) {
			this.notify();
		}
	}
	public void addTask(T[] l){
		if(l == null) return;
		synchronized (queue) {
			for(T t:l){
				queue.offer(t);
			}
			dispatch();
		}
		synchronized (this) {
			this.notify();
		}
	}
	private void dispatch(){
			int size = queue.size();
			if(size < 1)return;
			int currentThread = (int)threadPool.getActiveCount();
  		int threadNum = (size + dealNumPerThread/2)/dealNumPerThread;//需要线程数
  		if(threadNum < 1)threadNum = 1;
  		int needAdd = threadNum - currentThread;
  		needAdd = needAdd > 0 ?needAdd:0;
  		logger.debug(String.format("单线程处理任务数:%s,剩余处理任务[%s]，已启用线程[%s]，需要线程[%s]，最大线程设置[%s],调度线程总数[%s]",dealNumPerThread, size,threadPool.getActiveCount(),needAdd,maxThread,threadPool.getTaskCount()));
  		for(int i=0;i<needAdd;i++){
  			MyRunner runner = new MyRunner(this,queue, counter);
  			threadPool.execute(runner);
  			counter.countUp();
  		}
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
		synchronized (this) {
			this.notify();
		}
		return true;
	}
	
	@Override
	public void run(){
		while(true){
			if(stop) {
				stop = false;
				break;
			}
			if(queue.isEmpty() || counter.getCount() < 1){
				synchronized (this) {
					try {
						this.wait();
						if(stop) {
							stop = false;
							break;
						}
					} catch (InterruptedException e) {
						logger.error(e);
					}
				}
			}else{
				try {//对队列来讲任务清空即是完成，但还有可能正在执行中
					counter.await();
					if(hasFinish()){
						if(logger.isDebugEnabled())logger.debug(this + ",任务执行完毕----------------------------");
						callBack();
					}
					else{
						synchronized (this) {
							this.wait();
							callBack();
							if(logger.isDebugEnabled())logger.debug(this + ",任务执行完毕--------------------------");
						}
					}
				} catch (Throwable e) {
					logger.error(e);
				}
			}
		}
	}
	
	/**
	 * 
	 * 消费
	 * @author honghm 
	 * Create By 2016年7月16日 上午10:43:42
	 */
	class MyRunner extends QueueRunner<T>{
		
		private TaskAutoRunner main;
		
		public MyRunner(TaskAutoRunner main,Queue<T> queue, CountLatch countLatch) {
			super();
			super.queue = queue;
			super.countLatch = countLatch;
			this.main = main;
		}

		@Override
		public void init() {
		}

		@Override
		public void excute(T t) {
			if(logger.isDebugEnabled())logger.debug("当前执行{" + t + "},剩余[" + queue.size() + "]");
			try {
				synchronized (running) {
					running++;
				}
				excuteTask(t);
				synchronized (running) {
					running--;
				}
				synchronized (main) {
					if(hasFinish()){
						main.notify();
					}
				}
			} catch (Throwable e) {
				logger.error(t,e);
			}
		}
		
	}
}

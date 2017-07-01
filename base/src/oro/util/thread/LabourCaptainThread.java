package oro.util.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author honghm
 * 1.固定数目的消费线程
 * 2.队列空时出于阻塞状态，等待新任务加入
 * 3.线程不够时可以增加，不考虑减少消费线程
 * @param <T>
 */
public abstract class LabourCaptainThread<T> extends Thread {
	
	protected final static Log logger = LogFactory.getLog(LabourCaptainThread.class);
	protected int threadSize = 1;
	protected BlockingQueue<T> queue = new LinkedBlockingDeque<T>();
	protected int maxThreadSize = 20;
	
	private AtomicInteger tasks = new AtomicInteger(0);
	private Thread[] workers;
	private boolean isRunning;
	
	public boolean isRunning(){
		return isRunning;
	}
	
	public boolean isFinish(){
		return tasks.get() < 1;
	}
	
	/**
	 * 扩大规模
	 * @param 预算工人数目workersNeed
	 */
	
	public void expanse(int workersNeed){
		int crt = workers.length;
		int add = workersNeed - crt;
		if(workersNeed > maxThreadSize){
			add = maxThreadSize - crt;
		}
		if(add > 0){
			Thread[] newThreads = new Thread[crt + add];
			System.arraycopy(workers, 0,newThreads,0,crt);
			for(int i=0; i<add; i++){
				newThreads[crt + i] = employWorker(getName() + "-worker-" + (crt + i + 1));
			}
			workers = newThreads;
			logger.info(String.format("新增工人:%s,当前工人总数:%s", add,workers.length));
		}
	}
	
	private Thread employWorker(String name){
		Thread thread = new Thread(new Worker(),name);
		thread.start();
		return thread;
	}
	
	@Override
	public void run(){
		if(threadSize < 1){
			logger.error("初始化线程数不合法");
		}else{
			if(threadSize > maxThreadSize) threadSize = maxThreadSize;
			workers = new Thread[threadSize];
			for(int i=0;i<threadSize;i++){
				workers[i] = employWorker(getName() + "-worker-" + (i+1));
			}
			isRunning = true;
		}
	}
	
	protected void addTasks(T task){
		try {
			queue.put(task);
			tasks.getAndIncrement();
		} catch (InterruptedException e) {
			logger.error(e);
		}
	}
	
	protected abstract void excute(T task);
	protected abstract void finish();
		
	class Worker implements Runnable{

		@Override
		public void run() {
			for(;;){
				try {
					T resource = queue.take();
					excute(resource);
				} catch (Throwable e) {
					logger.error(e);
				}finally{
					tasks.decrementAndGet();
					while(tasks.get()<1){
						finish();
						break;
					}
				}
			}
		}
		
	}
}

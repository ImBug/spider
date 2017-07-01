package oro.util.thread;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * 
 * 
 * @author honghm 
 * Create By 2016年9月7日 下午4:33:28
 */
public abstract class DelayedBlockQueueRunner<T> implements Runnable {
	
	private final static TimeUnit UNIT_MS = TimeUnit.MILLISECONDS;//ms
	
	protected Log logger = LogFactory.getLog(DelayedBlockQueueRunner.class);
	protected DelayQueue<Entity> queue = new DelayQueue<Entity>();
	
	private int max = 0;
	
	/**
	 * 
	 * @param t
	 * @param delay 延迟执行时间，单位ms
	 */
	public void offerTask(T t,int delay){
		int x = getSize() + 1;
		if(x > max)max=x;
		queue.put(new Entity(t,delay));
	}
	
	private T poll() throws InterruptedException{
		return queue.take().t;
	}
	
	public abstract void excuteTask(T t);
	
	@Override
	public void run() {
		while(!Thread.currentThread().isInterrupted()){
			try {
				excuteTask(poll());
			} catch (InterruptedException e) {
				logger.error(e);
			}catch(Throwable e){
				logger.error("DelayedBlockQueueRunner",e);
			}
		}
	}
	
	class Entity implements Delayed{
		T t;
		long takeTime;//预计出队时间
		
		public Entity(T t, int delay) {
			super();
			this.t = t;
			this.takeTime = System.currentTimeMillis() + delay;
		}
		
		@Override
		public int compareTo(Delayed paramT) {
			if(paramT != null){
				return (int)(getDelay(UNIT_MS) - paramT.getDelay(UNIT_MS));
			}else{
				return 1;
			}
		}
		
		@Override
		public long getDelay(TimeUnit paramTimeUnit) {
			return UNIT_MS.convert(takeTime - System.currentTimeMillis(), UNIT_MS);
		}
	}

	public int getMax() {
		return max;
	}

	public int getSize() {
		return queue.size();
	}

}

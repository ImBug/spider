package oro.util.thread;

import java.util.Queue;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import oro.util.thread.entity.CountLatch;


/**
 * 
 * 
 * @author honghm 
 * Create By 2016年5月28日 下午2:12:19
 */
public abstract class QueueRunner<T> implements  Runnable {
	
	protected Log logger = LogFactory.getLog(QueueRunner.class);
	
	protected Queue<T> queue;//src
	
	protected CountDownLatch countDown;
	protected CountLatch countLatch;
	
	
	public abstract void init();
	public abstract void excute(T t);
	
	public void run() {
		init();
		while(true){
			T t = null;
			synchronized (queue) {
				if(queue.isEmpty())break;
				try {
					t = queue.poll();
				} catch (Throwable e) {
					logger.info(queue);
					logger.warn("出队错误",e);
					break;
				}
				if(t == null) break;
			}
			try {
				excute(t);
			} catch (Exception e) {
				logger.error(t,e);
			}
		}
		try {
			if(countDown != null)countDown.countDown(); 
			if(countLatch != null)countLatch.countDown();
		} catch (Exception e) {
			logger.error(e);
		} 
	}
	
}
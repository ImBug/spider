package oro.util.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 
 * 默认一个线程池
 * @author honghm 
 * Create By 2016年7月7日 上午10:19:57
 */
public class BridgeThreadPoolExecutor extends ThreadPoolExecutor {
	
	private final static ThreadFactory threadFactory = new BridgeThreadFactory();
	
	public BridgeThreadPoolExecutor(int paramInt1, int paramInt2, long paramLong, TimeUnit paramTimeUnit,
			BlockingQueue<Runnable> paramBlockingQueue) {
		super(paramInt1, paramInt2, paramLong, paramTimeUnit, paramBlockingQueue,threadFactory);
	}
	
	public  static BridgeThreadPoolExecutor createDefault(int maxThread){
		return new BridgeThreadPoolExecutor(maxThread*2/3, maxThread, 0, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
	}
	
}

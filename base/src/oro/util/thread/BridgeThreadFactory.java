package oro.util.thread;

import java.util.concurrent.ThreadFactory;


/**
 * 
 * 统一创建线程
 * @author honghm 
 * Create By 2016年7月7日 上午10:04:43
 */
public class BridgeThreadFactory implements ThreadFactory {

	@Override
	public Thread newThread(Runnable paramRunnable) {
		return ThreadUtil.createThread(paramRunnable);
	}

}

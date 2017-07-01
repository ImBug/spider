package oro.util.thread;

import java.util.concurrent.ThreadFactory;

import oro.util.thread.entity.BaseThread;

/**
 * 方便统计线程
 * @author honghm
 *
 */
public class ThreadUtil implements ThreadFactory{
	
	private static volatile ThreadFactory factory;
	public static final String THREAD_NAME_PRIX = "uyun-storeBridge-";
	
	public static String nameThread(String name){
		return THREAD_NAME_PRIX + name;
	}
	
	public static String nameThread(Class<?> name){
		return THREAD_NAME_PRIX + name.getSimpleName();
	}
	
	public static ThreadFactory geThreadFactory(){
		if(factory == null) factory = new ThreadUtil();
		return factory;
	}
	
	public static BaseThread createThread(Runnable runner){
		BaseThread t = new BaseThread(runner);
		t.setName(THREAD_NAME_PRIX + runner.getClass().getSimpleName());
		return t;
	}
	
	public static void sleep(long time){
		if(time > 0)
			try {
				Thread.sleep(time);
			} catch (InterruptedException e) {
			}
	}

	@Override
	public Thread newThread(Runnable r) {
		return createThread(r);
	}
}

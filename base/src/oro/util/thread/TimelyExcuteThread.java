package oro.util.thread;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import oro.service.Finishable;

/**
 * 
 * 周期性的消费队列
 * 
 * @author honghm Create By 2016年5月28日 下午2:20:13
 */
public abstract class TimelyExcuteThread<T> extends Thread implements Finishable{

	protected Log logger = LogFactory.getLog(TimelyExcuteThread.class);

	protected int interval = 60;//周期，单位秒
	protected int delay = 60;//周期，单位秒
	protected myRunner runner = new myRunner();
	
	private ScheduledFuture<?> future;
	private static ScheduledExecutorService schExcutor;
	
	static{
		int size = 20;
		try {
			int pSize = Integer.valueOf(System.getProperty("bridge.thread.roundSize", "30"));
			if(pSize > size && pSize < 50)size = pSize;
		} catch (NumberFormatException e) {
		}
		schExcutor = Executors.newScheduledThreadPool(size,ThreadUtil.geThreadFactory());
	}
	
	
	@Override
	public void finish() {
		runner.finish();
	}

	@Override
	public void run() {
		try {
			future = schExcutor.scheduleAtFixedRate(runner, delay, interval, TimeUnit.SECONDS);
		} catch (Exception e) {
			throw new RuntimeException(String.format("请检查参数[%s,%s,%s]", runner,delay,interval),e);
		}
	}

	public abstract void excute();

	/**
	 * 判断上一轮队列种的任务有没执行完
	 * 
	 * @return
	 */
	public abstract boolean hasFinish();

	class myRunner implements Runnable,Finishable {
		
		private boolean stop = false;
		
		@Override
		public void run() {
			if(!stop){
				if (hasFinish()) {
					excute();
				}
			}else{
				future.cancel(true);
			}
		}

		@Override
		public void finish() {
			stop = true;
		}
	}

}

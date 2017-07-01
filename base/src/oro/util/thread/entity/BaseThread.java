package oro.util.thread.entity;

import oro.service.Finishable;
import oro.service.JvmMonitor;
import oro.util.thread.ThreadUtil;
import oro.util.thread.TimelyExcuteThread;

/**
 * 提供开始和结束标识
 * @author honghm
 *
 */
public class BaseThread extends Thread implements Finishable {

	private boolean start,end;
	private Runnable runer;
	
	public BaseThread(Runnable target) {
		super(target);
		this.runer = target;
	}

	@Override
	public void run() {
		start = true;
		super.run();
		end = true;
	}

	public boolean isStart() {
		return start;
	}

	public boolean isEnd() {
		return end;
	}
	
	public boolean isRunning(){
		return start && !end;
	}
	
	@Override
	public void finish() {
		if(runer instanceof Finishable){
			((Finishable)runer).finish();
		}else{
			this.interrupt();
		}
	}
	
	public static void main(String[] args) {
		System.setProperty("bridge.jvmInfo.interval", "3");
		JvmMonitor monitor = JvmMonitor.getDefault();
		monitor.start();
		A a= new A();
		a.start();
		ThreadUtil.sleep(6000);
		System.out.println("==============finish a======================");
		a.finish();
	}
	
	static class A extends TimelyExcuteThread{
		boolean over = true;
		
		public A() {
			super.delay =1;
			super.interval = 5;
		}

		@Override
		public void excute() {
			over = false;
			for(int i=0;i<3;i++){
				System.out.println(getName() + "->" + i);
				ThreadUtil.sleep(1000);
			}
			over = true;
		}

		@Override
		public boolean hasFinish() {
			return over;
		}
		
	}
}

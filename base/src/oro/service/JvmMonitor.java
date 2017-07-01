package oro.service;

import oro.util.env.JvmInfo;
import oro.util.thread.TimelyExcuteThread;

/**
 * 环境信息输出
 * @author honghm
 *
 */
public class JvmMonitor extends TimelyExcuteThread<Object> {
	
	private static JvmMonitor instance;
	private long start;
	
	public static JvmMonitor getDefault(){
		if(instance == null) instance = new JvmMonitor();
		return instance;
	}
	
	private JvmMonitor(){
		super();
		super.interval = Integer.valueOf(System.getProperty("bridge.jvmInfo.interval", "600"));
		start = System.currentTimeMillis();
	}
	
	@Override
	public void excute() {
		JvmInfo.logJVMStatus(start,false);
	}

	@Override
	public boolean hasFinish() {
		return true;
	}
	
}

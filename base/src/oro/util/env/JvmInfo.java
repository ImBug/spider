package oro.util.env;

import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.Comparator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JvmInfo {
	
	private final static Log logger = LogFactory.getLog(JvmInfo.class);
	
	 /**
  * 非堆内存使用大小
  *
  * @return
  */
  public static long getNonHeapUsed() {
       return ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getUsed();
  }
  
	public static void logJVMStatus(long start,boolean trace) {
		StringBuffer sb = new StringBuffer();
		sb.append("--------------------------JVM状态信息如下：-------------------\n");
		if(start > 0){
			double time = ((System.currentTimeMillis() - start)*10/3600000)*1.0/10;
			String timeStr = time + "小时";
			if(time<1)timeStr = (System.currentTimeMillis()- start)/1000 + "秒";
			sb.append(String.format(" -- 运行时长:[%s]\n", timeStr));
		}
		sb.append(String.format(" -- 堆内存使用大小:[%sMB]\n", getHeapUsed() / 1024 / 1024));
		sb.append(String.format(" -- 非堆内存使用大小:[%sMB]\n", getNonHeapUsed() / 1024 / 1024));
		sb.append(String.format(" -- 加载类数:[%s]\n", getLoaderClassCount()));
		Thread[] all = findAllThreads();
		sb.append(String.format(" -- 线程总数:[%s],各线程基本信息：\n", all.length));
		for (Thread t : all) {
			sb.append("\t" + getThreadTraceInofo(t,trace) + "\n");
		}
		sb.append(" --------------------------------------------结束----------------------------------------");
		logger.info(sb);
	}
	 
	 /**
   * 堆总大小，单位Byte
   *
   * @return
   */
   public static long getHeapSize() {
        return Runtime.getRuntime().maxMemory();
   }

   /**
   * 堆已使用大小，单位Byte
   *
   * @return
   */
   public static long getHeapUsed() {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
   }

   /**
   * 堆未使用大小，单位Byte
   *
   * @return
   */
   public static long getHeapFree() {
        return getHeapSize() - getHeapUsed();
   }

   /**
   * 加载类数
   *
   * @return
   */
   public static int getLoaderClassCount() {
        return ManagementFactory.getClassLoadingMXBean().getLoadedClassCount();
   }

	/**
	 * 获取所有线程
	 *
	 * @return
	 */
	public static Thread[] findAllThreads() {
		ThreadGroup group = Thread.currentThread().getThreadGroup();
		ThreadGroup topGroup = group;

		// 遍历线程组树，获取根线程组
		while (group != null) {
			topGroup = group;
			group = group.getParent();
		}
		// 激活的线程数加倍
		int estimatedSize = topGroup.activeCount() * 2;
		Thread[] slackList = new Thread[estimatedSize];
		// 获取根线程组的所有线程
		int actualSize = topGroup.enumerate(slackList);
		// copy into a list that is the exact size
		Thread[] list = new Thread[actualSize];
		System.arraycopy(slackList, 0, list, 0, actualSize);
		Arrays.sort(list, new Comparator<Thread>() {

			public int compare(Thread o1, Thread o2) {
				int d= o1.getName().length()-o2.getName().length();
				if(d  == 0) return (int)(o1.getId() - o2.getId());
				return d;
			}

		});
		return list;
	}

   /**
   * 获取线程的堆栈信息
   *
   * @param t
   * @return
   */
   public static String getThreadTraceInofo(Thread t,boolean trace) {
        StringBuffer traceBuf = new StringBuffer(String.format("[%s],ID=[%s]", t.getName(), t.getId()));
        if(trace){
        	traceBuf.append(",堆栈信息：");
        	for (StackTraceElement s : Thread.getAllStackTraces().get(t)) {
        		traceBuf.append("\n\tat " + s);
        	}
        }
        return traceBuf.toString();
   }
}

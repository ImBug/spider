package oro.util.thread;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 指定时间执行,格式:hh:mm:ss
 * 如：01:30:00
 * @author honghm
 *
 * @param <T>
 */
public abstract class FixTimeExcuteThread<T> extends TimelyExcuteThread<T> {
	
	private final static int oneDay = 24 * 60 * 60 ;

	public FixTimeExcuteThread(String time) {
		super();
		long fixtime = getTimeMillis(format(time));
		long initDelay  = (fixtime - System.currentTimeMillis())/1000;  
    super.delay = (int)(initDelay > 0 ? initDelay : oneDay + initDelay);
    interval = oneDay;
	}
	
	private static String format(String time){
		if(time == null) return "00:00:00";
		String[] p = time.split(":");
		String t = p[0];
		if(t.length() < 2) t = "0" + t;
		if(p.length < 2) return t + ":00:00";
		if(p[1].length() < 2) t = t + ":0" + p[1];
		else t+= ":" + p[1];
		if(p.length < 3) return t + ":00";
		if(p[2].length() < 2) return t + ":0" + p[2];
		else t+= ":" + p[2];
		return t;
	}
	/** 
	 * 获取指定时间对应的毫秒数 
	 * @param time "HH:mm:ss" 
	 * @return 
	 */  
	private long getTimeMillis(String time) {  
	    try {  
	        DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");  
	        DateFormat dayFormat = new SimpleDateFormat("yy-MM-dd");  
	        Date curDate = dateFormat.parse(dayFormat.format(new Date()) + " " + time);  
	        return curDate.getTime();  
	    } catch (ParseException e) {
	    	logger.error("时间格式[" + time + "]不对");
	    }  
	    return 1;  
	} 
}

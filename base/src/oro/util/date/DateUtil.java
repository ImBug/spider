package oro.util.date;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {
	
	private static String pattern = "yyyy-MM-dd HH:mm:ss";
	private static SimpleDateFormat dsf = new SimpleDateFormat(pattern);
	
	/** 
	 * 获取指定时间对应的毫秒数 
	 * @param time "HH:mm:ss" 
	 * @return 
	 */  
	public static long getTimeMillis(String time) {  
	    try {  
	    		time  = formatTime(time);
	        DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");  
	        DateFormat dayFormat = new SimpleDateFormat("yy-MM-dd");  
	        Date curDate = dateFormat.parse(dayFormat.format(new Date()) + " " + time);  
	        return curDate.getTime();  
	    } catch (ParseException e) {
	    }  
	    return 1;  
	} 
	
	private static String formatTime(String time){
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
	 * 格式化日期
	 * 
	 * 默认格式：yyyy-MM-dd hh:mm:ss
	 * 
	 * @param date
	 * @return
	 */
	public static String format(Date date) {
		if (null == date) {
			return null;
		}
		return dsf.format(date);
	}

	/**
	 * 格式化日期
	 * 
	 * @param date
	 * @return
	 */
	public static String format(Date date, String format) {
		if (null == date || null == format) {
			return null;
		}
		SimpleDateFormat dsf = new SimpleDateFormat(format);
		return dsf.format(date);
	}

	/**
	 * 解析字符串
	 * 
	 * 默认格式：yyyy-MM-dd hh:mm:ss
	 * 
	 * @param str
	 * @return
	 */
	public static Date parse(String str) {
		if (null == str || str.length() < pattern.length()) {
			return null;
		}
		Date date = null;
		try {
			date = dsf.parse(str);
		} catch (ParseException e) {
		}
		return date;
	}

	/**
	 * 解析字符串
	 * 
	 * @param str
	 * @param pattern
	 * @return
	 */
	public static Date parse(String str, String pattern) {
		if (null == str || null == pattern || str.length() < pattern.length()) {
			return null;
		}
		SimpleDateFormat dsf = new SimpleDateFormat(pattern);
		Date date = null;
		try {
			date = dsf.parse(str);
		} catch (ParseException e) {
		}
		return date;
	}
	
	/**
	 * utc时间转本地时间
	 * @param utcTime
	 * @return
	 */
	public static Date parseToLocal(long utcTime){
		int zoneOffset = TimeZone.getDefault().getRawOffset();   
    int dstOffset =  TimeZone.getDefault().getDSTSavings();
    return new Date(utcTime + zoneOffset + dstOffset);
	}
}

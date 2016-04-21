package com.changhong.gehua.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Utils {
	
	public static final int WEEKDAYS = 7;
	  
	 public static String[] WEEK = {
	  "星期日",
	  "星期一",
	  "星期二",
	  "星期三",
	  "星期四",
	  "星期五",
	  "星期六",
	};
	
	public static String dateToString(Date date) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		df = DateFormat.getDateInstance(DateFormat.MEDIUM);
		// df.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		String str = df.format(date);
		return str;
	}

	// 把字符串转为日期
	public static Date strToDate(String strDate) throws Exception {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		df = DateFormat.getDateInstance(DateFormat.MEDIUM);
		// df.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		return df.parse(strDate);
	}
	
	/* 节目信息的开始于结束时间：时 分 */
	public static String hourAndMinute(Date outterDate) {
		DateFormat df = new SimpleDateFormat("HH:mm");
		
		return df.format(outterDate);
	}
	
	public static String millToDateStr(int milliseconds ){
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		return formatter.format(milliseconds);
	}
	
	
	/**
	 * 日期变量转成对应的星期字符串
	 * @param date
	 * @return
	 */
	public static String DateToWeek(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int dayIndex = calendar.get(Calendar.DAY_OF_WEEK);
		if (dayIndex < 1 || dayIndex > WEEKDAYS) {
			return null;
		}
		
		return WEEK[dayIndex - 1];
	}
	
	/* truncate date string length */
	public static String truncateDaateString(String dateStr, int start, int end) {

		return dateStr.substring(start, end);
	}
}

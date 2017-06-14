package com.changhong.gehua.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;


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
	 public static String stringTostring(String strDate) throws Exception {
			DateFormat df = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
			DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
			Date d = df.parse(strDate);
			return df1.format(d);
		}
	 
	 
	 public static String dateToFullStr(Date date) {
		DateFormat df =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str = df.format(date);
		return str;
	 }
	 
	 public static Date strToFullDate(String strDate) throws Exception {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return df.parse(strDate);
		}
	 
	 /*
	  * yyyy年MM月dd日
	  */
	public static String dateToString(Date date) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		df = DateFormat.getDateInstance(DateFormat.MEDIUM);
		// df.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		String str = df.format(date);
		return str;
	}
	
	/*
	 * yyyy-MM-dd
	 */
	public static String dateToStringSQL(Date date) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
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
	
	public static String millToDateStr(long milliseconds ){
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
		formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		return formatter.format(milliseconds);
	}
	
	public static String millToLiveBackStr(long milliseconds){
		Date date=new Date(milliseconds);
		DateFormat df = new SimpleDateFormat("HH:mm:ss");
		//DateFormat df = new SimpleDateFormat("HH:mm");
		return df.format(date);
	} 
	
	public static String millToLiveBackString(long milliseconds){
		Date date=new Date(milliseconds);
		DateFormat df = new SimpleDateFormat("HH:mm");
//		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		String strDate=df.format(date);
		return strDate;
	}
	
	public static String millToLiveBackStringEx(long milliseconds){
		Date date=new Date(milliseconds);
		DateFormat df = new SimpleDateFormat("HH:mm");
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		String strDate=df.format(date);
		return strDate;
	}
	
	/**
	 * 日期变量转成对应的星期字符串
	 * @param date
	 * @return
	 */
	public static String DateToWeek(Date date) {
		String str=null;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int dayIndex = calendar.get(Calendar.DAY_OF_WEEK);
		if (dayIndex < 1 || dayIndex > WEEKDAYS) {
			return null;
		}
		str= WEEK[dayIndex - 1];
		return str;
	}
	
	public static boolean isToday(Date date){
		int today=-1;
		int curDay=-2;
		Calendar calendar = Calendar.getInstance();
		today=calendar.get(Calendar.DAY_OF_MONTH);
		calendar.setTime(date);
		curDay=calendar.get(Calendar.DAY_OF_MONTH);
		return curDay==today;
	}
	
	/* truncate date string length */
	public static String truncateDaateString(String dateStr, int start, int end) {

		return dateStr.substring(start, end);
	}
	
	/*
	 * 
	 * list和json的互转
	 */
	
	public static String listToString(List<String> list){
		String str=null;
		str=JSONArray.toJSONString(list);
		return str;
	}
	
	public static List<String> stringToList(String str){
		List<String> list=JSON.parseArray(str, String.class);
		return list;
	}
	
	public static boolean isOutOfDate(ProgramInfo program){
		boolean flag=false;
		Date date=new Date();
		flag=date.after(program.getEndTime());
		
		return flag;
	}
}

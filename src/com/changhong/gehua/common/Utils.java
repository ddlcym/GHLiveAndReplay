package com.changhong.gehua.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Utils {

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
}

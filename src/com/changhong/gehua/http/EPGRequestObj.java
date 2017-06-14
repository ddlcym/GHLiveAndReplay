package com.changhong.gehua.http;
/** 
 * @author  cym  
 * @date 创建时间：2017年5月17日 上午10:40:18 
 * @version 1.0 
 * @parameter   
 */
public class EPGRequestObj {
	public static boolean isCancel=false;
	public static String tag="";
	
	public static boolean isCancel() {
		return isCancel;
	}

	public static void setCancel(boolean isCancel) {
		EPGRequestObj.isCancel = isCancel;
	}
}

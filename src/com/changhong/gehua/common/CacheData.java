package com.changhong.gehua.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CacheData {

	public static List<ChannelInfo> allChannelInfo=new ArrayList<ChannelInfo>();
	public static Map<String, Object> allChannelMap=new HashMap<String, Object>();
	
	public static Map<String, List<ProgramInfo>> allProgramMap=new HashMap<String, List<ProgramInfo>>();
//	public static List<ProgramInfo> curProgramsList=new ArrayList<ProgramInfo>();
	

	public static String curChannelNum="";
	public static List<String> dayMonths=new LinkedList<String>();
	
	

	public static Map<String, Object> getAllChannelMap() {
		return allChannelMap;
	}

	public static void setAllChannelMap(Map<String, Object> allChannelMap) {
		CacheData.allChannelMap = allChannelMap;
	}

	public static Map<String, List<ProgramInfo>> getAllProgramMap() {
		return allProgramMap;
	}

	public static void setAllProgramMap(Map<String, List<ProgramInfo>> allProgramMap) {
		CacheData.allProgramMap = allProgramMap;
	}

	public static String getCurChannelNum() {
		return curChannelNum;
	}
	public static void setCurChannelNum(String curChannelNum) {
		CacheData.curChannelNum = curChannelNum;
	}
	public static List<String> getDayMonths() {
		return dayMonths;
	}

	public static void setDayMonths(List<String> dayMonths) {
		CacheData.dayMonths = dayMonths;
	}

	public static List<ChannelInfo> getAllChannelInfo() {
		return allChannelInfo;
	}

	public static void setAllChannelInfo(List<ChannelInfo> allChannelInfo) {
		CacheData.allChannelInfo = allChannelInfo;
	}
	
}

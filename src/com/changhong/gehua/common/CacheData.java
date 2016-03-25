package com.changhong.gehua.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CacheData {

	public static List<ChannelInfo> allChannelInfo=new ArrayList<ChannelInfo>();
	
	public static String curChannelNum="";
	
	
	
	public static Map<String, Object> allChannelMap=new HashMap<String, Object>();
	

	public static Map<String, Object> getAllChannelMap() {
		return allChannelMap;
	}

	public static void setAllChannelMap(Map<String, Object> allChannelMap) {
		CacheData.allChannelMap = allChannelMap;
	}

	public static List<ChannelInfo> getAllChannelInfo() {
		return allChannelInfo;
	}

	public static void setAllChannelInfo(List<ChannelInfo> allChannelInfo) {
		CacheData.allChannelInfo = allChannelInfo;
	}
	
	
}

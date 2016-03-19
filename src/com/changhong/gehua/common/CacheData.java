package com.changhong.gehua.common;

import java.util.List;

public class CacheData {

	public static List<ChannelInfo> allChannelInfo;
	
	public static List<ProgramInfo> allProgramInfo;

	public static List<ChannelInfo> getAllChannelInfo() {
		return allChannelInfo;
	}

	public static void setAllChannelInfo(List<ChannelInfo> allChannelInfo) {
		CacheData.allChannelInfo = allChannelInfo;
	}

	public static List<ProgramInfo> getAllProgramInfo() {
		return allProgramInfo;
	}

	public static void setAllProgramInfo(List<ProgramInfo> allProgramInfo) {
		CacheData.allProgramInfo = allProgramInfo;
	}
	
	
}

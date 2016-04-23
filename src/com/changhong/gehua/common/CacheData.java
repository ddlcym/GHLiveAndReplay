package com.changhong.gehua.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CacheData {

	public static List<ChannelInfo> allChannelInfo=new ArrayList<ChannelInfo>();
	public static Map<String, ChannelInfo> allChannelMap=new HashMap<String, ChannelInfo>();
	
	public static Map<String, List<ProgramInfo>> allProgramMap=new HashMap<String, List<ProgramInfo>>();
//	public static List<ProgramInfo> curProgramsList=new ArrayList<ProgramInfo>();
	
	public static String replayCurDay="";

	public static String curChannelNum="621";
	public static List<String> dayMonths=new LinkedList<String>();
	
	public static List<ChannelInfo> allChannelExtraInfo=new ArrayList<ChannelInfo>();

	public static ChannelInfo curChannel;
	public static ProgramInfo curProgram;
	public static List<ProgramInfo> curPrograms=new ArrayList<ProgramInfo>();
	
	public static List<ChannelInfo> getAllChannelExtraInfo() {
		return allChannelExtraInfo;
	}

	public static void setAllChannelExtraInfo(List<ChannelInfo> allChannelExtraInfo) {
		CacheData.allChannelExtraInfo = allChannelExtraInfo;
	}

	public static String getReplayCurDay() {
		return replayCurDay;
	}

	public static void setReplayCurDay(String replayCurDay) {
		CacheData.replayCurDay = replayCurDay;
	}

	public static Map<String, ChannelInfo> getAllChannelMap() {
		return allChannelMap;
	}

	public static void setAllChannelMap(Map<String, ChannelInfo> allChannelMap) {
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

	public static ChannelInfo getCurChannel() {
		return curChannel;
	}

	public static void setCurChannel(ChannelInfo curChannel) {
		CacheData.curChannel = curChannel;
	}

	public static ProgramInfo getCurProgram() {
		return curProgram;
	}

	public static void setCurProgram(ProgramInfo curProgram) {
		CacheData.curProgram = curProgram;
	}

	public static List<ProgramInfo> getCurPrograms() {
		return curPrograms;
	}

	public static void setCurPrograms(List<ProgramInfo> curPrograms) {
		CacheData.curPrograms = curPrograms;
		setCurProgram(curPrograms.get(1));
	}
		
}

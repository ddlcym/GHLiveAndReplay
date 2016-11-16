package com.changhong.gehua.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.util.Log;

public class CacheData {

	public static boolean ab=true;
	public static List<ChannelInfo> allChannelInfo=new ArrayList<ChannelInfo>();
	public static Map<String, ChannelInfo> allChannelMap=new HashMap<String, ChannelInfo>();
	
	public static Map<String, List<ProgramInfo>> allProgramMap=new HashMap<String, List<ProgramInfo>>();
//	public static List<ProgramInfo> curProgramsList=new ArrayList<ProgramInfo>();
	
	public static String replayCurDay="";

	public static String curChannelNum="18";
	public static List<String> dayMonths=new LinkedList<String>();
	
	public static List<ChannelInfo> allChannelExtraInfo=new ArrayList<ChannelInfo>();

	public static ChannelInfo curChannel;
	public static ProgramInfo curProgram;//时移下正在播放的节目
	public static List<ProgramInfo> curPrograms=new ArrayList<ProgramInfo>();//当前频道的3个节目
	
	public static List<ProgramInfo> timeshiftPrograms=new ArrayList<ProgramInfo>();//时移下的节目列表
	
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
		Log.i("mmmm","setCurProgram:"+Utils.millToLiveBackString(curProgram.getBeginTime().getTime()));
		CacheData.curProgram = curProgram;
	}

	public static List<ProgramInfo> getCurPrograms() {
		return curPrograms;
	}

	public static void setCurPrograms(List<ProgramInfo> curPrograms) {
		CacheData.curPrograms = curPrograms;
		if(curPrograms!=null&&curPrograms.size()>1){
		setCurProgram(curPrograms.get(1));
		Log.i("mmmm","setCurPrograms:");
		}
	}

	public static List<ProgramInfo> getTimeshiftPrograms() {
		return timeshiftPrograms;
	}

	public static void setTimeshiftPrograms(List<ProgramInfo> timeshiftPrograms) {
		CacheData.timeshiftPrograms = timeshiftPrograms;
	}
		
	
}

package com.changhong.ghlive.datafactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.changhong.gehua.common.CacheData;
import com.changhong.gehua.common.ChannelInfo;
import com.changhong.gehua.common.ChannelType;
import com.changhong.gehua.common.PosterInfo;
import com.changhong.gehua.common.ProgramInfo;
import com.changhong.gehua.common.Utils;

import android.util.Log;

public class JsonResolve {

	private static JsonResolve liveJsonResolve;

	public static JsonResolve getInstance() {
		if (null == liveJsonResolve) {
			liveJsonResolve = new JsonResolve();
		}
		return liveJsonResolve;
	}

	// get single channel info
	public ChannelInfo jsonToChannel(JSONObject jsonObject) {
		ChannelInfo channel = new ChannelInfo();

		channel.setChannelID(getJsonObjectString(jsonObject, "channelID"));
		channel.setChannelName(getJsonObjectString(jsonObject, "channelName"));
		// channel.setChannelCode(getJsonObjectString(jsonObject,
		// "channelCode"));
		// channel.setDescription(getJsonObjectString(jsonObject,
		// "description"));
		channel.setVideoType(getJsonObjInt(jsonObject, "videoType"));
		channel.setFeeType(getJsonObjInt(jsonObject, "feeType"));
		channel.setResourceOrder(getJsonObjInt(jsonObject, "resourceOrder"));
		channel.setResourceCode(getJsonObjInt(jsonObject, "ResourceCode"));
		channel.setChannelType(getJsonObjectString(jsonObject, "channelType"));
		channel.setCityCode(getJsonObjectString(jsonObject, "cityCode"));
		channel.setGradeCode(getJsonObjectString(jsonObject, "gradeCode"));
		channel.setChannelSpec(getJsonObjectString(jsonObject, "channelSpec"));
		// channel.setNetworkId(getJsonObjInt(jsonObject, "networkId"));
		channel.setTSID(getJsonObjInt(jsonObject, "TSID"));
		channel.setServiceid(getJsonObjInt(jsonObject, "serviceid"));
		channel.setAssetID(getJsonObjectString(jsonObject, "assetID"));
		channel.setProviderID(getJsonObjectString(jsonObject, "providerID"));
		// channel.setPosterInfo(getJsonObjectString(jsonObject,
		// "posterInfo"));//待完成
		channel.setChannelTypes(splitTypes(getJsonObjectString(jsonObject, "channelTypes")));
		channel.setChannelNumber(getJsonObjectString(jsonObject, "channelNumber"));
		// channel.setFrequency(getJsonObjectString(jsonObject, "frequency"));
		// channel.setServiceid(getJsonObjectString(jsonObject, "serviceid"));
		// channel.setSymbolRate(getJsonObjectString(jsonObject, "symbolRate"));
		// channel.setModulation(getJsonObjectString(jsonObject, "modulation"));
		channel.setPlatform(getJsonObjInt(jsonObject, "platform"));

		return channel;
	}
	
	public ChannelType jsonToType(JSONObject json){
		ChannelType type=new ChannelType();
		type.setPramKey(getJsonObjectString(json, "pramKey"));
		type.setPramValue(getJsonObjectString(json, "pramValue"));
		type.setRank(getJsonObjInt(json, "rank"));
		return type;
	}
	
	//get channel type
	public  List<ChannelType> jsonToTypes(JSONObject json){
		List<ChannelType> list=new ArrayList<ChannelType>();
		JSONArray types = getJsonObjectArray(json, "datas");
		if(null==types){
			return null;
		}
		for (int i = 0; i < types.length(); i++) {
			ChannelType type=null;
			try {
				JSONObject obj=(JSONObject) types.get(i);
				type=jsonToType(obj);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(type!=null){
				list.add(type);
			}
		}
		return list;
	}
	
	// get all channel info
	public List<ChannelInfo> jsonToChannels(JSONObject jsonObject) {
		List<ChannelInfo> list = new ArrayList<ChannelInfo>();
		JSONArray channels = getJsonObjectArray(jsonObject, "channelInfo");
		for (int i = 0; i < channels.length(); i++) {
			ChannelInfo channelInfo = null;
			try {
				JSONObject object = (JSONObject) channels.get(i);
				channelInfo = jsonToChannel(object);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (channelInfo != null)
				list.add(channelInfo);

		}

		return sortChannels(list);
	}

	public PosterInfo getJsonPoster(JSONObject json) {
		PosterInfo posterInfo = null;

		return posterInfo;
	}


	public String getLivePlayURL(JSONObject json) {
		String sdURL = null;
		Map<String, String> playURLMap=new HashMap<String, String>();
		JSONArray bitUrlList = getJsonObjectArray(json, "bitPlayUrlList");
		if(bitUrlList!=null&&bitUrlList.length()>0){
			for (int i = 0; i < bitUrlList.length(); i++) {
				JSONObject bitUrl = null;
				try {
					bitUrl = bitUrlList.getJSONObject(i);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.e("mmmm", "bitUrlList:" + bitUrlList);
				}
				String type = getJsonObjectString(bitUrl, "bitrate");
				playURLMap.put(type, getJsonObjectString(bitUrl, "url"));
				
			}
		}
		if (playURLMap.containsKey("电视高清")) {
			sdURL =playURLMap.get("电视高清");
			return sdURL;
		}else if(playURLMap.containsKey("电视标清")){
			sdURL =playURLMap.get("电视标清");
			return sdURL;
		}else if(playURLMap.containsKey("高清")){
			sdURL =playURLMap.get("高清");
			return sdURL;
		}else if(playURLMap.containsKey("标清")){
			sdURL =playURLMap.get("标清");
			return sdURL;
		}
		return sdURL;
	}
	

	public ProgramInfo jsonToProgram(JSONObject json) {
		ProgramInfo program = new ProgramInfo();

		program.setProgramId(getJsonObjInt(json, "programId"));
		program.setChannelID(getJsonObjectString(json, "channelID"));
		program.setEventDate(getJsonData(json, "eventDate"));
		program.setBeginTime(getJsonData(json, "beginTime"));
		program.setEndTime(getJsonData(json, "endTime"));
		program.setEventName(getJsonObjectString(json, "eventName"));
		// program.setEventDesc(getJsonObjectString(json, "eventDesc"));
//		program.setKeyWord(getJsonObjectString(json, "keyWord"));
//		program.setIsBook(getJsonObjInt(json, "isBook"));
//		program.setIsRecommend(getJsonObjInt(json, "isRecommend"));
//		program.setPlayCount(getJsonObjInt(json, "playCount"));
		program.setAssetID(getJsonObjectString(json, "assetID"));
		// program.setPoster(getJsonObjInt(json, "poster"));
		// program.setPlaytime(getJsonObjInt(json, "playtime"));
//		program.setVolumeName(getJsonObjectString(json, "volumeName"));
		program.setChannelResourceCode(getJsonObjInt(json, "channelResourceCode"));
		program.setVideoType(getJsonObjectString(json, "videoType"));
		program.setProviderID(getJsonObjectString(json, "providerID"));
		// program.setChannelName(getJsonObjectString(json, "channelName"));
//		program.setStatus(getJsonObjInt(json, "status"));
//		program.setViewLevel(getJsonObjectString(json, "viewLevel"));

		return program;
	}

	public  List<ProgramInfo> jsonToPrograms(JSONObject json) {
		List<ProgramInfo> list = new ArrayList<ProgramInfo>();
		JSONArray programs = getJsonObjectArray(json, "program");
		for (int i = 0; i < programs.length(); i++) {
			ProgramInfo program = null;
			try {
				JSONObject object = (JSONObject) programs.get(i);
				program = jsonToProgram(object);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (program != null) {
				list.add(program);
			}
		}

		return list;
	}
	
	public List<ProgramInfo> timeShiftPrograms(JSONObject json) {
		List<ProgramInfo> list = new ArrayList<ProgramInfo>();
		JSONArray programs = getJsonObjectArray(json, "program");
		for (int i = 0; i < programs.length(); i++) {
			ProgramInfo program = null;
			try {
				JSONObject object = (JSONObject) programs.get(i);
				program = jsonToProgram(object);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (program != null) {
				list.add(program);
			}
		}
		CacheData.setTimeshiftPrograms(list);
		return list;
	}

	public List<ProgramInfo> curJsonProToString(JSONObject json) {
		JSONObject jsonDatas = null;
		List<ProgramInfo> list = new ArrayList<ProgramInfo>();
		
		try {
			jsonDatas = json.getJSONObject("lastProgram");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(jsonDatas!=null){
		
		list.add(0, jsonToProgram(jsonDatas));
		}
		try {
			jsonDatas = json.getJSONObject("currentProgram");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(jsonDatas!=null&&list.size()>0){
		list.add(1, jsonToProgram(jsonDatas));
		}
		try {
			jsonDatas = json.getJSONObject("nextProgram");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(jsonDatas!=null&&list.size()>0){
		list.add(2, jsonToProgram(jsonDatas));
		}
		CacheData.setCurPrograms(list);
		return list;
	}
	
	public List<ChannelInfo> jsonToChannelExtra(JSONObject json){
		ChannelInfo channel;
		List<ChannelInfo> listExtra=new ArrayList<ChannelInfo>();
		Map<String, ChannelInfo> map=CacheData.getAllChannelMap();
		
		
		JSONArray jsonArray=getJsonObjectArray(json, "datas");
		for(int i=0;i<jsonArray.length();i++){
			JSONObject extraJson;
			try {
				extraJson = (JSONObject) jsonArray.get(i);
				channel=new ChannelInfo();
				channel.setChannelNumber(getJsonObjectString(extraJson, "channelNO"));
				channel.setIsBTV(getJsonObjectString(extraJson, "isBTV"));
				channel.setIsTTV(getJsonObjectString(extraJson, "isTTV"));
				channel.setChannelLogo(getJsonObjectString(extraJson, "channelLogo"));
				listExtra.add(channel);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
		return sortChannels(listExtra);
	}

	// =================================base function add try catch=====================================

	private String getJsonObjectString(JSONObject jsonObj, String key) {

		String rValue = "";
		try {
			rValue = jsonObj.getString(key);
		} catch (JSONException ex) {
			ex.printStackTrace();
			Log.e("mmmm", "LiveJsonResolve:" + key);
		}
		return rValue;
	}

	private JSONArray getJsonObjectArray(JSONObject jsonObj, String key) {

		JSONArray rValue = null;
		try {
			rValue = jsonObj.getJSONArray(key);
		} catch (JSONException ex) {
			ex.printStackTrace();
			Log.e("mmmm", "LiveJsonResolve:" + key);
		}
		return rValue;
	}

	private int getJsonObjInt(JSONObject json, String key) {
		int i = 9999;
		try {
			i = json.getInt(key);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("mmmm", "LiveJsonResolve:" + key);
		}
		return i;
	}

	private Date getJsonData(JSONObject json, String key) {
		Date date = null;
		DateFormat sdf = null;
		String str = getJsonObjectString(json, key);
		if (key.equals("eventDate")) {
			sdf = new SimpleDateFormat("yyyy-MM-dd");
			
			// sdf= DateFormat.getDateInstance(DateFormat.MEDIUM);
		} else {
			sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
		sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		try {
			date = sdf.parse(str);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return date;
	}

	

	/* sort channels info num */
	public List<ChannelInfo> sortChannels(List<ChannelInfo> outterList) {

		Collections.sort(outterList, new Comparator<ChannelInfo>() {

			public int compare(ChannelInfo o1, ChannelInfo o2) {
				int result = Integer.parseInt(o1.getChannelNumber()) - Integer.parseInt(o2.getChannelNumber());
				if (result == 0) {
					result = o1.getChannelName().compareTo(o2.getChannelName());
				}
				return result;
			}
		});

		return outterList;
	}
	
	private boolean isOutOfDate(ProgramInfo program){
		boolean flag=false;
		Date date=new Date();
		flag=date.after(program.getEndTime());
		
		return flag;
	}
	
	public List<String> splitTypes(String types){
		List<String> list=new ArrayList<String>();
		String[] typeStr=types.split(",");
		for(int i=0;i<typeStr.length;i++){
			list.add(typeStr[i]);
		}
		return list;
	}
}

package com.changhong.ghlive.datafactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.provider.ContactsContract.Contacts.Data;
import android.util.Log;

import com.changhong.gehua.common.ChannelInfo;
import com.changhong.gehua.common.PosterInfo;
import com.changhong.gehua.common.ProgramInfo;

public class JsonResolve {

	private static JsonResolve liveJsonResolve;

	public static JsonResolve getInstance() {
		if (null == liveJsonResolve) {
			liveJsonResolve = new JsonResolve();
		}
		return liveJsonResolve;
	}

	//get single channel info 
	public ChannelInfo jsonToChannel(JSONObject jsonObject) {
		ChannelInfo channel = new ChannelInfo();

		channel.setChannelID(getJsonObjectString(jsonObject, "channelID"));
		channel.setChannelName(getJsonObjectString(jsonObject, "channelName"));
//		channel.setChannelCode(getJsonObjectString(jsonObject, "channelCode"));
//		channel.setDescription(getJsonObjectString(jsonObject, "description"));
		channel.setVideoType(getJsonObjInt(jsonObject, "videoType"));
		channel.setFeeType(getJsonObjInt(jsonObject, "feeType"));
		channel.setResourceOrder(getJsonObjInt(jsonObject, "resourceOrder"));
		channel.setResourceCode(getJsonObjInt(jsonObject, "ResourceCode"));
		channel.setChannelType(getJsonObjectString(jsonObject, "channelType"));
		channel.setCityCode(getJsonObjectString(jsonObject, "cityCode"));
		channel.setGradeCode(getJsonObjectString(jsonObject, "gradeCode"));
		channel.setChannelSpec(getJsonObjectString(jsonObject, "channelSpec"));
//		channel.setNetworkId(getJsonObjInt(jsonObject, "networkId"));
		channel.setTSID(getJsonObjInt(jsonObject, "TSID"));
		channel.setServiceid(getJsonObjInt(jsonObject, "serviceid"));
		channel.setAssetID(getJsonObjectString(jsonObject, "assetID"));
		channel.setProviderID(getJsonObjectString(jsonObject, "providerID"));
//		channel.setPosterInfo(getJsonObjectString(jsonObject, "posterInfo"));//待完成
		channel.setChannelTypes(getJsonObjectString(jsonObject, "channelTypes"));
		channel.setChannelNumber(getJsonObjectString(jsonObject, "channelNumber"));
//		channel.setFrequency(getJsonObjectString(jsonObject, "frequency"));
//		channel.setServiceid(getJsonObjectString(jsonObject, "serviceid"));
//		channel.setSymbolRate(getJsonObjectString(jsonObject, "symbolRate"));
//		channel.setModulation(getJsonObjectString(jsonObject, "modulation"));
		channel.setPlatform(getJsonObjInt(jsonObject, "platform"));

		return channel;
	}

	//get all channel info 
	public List<ChannelInfo> jsonToChannels(JSONObject jsonObject) {
		List<ChannelInfo> list = new ArrayList<ChannelInfo>();
		JSONArray channels = getJsonObjectArray(jsonObject, "channelInfo");
		for (int i = 0; i < channels.length(); i++) {
			ChannelInfo channelInfo=null;
			try {
				JSONObject object = (JSONObject) channels.get(i);
				channelInfo = jsonToChannel(object);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(channelInfo!=null)list.add(channelInfo);

		}

		return list;
	}
	
	public PosterInfo getJsonPoster(JSONObject json){
		PosterInfo posterInfo=null;
		
		
		return posterInfo;
	}
	
	public String getPlayURL(JSONObject json){
		String strURL=getJsonObjectString(json, "palyURL");
		return strURL;
	}
	
	public ProgramInfo jsonToProgram(JSONObject json){
		ProgramInfo program=new ProgramInfo();
		
		program.setProgramId(getJsonObjInt(json, "programId"));
		program.setChannelID(getJsonObjInt(json, "channelID"));
		program.setEventDate(getJsonData(json, "eventDate"));
		program.setBeginTime(getJsonData(json, "beginTime"));
		program.setEndTime(getJsonData(json, "endTime"));
		program.setEventName(getJsonObjectString(json, "eventName"));
//		program.setEventDesc(getJsonObjectString(json, "eventDesc"));
		program.setKeyWord(getJsonObjectString(json, "keyWord"));
		program.setIsBook(getJsonObjInt(json, "isBook"));
		program.setIsRecommend(getJsonObjInt(json, "isRecommend"));
		program.setPlayCount(getJsonObjInt(json, "playCount"));
		program.setAssetID(getJsonObjectString(json, "assetID"));
//		program.setPoster(getJsonObjInt(json, "poster"));
//		program.setPlaytime(getJsonObjInt(json, "playtime"));
		program.setVolumeName(getJsonObjectString(json, "volumeName"));
		program.setChannelResourceCode(getJsonObjInt(json, "channelResourceCode"));
		program.setVideoType(getJsonObjectString(json, "videoType"));
		program.setProviderID(getJsonObjectString(json, "providerID"));
//		program.setChannelName(getJsonObjectString(json, "channelName"));
		program.setStatus(getJsonObjInt(json, "status"));
		program.setViewLevel(getJsonObjectString(json, "viewLevel"));
		
		
		
		return program;
	}
	
	
	public List<ProgramInfo> jsonToPrograms(JSONObject json){
		List<ProgramInfo> list = new ArrayList<ProgramInfo>();
		JSONArray programs = getJsonObjectArray(json, "program");
		for (int i = 0; i < programs.length(); i++) {
			ProgramInfo program=null;
			try {
				JSONObject object = (JSONObject) programs.get(i);
				program = jsonToProgram(object);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(program!=null)list.add(program);

		}
		
		return list;
	}
	
	//=================================base function add  try catch=====================================

	private String getJsonObjectString(JSONObject jsonObj, String key) {

		String rValue = "";
		try {
			rValue = jsonObj.get(key).toString();
		} catch (JSONException ex) {
			ex.printStackTrace();
			Log.e("mmmm", "LiveJsonResolve:"+key);
		}
		return rValue;
	}

	private JSONArray getJsonObjectArray(JSONObject jsonObj, String key) {

		JSONArray rValue = null;
		try {
			rValue = jsonObj.getJSONArray(key);
		} catch (JSONException ex) {
			ex.printStackTrace();
			Log.e("mmmm", "LiveJsonResolve:"+key);
		}
		return rValue;
	}
	
	private int getJsonObjInt(JSONObject json,String key){
		int i=9999;
		try {
			i=json.getInt(key);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("mmmm", "LiveJsonResolve:"+key);
		}
		return i;
	}
	
	private Date getJsonData(JSONObject json,String key){
		Date date=null;
		SimpleDateFormat sdf=null;
		String str=getJsonObjectString(json, key);
		sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			date = sdf.parse(str);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return date;
	}
}

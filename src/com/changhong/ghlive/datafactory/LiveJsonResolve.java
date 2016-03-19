package com.changhong.ghlive.datafactory;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.changhong.gehua.common.ChannelInfo;
import com.changhong.gehua.common.PosterInfo;

public class LiveJsonResolve {

	private static LiveJsonResolve liveJsonResolve;

	public static LiveJsonResolve getInstance() {
		if (null == liveJsonResolve) {
			liveJsonResolve = new LiveJsonResolve();
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
	
	private PosterInfo getJsonPoster(JSONObject json){
		PosterInfo posterInfo=null;
		
		
		return posterInfo;
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
		int i=999;
		try {
			i=json.getInt(key);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("mmmm", "LiveJsonResolve:"+key);
		}
		return i;
	}
}

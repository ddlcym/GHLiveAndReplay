package com.changhong.ghlive.datafactory;

import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.changhong.gehua.common.CacheData;
import com.changhong.gehua.common.ChannelInfo;
import com.changhong.ghlive.activity.MyApp;
import com.changhong.ghlive.service.HttpService;

public class HandleLiveData {
	private JsonResolve JsonResolve=null;
	private Context con;
	
	
	private static HandleLiveData handliveData;
	
	public static HandleLiveData getInstance(){
		if(null==handliveData){
			handliveData=new HandleLiveData();
		}
		return handliveData;
	}

	public HandleLiveData(){
		if(null==JsonResolve){
			JsonResolve=JsonResolve.getInstance();
		}
		
		if(null==con){
			con=MyApp.getContext();
		}
	}
	
	public  List<ChannelInfo> dealChannelJson(JSONObject json){
		List<ChannelInfo> channels=	JsonResolve.jsonToChannels(json);
		CacheData.setAllChannelInfo(channels);
		
		return channels;
	}
	

}

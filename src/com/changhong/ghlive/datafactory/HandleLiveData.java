package com.changhong.ghlive.datafactory;

import java.util.List;

import org.json.JSONObject;

import com.changhong.gehua.common.ChannelInfo;

public class HandleLiveData {
	private LiveJsonResolve liveJsonResolve=null;
	
	
	private static HandleLiveData handliveData;
	
	public static HandleLiveData getInstance(){
		if(null==handliveData){
			handliveData=new HandleLiveData();
		}
		return handliveData;
	}

	public HandleLiveData(){
		if(null==liveJsonResolve){
			liveJsonResolve=LiveJsonResolve.getInstance();
		}
	}
	
	public  void dealChannelJson(JSONObject json){
		List<ChannelInfo> channels=	liveJsonResolve.jsonToChannels(json);
//		Log.i("mmmm", "channels"+channels.toString());
			
	}
	

}

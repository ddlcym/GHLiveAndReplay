package com.changhong.ghlive.datafactory;

import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.changhong.gehua.common.ChannelInfo;
import com.changhong.ghlive.activity.MyApp;
import com.changhong.ghlive.service.HttpService;

public class HandleLiveData {
	private JsonResolve liveJsonResolve=null;
	private Context con;
	
	
	private static HandleLiveData handliveData;
	
	public static HandleLiveData getInstance(){
		if(null==handliveData){
			handliveData=new HandleLiveData();
		}
		return handliveData;
	}

	public HandleLiveData(){
		if(null==liveJsonResolve){
			liveJsonResolve=JsonResolve.getInstance();
		}
		
		if(null==con){
			con=MyApp.getContext();
		}
	}
	
	public  void dealChannelJson(JSONObject json){
		List<ChannelInfo> channels=	liveJsonResolve.jsonToChannels(json);
		
		Intent intent =new Intent();
		intent.setClass(con, HttpService.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("playLiveChannel", channels.get(1));
		intent.putExtras(bundle);
		con.startService(intent);
		
//		Log.i("mmmm", "channels"+channels.toString());
			
	}
	

}

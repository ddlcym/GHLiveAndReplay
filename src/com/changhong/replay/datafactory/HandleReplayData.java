package com.changhong.replay.datafactory;

import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.changhong.gehua.common.ChannelInfo;
import com.changhong.gehua.common.ProgramInfo;
import com.changhong.ghlive.activity.MyApp;
import com.changhong.ghlive.datafactory.HandleLiveData;
import com.changhong.ghlive.datafactory.JsonResolve;

public class HandleReplayData {
	private JsonResolve jsonResolve=null;
	private Context con;
	
	
	private static HandleReplayData handleReplayData;
	
	public static HandleReplayData getInstance(){
		if(null==handleReplayData){
			handleReplayData=new HandleReplayData();
		}
		return handleReplayData;
	}

	public HandleReplayData(){
		if(null==jsonResolve){
			jsonResolve=JsonResolve.getInstance();
		}
		
		if(null==con){
			con=MyApp.getContext();
		}
	}
	
	public  void dealChannelJson(JSONObject json){
		List<ProgramInfo> programs=	jsonResolve.jsonToPrograms(json);
		
		Log.i("mmmm", "programs"+programs.toString());
	}
}

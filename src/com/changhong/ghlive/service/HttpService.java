package com.changhong.ghlive.service;


import com.alibaba.fastjson.JSONObject;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.changhong.ghlive.activity.VolleyTool;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class HttpService extends Service{

	private VolleyTool volleyTool;
	private RequestQueue mReQueue;
	
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		volleyTool=VolleyTool.getInstance();
		mReQueue=volleyTool.getRequestQueue();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

	
	private void getChannelList(){
		String URL="";
//		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
//		jsonObjectRequest.setTag(HttpService.class.getSimpleName());//设置tag cancelAll的时候使用
//		mReQueue.add(jsonObjectRequest);
	}
	
	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	
}

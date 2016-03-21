package com.changhong.ghlive.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.changhong.gehua.common.ChannelInfo;
import com.changhong.gehua.common.ProcessData;
import com.changhong.gehua.common.VolleyTool;
import com.changhong.ghlive.datafactory.HandleLiveData;
import com.changhong.replay.datafactory.HandleReplayData;

public class HttpService extends Service {

	private String TAG = "mmmm";

	private VolleyTool volleyTool;
	private RequestQueue mReQueue;

	private ProcessData processData;
	

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		initData();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		if (intent != null) {
			int command=0;
			command=intent.getIntExtra("command", 0);
			//play live show
			ChannelInfo channel = (ChannelInfo) intent.getSerializableExtra("playLiveChannel");
			if (channel != null) {
//				getPointProList(channel);
				
			}
		}

		return super.onStartCommand(intent, flags, startId);
	}

	private void initData() {
		if (null == processData) {
			processData = new ProcessData();
		}
		volleyTool = VolleyTool.getInstance();
		mReQueue = volleyTool.getRequestQueue();
		getChannelList();
	}


	private void getChannelList() {
		// 传入URL请求链接
		String URL = processData.getChannelList();
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
				Request.Method.GET, URL, null,
				new Response.Listener<org.json.JSONObject>() {

					@Override
					public void onResponse(org.json.JSONObject arg0) {
						// TODO Auto-generated method stub
						// 相应成功
//						Log.i(TAG, "HttpService=channle:" + arg0);
						getLivePlayURL(HandleLiveData.getInstance().dealChannelJson(arg0).get(1));
						
					}
				}, errorListener);
		jsonObjectRequest.setTag(HttpService.class.getSimpleName());// 设置tag,cancelAll的时候使用
		mReQueue.add(jsonObjectRequest);
	}
	
	private void getLivePlayURL(ChannelInfo outterchanInfo) {

		String realurl = processData.getLivePlayUrlString(outterchanInfo);

		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
				Request.Method.POST, realurl, null,
				new Response.Listener<org.json.JSONObject>() {

					@Override
					public void onResponse(org.json.JSONObject arg0) {
						// TODO Auto-generated method stub
						// 相应成功
						Log.i(TAG, "getPlayURL" + arg0);
					}
				}, errorListener);
		jsonObjectRequest.setTag(HttpService.class.getSimpleName());// 设置tag,cancelAll的时候使用
		mReQueue.add(jsonObjectRequest);

	}
	
	//get  programs in the channel 
	private void getPointProList(ChannelInfo channel){
		String realurl = processData.getChannelProgramList(channel);

		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
				Request.Method.GET, realurl, null,
				new Response.Listener<org.json.JSONObject>() {

					@Override
					public void onResponse(org.json.JSONObject arg0) {
						// TODO Auto-generated method stub
						// 相应成功
//						Log.i(TAG, "getPointProList:" + arg0)
						HandleReplayData.getInstance().dealChannelJson(arg0);
					}
				}, errorListener);
		jsonObjectRequest.setTag(HttpService.class.getSimpleName());// 设置tag,cancelAll的时候使用
		mReQueue.add(jsonObjectRequest);
	}

	private Response.ErrorListener errorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError arg0) {
			// TODO Auto-generated method stub
			Log.i(TAG, "HttpService=error：" + arg0);
		}
	};

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mReQueue.cancelAll(HttpService.class.getSimpleName());
	}

}

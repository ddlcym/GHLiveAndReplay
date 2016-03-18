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
import com.changhong.ghlive.activity.VolleyTool;




public class HttpService extends Service {

	private String TAG="mmmm";
	
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
		
		initData();
		
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}
	
	private void initData(){
		volleyTool=VolleyTool.getInstance();
		mReQueue=volleyTool.getRequestQueue();
		getChannelList();
	}

	
	private void getChannelList(){
		//传入URL请求链接
		String URL="http://ott.yun.gehua.net.cn:8080/msis/getChannels?version=V001&channelVersion=0&resolution=800*600&terminalType=1&authKey=16e21faf070e6bee21b26a7cc80fb5f6";
		
		
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,URL,null,new Response.Listener<org.json.JSONObject>() {

			@Override
			public void onResponse(org.json.JSONObject arg0) {
				// TODO Auto-generated method stub
				//相应成功
				Log.i(TAG, "channle"+arg0);
			}
		},errorListener); 
		jsonObjectRequest.setTag(HttpService.class.getSimpleName());//设置tag cancelAll的时候使用
		mReQueue.add(jsonObjectRequest);
	}
	
	private Response.ErrorListener errorListener=new Response.ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError arg0) {
			// TODO Auto-generated method stub
			Log.i(TAG, "error"+arg0);
		}
	};
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	
}

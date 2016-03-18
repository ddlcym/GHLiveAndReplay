package com.changhong.ghlive.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

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
		String URL="";
		
		
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,URL,null,new Response.Listener<org.json.JSONObject>() {

			@Override
			public void onResponse(org.json.JSONObject arg0) {
				// TODO Auto-generated method stub
				//相应成功
				
			}
		},new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				// TODO Auto-generated method stub
				
			}
		}); 
		jsonObjectRequest.setTag(HttpService.class.getSimpleName());//设置tag cancelAll的时候使用
		mReQueue.add(jsonObjectRequest);
	}
	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	
}

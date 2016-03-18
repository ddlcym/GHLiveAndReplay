package com.changhong.ghlive.activity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleyTool {

	private static VolleyTool httpFrame;
	
	private RequestQueue requestQueue;
	public static VolleyTool getInstance(){
		
		if(null==httpFrame){
			httpFrame=new VolleyTool();
		}
		return httpFrame;
	}
	
	public VolleyTool(){
		if(null==requestQueue){
			requestQueue=Volley.newRequestQueue(MyApp.getContext());
		}
	}
	
	public RequestQueue getRequestQueue(){
		return requestQueue;
	}
	
	public void stop(){
		requestQueue.cancelAll(this);
	}
}

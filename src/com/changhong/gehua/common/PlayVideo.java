package com.changhong.gehua.common;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.changhong.ghlive.datafactory.JsonResolve;
import com.changhong.ghlive.service.HttpService;

public class PlayVideo {
	private static PlayVideo playVideo = null;
	private String TAG = "mmmm";
	
	private VolleyTool volleyTool;
	private RequestQueue mReQueue;
	private JsonResolve jsonResolve;
	private ProcessData processData;

	public static PlayVideo getInstance() {

		if (null == playVideo) {
			playVideo = new PlayVideo();
		}
		return playVideo;
	}
	
	public PlayVideo (){
		if (null == processData) {
			processData = new ProcessData();
		}
		volleyTool = VolleyTool.getInstance();
		mReQueue = volleyTool.getRequestQueue();
		jsonResolve=JsonResolve.getInstance();
	}
	
	
	
	public void playLiveProgram(final VideoView videoView,ChannelInfo outterchanInfo) {

		String realurl = processData.getLivePlayUrlString(outterchanInfo);

		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
				Request.Method.POST, realurl, null,
				new Response.Listener<org.json.JSONObject>() {

					@Override
					public void onResponse(org.json.JSONObject arg0) {
						// TODO Auto-generated method stub
						// 相应成功
//						Log.i(TAG, "PlayVideo-getPlayURL：" + arg0);
//						Message msg=new Message();
//						msg.what=Class_Constant.PLAY_LIVE;
//						msg.obj=jsonResolve.getHDPlayURL(arg0);
//						handler.sendMessage(msg);
						final String playurl=jsonResolve.getHDPlayURL(arg0);
						videoView.post(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								videoView.setVideoPath(playurl);
								videoView.start();
							}
						});
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

}

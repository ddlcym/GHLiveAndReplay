package com.changhong.gehua.common;

import java.text.SimpleDateFormat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.changhong.ghlive.datafactory.Banner;
import com.changhong.ghlive.datafactory.JsonResolve;
import com.changhong.ghlive.service.HttpService;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class PlayVideo {
	private static PlayVideo playVideo = null;
	private String TAG = "mmmm";

	private VolleyTool volleyTool;
	private RequestQueue mReQueue;
	private JsonResolve jsonResolve;
	private ProcessData processData;
	private Banner mBan;

	public static PlayVideo getInstance() {

		if (null == playVideo) {
			playVideo = new PlayVideo();
		}
		return playVideo;
	}

	public PlayVideo() {
		if (null == processData) {
			processData = new ProcessData();
		}
		volleyTool = VolleyTool.getInstance();
		mReQueue = volleyTool.getRequestQueue();
		jsonResolve = JsonResolve.getInstance();
	}

	public void playLiveProgram(final VideoView videoView, ChannelInfo outterchanInfo) {

		String realurl = processData.getLivePlayUrlString(outterchanInfo);

		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, realurl, null,
				new Response.Listener<org.json.JSONObject>() {

					@Override
					public void onResponse(org.json.JSONObject arg0) {
						// TODO Auto-generated method stub
						// 相应成功
						// Log.i(TAG, "PlayVideo-getPlayURL：" + arg0);
						// Message msg=new Message();
						// msg.what=Class_Constant.PLAY_LIVE;
						// msg.obj=jsonResolve.getHDPlayURL(arg0);
						// handler.sendMessage(msg);
						final String playurl = jsonResolve.getHDPlayURL(arg0);
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

	public void showBanner(final Handler handler, ChannelInfo outterChannelInfo) {
		String pgmRequestURL = processData.getCurrentProgramInfo(outterChannelInfo);
		// Map<String, String> map = new HashMap<String, String>();
		Log.i("zyt", pgmRequestURL);
		// final ProgramInfo rPgmInfo = new ProgramInfo();
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, pgmRequestURL, null,
				new Response.Listener<org.json.JSONObject>() {

					@Override
					public void onResponse(org.json.JSONObject arg0) {
						// TODO Auto-generated method stub
						// 相应成功
						// Log.i(TAG, "PlayVideo-getPlayURL：" + arg0);
						// Message msg=new Message();
						// msg.what=Class_Constant.PLAY_LIVE;
						// msg.obj=jsonResolve.getHDPlayURL(arg0);
						// handler.sendMessage(msg);

						// final String playurl =
						// jsonResolve.getHDPlayURL(arg0);
						// outterBan.post(new Runnable() {

						// return jsonResolve.curJsonProToString(arg0);
						// map = jsonResolve.curJsonProToString(arg0);
						// Log.i("zyt", arg0 + "");

						ProgramInfo rPgmInfo = new ProgramInfo();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

						Message msg = new Message();
						msg.what = Class_Constant.BANNER_PROGRAM_PASS;
						msg.obj = jsonResolve.curJsonProToString(arg0);
						handler.sendMessage(msg);

						rPgmInfo.setEventName(jsonResolve.curJsonProToString(arg0).get("name"));
						try {
							rPgmInfo.setBeginTime(sdf.parse(jsonResolve.curJsonProToString(arg0).get("playTime")));
						} catch (java.text.ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						// Log.i("zyt", rPgmInfo.getEventName());
						// Log.i("zyt", rPgmInfo.getBeginTime() + "");

					}
				}, errorListener);
		jsonObjectRequest.setTag(HttpService.class.getSimpleName());// 设置tag,cancelAll的时候使用
		mReQueue.add(jsonObjectRequest);

		// String processData.getCurrentProgramInfo(curChannel);
		// JsonObjectRequest(Request.Method.POST, pgmRequestURL, null,
		// new Response.Listener<org.json.JSONObject>() {
		//
		// @Override
		// public void onResponse(org.json.JSONObject arg0) {
		// // TODO Auto-generated method stub
		// // 相应成功
		// // Log.i(TAG, "HttpService=channle:" + arg0);
		//
		// // final String playurl =
		// // jsonResolve.getHDPlayURL(arg0);
		// JsonResolve jsonResolve = new JsonResolve().getInstance();
		//
		// ProgramInfo pgmInfo = jsonResolve.jsonToProgram(arg0);
		// Log.i("zyt", pgmInfo.getEventName());
		// // channelsAll =
		// // HandleLiveData.getInstance().dealChannelJson(arg0);
		// // setadapter
		// // chLstAdapter.setData(channelsAll);
		// // Log.i(TAG, "HttpService=channelsAll:" +
		// // channelsAll.size());
		// // if (channelsAll.size() <= 0) {
		// // channelListLinear.setVisibility(View.INVISIBLE);
		// // }
		//
		// }
		// }, errorListener);
		// jsonObjectRequest.setTag(HttpService.class.getSimpleName());//
		// 设置tag,cancelAll的时候使用
		// mReQueue.add(jsonObjectRequest);
	}

	private Response.ErrorListener errorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError arg0) {
			// TODO Auto-generated method stub
			Log.i(TAG, "HttpService=error：" + arg0);
		}
	};

}

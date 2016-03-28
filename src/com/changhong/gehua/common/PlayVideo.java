package com.changhong.gehua.common;

import java.text.SimpleDateFormat;

import org.json.JSONException;
import org.json.JSONObject;

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
	// private int curProgramId = 0;

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
						// Log.i("zyt", "play url is + 0326" + playurl);

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

	/*******************************************************************************************/
	/* replay test */
	public void playReplayProgram(final Handler handler, String outterPlayUrl) {

		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, outterPlayUrl, null,
				new Response.Listener<org.json.JSONObject>() {

					@Override
					public void onResponse(org.json.JSONObject arg0) {
						 Log.i("mmmm", " PlayVideo-arg0:"+ arg0);

						String playurl = jsonResolve.getHDPlayURL(arg0);
						Message msg=new Message();
						msg.what=Class_Constant.REPLAY_URL;
						msg.obj=playurl;
						handler.sendMessage(msg);
					}
				}, errorListener);
		jsonObjectRequest.setTag(HttpService.class.getSimpleName());//
		// 设置tag,cancelAll的时候使用
		mReQueue.add(jsonObjectRequest);
	}

	public void playReplayProgramTest(final VideoView videoView, String outterPlayUrl) {
		// String pgmRequestURL =
		// processData.getCurrentProgramInfo(outterChannelInfo);

		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, outterPlayUrl, null,
				new Response.Listener<org.json.JSONObject>() {

					@Override
					public void onResponse(org.json.JSONObject arg0) {

						final String playurl = jsonResolve.getHDPlayURL(arg0);
						// Log.i("zyt", "replay url is + 0326 --" + playurl);
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
		jsonObjectRequest.setTag(HttpService.class.getSimpleName());//
		// 设置tag,cancelAll的时候使用
		mReQueue.add(jsonObjectRequest);
	}

	/*******************************************************************************************/

	/* 获取当前节目信息 */
	public void getProgramInfo(final Handler handler, ChannelInfo outterChannelInfo) {
		String pgmRequestURL = processData.getCurrentProgramInfo(outterChannelInfo);
		Log.i("zyt", pgmRequestURL);
		// final ProgramInfo rPgmInfo = new ProgramInfo();
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, pgmRequestURL, null,
				new Response.Listener<org.json.JSONObject>() {

					@Override
					public void onResponse(org.json.JSONObject arg0) {

						ProgramInfo rPgmInfo = new ProgramInfo();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

						Message msg = new Message();
						msg.what = Class_Constant.BANNER_PROGRAM_PASS;
						msg.obj = jsonResolve.curJsonProToString(arg0);
						handler.sendMessage(msg);

						Log.i("zyt", " 节目信息 + 返回内容 " + arg0);

						Log.i("zyt", " 节目信息 + 返回内容 " + jsonResolve.curJsonProToString(arg0));
						rPgmInfo.setEventName(jsonResolve.curJsonProToString(arg0).get("name"));
						try {
							// curProgramId =
							// Integer.parseInt(jsonResolve.curJsonProToString(arg0).get("id"));
							// Log.i("zyt", "解析详情得到的program id " +
							// curProgramId);
							rPgmInfo.setBeginTime(sdf.parse(jsonResolve.curJsonProToString(arg0).get("playTime")));
							rPgmInfo.setProgramId(Integer.parseInt(jsonResolve.curJsonProToString(arg0).get("id")));
							Log.i("zyt", " 节目信息 + 返回内容 + 直播节目ID " + rPgmInfo.getProgramId());

						} catch (java.text.ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}, errorListener);
		jsonObjectRequest.setTag(HttpService.class.getSimpleName());// 设置tag,cancelAll的时候使用
		mReQueue.add(jsonObjectRequest);
	}

	/* 从当前节目信息获取节目详情 */
	public void getProgramInfoDetail(final Handler handler, int outterPgramId) {
		Log.i("zyt", "传进来的id " + outterPgramId);
		String pgmInfoDetailURL = processData.getLivePlayProgramInfo(outterPgramId);
		// Log.i("zyt", "获取直播节目详情的加密后链接" + pgmInfoDetailURL);
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, pgmInfoDetailURL, null,
				new Response.Listener<org.json.JSONObject>() {

					@Override
					public void onResponse(org.json.JSONObject arg0) {
						JSONObject json = null;
						// ProgramInfo rPgmInfoDetail = new ProgramInfo();
						try {
							json = arg0.getJSONObject("programInfo");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Log.i("zyt", " 直播节目详情 " + arg0);

						ProgramInfo entilePgm = new ProgramInfo();
						Log.i("zyt", " json解析类之后的封装直播节目详情 " + arg0);
						entilePgm = jsonResolve.jsonToProgram(json);

						Log.i("zyt", " 直播节目详情 -- beginTime " + entilePgm.getBeginTime());
						Log.i("zyt", " 直播节目详情 -- endTime " + entilePgm.getEndTime());
						Log.i("zyt", " 直播节目详情 -- channelId " + entilePgm.getChannelID());

						Message msg = new Message();
						msg.what = Class_Constant.REPLAY_TIME_LENGTH;
						msg.obj = entilePgm;

						handler.sendMessage(msg);
					}
				}, errorListener);
		jsonObjectRequest.setTag(HttpService.class.getSimpleName());// 设置tag,cancelAll的时候使用
		mReQueue.add(jsonObjectRequest);
	}

	/* 获取节目信息 */
//	public void getChannelInfoDetail(final Handler handler, String outterChannelId) {
//		// Log.i("zyt", "传进来的id " + outterPgramId);
//		String channelInfoDetailURL = processData.getChannelIfo(outterChannelId);
//		// Log.i("zyt", "获取直播节目详情的加密后链接" + pgmInfoDetailURL);
//		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, channelInfoDetailURL, null,
//				new Response.Listener<org.json.JSONObject>() {
//
//					@Override
//					public void onResponse(org.json.JSONObject arg0) {
//						JSONObject json = null;
//						ChannelInfo channelInfoDetail = new ChannelInfo();
//						try {
//							json = arg0.getJSONObject("channelInfo");
//						} catch (JSONException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}

//						Log.i("zyt", "last" + " 频道详情 " + arg0);
//
//						ChannelInfo channelInfoD = new ChannelInfo();
//						// Log.i("zyt", " json解析类之后的封装直播节目详情 " + arg0);
//						channelInfoDetail = jsonResolve.jsonToChannel(json);
//						Log.i("zyt", "last" + " 频道详情 id " + channelInfoDetail.getChannelID());
//						Message msg = new Message();
//						msg.what = Class_Constant.REPLAY_CHANNEL_DETAIL;
//						msg.obj = channelInfoDetail;
//
//						handler.sendMessage(msg);

						//
						// Log.i("zyt", " 直播节目详情 -- beginTime " +
						// entilePgm.getBeginTime());
						// Log.i("zyt", " 直播节目详情 -- endTime " +
						// entilePgm.getEndTime());
						// Log.i("zyt", " 直播节目详情 -- channelId " +
						// entilePgm.getChannelID());

						// Message msg = new Message();
						// msg.what = Class_Constant.REPLAY_TIME_LENGTH;
						// msg.obj = entilePgm;

						// handler.sendMessage(msg);
//					}
//				}, errorListener);
//		jsonObjectRequest.setTag(HttpService.class.getSimpleName());// 设置tag,cancelAll的时候使用
//		mReQueue.add(jsonObjectRequest);
//	}

	private Response.ErrorListener errorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError arg0) {
			// TODO Auto-generated method stub
			Log.i(TAG, "PlayVideo=error：" + arg0);
		}
	};

}

package com.changhong.gehua.http;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.os.Handler;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.changhong.gehua.common.CacheData;
import com.changhong.gehua.common.ChannelInfo;
import com.changhong.gehua.common.ProcessData;
import com.changhong.gehua.common.ProgramInfo;
import com.changhong.gehua.common.Utils;
import com.changhong.gehua.common.VolleyTool;
import com.changhong.gehua.sqlite.DBManager;
import com.changhong.ghlive.activity.EPGActivity;
import com.changhong.ghlive.activity.MyApp;
import com.changhong.replay.datafactory.ResolveEPGInfoThread;

/** 
 * @author  cym  
 * @date 创建时间：2017年5月17日 上午10:53:22 
 * @version 1.0 
 * @parameter   
 */
public class ShowEPGData {
	/*
	 * SQLITE
	 */
	private DBManager dbManager;
	
	private VolleyTool volleyTool;
	private RequestQueue mReQueue;
	private ProcessData processData;
	
	/*
	 * data
	 */
	private List<ProgramInfo> curChannelProgramList = new ArrayList<ProgramInfo>();
	
	public ShowEPGData() {
		dbManager=DBManager.getInstance(MyApp.getContext());
		volleyTool = VolleyTool.getInstance();
		mReQueue = volleyTool.getRequestQueue();
		if (null == processData) {
			processData = new ProcessData();
		}
	}
	
	public void getPointProList(final Handler uiHandler,ChannelInfo channel) {
		dbManager=DBManager.getInstance(MyApp.getContext());
		if(dbManager.isNeedUpdateEPG(channel)){
			mReQueue.cancelAll("program");
			String realurl = processData.getChannelProgramList(channel);
			Log.i("mmmm", "getPointProList-realurl:" + realurl);
			JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, realurl, null,
					new Response.Listener<org.json.JSONObject>() {
	
						@Override
						public void onResponse(org.json.JSONObject arg0) {
							// TODO Auto-generated method stub
							// 相应成功
							// Log.i(TAG, "getPointProList:" + arg0);
							ResolveEPGInfoThread resolveProJsonThread = ResolveEPGInfoThread.getInstance();
							resolveProJsonThread.addData(uiHandler, arg0);
							resolveProJsonThread.startRes();
	
						}
					}, null);
			jsonObjectRequest.setTag("program");// 设置tag,cancelAll的时候使用
			mReQueue.add(jsonObjectRequest);
		}else{
			curChannelProgramList=dbManager.queryEPGByChannelID(channel.getChannelID());
			if(curChannelProgramList!=null&&!curChannelProgramList.isEmpty()){
				dealPointProList(uiHandler);
			}
		}
	}
	
	private void dealPointProList(Handler uiHandler){
		Map<String, List<ProgramInfo>> proMaps = new HashMap<String, List<ProgramInfo>>();
		LinkedList<String> dayMonth = new LinkedList<String>();
		for(ProgramInfo pro:curChannelProgramList){
			Date dt = pro.getEventDate();
			String date = Utils.dateToString(dt);
			
			if (!dayMonth.contains(date)) {
				dayMonth.addFirst(date);
				proMaps.put(date, new ArrayList<ProgramInfo>());

			}
			if(Utils.isOutOfDate(pro)){
				proMaps.get(date).add(pro);
			}
		}
		CacheData.setAllProgramMap(proMaps);
		CacheData.setDayMonths(dayMonth);
		uiHandler.sendEmptyMessage(EPGActivity.MSG_SHOW_WEEKDAY);
	}
}

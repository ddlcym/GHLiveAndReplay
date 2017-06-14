package com.changhong.replay.datafactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.os.Handler;
import android.os.Message;
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

/**
 * @author cym
 * @date 创建时间：2017年6月13日 上午10:27:54
 * @version 1.0
 * @parameter
 */
public class DealViewData {

	private static DealViewData viewData;
	private ResolveEPGInfoThread resolveProJsonThread = null;
	private List<ProgramInfo> curChannelProgramList = new ArrayList<ProgramInfo>();

	private static String curChannelID = "";// 正在执行的的请求节目新频道
	private static String desChannelID;// 用户最后一次操作的节目信息频道

	private static VolleyTool volleyTool;
	private static RequestQueue mReQueue;
	private static ProcessData processData;

	/*
	 * handler msg
	 */
	public static final int Query_EPGData=3001; 
	
	private Handler uiHandler;
	/*
	 * SQLITE
	 */
	private DBManager dbManager;

	public static String getCurChannelID() {
		return curChannelID;
	}

	public static void setCurChannelID(String curChannelID) {
		DealViewData.curChannelID = new String(curChannelID);
	}

	public static String getDesChannelID() {
		return desChannelID;
	}

	public static void setDesChannelID(String desChannelID) {
		DealViewData.desChannelID = new String(desChannelID);
	}

	public static DealViewData getInstance() {
		if (null == viewData) {
			viewData = new DealViewData();
			volleyTool = VolleyTool.getInstance();
			mReQueue = volleyTool.getRequestQueue();
			if (null == processData) {
				processData = new ProcessData();
			}
		}
		return viewData;
	}
	
	private Handler mhandler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case Query_EPGData:
				Message queryMsg=new Message();
				queryMsg.what=EPGActivity.MSG_SHOW_WEEKDAY;
				queryMsg.obj=msg.obj;
				uiHandler.sendMessage(queryMsg);
				break;
			}
			super.handleMessage(msg);
		}
		
	};

	public void getPointProList(final Handler uiHandler, final ChannelInfo channel) {
		setDesChannelID(channel.getChannelID());// 设置标志
		this.uiHandler=uiHandler;

		dbManager = DBManager.getInstance(MyApp.getContext());
		if (dbManager.isNeedUpdateEPG(channel)) {
			mReQueue.cancelAll("program");
			String realurl = processData.getChannelProgramList(channel);
			Log.i("mmmm", "getPointProList-realurl:" + realurl);

			JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
					Request.Method.GET, realurl, null,
					new Response.Listener<org.json.JSONObject>() {

						@Override
						public void onResponse(org.json.JSONObject arg0) {
							// TODO Auto-generated method stub
							// 相应成功
							// Log.i("mmmm", "getPointProList:" + arg0);
							resolveProJsonThread = ResolveEPGInfoThread.getInstance();
							resolveProJsonThread.addData(mhandler, arg0);
							resolveProJsonThread.startRes();

						}
					}, null);
			jsonObjectRequest.setTag("program");// 设置tag,cancelAll的时候使用
			mReQueue.add(jsonObjectRequest);
		} else {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					curChannelProgramList = dbManager.queryEPGByChannelID(channel.getChannelID());
					if (curChannelProgramList != null&& !curChannelProgramList.isEmpty()) {
						dealPointProList(uiHandler,channel);
					}
				}
			}).start();
			
		}
	}

	private void dealPointProList(Handler uiHandler,ChannelInfo channel) {
		Map<String, List<ProgramInfo>> proMaps = new HashMap<String, List<ProgramInfo>>();
		LinkedList<String> dayMonth = new LinkedList<String>();
		for (ProgramInfo pro : curChannelProgramList) {
			Date dt = pro.getEventDate();
			String date = Utils.dateToString(dt);

			if (!dayMonth.contains(date)) {
				dayMonth.addFirst(date);
				proMaps.put(date, new ArrayList<ProgramInfo>());

			}
			if (Utils.isOutOfDate(pro)) {
				proMaps.get(date).add(pro);
			}
		}
		CacheData.setAllProgramMap(proMaps);
		CacheData.setDayMonths(dayMonth);
		Message msg=new Message();
		msg.what=Query_EPGData;
		msg.obj=channel.getChannelID();
		mhandler.sendMessage(msg);
	}

	private class RequstMSG {
		private ChannelInfo channel;
		private Object tag;

		public ChannelInfo getChannel() {
			return channel;
		}

		public void setChannel(ChannelInfo channel) {
			this.channel = channel;
		}

		public Object getTag() {
			return tag;
		}

		public void setTag(Object tag) {
			this.tag = tag;
		}

	}
}

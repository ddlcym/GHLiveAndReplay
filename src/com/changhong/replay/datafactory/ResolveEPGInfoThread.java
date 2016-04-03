package com.changhong.replay.datafactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONObject;

import android.os.Handler;

public class ResolveEPGInfoThread  {
	private Handler mhandler;
	private JSONObject json;
	public static final int MSG_SHOW_WEEKDAY = 0x201;
	private boolean interFlag = false;
	private static ResolveEPGInfoThread resolveEPGInfoThread;
	private ExecutorService singleThreadExecutor= Executors.newSingleThreadExecutor();;

	public ResolveEPGInfoThread() {
	
	}
	
	

	public static ResolveEPGInfoThread getInstance() {
		if (null == resolveEPGInfoThread) {
			resolveEPGInfoThread = new ResolveEPGInfoThread();
		}
		return resolveEPGInfoThread;
	}
	
	public void addData(Handler hand, JSONObject json){
		this.mhandler = hand;
		this.json = json;
	}

	public void startRes(){
		singleThreadExecutor.execute(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				HandleReplayData.getInstance().dealChannelJson(json);
				// SimpleAdapterWeekdata = GetWeekDate();
				mhandler.sendEmptyMessage(MSG_SHOW_WEEKDAY);
			}
		});
	}
	
	

}

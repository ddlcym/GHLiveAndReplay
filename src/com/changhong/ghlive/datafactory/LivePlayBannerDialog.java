package com.changhong.ghlive.datafactory;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.changhong.gehua.common.CacheData;
import com.changhong.gehua.common.ChannelInfo;
import com.changhong.gehua.common.Class_Constant;
import com.changhong.gehua.common.PlayVideo;
import com.changhong.gehua.common.ProgramInfo;
import com.changhong.gehua.common.Utils;
import com.changhong.ghliveandreplay.R;
import com.changhong.replay.datafactory.Player;

import android.app.Dialog;
import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class LivePlayBannerDialog extends Dialog {

	//直播下的dialog
	private Context mContext;
	private ChannelInfo channelInfo;
	private List<ProgramInfo> programListInfo;
	private TextView channel_name = null;// 频道名称
	private TextView channel_number = null;// 频道ID
	private TextView live_curTime = null;//当前时间
	private TextView currentProgramName = null;
	private TextView nextProgramName = null;
	private TextView curprotime = null;
	private TextView nextprotime = null;
	private SeekBar programPlayBar;
	private LinearLayout livePlayInfo;
	private Handler parentHandler;
	
	
	private int postion;
	private int timeLength;
	
	private int mtype;
	private int mvolumn;
	//private ImageView backImageView;
	private ImageView muteicon;
	
	
	private ImageView volumnback;
	private ImageView volumnicon;
	private RelativeLayout banner_cur,banner_next,banner_volumn;
	//private LinearLayout livebannerLayout = null;
	private AudioManager mAudioManager;
	private int curvolumn;
	private int[] volres = new int[]{R.drawable.a,R.drawable.b,R.drawable.c,R.drawable.d,R.drawable.e,R.drawable.f,R.drawable.g,
			R.drawable.h,R.drawable.i,R.drawable.j,R.drawable.k,R.drawable.l,R.drawable.m,R.drawable.n,R.drawable.o,R.drawable.p}; 
	
	//自动更新
	private Timer mTimer;
	private MyTimerTask mTimerTask;

	public LivePlayBannerDialog(Context context, ChannelInfo outterChannelInfo, List<ProgramInfo> outterListProgramInfo,
			Handler outterHandler, AudioManager audioManager, ImageView muteIconImage) {

		super(context, R.style.Translucent_NoTitle);
		setContentView(R.layout.livebannerdia);
		mContext = context;
		channelInfo = outterChannelInfo;
		programListInfo = outterListProgramInfo;
		parentHandler = outterHandler;
		mAudioManager = audioManager; 
		muteicon = muteIconImage;
		initView();
		
	}

	/*******************************************************
	 * 通过handler更新seekbar
	 ******************************************************/
//	TimerTask mTimerTask = new TimerTask() {
//		@Override
//		public void run() {
//			int position=getPosition();
//			if(position>=timeLength){
//				//通知更新直播banner条
////				PlayVideo.getInstance().getProgramInfo(mHandler, CacheData.getCurChannel());
//				PlayVideo.getInstance().getProgramInfo(mhanlder, channelInfo);
//				Log.i("mmmm", "LivePlayBannerDialog--mTimerTask"+"-position:"+position+"-timeLength:"+timeLength);
//			}else{
//					programPlayBar.setProgress(position);
//			}
//			Log.i("mmmm", "LivePlayBannerDialog--mTimerTask");
//			mhanlder.sendEmptyMessage(Class_Constant.LIVE_BANNER_CURTIME);
//		}
//	};
	private class MyTimerTask extends TimerTask{
		@Override
		public void run() {
			int position=getPosition();
			if(position>=timeLength){
				//通知更新直播banner条
//				PlayVideo.getInstance().getProgramInfo(mHandler, CacheData.getCurChannel());
				PlayVideo.getInstance().getProgramInfo(mhanlder, channelInfo);
				Log.i("mmmm", "LivePlayBannerDialog--mTimerTask-position>=timeLength");
			}else{
					programPlayBar.setProgress(position);
			}
//			Log.i("mmmm", "LivePlayBannerDialog--mTimerTask");
			mhanlder.sendEmptyMessage(Class_Constant.LIVE_BANNER_CURTIME);
		}
	};
	
	private Handler mhanlder=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			
			switch (msg.what) {
			case Class_Constant.TOAST_BANNER_PROGRAM_PASS:
				int volumn = mAudioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
				Log.i("live", "****");
				setData(channelInfo, CacheData.getCurPrograms(),volumn,0);
				break;
			case Class_Constant.LIVE_BANNER_CURTIME:
				showCurTime();
				break;
			}
		}
	};
	
	public void setData(ChannelInfo outterChannelInfo, List<ProgramInfo> outterListProgramInfo, int volumn, int type) {
		channelInfo = outterChannelInfo;
		programListInfo = outterListProgramInfo;
		mtype = type;
		mvolumn = volumn;
		initData();
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		super.show();
		initData();
		if(null==mTimer){
			mTimer= new Timer();
		}
		mTimerTask=new MyTimerTask();
		try{
			mTimer.schedule(mTimerTask, 0, 1000);
		}catch (IllegalStateException e){
			e.printStackTrace();
		}
	}

	public void initView() {
		Window window = this.getWindow();
		WindowManager.LayoutParams wlp = window.getAttributes();
		wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
		wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
		window.setAttributes(wlp);
		window.setGravity(Gravity.BOTTOM);
		channel_name = (TextView) findViewById(R.id.banner_channel_name_id);
		channel_number = (TextView) findViewById(R.id.banner_service_id);
		currentProgramName = (TextView) findViewById(R.id.current_program_info);
		nextProgramName = (TextView) findViewById(R.id.next_program_info);
		curprotime = (TextView) findViewById(R.id.livecurprotime);
		nextprotime = (TextView) findViewById(R.id.livenextprotime);
		live_curTime = (TextView) findViewById(R.id.live_curtime);
		programPlayBar = (SeekBar) findViewById(R.id.program_progress);
		livePlayInfo = (LinearLayout) findViewById(R.id.id_dtv_banner);
		
		
		banner_volumn = (RelativeLayout)findViewById(R.id.banner_volumn);
		banner_cur = (RelativeLayout)findViewById(R.id.banner_curproinfo);
		banner_next = (RelativeLayout)findViewById(R.id.banner_nextproinfo);
		volumnback = (ImageView)findViewById(R.id.volumn_background);
		volumnicon = (ImageView)findViewById(R.id.bannervolumn_icon);
		
		//backImageView = (ImageView)findViewById(R.id.back);
		//livebannerLayout = (LinearLayout)findViewById(R.id.banner_live);
	}

	public void initData() {
		String currentProgramBginTime = null;
		String currentProgramEndTime = null;
		String nextProgramBeginTime = null;
		String nextProgramEndTime = null;
		if (programListInfo != null && programListInfo.size() == 3) {
			currentProgramBginTime = Utils.hourAndMinute(programListInfo.get(1).getBeginTime());
			currentProgramEndTime = Utils.hourAndMinute(programListInfo.get(1).getEndTime());
			nextProgramBeginTime = Utils.hourAndMinute(programListInfo.get(2).getBeginTime());
			nextProgramEndTime = Utils.hourAndMinute(programListInfo.get(2).getEndTime());
			timeLength=(int)(programListInfo.get(1).getEndTime().getTime()-programListInfo.get(1).getBeginTime().getTime());
		}
		channel_name.setText(channelInfo.getChannelName());
		channel_number.setText(channelInfo.getChannelNumber());
		programPlayBar.setMax(timeLength);
		// currentProgramName.setText(programListInfo.get(1).getEventName());
		// nextProgramName.setText(programListInfo.get(2).getEventName());
		if (programListInfo != null && programListInfo.size() == 3) {
			/*currentProgramName.setText("当前节目       " + currentProgramBginTime + "-" + currentProgramEndTime + "       "
					+ programListInfo.get(1).getEventName());*/
			/*nextProgramName.setText("下一节目       " + nextProgramBeginTime + "-" + nextProgramEndTime + "       "
					+ programListInfo.get(2).getEventName());*/
			currentProgramName.setText(programListInfo.get(1).getEventName());
			curprotime.setText(currentProgramBginTime + "-" + currentProgramEndTime);
			nextProgramName.setText(programListInfo.get(2).getEventName());
			nextprotime.setText(nextProgramBeginTime + "-" + nextProgramEndTime);
		} /*else {
			currentProgramName.setText("正在播放：");
			nextProgramName.setText("即将播放：");
		}*/
		
		if(mvolumn == 0){
			volumnicon.setBackgroundResource(R.drawable.ismuteicon);
			
		}else {
			volumnicon.setBackgroundResource(R.drawable.notmuteicon);
		}
		volumnback.setBackgroundResource(volres[mvolumn]);
		
		programPlayBar.setFocusable(false);
		Log.i("live", "mtype ="+mtype);
		switch (mtype) {
		case 0:
			banner_cur.setVisibility(View.VISIBLE);
			banner_next.setVisibility(View.VISIBLE);
			banner_volumn.setVisibility(View.GONE);
			break;
		case 1:
			banner_cur.setVisibility(View.GONE);
			banner_next.setVisibility(View.GONE);
			banner_volumn.setVisibility(View.VISIBLE);
			break;
			
		case 2:
			banner_cur.setVisibility(View.GONE);
			banner_next.setVisibility(View.GONE);
			banner_volumn.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
	}

	public boolean isToastShow() {
		return isShowing();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == Class_Constant.KEYCODE_VOICE_UP) {
			mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 0);
			curvolumn = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			volumnback.setBackgroundResource(volres[curvolumn]);
			
			muteicon.setVisibility(View.GONE);
			
			if (curvolumn == 0) {
				volumnicon.setBackgroundResource(R.drawable.ismuteicon);
				muteicon.setVisibility(View.VISIBLE);
			}else {
				volumnicon.setBackgroundResource(R.drawable.notmuteicon);
			}
			banner_cur.setVisibility(View.GONE);
			banner_next.setVisibility(View.GONE);
			banner_volumn.setVisibility(View.VISIBLE);
			parentHandler.sendEmptyMessage(Class_Constant.VOLUMN_KEY_END);
			
			return true;
		}
		if (keyCode == Class_Constant.KEYCODE_VOICE_DOWN) {
			mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0);
			curvolumn = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			volumnback.setBackgroundResource(volres[curvolumn]);
			
			muteicon.setVisibility(View.GONE);
			
			if (curvolumn == 0) {
				volumnicon.setBackgroundResource(R.drawable.ismuteicon);
				muteicon.setVisibility(View.VISIBLE);
			}else {
				volumnicon.setBackgroundResource(R.drawable.notmuteicon);
			}
			banner_cur.setVisibility(View.GONE);
			banner_next.setVisibility(View.GONE);
			banner_volumn.setVisibility(View.VISIBLE);
			parentHandler.sendEmptyMessage(Class_Constant.VOLUMN_KEY_END);
			
			return true;
		}
		
		if (keyCode == Class_Constant.KEYCODE_MUTE) {
			int sysvolumn = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			
			if (sysvolumn == 0) {
				mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
				sysvolumn = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
				Log.i("test", "setStreamMute false");
				muteicon.setVisibility(View.GONE);
				volumnback.setBackgroundResource(volres[sysvolumn]);
				volumnicon.setBackgroundResource(R.drawable.notmuteicon);
				
				banner_cur.setVisibility(View.GONE);
				banner_next.setVisibility(View.GONE);
				banner_volumn.setVisibility(View.VISIBLE);
			}else {
				mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
				sysvolumn = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
				Log.i("test", "setStreamMute true");
				muteicon.setVisibility(View.VISIBLE);
				volumnback.setBackgroundResource(volres[sysvolumn]);
				volumnicon.setBackgroundResource(R.drawable.ismuteicon);
				
				banner_cur.setVisibility(View.GONE);
				banner_next.setVisibility(View.GONE);
				banner_volumn.setVisibility(View.VISIBLE);
			}
			parentHandler.sendEmptyMessage(Class_Constant.VOLUMN_KEY_END);
			
			return true;
		}
		
		if(keyCode != Class_Constant.KEYCODE_BACK_KEY){
			Message msg = new Message();
			msg.what = Class_Constant.DIALOG_ONKEY_DOWN;
			msg.arg1 = keyCode;
			parentHandler.sendMessage(msg);
			return false;
		}
		
		
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		msg.what = Class_Constant.DIALOG_ONKEY_UP;
		msg.arg1 = keyCode;
		parentHandler.sendMessage(msg);
		return super.onKeyUp(keyCode, event);
	}

	private int getPosition(){
		int position=0;
		Date date=new Date();
		position=(int)(date.getTime()-programListInfo.get(1).getBeginTime().getTime());
		return position;
	}
	
	private void showCurTime(){
		
		Date date=new Date();
		live_curTime.setText(Utils.hourAndMinute(date));
	}

	/*public void setMuteicon(ImageView muteicon) {
		this.muteicon = muteicon;
	}*/
	/*Runnable volumnrunnable = new Runnable() {
		public void run() {
			parentHandler.sendEmptyMessage(Class_Constant.VOLUMN_KEY_END);
		}
	};
*/
	
	public void stopTimer(){
		if(mTimerTask!=null){
			mTimerTask.cancel();
		}
		
		if(mTimer!=null){
			mTimer.cancel();
			mTimer=null;
		}
	}
	

}

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
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class LivePlayBannerDialog extends Dialog {

	//直播下的dialog
	private Context mContext;
	private ChannelInfo channelInfo;
	private List<ProgramInfo> programListInfo;
	private TextView channel_name = null;// 频道名称
	private TextView channel_number = null;// 频道ID
	private TextView currentProgramName = null;
	private TextView nextProgramName = null;
	private SeekBar programPlayBar;
	private LinearLayout livePlayInfo;
	private Handler parentHandler;
	
	
	private int postion;
	private int timeLength;
	
	//自动更新
	private Timer mTimer = new Timer();

	public LivePlayBannerDialog(Context context, ChannelInfo outterChannelInfo, List<ProgramInfo> outterListProgramInfo,
			Handler outterHandler) {

		super(context, R.style.Translucent_NoTitle);
		setContentView(R.layout.livebannerdia);
		mContext = context;
		channelInfo = outterChannelInfo;
		programListInfo = outterListProgramInfo;
		parentHandler = outterHandler;
		initView();
		try{
			mTimer.schedule(mTimerTask, 0, 1000);
		}catch (IllegalStateException e){
			e.printStackTrace();
		}
	}

	/*******************************************************
	 * 通过handler更新seekbar
	 ******************************************************/
	TimerTask mTimerTask = new TimerTask() {
		@Override
		public void run() {
			int position=getPosition();
			if(position>=timeLength){
				//通知更新直播banner条
//				PlayVideo.getInstance().getProgramInfo(mHandler, CacheData.getCurChannel());
				PlayVideo.getInstance().getProgramInfo(mhanlder, channelInfo);
			}else{
					programPlayBar.setProgress(position);
			}
		}
	};
	
	private Handler mhanlder=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			
			switch (msg.what) {
			case Class_Constant.TOAST_BANNER_PROGRAM_PASS:
				setData(channelInfo, CacheData.getCurPrograms());
				break;
			}
		}
	};
	
	public void setData(ChannelInfo outterChannelInfo, List<ProgramInfo> outterListProgramInfo) {
		channelInfo = outterChannelInfo;
		programListInfo = outterListProgramInfo;
		initData();
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		super.show();
		initData();
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
		programPlayBar = (SeekBar) findViewById(R.id.program_progress);
		livePlayInfo = (LinearLayout) findViewById(R.id.id_dtv_banner);
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
			currentProgramName.setText("当前节目       " + currentProgramBginTime + "-" + currentProgramEndTime + "       "
					+ programListInfo.get(1).getEventName());
			nextProgramName.setText("下一节目       " + nextProgramBeginTime + "-" + nextProgramEndTime + "       "
					+ programListInfo.get(2).getEventName());
		} else {
			currentProgramName.setText("正在播放：");
			nextProgramName.setText("即将播放：");
		}
		programPlayBar.setFocusable(false);
	}

	public boolean isToastShow() {
		return isShowing();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
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
}

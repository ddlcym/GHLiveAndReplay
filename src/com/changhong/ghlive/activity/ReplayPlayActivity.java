package com.changhong.ghlive.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.gehua.common.CacheData;
import com.changhong.gehua.common.ChannelInfo;
import com.changhong.gehua.common.Class_Constant;
import com.changhong.gehua.common.CommonMethod;
import com.changhong.gehua.common.PlayVideo;
import com.changhong.gehua.common.ProcessData;
import com.changhong.gehua.common.ProgramInfo;
import com.changhong.gehua.common.Utils;
import com.changhong.gehua.widget.MySeekbar;
import com.changhong.gehua.widget.ReplayEndDialog;
import com.changhong.ghlive.service.HttpService;
import com.changhong.ghliveandreplay.R;
import com.changhong.replay.datafactory.Player;

public class ReplayPlayActivity extends BaseActivity {
	private SurfaceView surfaceView;

	private MySeekbar seekbar;
	//private SeekBar skbProgress;
	//private NumberSeekBar numberSeekBar;
	private TextView CurPro,curProtime,videoCurPro , NextPro,NextProtime,videoNextPro , videoTimeLength;//videoCurrentTime;
	private RelativeLayout curlinearLayout,nextLinearLayout;

	//private SeekBar skbProgress;
	//private TextView CurPro,curProtime,videoCurPro , NextPro,NextProtime,videoNextPro , videoTimeLength, videoCurrentTime;
	//private LinearLayout curlinearLayout,nextLinearLayout;

	private Player player;
	private String replayChannelId = null;
	private ReplayEndDialog replayEndDialog;
	private ReplayEndDialog replayEndlastDialog,firstreplayDialog,replayfirstDialog,backreplaydialog;
	private ImageView pfbackImageView , palyButton, pauseButton, forwardIcon, backwardIcon;
	//private ImageView muteIconImage;

	private int maxTimes = 0;
	ProcessData mProcessData;
	ChannelInfo channel;
	ProgramInfo mprogram;
	String replayurl = "";
	private List<ProgramInfo> curProgramList = new ArrayList<ProgramInfo>();
	//private boolean whetherMute;
	private HttpService mHttpService;
	private String cotentString = null;
	//add 
	AudioManager mAudioManager;
	int curvolumn;
	

	private Handler replayHandler = new Handler() {
		
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case Class_Constant.REPLAY_CHANNEL_DETAIL:
				break;

			case Class_Constant.PLAY_URL:
				replayurl = (String) msg.obj;
				playNetVideo();
				break;

			case Class_Constant.RE_NEXT_PROGRAM:
				String dialogcurDay = CacheData.getReplayCurDay();
				List<ProgramInfo> dialogcurProgramList = (List<ProgramInfo>) CacheData.getAllProgramMap().get(dialogcurDay);
				int curindex = dialogcurProgramList.indexOf(mprogram);
				Log.i("xb", String.valueOf(curindex));
				Log.i("xb", mprogram.getEventName());
				if (curindex == (dialogcurProgramList.size() - 1)) {
					//最后一个节目
					replayEndlastDialog = new ReplayEndDialog(ReplayPlayActivity.this,replayHandler,1,"<"+mprogram.getEventName()+">"+"播放结束");
					replayEndlastDialog.show();
				}else{
					ProgramInfo dialognextprogram = dialogcurProgramList.get(curindex + 1);
					replayEndDialog = new ReplayEndDialog(ReplayPlayActivity.this,replayHandler,0,"<"+dialognextprogram.getEventName()+">"+"即将开始");
					replayEndDialog.show();
				}

				break;
			case Class_Constant.RE_LAST_PROGRAM:
				Log.i("xb", "replay*********");
				String lastdialogcurDay = CacheData.getReplayCurDay();
				List<ProgramInfo> lastdialogcurProgramList = (List<ProgramInfo>) CacheData.getAllProgramMap().get(lastdialogcurDay);
				int lastcurindex = lastdialogcurProgramList.indexOf(mprogram);
				Log.i("xb", "last"+String.valueOf(lastcurindex));
				Log.i("xb", "last"+mprogram.getEventName());
				if (0 == lastcurindex) {
					//di一个节目
					replayfirstDialog = new ReplayEndDialog(ReplayPlayActivity.this,replayHandler,3,"<"+mprogram.getEventName()+">"+"即将开始");
					replayfirstDialog.show();
				}else{
					ProgramInfo lastdialognextprogram = lastdialogcurProgramList.get(lastcurindex - 1);
					firstreplayDialog = new ReplayEndDialog(ReplayPlayActivity.this,replayHandler,2,"<"+lastdialognextprogram.getEventName()+">"+"即将开始");
					firstreplayDialog.show();
				}
				// playLastProgram();
				Log.i("mmmm", "ReplayPlayActivity-RE_NEXT_PROGRAM:" + mprogram.getProgramId());
				break;
			case Class_Constant.REPLAY_DIALOG_END_CANCEL:

				int curtype = msg.getData().getInt("dialogcanceltype",-1);
				Log.i("xb", "CANCEL"+String.valueOf(curtype));
				switch (curtype) {
				case 0:
					replayEndDialog.dismiss();
					
					playVideo(channel, mprogram);
					break;
				case 1:
					replayEndlastDialog.dismiss();
					playVideo(channel, mprogram);
					break;
				case 2:
					firstreplayDialog.dismiss();
					playVideo(channel, mprogram);
					break;
				case 3:
					replayfirstDialog.dismiss();
					playVideo(channel, mprogram);
					break;
				case 4:
					backreplaydialog.dismiss();
					//playVideo(channel, mprogram);
					break;
				default:
					
					break;
				}
				
				
			case Class_Constant.REPLAY_DIALOG_END_OK:
				int type = msg.getData().getInt("dialogoktype",-1);
				Log.i("xb", "OK"+String.valueOf(type));
				switch (type) {
				case 0:
					replayEndDialog.dismiss();
					playNextProgram();
					break;
				case 1:
					replayEndlastDialog.dismiss();
					finish();
					break;
					
				case 2:
					firstreplayDialog.dismiss();
					playLastProgram();
					break;
					
				case 3:
					replayfirstDialog.dismiss();
					finish();
				case 4:
					backreplaydialog.dismiss();
					finish();
					break;
				default:
					break;
				}
				

				// player.playUrl(replayurl);
				
				//break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		// whetherMute = false;
		if (mHttpService == null) {
			mHttpService = new HttpService(getApplicationContext());
		}
		//whetherMute = Boolean.valueOf(CommonMethod.getMuteState(MyApp.getContext()));
		mAudioManager = (AudioManager) getApplicationContext().getSystemService(AUDIO_SERVICE);
		

	}

	@Override
	protected void initView() {
		setContentView(R.layout.replay_play);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		//skbProgress = (SeekBar) this.findViewById(R.id.skbProgress);
		seekbar = (MySeekbar) this.findViewById(R.id.mySeekBar);
		//int screenWidth = seekbar.getLayoutParams().width;
		//Log.i("xb", " MySeekbar screenWidth = "+screenWidth);
		//seekbar.getSeekBar().setOnSeekBarChangeListener(new mySeekChangeLis());
		surfaceView = (SurfaceView) this.findViewById(R.id.surfaceView1);
		//add
		CurPro = (TextView) this.findViewById(R.id.curpro);
		curProtime = (TextView) this.findViewById(R.id.curprotime);
		videoCurPro = (TextView) this.findViewById(R.id.replay_current_program_info);
		NextPro = (TextView) this.findViewById(R.id.nextpro);
		NextProtime = (TextView) this.findViewById(R.id.nextprotime);
		videoNextPro = (TextView) this.findViewById(R.id.replay_next_program_info);
		curlinearLayout = (RelativeLayout) this.findViewById(R.id.up_dialog_cur);
		nextLinearLayout = (RelativeLayout) this.findViewById(R.id.down_dialog_next);
		pfbackImageView = (ImageView) findViewById(R.id.PF_back);
		
		
		videoTimeLength = (TextView) this.findViewById(R.id.video_timelength);
		//videoCurrentTime = (TextView) this.findViewById(R.id.video_currenttime);
		//skbProgress.setClickable(false);
		//skbProgress.setFocusable(false);
		seekbar.getSeekBar().setClickable(false);
		seekbar.getSeekBar().setFocusable(false);
		
		palyButton = (ImageView) findViewById(R.id.play_btn);
		pauseButton = (ImageView) findViewById(R.id.pause_btn);

		//muteIconImage = (ImageView) findViewById(R.id.mute_icon);
		//timeShiftIcon = (ImageView) findViewById(R.id.time_shift_icon);
		forwardIcon = (ImageView) findViewById(R.id.fast_forward);
		backwardIcon = (ImageView) findViewById(R.id.fast_backward);
		//android.view.ViewGroup.LayoutParams ps = timeShiftIcon.getLayoutParams();
		//ps.height = 90;
		//ps.width = 90;
		//timeShiftIcon.setLayoutParams(ps);
		// timeShiftIcon.setVisibility(View.VISIBLE);
		/*if (whetherMute) {
			muteIconImage.setVisibility(View.VISIBLE);
		} else {
			muteIconImage.setVisibility(View.GONE);
		}*/
		
		
	}

	@Override
	protected void initData() {
		mProcessData = new ProcessData();
		//player = new Player(replayHandler, surfaceView, skbProgress, videoCurrentTime);
		player = new Player(replayHandler, surfaceView, seekbar.getSeekBar(), seekbar.getCurText());
		//replayEndDialog = new ReplayEndDialog(this,replayHandler,0,"TTTT");
		
		
		if (progressBarRunnable != null) {
			replayHandler.removeCallbacks(progressBarRunnable);
		}
		replayHandler.postDelayed(progressBarRunnable, 5000);
		
	}

	
	/* play net video */
	private void playNetVideo() {
		if (isNetConnected()) {
			newThreadPlay();
		} else {
			Toast.makeText(this, "请检查网络", Toast.LENGTH_SHORT).show();
		}
	}

	/* thread to play video */
	private void newThreadPlay() {
		//skbProgress.setProgress(0);
		seekbar.getSeekBar().setProgress(0);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				player.playUrl(replayurl);

			}
		}).start();
		// Log.i("mmmm", "ReplayPlayActivity-replayurl"+replayurl);
	}

	/* Date transfer to unix time */
	public long dateToUnixTime(String outterString) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		// String time = "2016-03-27 15:00";
		java.util.Date date = null;
		try {
			date = format.parse(outterString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date.getTime();
	}

	/* whether net is connected */
	private boolean isNetConnected() {
		ConnectivityManager ccM = (ConnectivityManager) getApplicationContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (null == ccM) {
			return false;
		} else {
			NetworkInfo[] nwInfo = ccM.getAllNetworkInfo();
			if (null != nwInfo) {
				for (int i = 0; i < nwInfo.length; i++) {
					if (nwInfo[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		channel = CacheData.getCurChannel();
		mprogram = CacheData.getCurProgram();
		playVideo(channel, mprogram);
		
		curvolumn =  mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		Log.i("test", "enter replay curvolumn is"+curvolumn);
		//进到回看，先判断是否是静音，显示图标
		/*if (curvolumn == 0) {
			muteIconImage.setVisibility(View.VISIBLE);
			Log.i("test", "muteIconImage muteIconImage");
		}*/
	}

	private void playVideo(ChannelInfo channel, ProgramInfo program) {
		ProgramInfo nextmprogram;
		
		pfbackImageView.setBackgroundResource(R.drawable.pf_back);
		replayChannelId = channel.getChannelID();
		maxTimes = (int) (mprogram.getEndTime().getTime() - mprogram.getBeginTime().getTime());
		// skbProgress.setMax(maxTimes);
		SimpleDateFormat formatter1 = new SimpleDateFormat("HH:mm");
		formatter1.setTimeZone(TimeZone.getTimeZone("GMT"));
		videoTimeLength.setText( formatter1.format(maxTimes));
		
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
		formatter.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
//		String text = getString(R.string.replay_cur_program)+formatter.format(mprogram.getBeginTime().getTime())+"-"+formatter.format(mprogram.getEndTime().getTime())
//						+getString(R.string.space)+ mprogram.getEventName();
		curProtime.setText(formatter.format(mprogram.getBeginTime().getTime())+"-"+formatter.format(mprogram.getEndTime().getTime()));
		videoCurPro.setText(mprogram.getEventName());
		
		String curDay = CacheData.getReplayCurDay();
		curProgramList = (List<ProgramInfo>) CacheData.getAllProgramMap().get(curDay);
		int index = curProgramList.indexOf(mprogram);
		if (index == (curProgramList.size() - 1)) {
			//Toast.makeText(ReplayPlayActivity.this, "最后一个节目", Toast.LENGTH_SHORT).show();
			NextProtime.setText("");
			videoNextPro.setText("");
		} else {
			nextmprogram = curProgramList.get(curProgramList.indexOf(mprogram) + 1);
//			String textex = getString(R.string.replay_next_program)+formatter.format(nextmprogram.getBeginTime().getTime())+"-"+formatter.format(nextmprogram.getEndTime().getTime())
//					+getString(R.string.space)+ nextmprogram.getEventName();
			NextProtime.setText(formatter.format(nextmprogram.getBeginTime().getTime())+"-"+formatter.format(nextmprogram.getEndTime().getTime()));
			videoNextPro.setText(nextmprogram.getEventName());
		}
		
		
		
		String requestURL = mProcessData.getReplayPlayUrlString(channel, mprogram, 0);
		// Log.i("mmmm", "ReplayPlayActivity-requestURL:" + requestURL);
		PlayVideo.getInstance().getProgramPlayURL(replayHandler, requestURL);
	}

	private void playNextProgram() {
		String curDay = "";
		int indexPro = 0;
		int curIndexDay = 0;
			player = new Player(replayHandler, surfaceView, seekbar.getSeekBar(), seekbar.getCurText());
		curDay = CacheData.getReplayCurDay();
		curProgramList = (List<ProgramInfo>) CacheData.getAllProgramMap().get(curDay);
		indexPro = curProgramList.indexOf(mprogram);
		if (indexPro == (curProgramList.size() - 1)) {
			// curIndexDay = CacheData.getDayMonths().indexOf(curDay);
			// if (curIndexDay == (CacheData.getDayMonths().size() - 1)) {
			//Toast.makeText(ReplayPlayActivity.this, "已经是最后一个节目", Toast.LENGTH_SHORT).show();
			return;
			// } else {
			// curDay = CacheData.getDayMonths().get(curIndexDay + 1);
			// CacheData.setReplayCurDay(curDay);
			// curProgramList = CacheData.getAllProgramMap().get(curDay);
			// mprogram = curProgramList.get(0);
			// }
		} else {
			mprogram = curProgramList.get(curProgramList.indexOf(mprogram) + 1);
		}
		playVideo(channel, mprogram);
	}
//播放上一个节目
	private void playLastProgram() {
		String curDay = "";
		int indexPro = 0;
		int curIndexDay = 0;
		ProgramInfo program = null;
			//player = new Player(replayHandler, surfaceView, skbProgress, videoCurrentTime);
			player = new Player(replayHandler, surfaceView, seekbar.getSeekBar(), seekbar.getCurText());
		curDay = CacheData.getReplayCurDay();
		curProgramList = (List<ProgramInfo>) CacheData.getAllProgramMap().get(curDay);
		for (int i = 0; i < curProgramList.size(); i++) {
			program = curProgramList.get(i);
			if (mprogram.getProgramId() == program.getProgramId()) {
				mprogram = program;
			}
		}

		indexPro = curProgramList.indexOf(mprogram);
		if (0 == indexPro) {
			curIndexDay = CacheData.getDayMonths().indexOf(curDay);
			if (0 == curIndexDay) {
				//Toast.makeText(ReplayPlayActivity.this, "已经是第一个节目", Toast.LENGTH_SHORT).show();
				return;
			} 
			/*else {
				curDay = CacheData.getDayMonths().get(curIndexDay - 1);
				CacheData.setReplayCurDay(curDay);
				curProgramList = CacheData.getAllProgramMap().get(curDay);
				mprogram = curProgramList.get(curProgramList.size() - 1);
			}*/
		} else {
			mprogram = curProgramList.get(curProgramList.indexOf(mprogram) - 1);
		}
		playVideo(channel, mprogram);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (keyCode) {
		case Class_Constant.KEYCODE_RIGHT_ARROW_KEY:
			Player.handleProgress.sendEmptyMessage(Class_Constant.RE_FAST_FORWARD_DOWN);
			//skbProgress.setVisibility(View.VISIBLE);
			seekbar.setVisibility(View.VISIBLE);
			palyButton.setVisibility(View.GONE);
			pauseButton.setVisibility(View.GONE);
			videoTimeLength.setVisibility(View.VISIBLE);
			//videoCurrentTime.setVisibility(View.VISIBLE);
			
//			videoCurPro.setVisibility(View.VISIBLE);
//			videoNextPro.setVisibility(View.VISIBLE);
			curlinearLayout.setVisibility(View.VISIBLE);
			nextLinearLayout.setVisibility(View.VISIBLE);
			pfbackImageView.setVisibility(View.VISIBLE);
			
			if (progressBarRunnable != null) {
				replayHandler.removeCallbacks(progressBarRunnable);
			}
			replayHandler.postDelayed(progressBarRunnable, 5000);
			backwardIcon.setVisibility(View.GONE);
			forwardIcon.setVisibility(View.VISIBLE);
			break;
		case Class_Constant.KEYCODE_LEFT_ARROW_KEY:
			Player.handleProgress.sendEmptyMessage(Class_Constant.RE_FAST_REVERSE_DOWN);
			//skbProgress.setVisibility(View.VISIBLE);
			seekbar.setVisibility(View.VISIBLE);
			palyButton.setVisibility(View.GONE);
			pauseButton.setVisibility(View.GONE);
			videoTimeLength.setVisibility(View.VISIBLE);
			//videoCurrentTime.setVisibility(View.VISIBLE);
//			
//			videoCurPro.setVisibility(View.VISIBLE);
//			videoNextPro.setVisibility(View.VISIBLE);
			curlinearLayout.setVisibility(View.VISIBLE);
			nextLinearLayout.setVisibility(View.VISIBLE);
			
			pfbackImageView.setVisibility(View.VISIBLE);
			
			if (progressBarRunnable != null) {
				replayHandler.removeCallbacks(progressBarRunnable);
			}
			replayHandler.postDelayed(progressBarRunnable, 5000);
			forwardIcon.setVisibility(View.GONE);
			backwardIcon.setVisibility(View.VISIBLE);
			break;
		case Class_Constant.KEYCODE_OK_KEY:
			forwardIcon.setVisibility(View.GONE);
			backwardIcon.setVisibility(View.GONE);
			if (player.isPlayerPlaying()) {
				player.pause();
				palyButton.setVisibility(View.GONE);
				pauseButton.setVisibility(View.VISIBLE);
				//skbProgress.setVisibility(View.VISIBLE);
				seekbar.setVisibility(View.VISIBLE);
				videoTimeLength.setVisibility(View.VISIBLE);
				//videoCurrentTime.setVisibility(View.VISIBLE);
				
//				videoCurPro.setVisibility(View.VISIBLE);
//				videoNextPro.setVisibility(View.VISIBLE);
				curlinearLayout.setVisibility(View.VISIBLE);
				nextLinearLayout.setVisibility(View.VISIBLE);
				pfbackImageView.setVisibility(View.VISIBLE);
				if (pfrunnable != null) {
					replayHandler.removeCallbacks(pfrunnable);
				}
				replayHandler.postDelayed(pfrunnable, 3000);
				
			} else {
				player.play();
				pauseButton.setVisibility(View.GONE);
				palyButton.setVisibility(View.VISIBLE);
				if (runnable != null) {
					replayHandler.removeCallbacks(runnable);
				}
				replayHandler.postDelayed(runnable, 5000);
			}

			break;
		case Class_Constant.KEYCODE_MUTE:
			Log.i("test", "KEYCODE_MUTE is coming");
			curvolumn =  mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			Log.i("test", "KEYCODE_MUTE later curvolumn is"+curvolumn);
			/*if (curvolumn == 0) {
				mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
				muteIconImage.setVisibility(View.GONE);
			}else {
				mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
				muteIconImage.setVisibility(View.VISIBLE);
			}
			return true;*/
			break;
		case Class_Constant.KEYCODE_VOICE_UP:
		case Class_Constant.KEYCODE_VOICE_DOWN:
			//if (muteIconImage.isShown()) {
			//	muteIconImage.setVisibility(View.GONE);
			//}
			// audioMgr.setStreamMute(AudioManager.STREAM_MUSIC, true);
			//whetherMute = false;
			//CommonMethod.saveMutesState((whetherMute + ""), MyApp.getContext());
			break;
		case Class_Constant.KEYCODE_MENU_KEY:
			// Log.i("zyt", "onkeydown menukey is pressed " + keyCode);
			CommonMethod.startSettingPage(MyApp.getContext());
			break;
		case Class_Constant.KEYCODE_BACK_KEY:
			
			Log.i("xb", "onkeydown back key is pressed " + keyCode);
			backreplaydialog = new ReplayEndDialog(this,replayHandler,4,"你确定退出电视回看");
			backreplaydialog.show();
			// finish();
			break;
		case Class_Constant.MENU_ID_DTV_ROOT:
			//skbProgress.setVisibility(View.VISIBLE);
			seekbar.setVisibility(View.VISIBLE);
			videoTimeLength.setVisibility(View.VISIBLE);
			//videoCurrentTime.setVisibility(View.VISIBLE);
			
//			videoCurPro.setVisibility(View.VISIBLE);
//			videoNextPro.setVisibility(View.VISIBLE);
			
			curlinearLayout.setVisibility(View.VISIBLE);
			nextLinearLayout.setVisibility(View.VISIBLE);
			pfbackImageView.setVisibility(View.VISIBLE);
			if (progressBarRunnable != null) {
				replayHandler.removeCallbacks(progressBarRunnable);
			}
			replayHandler.postDelayed(progressBarRunnable, 5000);
			break;
		// next process
		default:
			// Log.i("zyt", "default onkeydown back key is pressed " + keyCode);
			break;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		switch (keyCode) {
		case Class_Constant.KEYCODE_RIGHT_ARROW_KEY:
			forwardIcon.setVisibility(View.GONE);
			player.handleProgress.sendEmptyMessage(Class_Constant.RE_FAST_FORWARD_UP);
			break;
		case Class_Constant.KEYCODE_LEFT_ARROW_KEY:
			backwardIcon.setVisibility(View.GONE);
			player.handleProgress.sendEmptyMessage(Class_Constant.RE_FAST_REVERSE_UP);
			break;
		}

		return super.onKeyUp(keyCode, event);
	}

	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			palyButton.setVisibility(View.GONE);
			//skbProgress.setVisibility(View.GONE);
			seekbar.setVisibility(View.GONE);
			videoTimeLength.setVisibility(View.GONE);
		//	videoCurrentTime.setVisibility(View.GONE);
			
			/*videoCurPro.setVisibility(View.GONE);
			videoNextPro.setVisibility(View.GONE);*/
			curlinearLayout.setVisibility(View.GONE);
			nextLinearLayout.setVisibility(View.GONE);
			pfbackImageView.setVisibility(View.GONE);
			// pauseButton.setVisibility(View.GONE);
		}
	};

	
	Runnable pfrunnable = new Runnable() {
		@Override
		public void run() {
			//seekbar.getCurText().setVisibility(View.GONE);
		}
	};
	
	
	Runnable progressBarRunnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			// palyButton.setVisibility(View.GONE);
			// pauseButton.setVisibility(View.GONE);
			// timeShiftIcon.setVisibility(View.GONE);
			//skbProgress.setVisibility(View.GONE);
			seekbar.setVisibility(View.GONE);
			videoTimeLength.setVisibility(View.GONE);
			//videoCurrentTime.setVisibility(View.GONE);
			
//			videoCurPro.setVisibility(View.GONE);
//			videoNextPro.setVisibility(View.GONE);
			curlinearLayout.setVisibility(View.GONE);
			nextLinearLayout.setVisibility(View.GONE);
			pfbackImageView.setVisibility(View.GONE);
		}
	};

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		player.stop();
		//CommonMethod.saveMutesState((whetherMute + ""), MyApp.getContext());
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		player.stop();
	}

	// public long getMillSecondsOfTime(String outterTime) {
	// String hour = millSeconds.;
	// String minute = "";
	// long millSeconds = ((Integer.parseInt(hour) * 60 * 60) +
	// (Integer.parseInt(minute) * 60)) * 1000;
	// return millSeconds;
	// }

}

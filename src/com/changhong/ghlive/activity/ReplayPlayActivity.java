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
import com.changhong.gehua.widget.PlayButton;
import com.changhong.gehua.widget.ReplayEndDialog;
import com.changhong.ghlive.service.HttpService;
import com.changhong.ghliveandreplay.R;
import com.changhong.replay.datafactory.Player;

public class ReplayPlayActivity extends BaseActivity {
	private SurfaceView surfaceView;

	private MySeekbar seekbar;
	private TextView CurPro,curProtime,videoCurPro , NextPro,NextProtime,videoNextPro , videoTimeLength;//videoCurrentTime;
	private RelativeLayout play_button_con,curlinearLayout,nextLinearLayout;
	
	private Player player;
	private String replayChannelId = null;
	private ReplayEndDialog replayEndDialog;
	private ReplayEndDialog replayEndlastDialog,firstreplayDialog,replayfirstDialog,backreplaydialog;
	private ImageView pfbackImageView,replay_divide;
	private PlayButton playbtn; 
	//private LinearLayout replay_banner;

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
	private ImageView revolumnback;
	private int[] vols = new int[]{R.drawable.rea,R.drawable.reb,R.drawable.rec,R.drawable.red,R.drawable.ree,R.drawable.ref,R.drawable.reg,
			R.drawable.reh,R.drawable.rei,R.drawable.rej,R.drawable.rek,R.drawable.rel,R.drawable.rem,R.drawable.ren,R.drawable.reo,R.drawable.rep};

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
						break;
					case 4:
						backreplaydialog.dismiss();
						finish();
						
						break;
					default:
						break;
					}
					
	
					// player.playUrl(replayurl);
					
					//break;
				case Class_Constant.REPLAY_BANNER:
					if (progressBarRunnable != null) {
						replayHandler.removeCallbacks(progressBarRunnable);
					}
					replayHandler.postDelayed(progressBarRunnable, 5000);
					break;
					
				
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		
		if (mHttpService == null) {
			mHttpService = new HttpService(getApplicationContext());
		}
		mAudioManager = (AudioManager) getApplicationContext().getSystemService(AUDIO_SERVICE);
		

	}

	@Override
	protected void initView() {
		setContentView(R.layout.replay_play);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		seekbar = (MySeekbar) this.findViewById(R.id.mySeekBar);
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
		play_button_con = (RelativeLayout) this.findViewById(R.id.play_button_con);
		
		
		pfbackImageView = (ImageView) findViewById(R.id.PF_back);
		replay_divide= (ImageView) findViewById(R.id.replay_divide);
		
		playbtn = (PlayButton) findViewById(R.id.replay_play_btn);
		
		videoTimeLength = (TextView) this.findViewById(R.id.video_timelength);
		
		revolumnback = (ImageView) findViewById(R.id.replay_volumn_background);
		
		seekbar.getSeekBar().setClickable(false);
		seekbar.getSeekBar().setFocusable(false);
		
		
	}

	@Override
	protected void initData() {
		mProcessData = new ProcessData();
		player = new Player(replayHandler, surfaceView, seekbar.getSeekBar(), seekbar.getCurText());
		replayHandler.sendEmptyMessage(Class_Constant.REPLAY_BANNER);
		
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
		
//		curvolumn =  mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//		Log.i("volumn", "enter replay curvolumn is"+curvolumn);
//		if (curvolumn == 0) {
//			revolumnback.setBackgroundResource(vols[curvolumn]);
//		}
//		
	}

	private void playVideo(ChannelInfo channel, ProgramInfo program) {
		ProgramInfo nextmprogram;
		Log.i("debug", "set***playingFlag false");
		Player.setLiveFlag(false);
		Player.setPlayingFlag(false);
		
		playbtn.setMyBG(PlayButton.Pause);
		
		pfbackImageView.setBackgroundResource(R.drawable.pf_back);
		replayChannelId = channel.getChannelID();
		maxTimes = (int) (mprogram.getEndTime().getTime() - mprogram.getBeginTime().getTime());
		// skbProgress.setMax(maxTimes);
		SimpleDateFormat formatter1 = new SimpleDateFormat("HH:mm");
		formatter1.setTimeZone(TimeZone.getTimeZone("GMT"));
		videoTimeLength.setText( formatter1.format(maxTimes));
		
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
		formatter.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));

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
			NextProtime.setText(formatter.format(nextmprogram.getBeginTime().getTime())+"-"+formatter.format(nextmprogram.getEndTime().getTime()));
			videoNextPro.setText(nextmprogram.getEventName());
		}
		
		seekbar.setVisibility(View.VISIBLE);
		videoTimeLength.setVisibility(View.VISIBLE);
		curlinearLayout.setVisibility(View.VISIBLE);
		nextLinearLayout.setVisibility(View.VISIBLE);
		pfbackImageView.setVisibility(View.VISIBLE);
		play_button_con.setVisibility(View.VISIBLE);
		
		String requestURL = mProcessData.getReplayPlayUrlString(channel, mprogram, 0);
		
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
//		if (keyCode == Class_Constant.KEYCODE_VOICE_UP) {
//			mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 0);
//			curvolumn = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//			Log.i("volumn", "KEYCODE_VOICE_UP curvolumn is"+curvolumn);
//			revolumnback.setBackgroundResource(vols[curvolumn]);
//			revolumnback.setVisibility(View.VISIBLE);
//			if (curvolumn != 0) {
//				replayHandler.removeCallbacks(VolumnbackRunnable);
//				replayHandler.postDelayed(VolumnbackRunnable, 5000);
//			}else {
//				replayHandler.removeCallbacks(VolumnbackRunnable);
//			}
//			return true;
//		}
//		if (keyCode == Class_Constant.KEYCODE_VOICE_DOWN) {
//			mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0);
//			curvolumn = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//			Log.i("volumn", "KEYCODE_VOICE_DOWN curvolumn is"+curvolumn);
//			revolumnback.setBackgroundResource(vols[curvolumn]);
//			revolumnback.setVisibility(View.VISIBLE);
//			if (curvolumn != 0) {
//				replayHandler.removeCallbacks(VolumnbackRunnable);
//				replayHandler.postDelayed(VolumnbackRunnable, 5000);
//			}else {
//				replayHandler.removeCallbacks(VolumnbackRunnable);
//			}
//			return true;
//		}
//		
//		if (keyCode == Class_Constant.KEYCODE_MUTE){
//			curvolumn = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//			if (curvolumn == 0) {
//				mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
//				curvolumn = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//				Log.i("volumn", "huifu");
//				revolumnback.setBackgroundResource(vols[curvolumn]);
//				revolumnback.setVisibility(View.VISIBLE);
//				replayHandler.removeCallbacks(VolumnbackRunnable);
//				replayHandler.postDelayed(VolumnbackRunnable, 5000);
//			}else {
//				mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
//				curvolumn = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//				Log.i("volumn", "set mute");
//				revolumnback.setBackgroundResource(vols[curvolumn]);
//				revolumnback.setVisibility(View.VISIBLE);
//				if (VolumnbackRunnable != null) {
//					replayHandler.removeCallbacks(VolumnbackRunnable);
//				}
//			}
//			return true;
//		}
		
		switch (keyCode) {
		case Class_Constant.KEYCODE_RIGHT_ARROW_KEY:
			Log.i("debug", "isPlayingFlag"+Player.isPlayingFlag());
			if (!Player.isPlayingFlag()) {
				return true;
			}
			Player.handleProgress.sendEmptyMessage(Class_Constant.RE_FAST_FORWARD_DOWN);
			seekbar.setVisibility(View.VISIBLE);
			play_button_con.setVisibility(View.VISIBLE);
			playbtn.setMyBG(PlayButton.Forward);
			videoTimeLength.setVisibility(View.VISIBLE);

			curlinearLayout.setVisibility(View.VISIBLE);
			nextLinearLayout.setVisibility(View.VISIBLE);
			pfbackImageView.setVisibility(View.VISIBLE);
			replayHandler.sendEmptyMessage(Class_Constant.REPLAY_BANNER);
			
			break;
		case Class_Constant.KEYCODE_LEFT_ARROW_KEY:
			Player.handleProgress.sendEmptyMessage(Class_Constant.RE_FAST_REVERSE_DOWN);
			seekbar.setVisibility(View.VISIBLE);
			play_button_con.setVisibility(View.VISIBLE);
			playbtn.setMyBG(PlayButton.Backward);
			videoTimeLength.setVisibility(View.VISIBLE);
			curlinearLayout.setVisibility(View.VISIBLE);
			nextLinearLayout.setVisibility(View.VISIBLE);
			
			pfbackImageView.setVisibility(View.VISIBLE);
			
			replayHandler.sendEmptyMessage(Class_Constant.REPLAY_BANNER);
		
			break;
		case Class_Constant.KEYCODE_OK_KEY:
			if (player.isPlayerPlaying()) {
				player.pause();
				play_button_con.setVisibility(View.VISIBLE);
				playbtn.setMyBG(PlayButton.Play);
				seekbar.setVisibility(View.VISIBLE);
				videoTimeLength.setVisibility(View.VISIBLE);

				curlinearLayout.setVisibility(View.VISIBLE);
				nextLinearLayout.setVisibility(View.VISIBLE);
				pfbackImageView.setVisibility(View.VISIBLE);
				
				//处理banner还在的时候，按暂停，banner消失
				if (progressBarRunnable != null) {
					replayHandler.removeCallbacks(progressBarRunnable);
				}
				
			} else {
				player.play();
				playbtn.setMyBG(PlayButton.Pause);
				replayHandler.sendEmptyMessage(Class_Constant.REPLAY_BANNER);
				
			}

			break;
		/*case Class_Constant.KEYCODE_MUTE:
			Log.i("test", "KEYCODE_MUTE is coming");
			curvolumn =  mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			Log.i("test", "KEYCODE_MUTE later curvolumn is"+curvolumn);
			if (curvolumn == 0) {
				mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
				muteIconImage.setVisibility(View.GONE);
			}else {
				mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
				muteIconImage.setVisibility(View.VISIBLE);
			}
			return true;
			break;*/
		/*case Class_Constant.KEYCODE_VOICE_UP:
			mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 0);
			curvolumn = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			revolumnback.setBackgroundResource(vols[curvolumn]);
			replayHandler.sendEmptyMessage(Class_Constant.VOLUMN_KEY_END);
			return true;
			
		case Class_Constant.KEYCODE_VOICE_DOWN:
			mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0);
			curvolumn = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			revolumnback.setBackgroundResource(vols[curvolumn]);
			replayHandler.sendEmptyMessage(Class_Constant.VOLUMN_KEY_END);
			return true;*/
			
		case Class_Constant.KEYCODE_MENU_KEY:
			// Log.i("zyt", "onkeydown menukey is pressed " + keyCode);
			CommonMethod.startSettingPage(MyApp.getContext());
			break;
		case Class_Constant.KEYCODE_BACK_KEY:
			if (pfbackImageView.isShown()) {
				seekbar.setVisibility(View.INVISIBLE);
				videoTimeLength.setVisibility(View.INVISIBLE);
				curlinearLayout.setVisibility(View.INVISIBLE);
				nextLinearLayout.setVisibility(View.INVISIBLE);
				pfbackImageView.setVisibility(View.INVISIBLE);
				play_button_con.setVisibility(View.INVISIBLE);
				return false;
			}else {
				Log.i("xb", "onkeydown back key is pressed " + keyCode);
				backreplaydialog = new ReplayEndDialog(this,replayHandler,4,"你确定退出电视回看");
				backreplaydialog.show();
			}
			break;
		case Class_Constant.MENU_ID_DTV_ROOT://xinxijian
			seekbar.setVisibility(View.VISIBLE);
			videoTimeLength.setVisibility(View.VISIBLE);
			play_button_con.setVisibility(View.VISIBLE);
			curlinearLayout.setVisibility(View.VISIBLE);
			nextLinearLayout.setVisibility(View.VISIBLE);
			pfbackImageView.setVisibility(View.VISIBLE);
			if (progressBarRunnable != null) {
				replayHandler.removeCallbacks(progressBarRunnable);
			}
			replayHandler.postDelayed(progressBarRunnable, 5000);
			break;
		
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
			playbtn.setMyBG(PlayButton.Pause);
			player.handleProgress.sendEmptyMessage(Class_Constant.RE_FAST_FORWARD_UP);
			break;
		case Class_Constant.KEYCODE_LEFT_ARROW_KEY:
			playbtn.setMyBG(PlayButton.Pause);
			player.handleProgress.sendEmptyMessage(Class_Constant.RE_FAST_REVERSE_UP);
			break;
		}

		return super.onKeyUp(keyCode, event);
	}

	Runnable progressBarRunnable = new Runnable() {
		@Override
		public void run() {
			seekbar.setVisibility(View.INVISIBLE);
			videoTimeLength.setVisibility(View.INVISIBLE);
			curlinearLayout.setVisibility(View.INVISIBLE);
			nextLinearLayout.setVisibility(View.INVISIBLE);
			pfbackImageView.setVisibility(View.INVISIBLE);
			play_button_con.setVisibility(View.INVISIBLE);
			replay_divide.setVisibility(View.INVISIBLE);
		}
	};
	
	Runnable VolumnbackRunnable = new Runnable() {
		@Override
		public void run() {
			revolumnback.setVisibility(View.INVISIBLE);
		}
	};
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		player.stop();
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

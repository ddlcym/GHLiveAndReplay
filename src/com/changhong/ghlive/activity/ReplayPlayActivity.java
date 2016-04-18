package com.changhong.ghlive.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.List;
import java.util.TimeZone;

import com.changhong.gehua.common.CacheData;
import com.changhong.gehua.common.ChannelInfo;
import com.changhong.gehua.common.Class_Constant;
import com.changhong.gehua.common.PlayVideo;
import com.changhong.gehua.common.ProcessData;
import com.changhong.gehua.common.ProgramInfo;
import com.changhong.gehua.widget.ReplayEndDialog;
import com.changhong.ghlive.service.HttpService;
import com.changhong.ghliveandreplay.R;
import com.changhong.ghliveandreplay.R.id;
import com.changhong.replay.datafactory.Player;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class ReplayPlayActivity extends Activity {
	private SurfaceView surfaceView;
	private SeekBar skbProgress;
	private TextView videoTimeLength, videoCurrentTime;
	private Player player;
	private String replayChannelId = null;
	private ReplayEndDialog replayEndDialog;
	private ImageView palyButton, pauseButton, timeShiftIcon, forwardIcon, backwardIcon;
	private ImageView muteIconImage;

	private int maxTimes = 0;
	ProcessData mProcessData;
	ChannelInfo channel;
	ProgramInfo mprogram;
	String replayurl = "";
	private List<ProgramInfo> curProgramList = new ArrayList<ProgramInfo>();
	private boolean whetherMute;
	private HttpService mHttpService;

	private Handler replayHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case Class_Constant.REPLAY_CHANNEL_DETAIL:
				break;

			case Class_Constant.PLAY_URL:
				replayurl = (String) msg.obj;
				// Log.i("mmmm", "ReplayPlayActivity-replayurl:" + replayurl);
				// replayEndDialog = null;
				playNetVideo();
				break;

			case Class_Constant.RE_NEXT_PROGRAM:
				// playNextProgram();
				// replayEndDialog.
				replayEndDialog.show();
				Log.i("mmmm", "ReplayPlayActivity-RE_NEXT_PROGRAM:" + mprogram.getProgramId());

				break;
			case Class_Constant.RE_LAST_PROGRAM:
				playLastProgram();
				Log.i("mmmm", "ReplayPlayActivity-RE_NEXT_PROGRAM:" + mprogram.getProgramId());
				break;
			case Class_Constant.REPLAY_DIALOG_END_CANCEL:
				finish();
				break;
			case Class_Constant.REPLAY_DIALOG_END_OK:
				player.playUrl(replayurl);
				replayEndDialog.dismiss();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.replay_play);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		// whetherMute = false;
		if (mHttpService == null) {
			mHttpService = new HttpService(getApplicationContext());
		}
		whetherMute = Boolean.valueOf(mHttpService.getMuteState());

		initView();
		initData();
	}

	public void initView() {
		skbProgress = (SeekBar) this.findViewById(R.id.skbProgress);
		surfaceView = (SurfaceView) this.findViewById(R.id.surfaceView1);
		videoTimeLength = (TextView) this.findViewById(R.id.video_timelength);
		videoCurrentTime = (TextView) this.findViewById(R.id.video_currenttime);
		skbProgress.setClickable(false);
		skbProgress.setFocusable(false);
		palyButton = (ImageView) findViewById(R.id.play_btn);
		pauseButton = (ImageView) findViewById(R.id.pause_btn);

		muteIconImage = (ImageView) findViewById(R.id.mute_icon);
		timeShiftIcon = (ImageView) findViewById(R.id.time_shift_icon);
		forwardIcon = (ImageView) findViewById(R.id.fast_forward);
		backwardIcon = (ImageView) findViewById(R.id.fast_backward);
		android.view.ViewGroup.LayoutParams ps = timeShiftIcon.getLayoutParams();
		ps.height = 90;
		ps.width = 90;
		timeShiftIcon.setLayoutParams(ps);
		timeShiftIcon.setVisibility(View.VISIBLE);
		if (whetherMute) {
			muteIconImage.setVisibility(View.VISIBLE);
		} else {
			muteIconImage.setVisibility(View.GONE);
		}
	}

	public void initData() {
		mProcessData = new ProcessData();
		player = new Player(replayHandler, surfaceView, skbProgress, videoCurrentTime);
		replayEndDialog = new ReplayEndDialog(this, replayHandler);
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
		skbProgress.setProgress(0);
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
	}

	private void playVideo(ChannelInfo channel, ProgramInfo program) {
		replayChannelId = channel.getChannelID();
		maxTimes = (int) (mprogram.getEndTime().getTime() - mprogram.getBeginTime().getTime());
		// skbProgress.setMax(maxTimes);
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
		videoTimeLength.setText("/" + formatter.format(maxTimes));
		String requestURL = mProcessData.getReplayPlayUrlString(channel, mprogram, 0);
		// Log.i("mmmm", "ReplayPlayActivity-requestURL:" + requestURL);
		PlayVideo.getInstance().getProgramPlayURL(replayHandler, requestURL);
	}

	private void playNextProgram() {
		String curDay = "";
		int indexPro = 0;
		int curIndexDay = 0;
		if (null == player) {
			player = new Player(replayHandler, surfaceView, skbProgress, videoCurrentTime);
		}
		curDay = CacheData.getReplayCurDay();
		curProgramList = (List<ProgramInfo>) CacheData.getAllProgramMap().get(curDay);
		indexPro = curProgramList.indexOf(mprogram);
		if (indexPro == (curProgramList.size() - 1)) {
			curIndexDay = CacheData.getDayMonths().indexOf(curDay);
			if (curIndexDay == (CacheData.getDayMonths().size() - 1)) {
				Toast.makeText(ReplayPlayActivity.this, "已经是最后一个节目", Toast.LENGTH_SHORT).show();
				return;
			} else {
				curDay = CacheData.getDayMonths().get(curIndexDay + 1);
				CacheData.setReplayCurDay(curDay);
				curProgramList = CacheData.getAllProgramMap().get(curDay);
				mprogram = curProgramList.get(0);
			}
		} else {
			mprogram = curProgramList.get(curProgramList.indexOf(mprogram) + 1);
		}
		playVideo(channel, mprogram);
	}

	private void playLastProgram() {
		String curDay = "";
		int indexPro = 0;
		int curIndexDay = 0;
		ProgramInfo program = null;
		if (null == player) {
			player = new Player(replayHandler, surfaceView, skbProgress, videoCurrentTime);
		}
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
				Toast.makeText(ReplayPlayActivity.this, "已经是第一个节目", Toast.LENGTH_SHORT).show();
				return;
			} else {
				curDay = CacheData.getDayMonths().get(curIndexDay - 1);
				CacheData.setReplayCurDay(curDay);
				curProgramList = CacheData.getAllProgramMap().get(curDay);
				mprogram = curProgramList.get(curProgramList.size() - 1);
			}
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
			skbProgress.setVisibility(View.VISIBLE);
			palyButton.setVisibility(View.GONE);
			pauseButton.setVisibility(View.GONE);
			videoTimeLength.setVisibility(View.VISIBLE);
			videoCurrentTime.setVisibility(View.VISIBLE);
			if (progressBarRunnable != null) {
				replayHandler.removeCallbacks(progressBarRunnable);
			}
			replayHandler.postDelayed(progressBarRunnable, 5000);
			backwardIcon.setVisibility(View.GONE);
			forwardIcon.setVisibility(View.VISIBLE);
			replayHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					forwardIcon.setVisibility(View.GONE);
				}
			}, 5000);
			break;
		case Class_Constant.KEYCODE_LEFT_ARROW_KEY:
			Player.handleProgress.sendEmptyMessage(Class_Constant.RE_FAST_REVERSE_DOWN);
			skbProgress.setVisibility(View.VISIBLE);
			palyButton.setVisibility(View.GONE);
			pauseButton.setVisibility(View.GONE);
			videoTimeLength.setVisibility(View.VISIBLE);
			videoCurrentTime.setVisibility(View.VISIBLE);
			if (progressBarRunnable != null) {
				replayHandler.removeCallbacks(progressBarRunnable);
			}
			replayHandler.postDelayed(progressBarRunnable, 5000);
			forwardIcon.setVisibility(View.GONE);
			backwardIcon.setVisibility(View.VISIBLE);
			replayHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					backwardIcon.setVisibility(View.GONE);
				}
			}, 5000);
			break;
		case Class_Constant.KEYCODE_OK_KEY:
			forwardIcon.setVisibility(View.GONE);
			backwardIcon.setVisibility(View.GONE);
			if (player.isPlayerPlaying()) {
				player.pause();
				palyButton.setVisibility(View.GONE);
				pauseButton.setVisibility(View.VISIBLE);
			} else {
				player.play();
				pauseButton.setVisibility(View.GONE);
				palyButton.setVisibility(View.VISIBLE);
			}
			if (runnable != null) {
				replayHandler.removeCallbacks(runnable);
			}
			replayHandler.postDelayed(runnable, 5000);
			break;
		case Class_Constant.KEYCODE_MUTE:// mute
			// int current =
			// audioMgr.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
			whetherMute = !whetherMute;
			// Log.i("zyt", "keycode mute is " + whetherMute);
			mHttpService.saveMutesState(whetherMute + "");
			if (muteIconImage.isShown()) {
				muteIconImage.setVisibility(View.GONE);
			} else {
				muteIconImage.setVisibility(View.VISIBLE);
			}
			break;
		case Class_Constant.KEYCODE_VOICE_UP:
		case Class_Constant.KEYCODE_VOICE_DOWN:
			if (muteIconImage.isShown()) {
				muteIconImage.setVisibility(View.GONE);
			}
			// audioMgr.setStreamMute(AudioManager.STREAM_MUSIC, true);
			whetherMute = false;
			mHttpService.saveMutesState(whetherMute + "");
			break;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		switch (keyCode) {
		case Class_Constant.KEYCODE_RIGHT_ARROW_KEY:

			player.handleProgress.sendEmptyMessage(Class_Constant.RE_FAST_FORWARD_UP);
			break;
		case Class_Constant.KEYCODE_LEFT_ARROW_KEY:
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
			// pauseButton.setVisibility(View.GONE);
		}
	};

	Runnable progressBarRunnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			// palyButton.setVisibility(View.GONE);
			// pauseButton.setVisibility(View.GONE);
			// timeShiftIcon.setVisibility(View.GONE);
			skbProgress.setVisibility(View.GONE);
			videoTimeLength.setVisibility(View.GONE);
			videoCurrentTime.setVisibility(View.GONE);
		}
	};

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		player.stop();
		mHttpService.saveMutesState(whetherMute + "");
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

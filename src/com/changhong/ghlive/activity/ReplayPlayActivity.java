package com.changhong.ghlive.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.gehua.common.CacheData;
import com.changhong.gehua.common.ChannelInfo;
import com.changhong.gehua.common.Class_Constant;
import com.changhong.gehua.common.PlayVideo;
import com.changhong.gehua.common.ProcessData;
import com.changhong.gehua.common.ProgramInfo;
import com.changhong.ghliveandreplay.R;
import com.changhong.replay.datafactory.Player;

public class ReplayPlayActivity extends Activity {
	private SurfaceView surfaceView;
	private SeekBar skbProgress;
	private TextView videoTimeLength, videoCurrentTime;
	private Player player;
	private String replayChannelId = null;

	private int maxTimes = 0;
	ProcessData mProcessData;
	ChannelInfo channel;
	ProgramInfo mprogram;
	String replayurl = "";
	private List<ProgramInfo> curProgramList = new ArrayList<ProgramInfo>();

	private Handler replayHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case Class_Constant.REPLAY_CHANNEL_DETAIL:
				break;

			case Class_Constant.PLAY_URL:
				replayurl = (String) msg.obj;
				// Log.i("mmmm", "ReplayPlayActivity-replayurl:" + replayurl);
				playNetVideo();
				break;

			case Class_Constant.RE_NEXT_PROGRAM:
				playNextProgram();
				Log.i("mmmm",
						"ReplayPlayActivity-RE_NEXT_PROGRAM:"
								+ mprogram.getProgramId());

				break;
			case Class_Constant.RE_LAST_PROGRAM:
				playLastProgram();
				Log.i("mmmm",
						"ReplayPlayActivity-RE_NEXT_PROGRAM:"
								+ mprogram.getProgramId());
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
	}

	public void initData() {
		mProcessData = new ProcessData();
		player = new Player(replayHandler, surfaceView, skbProgress,
				videoCurrentTime);

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
		maxTimes = (int) (mprogram.getEndTime().getTime() - mprogram
				.getBeginTime().getTime());
		// skbProgress.setMax(maxTimes);
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
		videoTimeLength.setText("/" + formatter.format(maxTimes));
		String requestURL = mProcessData.getReplayPlayUrlString(channel,
				mprogram, 0);
		// Log.i("mmmm", "ReplayPlayActivity-requestURL:" + requestURL);
		PlayVideo.getInstance().getProgramPlayURL(replayHandler, requestURL);
	}

	private void playNextProgram() {
		String curDay = "";
		int indexPro = 0;
		int curIndexDay = 0;
		if (null == player) {
			player = new Player(replayHandler, surfaceView, skbProgress,
					videoCurrentTime);
		}
		curDay = CacheData.getReplayCurDay();
		curProgramList = (List<ProgramInfo>) CacheData.getAllProgramMap().get(
				curDay);
		indexPro = curProgramList.indexOf(mprogram);
		if (indexPro == (curProgramList.size() - 1)) {
			curIndexDay = CacheData.getDayMonths().indexOf(curDay);
			if (curIndexDay == (CacheData.getDayMonths().size() - 1)) {
				Toast.makeText(ReplayPlayActivity.this, "已经是最后一个节目",
						Toast.LENGTH_SHORT).show();
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
			player = new Player(replayHandler, surfaceView, skbProgress,
					videoCurrentTime);
		}
		curDay = CacheData.getReplayCurDay();
		curProgramList = (List<ProgramInfo>) CacheData.getAllProgramMap().get(
				curDay);
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
				Toast.makeText(ReplayPlayActivity.this, "已经是第一个节目",
						Toast.LENGTH_SHORT).show();
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
			
			Player.handleProgress
					.sendEmptyMessage(Class_Constant.RE_FAST_FORWARD_DOWN);
			break;
		case Class_Constant.KEYCODE_LEFT_ARROW_KEY:
			Player.handleProgress
					.sendEmptyMessage(Class_Constant.RE_FAST_REVERSE_DOWN);
			break;
		case Class_Constant.KEYCODE_OK_KEY:
			if(player.isPlayerPlaying()){
				player.pause();
			}else{
				player.play();
			}
			
			break;
		}

		return super.onKeyDown(keyCode, event);
	}
	
	
	

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		
		switch (keyCode) {
		case Class_Constant.KEYCODE_RIGHT_ARROW_KEY:
			
			Player.handleProgress
					.sendEmptyMessage(Class_Constant.RE_FAST_FORWARD_UP);
			break;
		case Class_Constant.KEYCODE_LEFT_ARROW_KEY:
			Player.handleProgress
					.sendEmptyMessage(Class_Constant.RE_FAST_REVERSE_UP);
			break;
		}
		
		return super.onKeyUp(keyCode, event);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		player.stop();
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

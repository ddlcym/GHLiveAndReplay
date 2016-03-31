package com.changhong.ghlive.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.widget.SeekBar;
import android.widget.Toast;

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

	private Player player;
	private String replayChannelId = null;

	ProcessData mProcessData;
	ChannelInfo channel;
	ProgramInfo mprogram;
	String replayurl = "";
	private Handler replayHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Class_Constant.REPLAY_CHANNEL_DETAIL:
				break;

			case Class_Constant.REPLAY_URL:
				replayurl = (String) msg.obj;
				Log.i("mmmm", "ReplayPlayActivity-replayurl:" + replayurl);
				playNetVideo();
				break;
			default:
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

	}

	public void initData() {
		mProcessData = new ProcessData();
		player = new Player(surfaceView, skbProgress);

	}

	/* play net video */
	private void playNetVideo() {
		if (isNetConnected()) {
			newThreadPlay();
		} else {
			Toast.makeText(this, "播放出错", Toast.LENGTH_SHORT).show();
		}
	}

	/* thread to play video */
	private void newThreadPlay() {
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				// loadingNetVideo();
				player.playUrl(replayurl);
				// Log.i("zyt", "last + last" + " 频道详情 replay url is pass " +
				// replayurl);
				super.run();
			}
		}.start();
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
		Bundle bundle = getIntent().getExtras();
		channel = (ChannelInfo) bundle.getSerializable("channel");
		mprogram = (ProgramInfo) bundle.getSerializable("program");
		replayChannelId = channel.getChannelID();

		String requestURL = mProcessData.getReplayPlayUrlString(channel,
				mprogram, 0);
		Log.i("mmmm", "ReplayPlayActivity-requestURL:" + requestURL);
		PlayVideo.getInstance().playReplayProgram(replayHandler, requestURL);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		Message msg=new Message();
		int curPos=0;
		msg.what=Class_Constant.REPLAY_SEEK_TO;
		switch (keyCode) {
		case Class_Constant.KEYCODE_RIGHT_ARROW_KEY:
			msg.obj=curPos;
			player.getHandler().sendMessage(msg);
			break;
		case Class_Constant.KEYCODE_LEFT_ARROW_KEY:

			break;
		}

		return super.onKeyDown(keyCode, event);
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

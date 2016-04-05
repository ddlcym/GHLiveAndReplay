package com.changhong.ghlive.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import android.R.integer;
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
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
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
	private TextView videoTimeLength, videoCurrentTime;
	private Player player;
	private String replayChannelId = null;

	private int maxTimes = 0;
	ProcessData mProcessData;
	ChannelInfo channel;
	ProgramInfo mprogram;
	String replayurl = "";
	private Handler replayHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			ProgramInfo program=null;
			switch (msg.what) {
			case Class_Constant.REPLAY_CHANNEL_DETAIL:
				break;

			case Class_Constant.PLAY_URL:
				replayurl = (String) msg.obj;
				Log.i("mmmm", "ReplayPlayActivity-replayurl:" + replayurl);
				playNetVideo();
				break;
				
			case Class_Constant.RE_NEXT_PROGRAM:
				
				Log.i("mmmm", "ReplayPlayActivity-RE_NEXT_PROGRAM:");
				
				
				break;
			case Class_Constant.RE_LAST_PROGRAM:
				Log.i("mmmm", "ReplayPlayActivity-RE_NEXT_PROGRAM:");
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
	}

	public void initData() {
		mProcessData = new ProcessData();
		player = new Player(replayHandler,surfaceView, skbProgress, videoCurrentTime);

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
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				// loadingNetVideo();
				player.playUrl(replayurl);
				// Log.i("mmmm", "ReplayPlayActivity-replayurl"+replayurl);
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
		playVideo(channel, mprogram);
	}
	
	private void playVideo(ChannelInfo channel,ProgramInfo program){
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (keyCode) {
		case Class_Constant.KEYCODE_RIGHT_ARROW_KEY:
			Player.handleProgress.sendEmptyMessage(Class_Constant.RE_FAST_FORWARD);
			break;
		case Class_Constant.KEYCODE_LEFT_ARROW_KEY:
			Player.handleProgress.sendEmptyMessage(Class_Constant.RE_FAST_REVERSE);
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

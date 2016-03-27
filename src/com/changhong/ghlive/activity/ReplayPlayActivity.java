package com.changhong.ghlive.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.changhong.gehua.common.ChannelInfo;
import com.changhong.gehua.common.Class_Constant;
import com.changhong.gehua.common.PlayVideo;
import com.changhong.gehua.common.ProcessData;
import com.changhong.ghliveandreplay.R;
import com.changhong.replay.datafactory.Player;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.VideoView;

public class ReplayPlayActivity extends Activity {
	private SurfaceView surfaceView;
	private Button btnPause, btnPlayUrl, btnList;
	private SeekBar skbProgress;

	private Player player;
	private VideoView vw;
	private int replayChannelId = 0;
	private String replayBeginTime = "";
	private String replayEndTime = "";
	// public MediaPlayer mediaPlayer;
	String uriNet = "http://ott.yun.gehua.net.cn:8080/msis/getPlayURL?version=V002&resourceCode=8406&providerID=gehua&assetID=8406&resolution=1280*768&playType=4&terminalType=4&shifttime=14:22&shiftend=15:17&authKey=c7e278212b81aff1992ac5e0017757d7";
	ProcessData mProcessData;
	ChannelInfo channelDetail;
	String replayurl = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.replay_play);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		surfaceView = (SurfaceView) this.findViewById(R.id.surfaceView1);

		initView();
		initData();

		Bundle bundle = getIntent().getExtras();
		bundle.getStringArray("pgmInfo");

		replayChannelId = Integer.parseInt(bundle.getStringArray("pgmInfo")[0]);
		replayBeginTime = bundle.getStringArray("pgmInfo")[1];
		replayEndTime = bundle.getStringArray("pgmInfo")[2];

		// replayBeginTime = replayBeginTime.substring(11, 16);
		// replayEndTime = replayEndTime.substring(11, 16);

		PlayVideo.getInstance().getChannelInfoDetail(replayHandler, replayChannelId);
		// replayurl = mProcessData.getReplayPlayUrlString(channelDetail,
		// dateToUnixTime(replayBeginTime),
		// dateToUnixTime(replayEndTime));
		// Log.i("zyt", "replay activity pass is + " +
		// bundle.getStringArray("pgmInfo")[0]);
		// Log.i("zyt", "replay activity pass is + " +
		// bundle.getStringArray("pgmInfo")[1]);
		// Log.i("zyt", "replay activity pass is + " +
		// bundle.getStringArray("pgmInfo")[2]);
		// Intent mIntent = getIntent();

	}

	public void initView() {
		btnPlayUrl = (Button) this.findViewById(R.id.btnNetMove);
		btnPause = (Button) this.findViewById(R.id.btnPause);
		btnList = (Button) this.findViewById(R.id.btnList);
		skbProgress = (SeekBar) this.findViewById(R.id.skbProgress);

		btnPlayUrl.setOnClickListener(new ClickEvent());
		btnPause.setOnClickListener(new ClickEvent());
		btnList.setOnClickListener(new ClickEvent());

		player = new Player(surfaceView, skbProgress);

	}

	public void initData() {
		mProcessData = new ProcessData();
		channelDetail = new ChannelInfo();
	}

	class ClickEvent implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnPause:
				if (player.isPlayerPlaying()) {
					player.pause();
					btnPause.setText("播放");
				} else {
					player.play();
					btnPause.setText("test");
				}
				break;
			// case R.id.btnList:
			// starMoveList();
			// break;
			case R.id.btnNetMove:
				// playNetVideo();
				break;
			}
		}
	}

	/* play net video */
	private void playNetVideo() {
		if (isNetConnected()) {
			// btnPause.setText("暂停");
			newThreadPlay();
			// loadingNetVideo();
		} else {
			Toast.makeText(this, "播放出错", Toast.LENGTH_SHORT).show();
		}
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

	/* thread to play video */
	private void newThreadPlay() {
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				// loadingNetVideo();
				player.playUrl(replayurl);
				Log.i("zyt", "last + last" + " 频道详情 replay url is pass " + replayurl);
				super.run();
			}
		}.start();
	}

	private Handler replayHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Class_Constant.REPLAY_CHANNEL_DETAIL:
				channelDetail = (ChannelInfo) msg.obj;
				Log.i("zyt", "last + last" + " 频道详情 id is " + channelDetail.getChannelID());

				Log.i("zyt", "last + last" + " 频道详情 replayBeginTime is " + replayBeginTime);
				Log.i("zyt", "last + last" + " 频道详情 replayEndTime is " + replayEndTime);
				Log.i("zyt", "last + last" + " 频道详情 replay url is " + replayurl);
				Log.i("zyt", "last + last" + " 频道详情 replay int begin time hour is " + (replayBeginTime));
				Log.i("zyt", "last + last" + " 频道详情 replay int begin time mintue is " + (replayBeginTime));

				Log.i("zyt", "last + last + last " + " 频道详情 replay int begin time mintue format is "
						+ dateToUnixTime(replayBeginTime));

				replayurl = mProcessData.getReplayPlayUrlString(channelDetail, dateToUnixTime(replayBeginTime),
						dateToUnixTime(replayEndTime));

				Log.i("zyt", "last + last + last " + " 频道详情 replay int begin time mintue format url is " + replayurl);

				// PlayVideo.getInstance().playReplayProgram(replayHandler,
				// replayurl);

				// replayurl =
				// mProcessData.getReplayPlayUrlString(channelDetail,
				// dateToUnixTime(replayBeginTime),
				// dateToUnixTime(replayEndTime));

				// String englishDate = "Sun Mar 27 14:22:00 GMT+08:00
				// 2016";

				// SimpleDateFormat sdfNew = new SimpleDateFormat("yyyy-MM-dd
				// HH:mm:ss");
				// Date beginDate = sdfNew.parse(string);
				// try {
				// Log.i("zyt", "last + last" + " 频道详情 transfor to date is " +
				// sdfNew.parse(replayBeginTime));
				// } catch (ParseException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
				// Log.i("zyt", "last + last" + " 频道详情 replay int end time hour
				// " + (replayEndTime));
				// Log.i("zyt", "last + last" + " 频道详情 replay int end time
				// minitue " + (replayEndTime));
				// playNetVideo();

				break;

			default:
				break;
			}
		}
	};

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
	// public long getMillSecondsOfTime(String outterTime) {
	// String hour = millSeconds.;
	// String minute = "";
	// long millSeconds = ((Integer.parseInt(hour) * 60 * 60) +
	// (Integer.parseInt(minute) * 60)) * 1000;
	// return millSeconds;
	// }
}

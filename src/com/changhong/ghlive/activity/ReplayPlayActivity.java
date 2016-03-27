package com.changhong.ghlive.activity;

import com.changhong.ghliveandreplay.R;
import com.changhong.replay.datafactory.Player;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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
	// public MediaPlayer mediaPlayer;
	String uriNet = "http://hls.yun.gehua.net.cn:8088/live/BTV3_1200.m3u8?a=1&provider_id=gehua&assetID=98784&ET=1458977445117&TOKEN=991b9299e887f690ca397f4c36e0bbd7";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.replay_play);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		surfaceView = (SurfaceView) this.findViewById(R.id.surfaceView1);

		initView();
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
				playNetVideo();
				break;
			}
		}
	}

	/* play net video */
	private void playNetVideo() {
		Log.i("zyt - process", " here 1");
		if (isNetConnected()) {
			Log.i("zyt - process", " here ");
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
				Log.i("zyt - process", " here  2 ");
				player.playUrl(uriNet);
				super.run();
			}
		}.start();
	}
}

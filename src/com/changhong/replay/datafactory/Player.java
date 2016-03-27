package com.changhong.replay.datafactory;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.changhong.ghlive.activity.MyApp;
import com.changhong.ghliveandreplay.R;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.SeekBar;
import android.widget.Toast;

public class Player implements OnBufferingUpdateListener, OnCompletionListener, MediaPlayer.OnPreparedListener,
		SurfaceHolder.Callback {
	private int videoWidth;
	private int videoHeight;
	public MediaPlayer mediaPlayer;
	private SurfaceHolder surfaceHolder;
	private SeekBar skbProgress;
	private SurfaceView surfaceView;
	private Timer mTimer = new Timer();
	private boolean brcastFlag = true;

	public Player(SurfaceView mySurfaceView, SeekBar skbProgress) {
		this.skbProgress = skbProgress;
		this.surfaceView = mySurfaceView;
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mTimer.schedule(mTimerTask, 0, 1000);
	}

	TimerTask mTimerTask = new TimerTask() {
		@Override
		public void run() {
			if (mediaPlayer == null)
				return;
			if (mediaPlayer.isPlaying() && skbProgress.isPressed() == false) {
				handleProgress.sendEmptyMessage(0);
			}
		}
	};

	Handler handleProgress = new Handler() {
		public void handleMessage(Message msg) {

			int position = mediaPlayer.getCurrentPosition();
			int duration = mediaPlayer.getDuration();

			if (duration > 0) {
				long pos = skbProgress.getMax() * position / duration;
				skbProgress.setProgress((int) pos);
			}
		};
	};

	// *****************************************************

	public void play() {
		mediaPlayer.start();
	}

	public void playUrl(String videoUrl) {
		Log.i("zyt - process", " here  3 - 0");
		if (null == mediaPlayer) {
			Log.i("zyt - process", " here  3 - 1");
			return;
		}

		brcastFlag = true;
		try {
			Log.i("zyt - process", " here  3 ");
			mediaPlayer.reset();
			mediaPlayer.setDataSource(videoUrl);
			mediaPlayer.prepare();// prepare֮���Զ�����
			// mediaPlayer.start();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void pause() {
		if (null != mediaPlayer) {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.pause();
			} else {
				mediaPlayer.start();
			}
		}
	}

	public boolean isPlayerPlaying() {
		boolean flag = false;
		if (null != mediaPlayer) {
			flag = mediaPlayer.isPlaying();
		}
		return flag;
	}

	public void stop() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		Log.e("mediaPlayer", "surface changed");
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		try {
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setDisplay(surfaceHolder);
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnBufferingUpdateListener(this);
			mediaPlayer.setOnPreparedListener(this);
		} catch (Exception e) {
			Log.e("mediaPlayer", "error", e);
		}
		Log.e("mediaPlayer", "surface created");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		Log.e("mediaPlayer", "surface destroyed");
	}

	@Override
	public void onPrepared(MediaPlayer arg0) {
		videoWidth = mediaPlayer.getVideoWidth();
		videoHeight = mediaPlayer.getVideoHeight();
		if (videoHeight != 0 && videoWidth != 0) {
			arg0.start();
		} else {
			Toast.makeText(MyApp.getContext(), "��Ƶ��������", Toast.LENGTH_SHORT).show();
			Log.i("mm", "videoWidth or videoHeight =0");
		}
		Log.e("mediaPlayer", "onPrepared");
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBufferingUpdate(MediaPlayer arg0, int bufferingProgress) {
		skbProgress.setSecondaryProgress(bufferingProgress);
		int currentProgress = skbProgress.getMax() * mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration();
		if (bufferingProgress != 0) {
			// sendBroadcast();
		}
		Log.i(currentProgress + "% play", bufferingProgress + "% buffer");

	}

	// private void sendBroadcast() {
	// if (brcastFlag) {
	// MyApp ma = MyApp.getContext();
	// Intent intent = new Intent();
	// intent.setAction(ma.getString(R.string.COM_EXAMPLE_M_BUFFERED));
	// ma.sendBroadcast(intent);
	// brcastFlag = false;
	// }
	// }
}

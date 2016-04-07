package com.changhong.replay.datafactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import com.changhong.gehua.common.Class_Constant;
import com.changhong.ghlive.activity.MyApp;

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
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class Player implements OnBufferingUpdateListener, OnCompletionListener, MediaPlayer.OnPreparedListener,
		SurfaceHolder.Callback {
	private int videoWidth;
	private int videoHeight;
	public static MediaPlayer mediaPlayer;
	private SurfaceHolder surfaceHolder;
	private static SeekBar skbProgress;
	private SurfaceView surfaceView;
	private Timer mTimer = new Timer();
	private static boolean playingFlag = false;
	private static  Handler parentHandler;
	public static boolean handlerFlag=true;

	private static TextView videoCurrentTime;

	public Player(Handler parentHandler,SurfaceView mySurfaceView, SeekBar skbProgress, TextView txvCurrent) {
		this.skbProgress = skbProgress;
		this.surfaceView = mySurfaceView;
		this.videoCurrentTime = txvCurrent;
		this.parentHandler=parentHandler;

		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		// 防止音频出不来
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mTimer.schedule(mTimerTask, 0, 1000);
		this.skbProgress.setOnSeekBarChangeListener(mySeekChangeLis);
	}

	/*******************************************************
	 * 通过handler更新seekbar
	 ******************************************************/
	TimerTask mTimerTask = new TimerTask() {
		@Override
		public void run() {
			if (mediaPlayer == null)
				return;
			if (mediaPlayer.isPlaying() && playingFlag&&videoCurrentTime!=null) {
				handleProgress.sendEmptyMessage(Class_Constant.RE_UPDATE_PROGRESS);
			}
		}
	};

	OnSeekBarChangeListener mySeekChangeLis = new OnSeekBarChangeListener() {
		int myprogress = 0;

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			// TODO Auto-generated method stub
			playingFlag = false;
			myprogress = progress * mediaPlayer.getDuration() / seekBar.getMax();
			mediaPlayer.seekTo(myprogress);
		}
	};

	public static Handler handleProgress = new Handler() {
		public void handleMessage(Message msg) {
			int curPos = 0;
			int fastPos = 0;
			int forPos=0;
			int duration = 0;
			switch (msg.what) {
			case Class_Constant.REPLAY_SEEK_TO:
				curPos = msg.arg1;
				duration = mediaPlayer.getDuration();

				if (duration > 0) {
					long pos = skbProgress.getMax() * curPos / duration;
					skbProgress.setProgress((int) pos);
				}
				break;
			case Class_Constant.RE_FAST_FORWARD:
				if(!playingFlag){
					return;
				}
				curPos = skbProgress.getProgress();
				forPos=curPos + 5;
				if(forPos<skbProgress.getMax()){
					skbProgress.setProgress(forPos);
					
				}else{
					if(handlerFlag){
					parentHandler.sendEmptyMessage(Class_Constant.RE_NEXT_PROGRAM);
					handlerFlag=false;
					}
				}
				
				break;
			case Class_Constant.RE_FAST_REVERSE:
				curPos = skbProgress.getProgress();
				fastPos = curPos - 5;
				
				if(!playingFlag){
					return;
				}
				if(fastPos<0){
					if(handlerFlag){
					parentHandler.sendEmptyMessage(Class_Constant.RE_LAST_PROGRAM);
					handlerFlag=false;
					}
				}else{
					skbProgress.setProgress(forPos);
				}
				
				skbProgress.setProgress(fastPos);
				break;
			case Class_Constant.RE_PLAY:

				break;
			case Class_Constant.RE_PAUSE:

				break;

			case Class_Constant.RE_UPDATE_PROGRESS:
				int position = mediaPlayer.getCurrentPosition();
				duration = mediaPlayer.getDuration();
				if (duration > 0) {
					long pos = skbProgress.getMax() * position / duration;
					skbProgress.setProgress((int) pos);
				}
				SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
				formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
				videoCurrentTime.setText(formatter.format(position));
				break;
			}

		};
	};

	// *****************************************************

	public void play() {
		mediaPlayer.start();
	}

	public void playUrl(String videoUrl) {
		handlerFlag=true;
		if (null == mediaPlayer)
			return;
		
		try {
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

	public static void stop() {
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
	/**
	 * ͨ��onPrepared����
	 */
	public void onPrepared(MediaPlayer arg0) {
		videoWidth = mediaPlayer.getVideoWidth();
		videoHeight = mediaPlayer.getVideoHeight();
		if (videoHeight != 0 && videoWidth != 0) {
			arg0.start();
		} else {
			Toast.makeText(MyApp.getContext(), "视频无效", Toast.LENGTH_SHORT).show();
			Log.i("mm", "videoWidth or videoHeight =0");
		}
		Log.e("mediaPlayer", "onPrepared");
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		// TODO Auto-generated method stub

	}

	// 播放视频准备好播放后调用此方法
	@Override
	public void onBufferingUpdate(MediaPlayer arg0, int bufferingProgress) {
		skbProgress.setSecondaryProgress(bufferingProgress);
		playingFlag = true;
		int currentProgress = skbProgress.getMax() * mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration();
		if (bufferingProgress != 0) {
		}
//		Log.i("mmmm", bufferingProgress + "% buffer");

	}

	/* 播放过程中时间进行更新显示 */
	// public void refreshVideoTime(TextView txvLen, TextView txvCur) {
	// txvLen.setText("");
	// txvCur.setText("");
	// }
}

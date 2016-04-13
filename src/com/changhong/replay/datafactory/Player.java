package com.changhong.replay.datafactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import com.changhong.gehua.common.Class_Constant;
import com.changhong.gehua.common.MD5Encrypt;
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
	public static boolean keyFlag=false;

	private static TextView videoCurrentTime;
	
	private static long desPositon=0;

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
//		this.skbProgress.setOnSeekBarChangeListener(mySeekChangeLis);
	}

	/*******************************************************
	 * 通过handler更新seekbar
	 ******************************************************/
	TimerTask mTimerTask = new TimerTask() {
		@Override
		public void run() {
			if (mediaPlayer == null)
				return;
			if (mediaPlayer.isPlaying() && playingFlag&&videoCurrentTime!=null&&!keyFlag) {
				handleProgress.sendEmptyMessage(Class_Constant.RE_UPDATE_PROGRESS);
			}
		}
	};


	public static Handler handleProgress = new Handler() {
		public void handleMessage(Message msg) {
			long skPos = 0;
			long skDuration = skbProgress.getMax();
			long mediaDuration = mediaPlayer.getDuration();;
			switch (msg.what) {
			case Class_Constant.REPLAY_SEEK_TO:
				skPos = msg.arg1;
				mediaDuration = mediaPlayer.getDuration();

				if (mediaDuration > 0) {
					long pos = skbProgress.getMax() * skPos / mediaDuration;
					skbProgress.setProgress((int) pos);
				}
				break;
			case Class_Constant.RE_FAST_FORWARD_DOWN:
				if(!playingFlag){
					return;
				}
				keyFlag=true;
				desPositon = skDuration*(mediaDuration*skbProgress.getProgress()/skDuration+15000)/mediaDuration;
				
				if(desPositon>=skDuration){
					if(handlerFlag){
						parentHandler.sendEmptyMessage(Class_Constant.RE_NEXT_PROGRAM);
						handlerFlag=false;
						}
					desPositon=0;
				}
				skbProgress.setProgress((int)desPositon);
				break;
			case Class_Constant.RE_FAST_REVERSE_DOWN:
				
				if(!playingFlag){
					return;
				}
				keyFlag=true;
				desPositon = skDuration*(mediaDuration*skbProgress.getProgress()/skDuration-15000)/mediaDuration;
				if(desPositon<0){
					desPositon=0;
				}
				
				if(desPositon<0){
					if(handlerFlag){
						handlerFlag=false;
					parentHandler.sendEmptyMessage(Class_Constant.RE_LAST_PROGRAM);
					desPositon=0;
					}
				}
					skbProgress.setProgress((int)desPositon);
//				
				break;
			case Class_Constant.RE_FAST_FORWARD_UP:
			case Class_Constant.RE_FAST_REVERSE_UP:
				mediaPlayer.seekTo((int)(mediaDuration*desPositon/skDuration));
				keyFlag=false;
				break;
			case Class_Constant.RE_PLAY:

				break;
			case Class_Constant.RE_PAUSE:

				break;

			case Class_Constant.RE_UPDATE_PROGRESS:
				int position = mediaPlayer.getCurrentPosition();
				if (mediaDuration > 0) {
					long pos = skbProgress.getMax() * position / mediaDuration;
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
		if (null == mediaPlayer||null==videoUrl)
			return;
		
		try {
			mediaPlayer.reset();
//			mediaPlayer.stop();
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
			mediaPlayer.setOnCompletionListener(this);
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
		parentHandler.sendEmptyMessage(Class_Constant.RE_NEXT_PROGRAM);
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
	
//	 public boolean onInfo(MediaPlayer arg0, int arg1, int arg2) {        
//		 switch (arg1) {        
//		 case MediaPlayer.MEDIA_INFO_BUFFERING_START:
//			 //开始缓存，暂停播放            
//			 if (isPlaying()) {                
//				 stopPlayer();                
//				 needResume = true;            
//				 }           
//			 mLoadingView.setVisibility(View.VISIBLE);            
//			 break;        
//			 case MediaPlayer.MEDIA_INFO_BUFFERING_END:            
//				 //缓存完成，继续播放            
//				 if (needResume)                
//					 startPlayer();            
//				 mLoadingView.setVisibility(View.GONE);            
//				 break;        
//				 case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
//					 //显示 下载速度            
//					 Logger.e("download rate:" + arg2);            
//					 break;        
//					 }        
//		 return true;    
//		 }
//		 }
//	 }

	/* 播放过程中时间进行更新显示 */
	// public void refreshVideoTime(TextView txvLen, TextView txvCur) {
	// txvLen.setText("");
	// txvCur.setText("");
	// }
}

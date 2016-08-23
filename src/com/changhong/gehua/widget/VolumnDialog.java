package com.changhong.gehua.widget;

import com.changhong.gehua.common.Class_Constant;
import com.changhong.ghliveandreplay.R;

import android.app.Dialog;
import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

public class VolumnDialog extends Dialog {
	
	private ImageView volumnicon,volumnback;
	private int index;
	private Context context;
	private AudioManager mAudioManager;
	private int curvolumn; 
	private int[] res = new int[]{R.drawable.a,R.drawable.b,R.drawable.c,R.drawable.d,R.drawable.e,R.drawable.f,R.drawable.g,
			R.drawable.h,R.drawable.i,R.drawable.j,R.drawable.k,R.drawable.l,R.drawable.m,R.drawable.n,R.drawable.o,R.drawable.p}; 
	
	private Handler handler = new Handler();
	
	
	public VolumnDialog(Context context, int index,AudioManager AudioManager) {
		super(context, R.style.volumnDialog);
		this.mAudioManager = AudioManager;
		this.context = context;
		this.index = index;
		init();
	}

	
	private void init() {
		setContentView(R.layout.volumn);
		volumnicon = (ImageView)findViewById(R.id.volumn_icon);
		volumnback = (ImageView)findViewById(R.id.volumn_back);
		volumnicon.setVisibility(View.VISIBLE);
		volumnback.setVisibility(View.VISIBLE);
		if(index == 0){
			volumnicon.setBackgroundResource(R.drawable.volumnmuteicon);
		}else {
			volumnicon.setBackgroundResource(R.drawable.volumnicon);
		}
		volumnback.setBackgroundResource(res[index]);
		
		if (VolumnRunnable != null) {
			handler.removeCallbacks(VolumnRunnable);
		}
		handler.postDelayed(VolumnRunnable, 5000);
	}


	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case Class_Constant.KEYCODE_VOICE_UP:
			mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 0);
			curvolumn = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			if (curvolumn == 0) {
				volumnicon.setBackgroundResource(R.drawable.volumnmuteicon);
			}else {
				volumnicon.setBackgroundResource(R.drawable.volumnicon);
			}
			volumnback.setBackgroundResource(res[curvolumn]);

			if (VolumnRunnable != null) {
				handler.removeCallbacks(VolumnRunnable);
			}
			handler.postDelayed(VolumnRunnable, 5000);
			return true;
			
		case Class_Constant.KEYCODE_VOICE_DOWN:
			mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0);
			curvolumn = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			volumnback.setBackgroundResource(res[curvolumn]);
			if (curvolumn == 0) {
				volumnicon.setBackgroundResource(R.drawable.volumnmuteicon);
			}else {
				volumnicon.setBackgroundResource(R.drawable.volumnicon);
			}
			if (VolumnRunnable != null) {
				handler.removeCallbacks(VolumnRunnable);
			}
			handler.postDelayed(VolumnRunnable, 5000);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	Runnable VolumnRunnable = new Runnable() {
		public void run() {
			dismiss();
		}
	};
}

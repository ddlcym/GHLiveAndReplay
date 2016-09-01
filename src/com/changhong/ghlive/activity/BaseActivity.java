package com.changhong.ghlive.activity;

import com.changhong.gehua.common.Class_Constant;
import com.changhong.gehua.common.CommonMethod;
import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

public abstract class BaseActivity extends Activity{
	private int currentindex = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initView();
		initData();
	}
	
	
	protected void initData(){
	}
	
	protected abstract void initView();

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	/*@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		AudioManager mAudioManager = (AudioManager) getApplicationContext().getSystemService(AUDIO_SERVICE);
		currentindex = mAudioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
		Log.i("volumn", "current0 : "+ currentindex);
		//mute = Boolean.valueOf(CommonMethod.getMuteState(getApplicationContext()));
		//Log.i("test", "mute is "+mute);
		
		switch (keyCode) {
		case Class_Constant.KEYCODE_VOICE_UP:
		case Class_Constant.KEYCODE_VOICE_DOWN:
			dialog = new VolumnDialog(BaseActivity.this,currentindex,mAudioManager);
			dialog.show();
			return true;
		case Class_Constant.KEYCODE_MUTE:
			Log.i("test", "KEYCODE_MUTE is coming");
			if (mute) {
				mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
				int n = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
				Log.i("test", "n is "+n);
			}else {
				mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
				int m = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
				Log.i("test", "m is "+m);
			}
			mute = !mute;
			Log.i("test", "mute is "+mute);
			CommonMethod.saveMutesState((mute + ""), getApplicationContext());
			
			return true;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}*/
	
	
	
}

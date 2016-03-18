package com.changhong.ghlive.activity;

import android.content.Intent;
import android.os.Bundle;

import com.changhong.ghlive.service.HttpService;
import com.changhong.ghliveandreplay.R;

public class MainActivity extends BaseActivity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		
		startHttpSer();
		
//		Log.i("mmmm", "c"+date.getHours()+"-"+date.getMonth());
	}
	
	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();
	}

	private void startHttpSer(){
		Intent intent=new Intent(this, HttpService.class);
		startService(intent);
	}
	
	
	
	private void showTime(){
	}
	
	
	// ================================================================================
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

	
}

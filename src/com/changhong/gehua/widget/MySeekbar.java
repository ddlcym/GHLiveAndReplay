package com.changhong.gehua.widget;



import com.changhong.ghliveandreplay.R;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MySeekbar extends LinearLayout {
	
	private Context context;
	
	private SeekBar seekBar;
	private TextMoveLayout textMoveLayout;
	private TextView curText;
	/**
	 * 屏幕宽度
	 */
	private int screenWidth;
	private ViewGroup.LayoutParams layoutParams;

	
	public MySeekbar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context=context;
		initView();
	}

	public MySeekbar(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context=context;
		initView();
	}

	public MySeekbar(Context context) {
		super(context);
		this.context=context;
		initView();
		
	}

	private void initView() {
		LayoutInflater.from(context).inflate(R.layout.myseek,this);
		
		seekBar=(SeekBar)findViewById(R.id.skbProgress);
		screenWidth = seekBar.getLayoutParams().width;
		Log.i("xb", " getLayoutParams screenWidth = "+screenWidth);
		
		textMoveLayout=(TextMoveLayout)findViewById(R.id.textLayout);
		
		curText = new TextView(context);
		curText.setBackgroundDrawable(getResources().getDrawable(R.drawable.shift_curtime_back));
		curText.setTextColor(Color.rgb(0, 161, 229));
		curText.setTextSize(16);
		layoutParams = new ViewGroup.LayoutParams(60, 10);
		
		textMoveLayout.addView(curText, layoutParams);
		curText.layout(0, 0, 60, 20);
	}
	public void setMac(int max){
		seekBar.setMax(max);
	}
	
	public void setCurText(String curTime){
		curText.setText(curTime);
	}
	
	public void setOnSeekBarChangeListener(OnSeekBarChangeListener listener){
		seekBar.setOnSeekBarChangeListener(listener);
	}

	public SeekBar getSeekBar() {
		return seekBar;
	}

	public TextView getCurText() {
		return curText;
	}

}

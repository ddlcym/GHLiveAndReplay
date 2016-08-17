package com.changhong.gehua.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.changhong.ghliveandreplay.R;

/** 
 * @author  cym  
 * @date 创建时间：2016年8月9日 上午9:56:17 
 * @version 1.0 
 * @parameter   
 */
public class MySeekbar extends LinearLayout{
	
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
		// TODO Auto-generated constructor stub
		this.context=context;
		initView();
	}

	public MySeekbar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.context=context;
		initView();
	}

	public MySeekbar(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context=context;
		initView();
		
	}
	
	
	private void initView(){
		LayoutInflater.from(context).inflate(R.layout.myseekbar, this);
		
		screenWidth = getWidth();
		
		seekBar=(SeekBar)findViewById(R.id.seekbar);
		textMoveLayout=(TextMoveLayout)findViewById(R.id.textLayout);
		
		curText = new TextView(context);
		curText.setBackgroundDrawable(getResources().getDrawable(R.drawable.shift_curtime_back));
		curText.setTextColor(Color.rgb(0, 161, 229));
		curText.setTextSize(16);
		layoutParams = new ViewGroup.LayoutParams(screenWidth, 50);
		textMoveLayout = (TextMoveLayout) findViewById(R.id.textLayout);
		textMoveLayout.addView(curText, layoutParams);
		curText.layout(0, 20, screenWidth, 80);
		
	}
	
	public void setMax(int max){
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

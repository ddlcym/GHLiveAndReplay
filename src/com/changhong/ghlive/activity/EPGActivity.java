package com.changhong.ghlive.activity;

import com.changhong.ghliveandreplay.R;
import com.changhong.replay.datafactory.EpgListview;

import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class EPGActivity extends BaseActivity{
	
	private static EpgListview channelListview;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	
	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.epg);
		channelListview = (EpgListview) findViewById(R.id.ChanlIstView);
		channelListview.setFocusable(true);
		channelListview.setFocusableInTouchMode(true);
//		channelListview.setOnFocusChangeListener(ChanListOnfocusChange);
//		channelListview.setOnItemSelectedListener(ChanListOnItemSelected);
//		channelListview.setOnItemClickListener(ChanListOnItemClick);
	}
	
//	private OnItemClickListener ChanListOnItemClick = new OnItemClickListener() {
//		@Override
//		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//				long arg3) {
//			switch (arg0.getId()) {
//			case R.id.ChanlIstView:
//				Point point = new Point();
//				getWindowManager().getDefaultDisplay().getSize(point);
//				DVB_RectSize.Builder builder = DVB_RectSize.newBuilder()
//						.setX(0).setY(0).setW(point.x).setH(point.y);
//				objApplication.dvbPlayer.setSize(builder.build());
//				finish();
//				break;
//
//			default:
//				break;
//			}
//		}
//	};
//	private OnItemSelectedListener ChanListOnItemSelected = new OnItemSelectedListener() {
//
//		@Override
//		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
//				long arg3) {
//			if (arg1 == null) {
//				return;
//			}
//			TextView channelIdText = (TextView) arg1
//					.findViewById(R.id.channelId);
//			channelID = Integer.parseInt(channelIdText.getText().toString());
//			uiHandler.removeMessages(MSG_CHANNEL_CHANGE);
//			uiHandler.sendEmptyMessageDelayed(MSG_CHANNEL_CHANGE, 3000);// send
//																		// a
//																		// message
//																		// delay
//																		// 3s,
//																		// to
//																		// play
//																		// the
//																		// channel
//			channelCurSelect = (LinearLayout) arg1
//					.findViewById(R.id.epg_chan_itemlayout);
//			if (channelListFoucus == false) {
//				return;
//			}
//
//			if (channelLastSelect != null) {
//				ChannelItemScale(channelLastSelect, false, false);
//				channelLastSelect = null;
//			}
//
//			if (channelCurSelect != null) {
//				ChannelItemScale(channelCurSelect, true, true);
//				channelLastSelect = channelCurSelect;
//			}
//
//		}
//
//		@Override
//		public void onNothingSelected(AdapterView<?> arg0) {
//
//		}
//	};
//
//	private OnFocusChangeListener ChanListOnfocusChange = new OnFocusChangeListener() {
//
//		@Override
//		public void onFocusChange(View arg0, boolean arg1) {
//			channelListFoucus = arg1;
//
//			if (arg0 != null) {
//				if (channelCurSelect != null) {
//					ChannelItemScale(channelCurSelect, true, channelListFoucus);
//					channelLastSelect = channelListLinear;
//				}
//			}
//		}
//	};
	
	
	
	
	//-------------------system function------------------
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

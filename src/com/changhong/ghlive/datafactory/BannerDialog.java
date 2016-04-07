package com.changhong.ghlive.datafactory;

import java.util.List;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.changhong.gehua.common.CacheData;
import com.changhong.gehua.common.ChannelInfo;
import com.changhong.gehua.common.Class_Constant;
import com.changhong.gehua.common.ProcessData;
import com.changhong.gehua.common.ProgramInfo;
import com.changhong.gehua.common.VolleyTool;
import com.changhong.ghlive.activity.MainActivity;
import com.changhong.ghliveandreplay.R;
import com.changhong.replay.datafactory.Player;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class BannerDialog extends Dialog {

	private Context mContext;
	private ChannelInfo channelInfo;
	private List<ProgramInfo> programListInfo;
	private Handler mHandler;
	private Player player;
	private String TAG = "mmmm";
	
	
	private SeekBar programPlayBar;
	TextView channel_name = null;// 频道名称
	TextView channel_number = null;// 频道ID
	TextView currentProgramName = null;
	TextView nextProgramName = null;
	private ProcessData processData=null;
	private RequestQueue mReQueue;
	
	
	public BannerDialog(Context context, ChannelInfo outterChannelInfo, List<ProgramInfo> outterListProgramInfo,
			Handler outterHandler,Player play) {
		super(context, R.style.Translucent_NoTitle);
		setContentView(R.layout.bannernew);

		mContext = context.getApplicationContext();
		channelInfo = outterChannelInfo;
		programListInfo = outterListProgramInfo;
		mHandler = outterHandler;
		this.player=play;

		initView();
		// initData();
		// setContentView(R.layout.setting_sys_help_dialog_details);
		// help_name=(TextView)findViewById(R.id.help_name);
		// help_content=(TextView)findViewById(R.id.help_content);
		// ibCancel=(ImageButton)findViewById(R.id.cancel_help);
		// Log.i("mmmm","content==" +name+content);
		//
		// ibCancel.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// MyApplication.vibrator.vibrate(100);
		// dismiss();
		// }
		// });
	}

	public void initView() {
		Window window = this.getWindow();
		WindowManager.LayoutParams wlp = window.getAttributes();
		wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
		wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
		window.setAttributes(wlp);
		window.setGravity(Gravity.BOTTOM);

		/* 频道名称、频道ID 节目名称 */
		channel_name = (TextView) findViewById(R.id.banner_channel_name_id);
		channel_number = (TextView) findViewById(R.id.banner_service_id);
		currentProgramName = (TextView) findViewById(R.id.current_program_info);
		nextProgramName = (TextView) findViewById(R.id.next_program_info);
		programPlayBar = (SeekBar) findViewById(R.id.program_progress);
		// View bannerView = findViewById(R.id.id_dtv_banner);
		// bannerView.getBackground().setAlpha(255);
		programPlayBar.setOnSeekBarChangeListener(myOnSeekChange);
	}

	public void initData() {
		channel_name.setText(channelInfo.getChannelName());
		channel_number.setText(channelInfo.getChannelNumber());
		currentProgramName.setText(programListInfo.get(1).getEventName());
		nextProgramName.setText(programListInfo.get(2).getEventName());
		processData=new ProcessData();
		mReQueue=VolleyTool.getInstance().getRequestQueue();
		dvbBack();
	}

	OnSeekBarChangeListener myOnSeekChange=new OnSeekBarChangeListener() {
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
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			
			myprogress = progress * player.mediaPlayer.getDuration() / seekBar.getMax();
			player.mediaPlayer.seekTo(myprogress);
		}
	};
	
	@Override
	public void show() {
		super.show();
		initData();
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		super.hide();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (keyCode) {
		/* 返回--取消 */
		case KeyEvent.KEYCODE_BACK:
			dismiss();
			break;
		case Class_Constant.KEYCODE_DOWN_ARROW_KEY:
			Log.i("zyt","dialog down key is pressed");
			break;
			
		case Class_Constant.KEYCODE_RIGHT_ARROW_KEY:
//			mHandler.sendEmptyMessage(Class_Constant.LIVE_FAST_FORWARD);
			break;
		case Class_Constant.KEYCODE_LEFT_ARROW_KEY:
//			mHandler.sendEmptyMessage(Class_Constant.LIVE_FAST_REVERSE);
			break;
		default:
			mHandler.removeCallbacks(bannerRunnable);
			mHandler.postDelayed(bannerRunnable, 5000);
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void dvbBack(){
		ChannelInfo channel=CacheData.getAllChannelMap().get(CacheData.getCurChannelNum());
		
		String equestURL=processData.getReplayPlayUrlString(channel, programListInfo.get(1), 0);
		
//		String equestURL=processData.getLiveBackPlayUrl(channel);
		
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, equestURL, null,
				new Response.Listener<org.json.JSONObject>() {

					@Override
					public void onResponse(org.json.JSONObject arg0) {
						// TODO Auto-generated method stub
//						 Log.i(TAG, "MainActivity=dvbBack:" + arg0);
						final String url=JsonResolve.getInstance().getHDPlayURL(arg0);
						new Thread(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								player.playUrl(url);
							}
						}).start();
						
					}
				}, errorListener);
		jsonObjectRequest.setTag(MainActivity.class.getSimpleName());// 设置tag,cancelAll的时候使用
		mReQueue.add(jsonObjectRequest);
		
		
		
	}
	
	private Response.ErrorListener errorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError arg0) {
			// TODO Auto-generated method stub
			Log.i(TAG, "MainActivity=error：" + arg0);
		}
	};
	
	Runnable bannerRunnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			dismiss();
		}
	};
}

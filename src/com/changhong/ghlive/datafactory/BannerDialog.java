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
import com.changhong.gehua.common.Utils;
import com.changhong.gehua.common.VolleyTool;
import com.changhong.ghlive.activity.MainActivity;
import com.changhong.ghlive.service.HttpService;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
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
	private boolean whetherMute;
	
	private SeekBar programPlayBar;
	TextView channel_name = null;// 频道名称
	TextView channel_number = null;// 频道ID
	TextView currentProgramName = null;
	TextView nextProgramName = null;
	private ProcessData processData = null;
	private RequestQueue mReQueue;
	private LinearLayout timeShiftInfo;
	private ImageView palyButton, pauseButton, timeShiftIcon;
	private ImageView muteIconImage;
	private HttpService mHttpService;
	
	public BannerDialog(Context context, ChannelInfo outterChannelInfo, List<ProgramInfo> outterListProgramInfo,
			Handler outterHandler, Player play, HttpService outterHttpService) {
		super(context, R.style.Translucent_NoTitle);
		setContentView(R.layout.bannernew);

		mContext = context.getApplicationContext();
		channelInfo = outterChannelInfo;
		programListInfo = outterListProgramInfo;
		mHandler = outterHandler;
		mHttpService = outterHttpService;
		this.player = play;
		whetherMute = false;

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
		programPlayBar.setOnSeekBarChangeListener(myOnSeekChange);

		palyButton = (ImageView) findViewById(R.id.play_btn);
		pauseButton = (ImageView) findViewById(R.id.pause_btn);
		muteIconImage = (ImageView) findViewById(R.id.mute_icon);
		whetherMute = Boolean.valueOf(mHttpService.getMuteState());
		if (whetherMute) {
			muteIconImage.setVisibility(View.VISIBLE);
		} else {
			muteIconImage.setVisibility(View.GONE);
		}
		timeShiftInfo = (LinearLayout) findViewById(R.id.id_dtv_banner);
		timeShiftIcon = (ImageView) findViewById(R.id.time_shift_icon);
		android.view.ViewGroup.LayoutParams ps = timeShiftIcon.getLayoutParams();
		ps.height = 50;
		ps.width = 50;
		timeShiftIcon.setLayoutParams(ps);
	}

	public void initData() {
		// + programListInfo.get(1).getBeginTime()
		String currentProgramBginTime = Utils.hourAndMinute(programListInfo.get(1).getBeginTime());
		String currentProgramEndTime = Utils.hourAndMinute(programListInfo.get(1).getEndTime());
		String nextProgramBeginTime = Utils.hourAndMinute(programListInfo.get(2).getBeginTime());
		String nextProgramEndTime = Utils.hourAndMinute(programListInfo.get(2).getEndTime());

		channel_name.setText(channelInfo.getChannelName());
		channel_number.setText(channelInfo.getChannelNumber());
		currentProgramName.setText("正在播放：" + currentProgramBginTime + "-" + currentProgramEndTime + "  "
				+ programListInfo.get(1).getEventName());
		nextProgramName.setText("即将播放：" + nextProgramBeginTime + "-" + nextProgramEndTime + "  "
				+ programListInfo.get(2).getEventName());
		palyButton.setVisibility(View.GONE);
		pauseButton.setVisibility(View.GONE);

		processData = new ProcessData();
		mReQueue = VolleyTool.getInstance().getRequestQueue();
		dvbBack();
	}

	OnSeekBarChangeListener myOnSeekChange = new OnSeekBarChangeListener() {
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
			mHttpService.saveMutesState(whetherMute + "");
			Message msg = new Message();
			msg.what = Class_Constant.PLAY_BACKFROM_SHIFT;
			mHandler.sendMessage(msg);
			dismiss();
			break;
		case Class_Constant.KEYCODE_DOWN_ARROW_KEY:
			Log.i("zyt", "dialog down key is pressed");
			break;

		case Class_Constant.KEYCODE_RIGHT_ARROW_KEY:
			palyButton.setVisibility(View.GONE);
			pauseButton.setVisibility(View.GONE);
			// mHandler.sendEmptyMessage(Class_Constant.LIVE_FAST_FORWARD);
			break;
		case Class_Constant.KEYCODE_LEFT_ARROW_KEY:
			palyButton.setVisibility(View.GONE);
			pauseButton.setVisibility(View.GONE);
			// mHandler.sendEmptyMessage(Class_Constant.LIVE_FAST_REVERSE);
			break;
		case Class_Constant.KEYCODE_OK_KEY:
			if (player.isPlayerPlaying()) {
				player.pause();
				palyButton.setVisibility(View.GONE);
				pauseButton.setVisibility(View.VISIBLE);
			} else {
				player.play();
				pauseButton.setVisibility(View.GONE);
				palyButton.setVisibility(View.VISIBLE);
			}
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					palyButton.setVisibility(View.GONE);
				}
			}, 5000);
			break;
		case Class_Constant.KEYCODE_MUTE:// mute
			// int current =
			// audioMgr.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
			whetherMute = !whetherMute;
			// Log.i("zyt", "keycode mute is " + whetherMute);
			if (muteIconImage.isShown()) {
				muteIconImage.setVisibility(View.GONE);
			} else {
				muteIconImage.setVisibility(View.VISIBLE);
			}
			break;
		case Class_Constant.KEYCODE_VOICE_UP:
		case Class_Constant.KEYCODE_VOICE_DOWN:
			if (muteIconImage.isShown()) {
				muteIconImage.setVisibility(View.GONE);
			}
			// audioMgr.setStreamMute(AudioManager.STREAM_MUSIC, true);
			whetherMute = false;
			break;
		default:
			mHandler.removeCallbacks(bannerRunnable);
			mHandler.postDelayed(bannerRunnable, 5000);
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void dvbBack() {
		ChannelInfo channel = CacheData.getAllChannelMap().get(CacheData.getCurChannelNum());

		// String equestURL=processData.getReplayPlayUrlString(channel,
		// programListInfo.get(1), 0);

		String requestURL = processData.getLiveBackPlayUrl(channel, 0);

		playLiveBack(requestURL);
	}

	private void playLiveBack(String requestURL) {
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, requestURL, null,
				new Response.Listener<org.json.JSONObject>() {

					@Override
					public void onResponse(org.json.JSONObject arg0) {
						// TODO Auto-generated method stub
						// Log.i(TAG, "MainActivity=dvbBack:" + arg0);
						final String url = JsonResolve.getInstance().getLivePlayURL(arg0);
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

	public void dismissInfoBan() {
		timeShiftInfo.setVisibility(View.GONE);
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

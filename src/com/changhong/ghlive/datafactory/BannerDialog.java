package com.changhong.ghlive.datafactory;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.changhong.gehua.common.CacheData;
import com.changhong.gehua.common.ChannelInfo;
import com.changhong.gehua.common.Class_Constant;
import com.changhong.gehua.common.CommonMethod;
import com.changhong.gehua.common.ProcessData;
import com.changhong.gehua.common.ProgramInfo;
import com.changhong.gehua.common.Utils;
import com.changhong.gehua.common.VolleyTool;
import com.changhong.gehua.widget.HorizontalListView;
import com.changhong.gehua.widget.MySeekbar;
import com.changhong.gehua.widget.TwoWayAdapterView;
import com.changhong.gehua.widget.TwoWayAdapterView.OnItemClickListener;
import com.changhong.gehua.widget.TwoWayGridView;
import com.changhong.ghlive.activity.MyApp;
import com.changhong.ghlive.service.HttpService;
import com.changhong.ghliveandreplay.R;
import com.changhong.replay.datafactory.Player;
import com.changhong.replay.datafactory.ResolveEPGInfoThread;

/**
 * @author OscarChang 时移信息条
 */
public class BannerDialog extends Dialog {

	private Context mContext;
	private ChannelInfo channelInfo; //当前频道
	private List<ProgramInfo> programListInfo;
	private Handler parentHandler;
	private Player player;
	private String TAG = "mmmm";
	private boolean whetherMute;

	private MySeekbar programPlayBar;
	// private TextView channel_name = null;// 频道名称
	// private TextView channel_number = null;// 频道ID
	private TextView currentProgramName = null;
	private TextView nextProgramName = null;
	private TextView timeLength;
	private SurfaceView surView;
	private LinearLayout bannerView;

	private ProcessData processData = null;
	private RequestQueue mReQueue;
	private ChannelInfo curChannel;
	private LinearLayout timeShiftInfo;
	private ImageView palyButton, pauseButton, timeShiftIcon, forwardIcon,
			backwardIcon;
	private ImageView muteIconImage;
	private HttpService mHttpService;
	private List<ProgramInfo> list;

	private TwoWayGridView timeshiftProList;
	private TimeShiftProgramAdapter programListAdapter;
	private RelativeLayout nextProgramContainer;
	private RelativeLayout programListContainer;

	public BannerDialog(Context context, ChannelInfo outterChannelInfo,
			List<ProgramInfo> outterListProgramInfo, Handler outterHandler,
			SurfaceView surView, HttpService outterHttpService) {
		super(context, R.style.Translucent_NoTitle);
		setContentView(R.layout.bannernew);

		mContext = context.getApplicationContext();
		channelInfo = outterChannelInfo;
		programListInfo = outterListProgramInfo;
		parentHandler = outterHandler;
		mHttpService = outterHttpService;
		whetherMute = false;
		this.surView = surView;

		initView();
		// 获取时移节目列表数据，并填充
		setTimeShiftProgramList();
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
		forwardIcon = (ImageView) findViewById(R.id.fast_forward);
		backwardIcon = (ImageView) findViewById(R.id.fast_backward);

		// 时移节目列表
		timeshiftProList = (TwoWayGridView) findViewById(R.id.timeshift_program_list);
		nextProgramContainer=(RelativeLayout)findViewById(R.id.timeshift_nextpro_container);
		programListContainer=(RelativeLayout)findViewById(R.id.timeshift_program_list_container);

		// channel_name = (TextView) findViewById(R.id.banner_channel_name_id);
		// channel_number = (TextView) findViewById(R.id.banner_service_id);
		currentProgramName = (TextView) findViewById(R.id.current_program_info);
		nextProgramName = (TextView) findViewById(R.id.next_program_info);
		programPlayBar = (MySeekbar) findViewById(R.id.bannernew_program_progress);
		bannerView = (LinearLayout) findViewById(R.id.live_back_banner);
		timeLength = (TextView) findViewById(R.id.live_timelength);
		// bannerView.getBackground().setAlpha(255);
		palyButton = (ImageView) findViewById(R.id.play_btn);
		pauseButton = (ImageView) findViewById(R.id.pause_btn);
		pauseButton.setVisibility(View.VISIBLE);
		muteIconImage = (ImageView) findViewById(R.id.mute_icon);
		whetherMute = Boolean.valueOf(CommonMethod.getMuteState(MyApp
				.getContext()));
		if (whetherMute) {
			muteIconImage.setVisibility(View.VISIBLE);
		} else {
			muteIconImage.setVisibility(View.GONE);
		}
		timeShiftInfo = (LinearLayout) findViewById(R.id.id_dtv_banner);
		timeShiftIcon = (ImageView) findViewById(R.id.time_shift_icon);
		// android.view.ViewGroup.LayoutParams ps =
		// timeShiftIcon.getLayoutParams();
		// ps.height = 90;
		// ps.width = 90;
		// timeShiftIcon.setLayoutParams(ps);
		programPlayBar.setFocusable(false);
		programPlayBar.setClickable(false);

		if (null == processData) {
			processData = new ProcessData();
		}
		mReQueue = VolleyTool.getInstance().getRequestQueue();
		curChannel = CacheData.getAllChannelMap().get(
				CacheData.getCurChannelNum());
		
		// 设置时移节目列表的adapter
		programListAdapter = new TimeShiftProgramAdapter(mContext);
		timeshiftProList.setAdapter(programListAdapter);

		timeshiftProList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(TwoWayAdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (list != null&&programListInfo.size()!=0) {
					ProgramInfo program = list.get(position);
					
					long curTime=System.currentTimeMillis();
					int delayTime=(int) (curTime-program.getBeginTime().getTime())/1000;
					playLiveBack(curChannel, delayTime);
					programListInfo.remove(1);
					programListInfo.add(1, program);
					if(programListInfo.size()!=0&&(list.size()-1)!=position){
						programListInfo.remove(2);
						programListInfo.add(2, list.get(position+1));
					}
					initData();
					nextProgramContainer.setVisibility(View.VISIBLE);
					programListContainer.setVisibility(View.GONE);
				}
			}
		});

	}

	public void initData() {
		// + programListInfo.get(1).getBeginTime()
		if (programListInfo.size() < 2) {
			return;
		}
		
		String currentProgramBginTime = Utils.hourAndMinute(programListInfo
				.get(1).getBeginTime());
		String currentProgramEndTime = Utils.hourAndMinute(programListInfo.get(
				1).getEndTime());
		String nextProgramBeginTime = Utils.hourAndMinute(programListInfo
				.get(2).getBeginTime());
		String nextProgramEndTime = Utils.hourAndMinute(programListInfo.get(2)
				.getEndTime());
		long length = programListInfo.get(1).getEndTime().getTime()
				- programListInfo.get(1).getBeginTime().getTime();
		// 设置频道号和频道名称
		// channel_name.setText(channelInfo.getChannelName());
		// channel_number.setText(channelInfo.getChannelNumber());
		currentProgramName.setText("当前节目       " + currentProgramBginTime + "-"
				+ currentProgramEndTime + "       "
				+ programListInfo.get(1).getEventName());
		nextProgramName.setText("下一节目       " + nextProgramBeginTime + "-"
				+ nextProgramEndTime + "       "
				+ programListInfo.get(2).getEventName());
		timeLength.setText(currentProgramEndTime);
		programPlayBar.setMax((int) length);
		player.setDuration((int) length);
		palyButton.setVisibility(View.GONE);
		
		player = new Player(parentHandler, surView,
				programPlayBar.getSeekBar(), programPlayBar.getCurText());
		player.setLiveFlag(true);
		

	}

	@Override
	public void show() {
		super.show();
		// player.pause();
		// parentHandler.removeCallbacks(bannerRunnable);
		// parentHandler.postDelayed(bannerRunnable, 5000);
		initData();
		Player.setFirstPlayInShift(true);
		dvbBack();
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
			if (bannerView.isShown()) {
				bannerView.setVisibility(View.INVISIBLE);
				return false;
			} else {
				player.setLiveFlag(false);
				CommonMethod.saveMutesState((whetherMute + ""),
						MyApp.getContext());
				Message msg = new Message();
				msg.what = Class_Constant.PLAY_BACKFROM_SHIFT;
				parentHandler.sendMessage(msg);
				dismiss();
				parentHandler.sendEmptyMessage(Class_Constant.BACK_TO_LIVE);
			}
			break;
		case Class_Constant.KEYCODE_DOWN_ARROW_KEY:
			Log.i("zyt", "dialog down key is pressed");
			timeshiftProList.setFocusable(true);
			timeshiftProList.requestFocus();

			break;
		case Class_Constant.KEYCODE_UP_ARROW_KEY:
			Log.i("zyt", "dialog up key is pressed");
			nextProgramContainer.setVisibility(View.GONE);
			programListContainer.setVisibility(View.VISIBLE);

			break;

		case Class_Constant.KEYCODE_RIGHT_ARROW_KEY:
			bannerView.setVisibility(View.VISIBLE);
			palyButton.setVisibility(View.GONE);
			pauseButton.setVisibility(View.GONE);
			parentHandler.removeCallbacks(bannerRunnable);
			parentHandler.postDelayed(bannerRunnable, 5000);
			backwardIcon.setVisibility(View.GONE);
			forwardIcon.setVisibility(View.VISIBLE);
			Log.i("mmmm", "banner-handleProgress" + Player.handleProgress);
			Player.handleProgress
					.sendEmptyMessage(Class_Constant.LIVE_FAST_FORWARD);
			break;
		case Class_Constant.KEYCODE_LEFT_ARROW_KEY:
			bannerView.setVisibility(View.VISIBLE);
			palyButton.setVisibility(View.GONE);
			pauseButton.setVisibility(View.GONE);
			parentHandler.removeCallbacks(bannerRunnable);
			parentHandler.postDelayed(bannerRunnable, 5000);
			forwardIcon.setVisibility(View.GONE);
			backwardIcon.setVisibility(View.VISIBLE);
			Player.handleProgress
					.sendEmptyMessage(Class_Constant.LIVE_FAST_REVERSE);
			break;
		case Class_Constant.KEYCODE_OK_KEY:
			forwardIcon.setVisibility(View.GONE);
			backwardIcon.setVisibility(View.GONE);
			
			if(programListContainer.isShown()){
				player.play();
				break;
			}
			if (player.isPlayerPlaying()) {
				player.pause();
				palyButton.setVisibility(View.GONE);
				pauseButton.setVisibility(View.VISIBLE);
				if (bannerRunnable != null) {
					parentHandler.removeCallbacks(bannerRunnable);
				}
				bannerView.setVisibility(View.VISIBLE);
			} else {
				player.play();
				pauseButton.setVisibility(View.GONE);
				palyButton.setVisibility(View.VISIBLE);
				parentHandler.removeCallbacks(bannerRunnable);
				parentHandler.postDelayed(bannerRunnable, 5000);
				parentHandler.removeCallbacks(playBtnRunnable);
				parentHandler.postDelayed(playBtnRunnable, 5000);
			}

			break;

		case Class_Constant.KEYCODE_MUTE:// mute
			// int current =
			// audioMgr.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
			whetherMute = !whetherMute;
			CommonMethod.saveMutesState((whetherMute + ""), MyApp.getContext());
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
			CommonMethod.saveMutesState((whetherMute + ""), MyApp.getContext());
			break;
		case Class_Constant.KEYCODE_MENU_KEY:
			// Log.i("zyt", "onkeydown menukey is pressed " + keyCode);
			CommonMethod.startSettingPage(MyApp.getContext());
			break;
		case Class_Constant.MENU_ID_DTV_ROOT:
			bannerView.setVisibility(View.VISIBLE);
			if (bannerRunnable != null) {
				parentHandler.removeCallbacks(bannerRunnable);
				parentHandler.postDelayed(bannerRunnable, 5000);
			}
			break;
		default:
			nextProgramContainer.setVisibility(View.VISIBLE);
			programListContainer.setVisibility(View.GONE);
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		switch (keyCode) {
		case Class_Constant.KEYCODE_RIGHT_ARROW_KEY:

			Player.handleProgress
					.sendEmptyMessage(Class_Constant.RE_FAST_FORWARD_UP);
			forwardIcon.setVisibility(View.GONE);
			break;
		case Class_Constant.KEYCODE_LEFT_ARROW_KEY:
			Player.handleProgress
					.sendEmptyMessage(Class_Constant.RE_FAST_REVERSE_UP);
			backwardIcon.setVisibility(View.GONE);
			break;
		}

		return super.onKeyUp(keyCode, event);
	}

	private void dvbBack() {

		// String equestURL=processData.getReplayPlayUrlString(channel,
		// programListInfo.get(1), 0);

		playLiveBack(curChannel, 0);
	}

	private void playLiveBack(ChannelInfo channel, int delay) {
		mReQueue.cancelAll("bannerDialog");
		String requestURL = processData.getLiveBackPlayUrl(channel, delay);
		CacheData.setCurProgram(programListInfo.get(1));
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
				Request.Method.POST, requestURL, null,
				new Response.Listener<org.json.JSONObject>() {

					@Override
					public void onResponse(org.json.JSONObject arg0) {
						// TODO Auto-generated method stub
						// Log.i(TAG, "MainActivity=dvbBack:" + arg0);
						final String url = JsonResolve.getInstance()
								.getLivePlayURL(arg0);
						new Thread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								player.playUrl(url);
							}
						}).start();

					}
				}, errorListener);
		jsonObjectRequest.setTag("bannerDialog");// 设置tag,cancelAll的时候使用
		mReQueue.add(jsonObjectRequest);
	}

	private Response.ErrorListener errorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError arg0) {
			// TODO Auto-generated method stub
			Log.i(TAG, "bannerdialog=error：" + arg0);
		}
	};

	Runnable bannerRunnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			bannerView.setVisibility(View.GONE);
		}
	};

	Runnable playBtnRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			palyButton.setVisibility(View.GONE);
		}
	};

	private void setTimeShiftProgramList() {
		String URL = processData.get4HoursProgramList(channelInfo);
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
				Request.Method.GET, URL, null,
				new Response.Listener<org.json.JSONObject>() {

					@Override
					public void onResponse(org.json.JSONObject arg0) {
						// TODO Auto-generated method stub
						// 相应成功
						// Log.i(TAG, "getPointProList:" + arg0);
						list = JsonResolve.getInstance()
								.timeShiftPrograms(arg0);
						programListAdapter.setData(list);
						timeshiftProList.requestFocus();
						timeshiftProList.setSelection(list.size() - 1);
					}
				}, null);
		jsonObjectRequest.setTag("program");// 设置tag,cancelAll的时候使用
		mReQueue.add(jsonObjectRequest);
	}
	

}

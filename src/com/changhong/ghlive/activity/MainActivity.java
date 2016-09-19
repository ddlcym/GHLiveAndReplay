package com.changhong.ghlive.activity;

import java.util.ArrayList;
import java.util.List;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.changhong.gehua.common.CacheData;
import com.changhong.gehua.common.ChannelInfo;
import com.changhong.gehua.common.ChannelType;
import com.changhong.gehua.common.Class_Constant;
import com.changhong.gehua.common.CommonMethod;
import com.changhong.gehua.common.PlayVideo;
import com.changhong.gehua.common.PosterInfo;
import com.changhong.gehua.common.ProcessData;
import com.changhong.gehua.common.ProgramInfo;
import com.changhong.gehua.common.VolleyTool;
import com.changhong.gehua.update.StringUtils;
import com.changhong.gehua.widget.DigitalRoot;
import com.changhong.ghlive.datafactory.Banner;
import com.changhong.ghlive.datafactory.BannerDialog;
import com.changhong.ghlive.datafactory.ChannelListAdapter;
import com.changhong.ghlive.datafactory.HandleLiveData;
import com.changhong.ghlive.datafactory.LivePlayBannerDialog;
import com.changhong.ghlive.service.HttpService;
import com.changhong.ghliveandreplay.R;
import com.changhong.replay.datafactory.Player;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends BaseActivity {

	private String TAG = "mmmm";

	// view
	private ImageView focusView; // foucus image
	private TextView epgListTitleView;// chanellist title
	private String[] TVtype;// all tv type
	private SurfaceView surfaceView;
	private LinearLayout channelListLinear;// channellist layout
	
	private LinearLayout linear_vertical_line;// straight line right of
	private ImageView fullscrBack;											// channellist layout
	private ListView chListView;//频道列表
	private SeekBar liveSeekBar;
	private TextView tvRootDigitalKeyInvalid;
	private DigitalRoot tvRootDigitalkey;
	private ImageView muteIconImage;
	
	/*
	 * 频道类型
	 */
	private TextView lastCategory,curCategory,nextCategory;
	
	/**
	 * Digital key
	 */
	private int iKeyNum = 0;
	private int iKey = 0;

	// private time_shift Banner programBan;
	private BannerDialog programBannerDialog;
	/*
	 * 直播banner信息条
	 */
	private LivePlayBannerDialog livePlayBanner;
	private VolleyTool volleyTool;
	private RequestQueue mReQueue;
	private ProcessData processData;

	private List<ChannelType> channelTypes;
	
	// kinds of channel list
	private List<ChannelInfo> mCurChannels = new ArrayList<ChannelInfo>();// 当前频道列表清单
	private List<ChannelInfo> channelsAll = new ArrayList<ChannelInfo>();
	private List<ChannelInfo> CCTVList = new ArrayList<ChannelInfo>();
	private List<ChannelInfo> starTvList = new ArrayList<ChannelInfo>();
	// private List<ChannelInfo> favTvList = new ArrayList<ChannelInfo>();
	private List<ChannelInfo> localTvList = new ArrayList<ChannelInfo>();
	private List<ChannelInfo> HDTvList = new ArrayList<ChannelInfo>();
	private List<ChannelInfo> otherTvList = new ArrayList<ChannelInfo>();

	private List<ProgramInfo> curChannelPrograms = new ArrayList<ProgramInfo>();// 当前频道下的上一个节目，当前节目，下一个节目信息
	private int curListIndex = 0;// 当前list下正在播放的当前节目的index
	private int curType = 0;
	private String curChannelNO = null; // 当前播放的节目的channelno
	private ProgramInfo curProgram = null;
	private String curPlayURL = null;
	private boolean keydownFlag = false;

	private ChannelListAdapter chLstAdapter;
	private Player player;
	private AudioManager mAudioManager = null; // Audio管理器，用了控制音量
	
	private boolean whetherMute;// mute flag
	private HttpService mHttpService;
	
	private int curvolumn;

	private Handler mhandler = new Handler() {
		ChannelInfo curChannel;

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Class_Constant.PLAY_LIVE:// 直播

				curPlayURL = (String) msg.getData().getString("PLAY_URL");
				// Log.i("zyt MainActivity", curPlayURL);

				// player.playUrl(curPlayURL);
				playNetVideo();

				curChannel = CacheData.getAllChannelMap().get(curChannelNO);
				PlayVideo.getInstance().getProgramInfo(mhandler, curChannel);

				// show toast banner
				// showToastBanner();
				break;
			case Class_Constant.TOAST_BANNER_PROGRAM_PASS:
				int curIndex = -1;
				int curtype = msg.getData().getInt("type",0);
				Log.i("live", "curtype = "+curtype);
				curChannelPrograms = CacheData.getCurPrograms();
				curChannel = channelsAll.get(curListIndex);
				curIndex = mCurChannels.indexOf(curChannel);
				if (curIndex >= 0 && chListView.isShown()) {

					chListView.setFocusable(true);
					chListView.requestFocus();
					chListView.setSelection(curIndex);
				}
				if (null == curProgram) {
					curProgram = new ProgramInfo();
				}
				if (curChannelPrograms.size() > 0) {
					curProgram = curChannelPrograms.get(1);
					showToastBanner(CacheData.getCurChannel(),curtype);
				}
				break;

			case Class_Constant.MESSAGE_HANDLER_DIGITALKEY: 
				playDigitalChannel();
			
				break;
			case Class_Constant.MESSAGE_CHANNELIST_SELECT_DIGITAL:
				selectChannelListDigital();
				break;
			case Class_Constant.MESSAGE_SHOW_DIGITALKEY:
				int channelId = msg.arg1;
				tvRootDigitalKeyInvalid.setVisibility(View.GONE);
				tvRootDigitalkey.setVisibility(View.VISIBLE);
				int digitalText = 0;
//				if (channelId < 10) {
//					digitalText = "00" + channelId;
//				} else if (channelId < 100) {
//					digitalText = "0" + channelId;
//				} else {
//					digitalText = "" + channelId;
//				}
				digitalText =channelId;
				tvRootDigitalkey.setData(digitalText);
				mhandler.removeMessages(Class_Constant.MESSAGE_DISAPPEAR_DIGITAL);
				mhandler.sendEmptyMessageDelayed(Class_Constant.MESSAGE_DISAPPEAR_DIGITAL, 3500);
				break;

			case Class_Constant.MESSAGE_DISAPPEAR_DIGITAL:
				if (tvRootDigitalKeyInvalid != null) {
					tvRootDigitalKeyInvalid.setVisibility(View.INVISIBLE);
				}
				if (tvRootDigitalkey != null) {
					tvRootDigitalkey.setVisibility(View.INVISIBLE);
				}

				break;

			case Class_Constant.BACK_TO_LIVE:
				if (programBannerDialog != null) {
					programBannerDialog.dismiss();
					Toast.makeText(MainActivity.this, "退回到直播模式", Toast.LENGTH_SHORT).show();
					Log.i(TAG, "退回到直播模式");
				}
				PlayVideo.getInstance().playLiveProgram(mhandler, CacheData.getCurChannel());
				break;
			/* play state is back from time shift mode */
			/*case Class_Constant.PLAY_BACKFROM_SHIFT: 
			/*{
				whetherMute = Boolean.valueOf(CommonMethod.getMuteState(MyApp.getContext()));
				if (whetherMute) {
					muteIconImage.setVisibility(View.VISIBLE);
				} else {
					muteIconImage.setVisibility(View.GONE);
				}
				break;
			}*/
			case Class_Constant.DIALOG_ONKEY_DOWN: {
				dealOnKeyDown(msg.arg1);
				break;
			}
			case Class_Constant.DIALOG_ONKEY_UP:
				dealOnKeyUp(msg.arg1);
				break;
			case Class_Constant.LIVE_BACK_PROGRAM_OVER:
				showDialogBanner(curChannelNO);
				break;
			// next message
			case Class_Constant.VOLUMN_KEY_END:
				if (liveBannerInfoRunnable != null) {
					mhandler.removeCallbacks(liveBannerInfoRunnable);
			    }
				mhandler.postDelayed(liveBannerInfoRunnable, 5000);
				break;	
				
				
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		whetherMute = false;
		startHttpSer();
		whetherMute = Boolean.valueOf(CommonMethod.getMuteState(MyApp.getContext()));
		curChannelNO = String.valueOf(CommonMethod.getChannelLastTime(MyApp.getContext()));
		// Log.i("zyt", "current volume state is " + whetherMute);
		Log.i("zyt", "current channel number is " + curChannelNO);
		
	}

	@Override
	protected void initData() {
		volleyTool = VolleyTool.getInstance();
		mReQueue = volleyTool.getRequestQueue();
		if (null == processData) {
			processData = new ProcessData();
		}
		getChannelTypes();
		getChannelList();
		
		player = new Player(mhandler, surfaceView, liveSeekBar, null);
		
		
//		Player.setFirstPlayInShift(false);
		// chListView.setOnItemSelectedListener(myItemSelectLis);
		chListView.setOnItemClickListener(myClickLis);

		//audioMgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mAudioManager = (AudioManager) getApplicationContext().getSystemService(AUDIO_SERVICE);
		
		
		// audioMgr.setStreamMute(AudioManager.STREAM_MUSIC, false);
		// int max = audioMgr.getStreamMaxVolume( AudioManager.STREAM_VOICE_CALL
		// );
		// Log.d("VIOCE_CALL", “max : ” + max + ” current : ” + current);
		// registerUser();
		// getUserChannel();
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.channellist);

		TVtype = getResources().getStringArray(R.array.tvtype);
		chListView = (ListView) findViewById(R.id.id_epg_chlist);
		focusView = (ImageView) findViewById(R.id.set_focus_id);
		lastCategory = (TextView) findViewById(R.id.last_category);
		curCategory = (TextView) findViewById(R.id.cur_category);
		nextCategory = (TextView) findViewById(R.id.next_category);
		surfaceView = (SurfaceView) findViewById(R.id.surfaceview_live);
		channelListLinear = (LinearLayout) findViewById(R.id.chlist_back);
		
		fullscrBack = (ImageView) findViewById(R.id.prolistback); 
		linear_vertical_line = (LinearLayout) findViewById(R.id.linear_vertical_line);
		liveSeekBar = (SeekBar) findViewById(R.id.liveskbProgress);

		tvRootDigitalkey = (DigitalRoot) findViewById(R.id.id_dtv_digital_root);
		tvRootDigitalKeyInvalid = (TextView) findViewById(R.id.id_dtv_digital_root_invalid);
		// videoView.setMediaController(new MediaController(this));
		surfaceView.setFocusable(false);
		chListView.setFocusable(false);

		chLstAdapter = new ChannelListAdapter(MainActivity.this);
		chListView.setAdapter(chLstAdapter);
		chListView.setOnItemClickListener(myClickLis);
		chListView.setOnItemSelectedListener(myItemSelectLis);
		muteIconImage = (ImageView) findViewById(R.id.live_mute_icon);

		/*if (whetherMute) {
			muteIconImage.setVisibility(View.VISIBLE);
		} else {
			muteIconImage.setVisibility(View.GONE);
		}*/
	}

	private void startHttpSer() {
		mHttpService = new HttpService(getApplicationContext());
		Intent intent = new Intent(this, mHttpService.getClass());
		startService(intent);
	}

	private OnItemSelectedListener myItemSelectLis = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			// TODO show the select channel
			// int[] pos = { -1, -1 };
			if (view != null) {
				// view.getLocationOnScreen(pos);
				// dislistfocus((FrameLayout) view);
				// mhandler.removeCallbacks(runnable);
				// mhandler.postDelayed(runnable, 5000);
				// TextView channelIndex = (TextView)
				// view.findViewById(R.id.chanId);
				// curChannelNO = channelIndex.getText().toString();
				// curListIndex = position;
				// playChannel(curChannelNO, true);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

		}
	};

	private OnItemClickListener myClickLis = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			// TODO Auto-generated method stub
			TextView channelIndex = (TextView) view.findViewById(R.id.chanId);
			// if (lockSwap) {
			// new_ChanId = Integer
			// .parseInt(channelIndex.getText().toString());
			//
			// if (new_ChanId == old_chanId) {
			// Toast.makeText(MainActivity.this, "不能与本身位置交换",
			// Toast.LENGTH_SHORT).show();
			// lockSwap = false;
			// return;
			// }
			// // getAllTVtype(curType);
			// // showChannelList();
			// lockSwap = false;
			// Toast.makeText(MainActivity.this, "交换成功", Toast.LENGTH_SHORT)
			// .show();
			//
			// return;
			//
			// }
			curListIndex = position;
			String channelNO = channelIndex.getText().toString();
			// Log.i(TAG, "myClickLis"+position);
			playChannel(channelNO, true);

			curChannelNO = channelNO;
			mhandler.post(runnable);
			Log.i("zyt", "play channel number is " + curChannelNO);

		}

	};
	
	private void getChannelTypes(){
		String URL = processData.getTypes();
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
				new Response.Listener<org.json.JSONObject>() {

					@Override
					public void onResponse(org.json.JSONObject arg0) {
						// TODO Auto-generated method stub
						Log.i("test", "MainActivity***getChannelTypes:" + arg0);
						channelTypes=HandleLiveData.getInstance().dealChannelTypes(arg0);
						}
					}, errorListener);
		jsonObjectRequest.setTag(MainActivity.class.getSimpleName());// 设置tag,cancelAll的时候使用
		mReQueue.add(jsonObjectRequest);
	}

	private void getChannelList() {
		// 传入URL请求链接
		String URL = processData.getChannelList();
		Log.i("test", "MainActivity");
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
				new Response.Listener<org.json.JSONObject>() {

					@Override
					public void onResponse(org.json.JSONObject arg0) {
						// TODO Auto-generated method stub
						// 相应成功
//						Log.i("test", "MainActivity***" + arg0);
						channelsAll = HandleLiveData.getInstance().dealChannelJson(arg0);
						// first set adapter
						curType = 0;
						getAllTVtype();
						showChannelList();
						playChannel(curChannelNO, false);
						// Log.i(TAG,
						// "HttpService=channelsAll:" + channelsAll.size());
						if (channelsAll.size() <= 0) {
							channelListLinear.setVisibility(View.INVISIBLE);
							fullscrBack.setVisibility(View.INVISIBLE);
						}
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

	private void dislistfocus(FrameLayout selected) {
		Rect imgRect = new Rect();
		FrameLayout.LayoutParams focusItemParams = new FrameLayout.LayoutParams(10, 10);
		selected.getGlobalVisibleRect(imgRect);
		focusItemParams.leftMargin = imgRect.left - 8;
		focusItemParams.topMargin = imgRect.top - 8;
		focusItemParams.width = imgRect.width() + 16;
		focusItemParams.height = imgRect.height() + 14;

		focusView.setLayoutParams(focusItemParams);
		focusView.setVisibility(View.VISIBLE);
		focusView.bringToFront();
	}

	private void getAllTVtype() {
		// fill all type tv

		// clear all tv type;
		channelsAll.clear();
		CCTVList.clear();
		starTvList.clear();
		// favTvList.clear();
		localTvList.clear();
		HDTvList.clear();
		otherTvList.clear();
		List<ChannelInfo> channels = CacheData.allChannelInfo;
		for (ChannelInfo dvbChannel : channels) {
			channelsAll.add(dvbChannel);

			// 喜爱频道列表---------------待完成---------------
			// if (dvbChannel.favorite == 1) {
			// favTvList.add(dvbChannel);
			// }
			String regExCCTV;
			regExCCTV = getResources().getString(R.string.zhongyang);
			java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("CCTV|" + regExCCTV);
			java.util.regex.Matcher matcher = pattern.matcher(dvbChannel.getChannelName());
			boolean classBytype = matcher.find();
			if (classBytype) {
				CCTVList.add(dvbChannel);
			}
			
			String regExLocal = "BTV|" + getResources().getString(R.string.beijing_h);
			// + "|"+ getResources().getString(R.string.jingniu) + "|" +
			// getResources().getString(R.string.qingyang)
			// + "|" + getResources().getString(R.string.wuhou) + "|" +
			// getResources().getString(R.string.chenghua)
			// + "|" + getResources().getString(R.string.jinjiang) + "|"
			// + getResources().getString(R.string.chengdu) + "|" +
			// getResources().getString(R.string.sichuan);
			java.util.regex.Pattern patternLocal = java.util.regex.Pattern.compile(regExLocal);
			java.util.regex.Matcher matcherLocal = patternLocal.matcher(dvbChannel.getChannelName());
			boolean classBytypeLocal = matcherLocal.find();
			if (classBytypeLocal) {
				localTvList.add(dvbChannel);
			}
			
			String regExStar;
			regExStar = "CETV|"+"山东教育|"+"CHC|"+getResources().getString(R.string.weishi);
			java.util.regex.Pattern patternStar = java.util.regex.Pattern.compile(regExStar);
			java.util.regex.Matcher matcherStar = patternStar.matcher(dvbChannel.getChannelName());
			boolean classBytypeStar = matcherStar.find();
			if (classBytypeStar&&!classBytypeLocal) {
				starTvList.add(dvbChannel);
			}
			
			String regExHD = getResources().getString(R.string.hd_dtv) + "|"
					+ getResources().getString(R.string.xinyuan_hdtv1) + "|"
					+ getResources().getString(R.string.xinyuan_hdtv2) + "|"
					+ getResources().getString(R.string.xinyuan_hdtv3) + "|"
					+ getResources().getString(R.string.xinyuan_hdtv4);
			java.util.regex.Pattern patternHD = java.util.regex.Pattern.compile("3D|" + regExHD + "|.*HD$");

			java.util.regex.Matcher matcherHD = patternHD.matcher(dvbChannel.getChannelName());
			boolean classBytypeHD = matcherHD.find();
			if (classBytypeHD) {
				HDTvList.add(dvbChannel);
			}
			String regExOther = "CDTV|SCTV|CCTV|" + getResources().getString(R.string.weishi) + "|"
					+ getResources().getString(R.string.rongcheng) + "|" + getResources().getString(R.string.jingniu)
					+ "|" + getResources().getString(R.string.qingyang) + "|" + getResources().getString(R.string.wuhou)
					+ "|" + getResources().getString(R.string.chenghua) + "|"
					+ getResources().getString(R.string.jinjiang) + "|" + getResources().getString(R.string.chengdu)
					+ "|" + getResources().getString(R.string.sichuan);
			java.util.regex.Pattern patternOther = java.util.regex.Pattern.compile(regExOther);
			java.util.regex.Matcher matcherOther = patternOther.matcher(dvbChannel.getChannelName());
			boolean classBytypeOther = matcherOther.find();
			if (!classBytypeOther) {
				otherTvList.add(dvbChannel);
			}
		}
	}

	// private void getAllTVtype(int index) {
	// // fill all type tv
	// // Channel[] Channels =
	// // objApplication.dvbDatabase.getChannelsAllSC();//Only get channels
	// // type=1(TV)
	// // clear all tv type;
	// switch (index) {
	// case 0:
	// break;
	//
	// case 1:
	// CCTVList.clear();
	// String regExCCTV;
	// regExCCTV = getResources().getString(R.string.zhongyang);
	// java.util.regex.Pattern pattern = java.util.regex.Pattern
	// .compile("CCTV|" + regExCCTV);
	// for (ChannelInfo Channel : channelsAll) {
	// java.util.regex.Matcher matcher = pattern.matcher(Channel
	// .getChannelName());
	// boolean classBytype = matcher.find();
	// if (classBytype) {
	// CCTVList.add(Channel);
	// }
	// }
	// break;
	// case 2:
	// starTvList.clear();
	// String regExStar;
	// regExStar = getResources().getString(R.string.weishi);
	// java.util.regex.Pattern patternStar = java.util.regex.Pattern
	// .compile(".*" + regExStar + "$");
	// for (ChannelInfo Channel : channelsAll) {
	// java.util.regex.Matcher matcherStar = patternStar
	// .matcher(Channel.getChannelName());
	// boolean classBytypeStar = matcherStar.matches();
	// if (classBytypeStar) {
	// starTvList.add(Channel);
	// }
	// }
	// break;
	// case 3:
	// localTvList.clear();
	// String regExLocal = "CDTV|SCTV|"
	// + getResources().getString(R.string.rongcheng) + "|"
	// + getResources().getString(R.string.jingniu) + "|"
	// + getResources().getString(R.string.qingyang) + "|"
	// + getResources().getString(R.string.wuhou) + "|"
	// + getResources().getString(R.string.chenghua) + "|"
	// + getResources().getString(R.string.jinjiang) + "|"
	// + getResources().getString(R.string.chengdu) + "|"
	// + getResources().getString(R.string.sichuan);
	// java.util.regex.Pattern patternLocal = java.util.regex.Pattern
	// .compile(regExLocal);
	// for (ChannelInfo Channel : channelsAll) {
	// java.util.regex.Matcher matcherLocal = patternLocal
	// .matcher(Channel.getChannelName());
	// boolean classBytypeLocal = matcherLocal.find();
	// if (classBytypeLocal) {
	// localTvList.add(Channel);
	// }
	// }
	// break;
	// case 4:
	// HDTvList.clear();
	// String regExHD = getResources().getString(R.string.hd_dtv) + "|"
	// + getResources().getString(R.string.xinyuan_hdtv1) + "|"
	// + getResources().getString(R.string.xinyuan_hdtv2) + "|"
	// + getResources().getString(R.string.xinyuan_hdtv3) + "|"
	// + getResources().getString(R.string.xinyuan_hdtv4);
	// java.util.regex.Pattern patternHD = java.util.regex.Pattern
	// .compile("3D|" + regExHD + "|.*HD$");
	// for (ChannelInfo Channel : channelsAll) {
	// java.util.regex.Matcher matcherHD = patternHD.matcher(Channel
	// .getChannelName());
	// boolean classBytypeHD = matcherHD.find();
	// if (classBytypeHD) {
	// HDTvList.add(Channel);
	// }
	// }
	// break;
	// case 5:
	// // favTvList.clear();
	// // for (ChannelInfo Channel : Channels) {
	// // if (Channel.favorite == 1) {
	// // favTvList.add(Channel);
	// // }
	// //
	// // }
	// break;
	// case 6:
	// otherTvList.clear();
	// String regExOther = "CDTV|SCTV|CCTV|"
	// + getResources().getString(R.string.weishi) + "|"
	// + getResources().getString(R.string.rongcheng) + "|"
	// + getResources().getString(R.string.jingniu) + "|"
	// + getResources().getString(R.string.qingyang) + "|"
	// + getResources().getString(R.string.wuhou) + "|"
	// + getResources().getString(R.string.chenghua) + "|"
	// + getResources().getString(R.string.jinjiang) + "|"
	// + getResources().getString(R.string.chengdu) + "|"
	// + getResources().getString(R.string.sichuan);
	// java.util.regex.Pattern patternOther = java.util.regex.Pattern
	// .compile(regExOther);
	// for (ChannelInfo Channel : channelsAll) {
	// java.util.regex.Matcher matcherOther = patternOther
	// .matcher(Channel.getChannelName());
	// boolean classBytypeOther = matcherOther.find();
	// if (!classBytypeOther) {
	// otherTvList.add(Channel);
	// }
	// }
	// break;
	// }
	//
	// }

	private void showChannelList() {
		// TODO show channellist
		switch (curType) {
		case 0:
			mCurChannels = channelsAll;
			break;
		case 1:
			mCurChannels = CCTVList;
			break;
		case 2:
			mCurChannels = starTvList;
			break;
		case 3:
			mCurChannels = localTvList;
			break;
		case 4:
			mCurChannels = HDTvList;
			break;
		}
		setCategoryTitle(curType);
		curListIndex = mCurChannels.indexOf(CacheData.getCurChannel());
		if (-1 == curListIndex) {
			curListIndex = 0;
		}
		chLstAdapter.setData(mCurChannels);
		chListView.setVisibility(View.VISIBLE);
		if (mCurChannels.size() <= 0) {
			focusView.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (Class_Constant.KEYCODE_BACK_KEY == keyCode) {

			if (channelListLinear.isShown()) {
				mhandler.post(runnable);
				return false;
			}

		}
		if (keyCode == Class_Constant.KEYCODE_VOICE_UP || keyCode == Class_Constant.KEYCODE_VOICE_DOWN ) {
			Message msg = new Message();
			msg.what = Class_Constant.TOAST_BANNER_PROGRAM_PASS;
			Bundle bundle = new Bundle();
			bundle.putInt("type", 1);
			msg.setData(bundle);
			mhandler.sendMessage(msg);
			return true;
		}
		
		if (keyCode == Class_Constant.KEYCODE_MUTE) {
			curvolumn =  mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			Log.i("test", "live KEYCODE_MUTE later curvolumn is"+curvolumn);
			if (curvolumn == 0) {
				mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
				Log.i("test", "setStreamMute false");
				muteIconImage.setVisibility(View.GONE);
			}else {
				mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
				Log.i("test", "setStreamMute true");
				muteIconImage.setVisibility(View.VISIBLE);
			}
			Message msg = new Message();
			msg.what = Class_Constant.TOAST_BANNER_PROGRAM_PASS;
			Bundle bundle = new Bundle();
			bundle.putInt("type", 2);
			msg.setData(bundle);
			mhandler.sendMessage(msg);
			return true;
		}
		
		dealOnKeyDown(keyCode);
		return super.onKeyDown(keyCode, event);
	}

	private boolean dealOnKeyDown(int keyCode) {
		switch (keyCode) {
		case Class_Constant.KEYCODE_RIGHT_ARROW_KEY:
			// if (ban != null && ban.isToastShow()) {
			// ban.cancelBanner();
			// }
			//当按了数字键后，立刻按右键，需要取消发送数字延迟发送的消息，不然会出现逻辑混乱的情况
			mhandler.removeMessages(Class_Constant.MESSAGE_HANDLER_DIGITALKEY);
			
			if (livePlayBanner != null && livePlayBanner.isToastShow()) {
				livePlayBanner.dismiss();
			}

			// 切换频道类型，更新频道列表的数据
			if (chListView.isShown()) {
				if (curType == (TVtype.length - 1)) {
					curType = 0;
				} else {
					curType++;
				}
			} else {
				curType = 0;
			}
			showChannelListView();
			showChannelList();
			chListView.setFocusable(true);
			chListView.requestFocus();
			chListView.setSelection(curListIndex);
			break;
		case Class_Constant.KEYCODE_LEFT_ARROW_KEY:
			// 切换频道类型，更新频道列表的数据
			if (curType == 0) {
				curType = (TVtype.length - 1);
			} else {
				curType--;
			}
			showChannelList();
			chListView.setSelection(0);

			break;

		case Class_Constant.KEYCODE_PAGE_UP:
			// if (curListIndex == 0) {
			// showChannelList();
			// chListView.setSelection(chListView.getCount() - 1);
			// }
			break;
		case Class_Constant.KEYCODE_PAGE_DOWN:
			// if (curListIndex == (chListView.getCount() - 1)) {
			// showChannelList();
			// chListView.setSelection(0);
			// }
			break;
		case Class_Constant.KEYCODE_CHANNEL_UP:

			if (chListView.isShown()) {// 呼出频道列表，进行翻页+
				if (chListView.hasFocus()) {
					Log.i("zyt press page +", "page + is pressed");
					curListIndex = mCurChannels.indexOf(CacheData.getCurChannel());

					// int pageUpIndex = curListIndex + 8;
					int pageUpIndex = chListView.getSelectedItemPosition() + 8;
					if (pageUpIndex > (mCurChannels.size() - 1)) {
						pageUpIndex = pageUpIndex - (mCurChannels.size());
					}
					if ((pageUpIndex > (mCurChannels.size() - 8)) && (pageUpIndex <= (mCurChannels.size() - 1))) {
						pageUpIndex = mCurChannels.size() - 8;
					}
					if ((pageUpIndex > 0) && (pageUpIndex < 7)) {
						pageUpIndex = 0;
					}
					// if (curListIndex == (mCurChannels.size() - 1)) {
					// chListView.setSelection(0);
					// curListIndex = 0;
					// } else {
					// curListIndex = curListIndex + 1;
					// }
					chListView.setFocusable(true);
					chListView.requestFocus();
					chListView.setSelection(pageUpIndex);
				}
			} else {
				curListIndex = channelsAll.indexOf(CacheData.getCurChannel());
				if (curListIndex == (channelsAll.size() - 1)) {
					chListView.setSelection(0);
					curListIndex = 0;
				} else {
					curListIndex = curListIndex + 1;
				}
				if (!StringUtils.hasLength(curChannelNO)) {
					// 第一次进入时
					chListView.setSelection(0);
					curChannelNO = channelsAll.get(0).getChannelNumber();
					if (channelsAll != null && channelsAll.size() != 0) {
						playChannel(curChannelNO, true);
					}
				} else {
					// chListView.setFocusable(true);
					// chListView.requestFocus();
					// chListView.setSelection(curListIndex);
					if (channelsAll != null && channelsAll.size() != 0) {
						// playChannel(mCurChannels.get(curListIndex).getChannelNumber(),
						// true);
						// 显示频道号和名称
						// showToastBanner(channelsAll.get(curListIndex).getChannelNumber());
						ChannelInfo curChannel = channelsAll.get(curListIndex);
						CacheData.setCurChannel(curChannel);
						PlayVideo.getInstance().getProgramInfo(mhandler, curChannel);
						keydownFlag = true;
					}
				}
			}

			break;
		case Class_Constant.KEYCODE_CHANNEL_DOWN:
			if (chListView.isShown()) {
				if (chListView.hasFocus()) {
					curListIndex = mCurChannels.indexOf(CacheData.getCurChannel());

					int pageDownIndex = chListView.getSelectedItemPosition() - 8;
					if (pageDownIndex < 0) {
						pageDownIndex = mCurChannels.size() + pageDownIndex;
					}
					if ((pageDownIndex > (mCurChannels.size() - 8)) && (pageDownIndex <= (mCurChannels.size() - 1))) {
						pageDownIndex = mCurChannels.size() - 8;
					}
					if ((pageDownIndex > 0) && (pageDownIndex < 7)) {
						pageDownIndex = 0;
					}
					// if (curListIndex == 0) {
					// curListIndex = channelsAll.size() - 1;
					// } else {
					// curListIndex = curListIndex - 1;
					// }
					chListView.setFocusable(true);
					chListView.requestFocus();
					chListView.setSelection(pageDownIndex);
				}
			} else {
				curListIndex = channelsAll.indexOf(CacheData.getCurChannel());
				if (curListIndex == 0) {
					// chListView.setSelection(channelsAll.size() - 1);
					curListIndex = channelsAll.size() - 1;
				} else {
					// chListView.setSelection(curListIndex - 1);
					curListIndex = curListIndex - 1;
				}
				if (!StringUtils.hasLength(curChannelNO)) {
					curChannelNO = channelsAll.get(0).getChannelNumber();
				}
				// chListView.setFocusable(true);
				// chListView.requestFocus();
				// chListView.setSelection(curListIndex);
				if (mCurChannels != null && channelsAll.size() != 0) {
					// playChannel(mCurChannels.get(curListIndex).getChannelNumber(),
					// true);
					// 显示频道号和名称
					// showToastBanner(channelsAll.get(curListIndex));
					ChannelInfo curChannel = channelsAll.get(curListIndex);
					CacheData.setCurChannel(curChannel);
					PlayVideo.getInstance().getProgramInfo(mhandler, curChannel);
					keydownFlag = true;
				}
			}
			break;

		case Class_Constant.KEYCODE_OK_KEY:

			/* 获取节目信息，并进行banner显示 */
			// ChannelInfo curChannel =
			// CacheData.getAllChannelMap().get(curChannelNO);
			// PlayVideo.getInstance().getProgramInfo(mhandler, curChannel);

			if (tvRootDigitalkey.isShown()) {
				mhandler.sendEmptyMessage(Class_Constant.MESSAGE_HANDLER_DIGITALKEY);
			} else {
				//CommonMethod.saveMutesState((whetherMute + ""), MyApp.getContext());
				showDialogBanner(curChannelNO);
				//muteIconImage.setVisibility(View.GONE);
			}
			break;
		case Class_Constant.KEYCODE_UP_ARROW_KEY:
			if (chListView.isShown()) {
				chListView.setFocusable(true);
				chListView.requestFocus();
				if (0 == chListView.getSelectedItemPosition()) {
					chListView.setSelection(chLstAdapter.getCount() - 1);
				}
			} else {
				// 播放之后的一个频道
				curListIndex = channelsAll.indexOf(CacheData.getCurChannel());
				if (curListIndex == (channelsAll.size() - 1)) {
					chListView.setSelection(0);
					curListIndex = 0;
				} else {
					chListView.setSelection(curListIndex + 1);
					curListIndex = curListIndex + 1;
				}
				if (channelsAll != null && channelsAll.size() != 0) {
					// playChannel(mCurChannels.get(curListIndex).getChannelNumber(),
					// true);
					// showToastBanner(channelsAll.get(curListIndex));
					ChannelInfo curChannel = channelsAll.get(curListIndex);
					CacheData.setCurChannel(curChannel);
					PlayVideo.getInstance().getProgramInfo(mhandler, curChannel);
					keydownFlag = true;
				}
			}
			break;
		case Class_Constant.KEYCODE_DOWN_ARROW_KEY:
			if (chListView.isShown()) {
				chListView.setFocusable(true);
				chListView.requestFocus();
				if ((chLstAdapter.getCount() - 1) == chListView.getSelectedItemPosition()) {
					chListView.setSelection(0);
				}
			} else {
				// 播放之前一个频道
				curListIndex = channelsAll.indexOf(CacheData.getCurChannel());
				if (curListIndex == 0) {
					chListView.setSelection(channelsAll.size() - 1);
					curListIndex = channelsAll.size() - 1;
				} else {
					chListView.setSelection(curListIndex - 1);
					curListIndex = curListIndex - 1;
				}
				if (channelsAll != null && channelsAll.size() != 0) {
					// playChannel(mCurChannels.get(curListIndex).getChannelNumber(),
					// true);
					// showToastBanner(channelsAll.get(curListIndex));
					ChannelInfo curChannel = channelsAll.get(curListIndex);
					CacheData.setCurChannel(curChannel);
					PlayVideo.getInstance().getProgramInfo(mhandler, curChannel);
					keydownFlag = true;
				}
			}
			break;

		case Class_Constant.KEYCODE_KEY_DIGIT0:
		case Class_Constant.KEYCODE_KEY_DIGIT1:
		case Class_Constant.KEYCODE_KEY_DIGIT2:
		case Class_Constant.KEYCODE_KEY_DIGIT3:
		case Class_Constant.KEYCODE_KEY_DIGIT4:
		case Class_Constant.KEYCODE_KEY_DIGIT5:
		case Class_Constant.KEYCODE_KEY_DIGIT6:
		case Class_Constant.KEYCODE_KEY_DIGIT7:
		case Class_Constant.KEYCODE_KEY_DIGIT8:
		case Class_Constant.KEYCODE_KEY_DIGIT9: {
			onVkey(keyCode);
		}

			break;
		case Class_Constant.MENU_ID_DTV_ROOT:
			// if (ban != null && !ban.isToastShow()) {
			// ban.show();
			// }
			if (livePlayBanner != null && !(livePlayBanner.isToastShow())) {
				livePlayBanner.show();
				if (liveBannerInfoRunnable != null) {
					mhandler.postDelayed(liveBannerInfoRunnable, 5000);
				}
			}
			break;

		case Class_Constant.KEYCODE_MENU_KEY:
			// Log.i("zyt", "onkeydown menukey is pressed " + keyCode);
			CommonMethod.startSettingPage(MyApp.getContext());
			break;

		/*case Class_Constant.KEYCODE_MUTE:// mute
			// int current =
			// audioMgr.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
			//whetherMute = !whetherMute;
			// Log.i("zyt", "keycode mute is " + whetherMute);
			
			CommonMethod.saveMutesState((whetherMute + ""), MyApp.getContext());
			if (muteIconImage.isShown()) {
				muteIconImage.setVisibility(View.GONE);
			} else {
				muteIconImage.setVisibility(View.VISIBLE);
			}
			break;
			Log.i("test", "live KEYCODE_MUTE is coming");
			curvolumn =  mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			Log.i("test", "live KEYCODE_MUTE later curvolumn is"+curvolumn);
			if (curvolumn == 0) {
				mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
				Log.i("test", "setStreamMute false");
				muteIconImage.setVisibility(View.GONE);
			}else {
				mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
				Log.i("test", "setStreamMute true");
				muteIconImage.setVisibility(View.VISIBLE);
			}
			return true;*/
		default:
			
			Log.i("zyt", "onkeydown default is " + keyCode);
			break;
		// next key down call
		}
		return true;
	}

	private void dealOnKeyUp(int keyCode) {
		switch (keyCode) {
		case Class_Constant.KEYCODE_CHANNEL_UP:
		case Class_Constant.KEYCODE_CHANNEL_DOWN:
		case Class_Constant.KEYCODE_UP_ARROW_KEY:
		case Class_Constant.KEYCODE_DOWN_ARROW_KEY:
			if (channelsAll != null && channelsAll.size() != 0 && keydownFlag) {
				playChannel(channelsAll.get(curListIndex).getChannelNumber(), true);
				keydownFlag = false;
			}

			break;
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		dealOnKeyUp(keyCode);
		return super.onKeyUp(keyCode, event);
	}

	private boolean onVkey(int ri_KeyCode) {
		boolean b_Result = false;

		switch (ri_KeyCode) {
		case Class_Constant.KEYCODE_KEY_DIGIT0:
		case Class_Constant.KEYCODE_KEY_DIGIT1:
		case Class_Constant.KEYCODE_KEY_DIGIT2:
		case Class_Constant.KEYCODE_KEY_DIGIT3:
		case Class_Constant.KEYCODE_KEY_DIGIT4:
		case Class_Constant.KEYCODE_KEY_DIGIT5:
		case Class_Constant.KEYCODE_KEY_DIGIT6:
		case Class_Constant.KEYCODE_KEY_DIGIT7:
		case Class_Constant.KEYCODE_KEY_DIGIT8:
		case Class_Constant.KEYCODE_KEY_DIGIT9: {

			
			iKeyNum++;
			if (iKeyNum > 0 && iKeyNum <= 4) {
				if (ri_KeyCode == Class_Constant.KEYCODE_KEY_DIGIT0) {
					iKey = iKey * 10;
				} else if (ri_KeyCode == Class_Constant.KEYCODE_KEY_DIGIT1) {
					iKey = 1 + iKey * 10;

				} else if (ri_KeyCode == Class_Constant.KEYCODE_KEY_DIGIT2) {
					iKey = 2 + iKey * 10;

				} else if (ri_KeyCode == Class_Constant.KEYCODE_KEY_DIGIT3) {
					iKey = 3 + iKey * 10;

				} else if (ri_KeyCode == Class_Constant.KEYCODE_KEY_DIGIT4) {
					iKey = 4 + iKey * 10;

				} else if (ri_KeyCode == Class_Constant.KEYCODE_KEY_DIGIT5) {
					iKey = 5 + iKey * 10;

				} else if (ri_KeyCode == Class_Constant.KEYCODE_KEY_DIGIT6) {
					iKey = 6 + iKey * 10;

				} else if (ri_KeyCode == Class_Constant.KEYCODE_KEY_DIGIT7) {
					iKey = 7 + iKey * 10;

				} else if (ri_KeyCode == Class_Constant.KEYCODE_KEY_DIGIT8) {
					iKey = 8 + iKey * 10;

				} else if (ri_KeyCode == Class_Constant.KEYCODE_KEY_DIGIT9) {
					iKey = 9 + iKey * 10;

				}

				tvRootDigitalKeyInvalid.setVisibility(View.GONE);
				tvRootDigitalkey.setVisibility(View.VISIBLE);
				// if (iKey < 10) {
				// if (iKeyNum == 1) {
				// tvRootDigitalkey.setText("--" + iKey);
				// } else if (iKeyNum == 2) {
				// tvRootDigitalkey.setText("-0" + iKey);
				// } else {
				// tvRootDigitalkey.setText("00" + iKey);
				// }
				// } else if (iKey < 100) {
				// if (iKeyNum == 2) {
				// tvRootDigitalkey.setText("-" + iKey);
				// } else {
				// tvRootDigitalkey.setText("0" + iKey);
				// }
				// } else {
				tvRootDigitalkey.setData(iKey);
				mhandler.removeMessages(Class_Constant.MESSAGE_DISAPPEAR_DIGITAL);
				mhandler.sendEmptyMessageDelayed(Class_Constant.MESSAGE_DISAPPEAR_DIGITAL, 6000);
				// }
				if(chListView.isShown()){
					mhandler.removeMessages(Class_Constant.MESSAGE_HANDLER_DIGITALKEY);
					mhandler.removeMessages(Class_Constant.MESSAGE_CHANNELIST_SELECT_DIGITAL);
					mhandler.sendEmptyMessageDelayed(Class_Constant.MESSAGE_CHANNELIST_SELECT_DIGITAL, 1500);
				}else{
					mhandler.removeMessages(Class_Constant.MESSAGE_HANDLER_DIGITALKEY);
					if (iKey >= 100) {
						mhandler.sendEmptyMessageDelayed(Class_Constant.MESSAGE_HANDLER_DIGITALKEY, 2000);
					} else {
						
						mhandler.sendEmptyMessageDelayed(Class_Constant.MESSAGE_HANDLER_DIGITALKEY, 5000);
					}
				}
			}

		}
			break;
		}
		return b_Result;
	}

	private void showChannelListView() {
		channelListLinear.setVisibility(View.VISIBLE);
		fullscrBack.setVisibility(View.VISIBLE);
//		focusView.setVisibility(View.VISIBLE);
		linear_vertical_line.setVisibility(View.VISIBLE);
		chListView.setVisibility(View.VISIBLE);
		// mhandler.removeCallbacks(runnable);
		// mhandler.postDelayed(runnable, 5000);
		// chListView.setFocusable(true);
		// chListView.requestFocus();
		// chListView.setSelection(curListIndex);
	}

	// 用户注册
	private void registerUser() {
		String URL = processData.sendRegValidCode();
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
				new Response.Listener<org.json.JSONObject>() {

					@Override
					public void onResponse(org.json.JSONObject arg0) {
						// TODO Auto-generated method stub
						Log.i(TAG, "MainActivity=registerUser:" + arg0);
					}
				}, errorListener);
		jsonObjectRequest.setTag(MainActivity.class.getSimpleName());// 设置tag,cancelAll的时候使用
		mReQueue.add(jsonObjectRequest);
	}

	// 获取频道是否支持时移和频道logoURL
	private void getUserChannel() {
		String URL = processData.getChannelsInfo();
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
				new Response.Listener<org.json.JSONObject>() {

					@Override
					public void onResponse(org.json.JSONObject arg0) {
						// TODO Auto-generated method stub
						Log.i(TAG, "MainActivity=getUserChannel:" + arg0);

						HandleLiveData.getInstance().dealChannelExtra(arg0);
					}
				}, errorListener);
		jsonObjectRequest.setTag(MainActivity.class.getSimpleName());// 设置tag,cancelAll的时候使用
		mReQueue.add(jsonObjectRequest);
	}

	// 第一次快退的时候获取播放串
	private void requestPlayURL() {
		ChannelInfo channel = (ChannelInfo) CacheData.getAllChannelMap().get(curChannelNO);

		String url = processData.getReplayPlayUrlString(channel, curProgram, 0);
		PlayVideo.getInstance().getProgramPlayURL(mhandler, url);

	}

	/* show time-shifting banner dialog */
	public void showDialogBanner(String channelno) {
		// if (ban != null) {
		// ban.cancelBanner();
		// }
		if(null==curChannelPrograms||curChannelPrograms.size()<=0){
			Toast.makeText(MainActivity.this, "节目信息为空，不能进入时移模式", Toast.LENGTH_SHORT).show();
			return;
		}
		if (livePlayBanner != null) {
			livePlayBanner.stopTimer();
			livePlayBanner.dismiss();
		}
		ChannelInfo curChannel = (ChannelInfo) CacheData.allChannelMap.get(channelno);
		if (programBannerDialog != null) {
			programBannerDialog.cancel();
		}
		programBannerDialog = new BannerDialog(this, curChannel, curChannelPrograms, mhandler, surfaceView,
				mHttpService);
		programBannerDialog.show();
	}

	/* show live banner toast */
	public void showToastBanner(ChannelInfo channel, int type) {

		// if(ban!=null){
		// ban.cancelBanner();
		// }
		// if (null == ban) {
		// ban = new Banner(this, channel, curChannelPrograms);
		// }
		// ban.setData(channel, curChannelPrograms);
		// ban.show();
		
		int volumn = mAudioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
		Log.i("live", "volumn : "+ volumn);
		if (null == livePlayBanner) {
			Log.i("live", "----> ");
			livePlayBanner = new LivePlayBannerDialog(this, channel, curChannelPrograms, mhandler,mAudioManager,muteIconImage);
			//livePlayBanner.setMuteicon(muteIconImage);
		}
		
		
		
		livePlayBanner.setData(channel, curChannelPrograms,volumn,type);
		livePlayBanner.show();
		
		mhandler.sendEmptyMessage(Class_Constant.VOLUMN_KEY_END);
		
		/*if (liveBannerInfoRunnable != null) {
			mhandler.removeCallbacks(liveBannerInfoRunnable);
			mhandler.postDelayed(liveBannerInfoRunnable, 5000);
	    }*/
		// ChannelInfo curChannel = (ChannelInfo)
		// CacheData.allChannelMap.get(channelno);
		// if (programBannerDialog != null) {
		// programBannerDialog.cancel();
		// }
		// programBannerDialog = new BannerDialog(this, curChannel,
		// curChannelPrograms, mhandler, player);
		// programBannerDialog.show();
	}

	// ============play video=========================================
	/*
	 * 
	 */
	public String playChannel(String channelno, boolean isCheckPlaying) {

		if (channelno.equals(curChannelNO) && isCheckPlaying) {
			return channelno;
		}

		ChannelInfo curChannel = (ChannelInfo) CacheData.allChannelMap.get(channelno);
		if (curChannel == null) {
			return null;
		}

		/*--------------- If it is audio channel, blank the screen------------- */
		// if (curChannel.sortId == 2 || curChannel.videoPid == 0x0
		// || curChannel.videoPid == 0x1fff) {
		// dvbPlayer.blank();
		// showAudioPlaying(true);
		// } else {
		// showAudioPlaying(false);
		// }

		// Log.i(TAG, "1");
		PlayVideo.getInstance().playLiveProgram(mhandler, curChannel);

		CacheData.curChannelNum = curChannel.getChannelNumber();
		CacheData.setCurChannel(curChannel);
		curChannelNO = channelno;
		curListIndex = channelsAll.indexOf(curChannel);
		// curChannel = CacheData.getAllChannelMap().get(curChannelNO);
		// PlayVideo.getInstance().getProgramInfo(mhandler, curChannel);

		CommonMethod.saveChannelLastTime(Integer.parseInt(curChannelNO), MyApp.getContext());

		return curChannelNO;
	}
	
	//根据数字键播放对应频道
	private void playDigitalChannel(){
		String NO;
		NO=""+tvRootDigitalkey.getCurNO();
		String succ = playChannel(String.valueOf(NO), true);
		// int succ = objApplication.playChannelKeyInput(iKey,true);
		if (null == succ) {
			tvRootDigitalkey.setVisibility(View.INVISIBLE);
			tvRootDigitalKeyInvalid.setVisibility(View.VISIBLE);
		} else {
			Message msg2 = new Message();
			msg2.what = Class_Constant.MESSAGE_SHOW_DIGITALKEY;
			msg2.arg1 = tvRootDigitalkey.getCurNO();
			mhandler.sendMessage(msg2);
			iKeyNum=0;
			iKey=0;
			return;
		}
		iKeyNum = 0;
		iKey=0;
	}
	
	private void selectChannelListDigital(){
		int position=-1;
		if(curType!=0){
			curType=0;
			showChannelList();
		}
		for(int i=0;i<channelsAll.size();i++){
			String channelNO=channelsAll.get(i).getChannelNumber();
			if(Integer.parseInt(channelNO)==iKey){
				position=i;
				break;
			}
		}
		if(position!=-1){
			chListView.setSelection(position);
		}else{
			tvRootDigitalkey.setVisibility(View.INVISIBLE);
			tvRootDigitalKeyInvalid.setVisibility(View.VISIBLE);
		}
		iKeyNum = 0;
		iKey = 0;
		
	}

	/* timer for channel listview hide */
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			curType = 0;
			mCurChannels = channelsAll;
			channelListLinear.setVisibility(View.INVISIBLE);
			fullscrBack.setVisibility(View.INVISIBLE);
			focusView.setVisibility(View.INVISIBLE);
			linear_vertical_line.setVisibility(View.INVISIBLE);
			liveSeekBar.setVisibility(View.INVISIBLE);
			chListView.setVisibility(View.INVISIBLE);
		}
	};

	public int playPlusOrSub(int outterChannelId, int code) {
		// code :0 plus 1 sub
		int comp = 0;
		for (int i = 0; i < mCurChannels.size(); i++) {
			if (Integer.parseInt(mCurChannels.get(i).getChannelNumber()) == outterChannelId)
				switch (code) {
				case 0:
					comp = (i == mCurChannels.size()) ? 0 : (i + 1);
				case 1:
					comp = (i == 0) ? mCurChannels.size() : (i - 1);
				}

		}
		return comp;

	}

	private void playNetVideo() {
		if (isNetConnected()) {
			newThreadPlay();
		} else {
			Toast.makeText(this, "请检查网络", Toast.LENGTH_SHORT).show();
		}
	}

	/* thread to play video */
	private void newThreadPlay() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				player.playUrl(curPlayURL);

			}
		}).start();
		channelListLinear.setVisibility(View.GONE);
		fullscrBack.setVisibility(View.GONE);
	}

	/* whether net is connected */
	private boolean isNetConnected() {
		ConnectivityManager ccM = (ConnectivityManager) getApplicationContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (null == ccM) {
			return false;
		} else {
			NetworkInfo[] nwInfo = ccM.getAllNetworkInfo();
			if (null != nwInfo) {
				for (int i = 0; i < nwInfo.length; i++) {
					if (nwInfo[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		// Log.i("zyt", "resume mute is " + whetherMute);
		curChannelNO = String.valueOf(CommonMethod.getChannelLastTime(MyApp.getContext()));
		
		curvolumn =  mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		Log.i("test", "enter live curvolumn is"+curvolumn);
		if (curvolumn == 0) {
			muteIconImage.setVisibility(View.VISIBLE);
			Log.i("test", "muteIconImage muteIconImage");
		}else {
			muteIconImage.setVisibility(View.GONE);
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		// programBan.cancelBanner();
		Log.i("zyt", "pause mute is  " + whetherMute);
		Intent intent = new Intent();
		intent.setAction("WHETHER_MUTE"); // 设置Action
		intent.putExtra("msg", "接收动态注册广播成功！"); // 添加附加信息
		sendBroadcast(intent);
		CommonMethod.saveMutesState((whetherMute + ""), MyApp.getContext());
		// CommonMethod.saveChannelLastTime(Integer.parseInt(curChannelNO),
		finish();
		super.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		// Log.i("zyt", "activity stop()" + mHttpService.getMuteState());
		if(mhandler!=null){
			mhandler.removeCallbacksAndMessages(null);
		}
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.i("mmmm", "mainactivity destroyed()");
		whetherMute = false;
		player.setLiveFlag(false);
		player.stop();
		player=null;
		if(programBannerDialog!=null){
			programBannerDialog.dismiss();
			programBannerDialog=null;
		}
		// ban.cancelBanner();
		if(livePlayBanner!=null){
		livePlayBanner.dismiss();
		livePlayBanner=null;
		}
		
		super.onDestroy();
	}

	Runnable liveBannerInfoRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(livePlayBanner!=null)
			/*livePlayBanner.getLivebannerLayout().setVisibility(View.INVISIBLE);
			livePlayBanner.getBackImageView().setVisibility(View.INVISIBLE);*/
		    livePlayBanner.dismiss();	
		}
	};
	
	/*
	 * 设置频道分类标题
	 */
	private  void setCategoryTitle(int index){
		int size=TVtype.length;
		int lastIndex=(index-1)>=0?(index-1):(size-1);
		lastCategory.setText(TVtype[lastIndex % size]);
		curCategory.setText(TVtype[index]);
		nextCategory.setText(TVtype[(index+1) % size]);
	}
}

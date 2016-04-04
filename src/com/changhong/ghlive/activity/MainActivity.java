package com.changhong.ghlive.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.changhong.gehua.common.CacheData;
import com.changhong.gehua.common.ChannelInfo;
import com.changhong.gehua.common.Class_Constant;
import com.changhong.gehua.common.PlayVideo;
import com.changhong.gehua.common.ProcessData;
import com.changhong.gehua.common.ProgramInfo;
import com.changhong.gehua.common.VideoView;
import com.changhong.gehua.common.VolleyTool;
import com.changhong.gehua.update.StringUtils;
import com.changhong.ghlive.datafactory.Banner;
import com.changhong.ghlive.datafactory.ChannelListAdapter;
import com.changhong.ghlive.datafactory.HandleLiveData;
import com.changhong.ghlive.service.HttpService;
import com.changhong.ghliveandreplay.R;

public class MainActivity extends BaseActivity {

	private String TAG = "mmmm";

	// view
	private ImageView focusView; // foucus image
	private TextView epgListTitleView;// chanellist title
	private String[] TVtype;// all tv type
	private VideoView videoView;
	private LinearLayout channelListLinear;// channellist layout
	private LinearLayout linear_vertical_line;// straight line right of
												// channellist layout
	private ListView chListView;
	private SeekBar liveSeekBar;


	private VolleyTool volleyTool;
	private RequestQueue mReQueue;

	private ProcessData processData;

	// kinds of channel list
	private List<ChannelInfo> mCurChannels = new ArrayList<ChannelInfo>();// 当前频道列表清单
	private List<ChannelInfo> channelsAll = new ArrayList<ChannelInfo>();
	private List<ChannelInfo> CCTVList = new ArrayList<ChannelInfo>();
	private List<ChannelInfo> starTvList = new ArrayList<ChannelInfo>();
	private List<ChannelInfo> favTvList = new ArrayList<ChannelInfo>();
	private List<ChannelInfo> localTvList = new ArrayList<ChannelInfo>();
	private List<ChannelInfo> HDTvList = new ArrayList<ChannelInfo>();
	private List<ChannelInfo> otherTvList = new ArrayList<ChannelInfo>();

	private List<ProgramInfo> curChannelPrograms = new ArrayList<ProgramInfo>();//当前频道下的上一个节目，当前节目，下一个节目信息

	private int curListIndex = 0;// 当前list下正在播放的当前节目的index
	private int curType = 0;
	private String curChannelNO = null; // 当前播放的节目的channelno
	private ProgramInfo curProgram=null;

	private ChannelListAdapter chLstAdapter;
	private Handler mhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Class_Constant.PLAY_URL:// 直播
				// content = (String) msg.obj;
				// Log.i(TAG, "playURL:" + content);
				// videoView.setVideoPath(content);
				// videoView.start();
				break;
			case Class_Constant.BANNER_PROGRAM_PASS:
				curChannelPrograms =  (List<ProgramInfo>) msg.obj;
				// Log.i("zyt", "pgmcontent" + pgmContent.get("name"));
				// Log.i("zyt", "pgmcontent" + pgmContent.get("playTime"));

				if(null==curProgram){
				curProgram = new ProgramInfo();}
				curProgram=curChannelPrograms.get(1);
				showBanner(curChannelNO, curProgram);
				break;


			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		volleyTool = VolleyTool.getInstance();
		mReQueue = volleyTool.getRequestQueue();
		if (null == processData) {
			processData = new ProcessData();
		}
		// startHttpSer();
		getChannelList();
		// Log.i("mmmm", "c"+date.getHours()+"-"+date.getMonth());

		chListView.setOnItemSelectedListener(myItemSelectLis);
		chListView.setOnItemClickListener(myClickLis);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.channellist);

		TVtype = getResources().getStringArray(R.array.tvtype);
		chListView = (ListView) findViewById(R.id.id_epg_chlist);
		focusView = (ImageView) findViewById(R.id.set_focus_id);
		epgListTitleView = (TextView) findViewById(R.id.id_epglist_title);
		videoView = (VideoView) findViewById(R.id.videoview);
		channelListLinear = (LinearLayout) findViewById(R.id.chlist_back);
		linear_vertical_line = (LinearLayout) findViewById(R.id.linear_vertical_line);
		 liveSeekBar = (SeekBar) findViewById(R.id.liveskbProgress);
		// videoView.setMediaController(new MediaController(this));
		videoView.setFocusable(false);
		chListView.setFocusable(true);

		chLstAdapter = new ChannelListAdapter(MainActivity.this);
		chListView.setAdapter(chLstAdapter);
		chListView.setOnItemClickListener(myClickLis);
		chListView.setOnItemSelectedListener(myItemSelectLis);


	}

	private void startHttpSer() {
		Intent intent = new Intent(this, HttpService.class);
		startService(intent);
	}

	private OnItemSelectedListener myItemSelectLis = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO show the select channel

			// int[] pos = { -1, -1 };
			if (view != null) {
				// view.getLocationOnScreen(pos);
				// dislistfocus((FrameLayout) view);
				mhandler.removeCallbacks(runnable);
				mhandler.postDelayed(runnable, 5000);
				TextView channelIndex = (TextView) view
						.findViewById(R.id.chanId);
				curChannelNO = channelIndex.getText().toString();
				curListIndex=position;
				playChannel(curChannelNO, false);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

		}
	};

	private OnItemClickListener myClickLis = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
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
			String index = channelIndex.getText().toString();
			Log.i(TAG, index);
			playChannel(index, true);

			curChannelNO = index;
			mhandler.post(runnable);
			Log.i("zyt", "play channel number is " + curChannelNO);

		}

	};

	private void getChannelList() {
		// 传入URL请求链接
		String URL = processData.getChannelList();
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
				Request.Method.GET, URL, null,
				new Response.Listener<org.json.JSONObject>() {

					@Override
					public void onResponse(org.json.JSONObject arg0) {
						// TODO Auto-generated method stub
						// 相应成功
						// Log.i(TAG, "HttpService=channle:" + arg0);
						channelsAll = HandleLiveData.getInstance()
								.dealChannelJson(arg0);
						// first set adapter
						curType = 0;
						getAllTVtype();
						showChannelList();
						// Log.i(TAG,
						// "HttpService=channelsAll:" + channelsAll.size());
						if (channelsAll.size() <= 0) {
							channelListLinear.setVisibility(View.INVISIBLE);
						}
					}
				}, errorListener);
		jsonObjectRequest.setTag(HttpService.class.getSimpleName());// 设置tag,cancelAll的时候使用
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
		FrameLayout.LayoutParams focusItemParams = new FrameLayout.LayoutParams(
				10, 10);
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
		favTvList.clear();
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
			java.util.regex.Pattern pattern = java.util.regex.Pattern
					.compile("CCTV|" + regExCCTV);
			java.util.regex.Matcher matcher = pattern.matcher(dvbChannel
					.getChannelName());
			boolean classBytype = matcher.find();
			if (classBytype) {
				CCTVList.add(dvbChannel);
			}
			String regExStar;
			regExStar = getResources().getString(R.string.weishi);
			java.util.regex.Pattern patternStar = java.util.regex.Pattern
					.compile(".*" + regExStar + "$");
			java.util.regex.Matcher matcherStar = patternStar
					.matcher(dvbChannel.getChannelName());
			boolean classBytypeStar = matcherStar.matches();
			if (classBytypeStar) {
				starTvList.add(dvbChannel);
			}
			String regExLocal = "CDTV|SCTV|"
					+ getResources().getString(R.string.rongcheng) + "|"
					+ getResources().getString(R.string.jingniu) + "|"
					+ getResources().getString(R.string.qingyang) + "|"
					+ getResources().getString(R.string.wuhou) + "|"
					+ getResources().getString(R.string.chenghua) + "|"
					+ getResources().getString(R.string.jinjiang) + "|"
					+ getResources().getString(R.string.chengdu) + "|"
					+ getResources().getString(R.string.sichuan);
			java.util.regex.Pattern patternLocal = java.util.regex.Pattern
					.compile(regExLocal);
			java.util.regex.Matcher matcherLocal = patternLocal
					.matcher(dvbChannel.getChannelName());
			boolean classBytypeLocal = matcherLocal.find();
			if (classBytypeLocal) {
				localTvList.add(dvbChannel);
			}
			String regExHD = getResources().getString(R.string.hd_dtv) + "|"
					+ getResources().getString(R.string.xinyuan_hdtv1) + "|"
					+ getResources().getString(R.string.xinyuan_hdtv2) + "|"
					+ getResources().getString(R.string.xinyuan_hdtv3) + "|"
					+ getResources().getString(R.string.xinyuan_hdtv4);
			java.util.regex.Pattern patternHD = java.util.regex.Pattern
					.compile("3D|" + regExHD + "|.*HD$");

			java.util.regex.Matcher matcherHD = patternHD.matcher(dvbChannel
					.getChannelName());
			boolean classBytypeHD = matcherHD.find();
			if (classBytypeHD) {
				HDTvList.add(dvbChannel);
			}
			String regExOther = "CDTV|SCTV|CCTV|"
					+ getResources().getString(R.string.weishi) + "|"
					+ getResources().getString(R.string.rongcheng) + "|"
					+ getResources().getString(R.string.jingniu) + "|"
					+ getResources().getString(R.string.qingyang) + "|"
					+ getResources().getString(R.string.wuhou) + "|"
					+ getResources().getString(R.string.chenghua) + "|"
					+ getResources().getString(R.string.jinjiang) + "|"
					+ getResources().getString(R.string.chengdu) + "|"
					+ getResources().getString(R.string.sichuan);
			java.util.regex.Pattern patternOther = java.util.regex.Pattern
					.compile(regExOther);
			java.util.regex.Matcher matcherOther = patternOther
					.matcher(dvbChannel.getChannelName());
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
		List<ChannelInfo> curChannels = null;
		switch (curType) {
		case 0:
			epgListTitleView.setText(TVtype[0]);
			curChannels = channelsAll;
			break;
		case 1:
			epgListTitleView.setText(TVtype[1]);
			curChannels = CCTVList;
			break;
		case 2:
			epgListTitleView.setText(TVtype[2]);
			curChannels = starTvList;
			break;
		case 3:
			epgListTitleView.setText(TVtype[3]);
			curChannels = localTvList;
			break;
		case 4:
			epgListTitleView.setText(TVtype[4]);
			curChannels = HDTvList;
			break;
		case 5:
			epgListTitleView.setText(TVtype[5]);
			curChannels = favTvList;
			break;
		case 6:
			epgListTitleView.setText(TVtype[6]);
			curChannels = otherTvList;
			break;
		}
		mCurChannels = curChannels;
		chLstAdapter.setData(mCurChannels);
		chListView.setSelection(0);
		if (mCurChannels.size() <= 0) {
			focusView.setVisibility(View.INVISIBLE);
		}
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		String dialogButtonTextOk = MainActivity.this
				.getString(R.string.str_zhn_yes);
		String dialogButtonTextCancel = MainActivity.this
				.getString(R.string.str_zhn_no);

		channelListLinear.setVisibility(View.VISIBLE);
		focusView.setVisibility(View.VISIBLE);
		linear_vertical_line.setVisibility(View.VISIBLE);
		chListView.setSelection(curListIndex);
		mhandler.removeCallbacks(runnable);
		mhandler.postDelayed(runnable, 5000);
		switch (keyCode) {
		case Class_Constant.KEYCODE_RIGHT_ARROW_KEY:
			//切换频道类型，更新频道列表的数据
//			if (curType == 6) {
//				curType = 0;
//			} else {
//				curType++;
//			}
//			showChannelList();
//			chListView.setFocusable(true);
//			chListView.requestFocus();
//			chListView.setSelection(curListIndex);
			break;
		case Class_Constant.KEYCODE_LEFT_ARROW_KEY:
			//切换频道类型，更新频道列表的数据
//			if (curType == 0) {
//				curType = 6;
//			} else {
//				curType--;
//			}
//			showChannelList();
			
			break;

		case Class_Constant.KEYCODE_PAGE_UP:
//			if (curListIndex == 0) {
//				showChannelList();
//				chListView.setSelection(chListView.getCount() - 1);
//			}
			break;
		case Class_Constant.KEYCODE_PAGE_DOWN:
//			if (curListIndex == (chListView.getCount() - 1)) {
//				showChannelList();
//				chListView.setSelection(0);
//			}
			break;
		case Class_Constant.KEYCODE_CHANNEL_UP:
			if (curListIndex == (chListView.getCount() - 1)) {
				chListView.setSelection(0);
				curListIndex = 0;
			} else {
				chListView.setSelection(curListIndex + 1);
				curListIndex = curListIndex + 1;
			}
			if (!StringUtils.hasLength(curChannelNO)) {
				//第一次进入时
				chListView.setSelection(0);
				curChannelNO=mCurChannels.get(0).getChannelNumber();
				if (mCurChannels != null && mCurChannels.size() != 0) {
					playChannel(curChannelNO, true);
				}
			} else {
				chListView.setFocusable(true);
				chListView.requestFocus();
				chListView.setSelection(curListIndex);
//				if (mCurChannels != null && mCurChannels.size() != 0) {
//					playChannel(mCurChannels.get(curListIndex)
//							.getChannelNumber(), true);
//				}
			}
			break;
		case Class_Constant.KEYCODE_CHANNEL_DOWN:
			if (curListIndex == 0) {
				chListView.setSelection(chListView.getCount() - 1);
				curListIndex = chListView.getCount() - 1;
			} else {
				chListView.setSelection(curListIndex - 1);
				curListIndex = curListIndex - 1;
			}
			if (!StringUtils.hasLength(curChannelNO)) {
				curChannelNO = mCurChannels.get(0).getChannelNumber();
			}
			chListView.setFocusable(true);
			chListView.requestFocus();
			chListView.setSelection(curListIndex);
//			if (mCurChannels != null && mCurChannels.size() != 0) {
//				playChannel(mCurChannels.get(curListIndex)
//						.getChannelNumber(), true);
//			}
			break;
			
		case Class_Constant.KEYCODE_OK_KEY:
			liveSeekBar.setVisibility(View.VISIBLE);
			break;
		case Class_Constant.KEYCODE_UP_ARROW_KEY:
			chListView.setFocusable(true);
			chListView.requestFocus();
			break;
			
		case Class_Constant.KEYCODE_DOWN_ARROW_KEY:
			chListView.setFocusable(true);
			chListView.requestFocus();
			
			break;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void leftReverse(){
		
	} 
	
	private void rightForward(){
		
	}
	
	//第一次快退的时候获取播放串
	private void requestPlayURL(){
		ChannelInfo channel=(ChannelInfo) CacheData.getAllChannelMap().get(curChannelNO);
		
		String url=processData.getReplayPlayUrlString(channel, curProgram, 0);
		PlayVideo.getInstance().getProgramPlayURL(mhandler, url);
		
	}

	// // ============show banner=========================================
	public void showBanner(String channelno, ProgramInfo pgmInfo) {
		ChannelInfo curChannel = (ChannelInfo) CacheData.allChannelMap
				.get(channelno);
		Banner ban = new Banner(this, curChannel, pgmInfo);
		ban.show();
	}

	// ============play video=========================================
	public String playChannel(String channelno, boolean isCheckPlaying) {

		if (channelno.equals(curChannelNO) && isCheckPlaying) {
			return channelno;
		}

		ChannelInfo curChannel = (ChannelInfo) CacheData.allChannelMap
				.get(channelno);
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

		PlayVideo.getInstance().playLiveProgram(videoView, curChannel);
		PlayVideo.getInstance().getProgramInfo(mhandler, curChannel);


		// 显示banner信息
		CacheData.curChannelNum = curChannel.getChannelNumber();
		curChannelNO = channelno;
		// CacheData.curChannelNum = channelId;

		// Banner ban = new Banner(this, curChannel);
		// ban.show();

		return curChannelNO;
	}

	// =============onKeydown timer====================================
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub

			channelListLinear.setVisibility(View.INVISIBLE);
			focusView.setVisibility(View.INVISIBLE);
			linear_vertical_line.setVisibility(View.INVISIBLE);
			liveSeekBar.setVisibility(View.INVISIBLE);
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

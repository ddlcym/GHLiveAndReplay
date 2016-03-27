package com.changhong.ghlive.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.changhong.ghlive.datafactory.Banner;
import com.changhong.ghlive.datafactory.ChannelListAdapter;
import com.changhong.ghlive.datafactory.HandleLiveData;
import com.changhong.ghlive.service.HttpService;
import com.changhong.ghliveandreplay.R;

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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends BaseActivity {

	private String TAG = "mmmm";

	// test
	private Button btnTest;
	// view
	private ImageView focusView; // foucus image
	private TextView epgListTitleView;// chanellist title
	private String[] TVtype;// all tv type
	private VideoView videoView;
	private LinearLayout channelListLinear;// channellist layout
	private LinearLayout linear_vertical_line;// straight line right of
												// channellist layout
	private ListView chListView;

	// private View curView;
	private boolean lockSwap = false;

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

	Map<String, String> pgmContent = new HashMap<String, String>();

	private int curListIndex = 0;
	private int new_ChanId;
	private int old_chanId;
	private int curType = 0;
	private int curId = 0;
	private boolean shelterListView = false;
	private int replayProgramId = 0;

	private ChannelListAdapter chLstAdapter;
	private Handler mhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String content = null;
			switch (msg.what) {
			case Class_Constant.PLAY_LIVE:// 直播
				// content = (String) msg.obj;
				// Log.i(TAG, "playURL:" + content);
				// videoView.setVideoPath(content);
				// videoView.start();
				break;
			case Class_Constant.BANNER_PROGRAM_PASS:
				pgmContent = (Map<String, String>) msg.obj;
				// Log.i("zyt", "pgmcontent" + pgmContent.get("name"));
				// Log.i("zyt", "pgmcontent" + pgmContent.get("playTime"));

				ProgramInfo innerPgmInfo = new ProgramInfo();
				innerPgmInfo.setEventName(pgmContent.get("name"));

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

				try {
					innerPgmInfo.setBeginTime(sdf.parse(pgmContent.get("playTime")));

				} catch (java.text.ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// Log.i("zyt map time ", "program playTime Date:" +
				// pgmContent.get("playTime"));
				// Log.i("zyt map time ", "program id is 当前 id:" +
				// pgmContent.get("id")); // 节目信息中的节目id
				replayProgramId = Integer.parseInt(pgmContent.get("id"));
				PlayVideo.getInstance().getProgramInfoDetail(mhandler, replayProgramId);
				showBanner(curId, innerPgmInfo);
				break;

			case Class_Constant.REPLAY_TIME_LENGTH:
				ProgramInfo pgmInfoDetail = (ProgramInfo) msg.obj;
				// Log.i("zyt", "传递handler之后的节目详情 + name " +
				// pgmInfoDetail.getChannelName());
				// Log.i("zyt", "传递handler之后的节目详情 + pgmId " +
				// pgmInfoDetail.getProgramId());

				// Log.i("zyt", "传递handler之后的节目详情 + beginTime " +
				// pgmInfoDetail.getBeginTime());
				// Log.i("zyt", "传递handler之后的节目详情 + endTime " +
				// pgmInfoDetail.getEndTime());
				// Log.i("zyt", "传递handler之后的节目详情 + endTime " +
				// pgmInfoDetail.getChannelID());

				SimpleDateFormat sdfNew = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

				// ProgramInfo passPgmInfo = new ProgramInfo();

				// passPgmInfo.setBeginTime((sdfNew.parse());
				// Intent mIntent = new Intent(this, ObjectTranDemo1.class);

				java.text.DateFormat format1 = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				String formatBeginTime = format1.format(pgmInfoDetail.getBeginTime());
				String formatEndTime = format1.format(pgmInfoDetail.getEndTime());

				Bundle mBundle = new Bundle();
				String[] value = { String.valueOf(pgmInfoDetail.getChannelID()), formatBeginTime, formatEndTime };
				mBundle.putStringArray("pgmInfo", value);
				Intent rplayAct = new Intent(MainActivity.this, ReplayPlayActivity.class);

				// Log.i("zyt", "传递handler之后的节目详情 + endTime this is fourth " +
				// s);
				// rplayAct.put
				// mBundle.putStringArray("pgmInfo", value);
				// mBundle.putSerializable("pgmInfo", pgmInfoDetail);
				rplayAct.putExtras(mBundle);
				// PlayVideo.getInstance().getProgramInfoDetail(mhandler,
				// replayProgramId);

				// Intent rplayAct = new Intent(MainActivity.this,
				// ReplayPlayActivity.class);

//				startActivity(rplayAct);

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
		startHttpSer();
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
		btnTest = (Button) findViewById(R.id.testBtn);

		// videoView.setMediaController(new MediaController(this));
		videoView.setFocusable(false);
		chListView.setFocusable(true);

		chLstAdapter = new ChannelListAdapter(MainActivity.this);
		chListView.setAdapter(chLstAdapter);
		chListView.setOnItemClickListener(myClickLis);
		chListView.setOnItemSelectedListener(myItemSelectLis);

		btnTest.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent rplayAct = new Intent(MainActivity.this, ReplayPlayActivity.class);
				startActivity(rplayAct);
				// Toast.makeText(MainActivity.this, " test ",
				// Toast.LENGTH_LONG).show();
			}
		});

	}

	private void startHttpSer() {
		Intent intent = new Intent(this, HttpService.class);
		startService(intent);
	}

	private OnItemSelectedListener myItemSelectLis = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			// TODO show the select channel
			curListIndex = position;
			// int[] pos = { -1, -1 };
			if (view != null) {
				// view.getLocationOnScreen(pos);
				dislistfocus((FrameLayout) view);

				TextView channelIndex = (TextView) view.findViewById(R.id.chanId);
				int index = Integer.parseInt(channelIndex.getText().toString());
				// curView = view;
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
			int index = Integer.parseInt(channelIndex.getText().toString());
			Log.i(TAG, String.valueOf(index));
			playChannel(index, true);

			curId = index;
			Log.i("zyt", "play channel number is " + curId);

		}

	};

	private void getChannelList() {
		// 传入URL请求链接
		String URL = processData.getChannelList();
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
				new Response.Listener<org.json.JSONObject>() {

					@Override
					public void onResponse(org.json.JSONObject arg0) {
						// TODO Auto-generated method stub
						// 相应成功
						// Log.i(TAG, "HttpService=channle:" + arg0);
						channelsAll = HandleLiveData.getInstance().dealChannelJson(arg0);
						// first set adapter
						curType = 0;
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

	private void getAllTVtype(int index) {
		// fill all type tv
		// Channel[] Channels =
		// objApplication.dvbDatabase.getChannelsAllSC();//Only get channels
		// type=1(TV)
		// clear all tv type;
		switch (index) {
		case 0:
			break;

		case 1:
			CCTVList.clear();
			String regExCCTV;
			regExCCTV = getResources().getString(R.string.zhongyang);
			java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("CCTV|" + regExCCTV);
			for (ChannelInfo Channel : channelsAll) {
				java.util.regex.Matcher matcher = pattern.matcher(Channel.getChannelName());
				boolean classBytype = matcher.find();
				if (classBytype) {
					CCTVList.add(Channel);
				}
			}
			break;
		case 2:
			starTvList.clear();
			String regExStar;
			regExStar = getResources().getString(R.string.weishi);
			java.util.regex.Pattern patternStar = java.util.regex.Pattern.compile(".*" + regExStar + "$");
			for (ChannelInfo Channel : channelsAll) {
				java.util.regex.Matcher matcherStar = patternStar.matcher(Channel.getChannelName());
				boolean classBytypeStar = matcherStar.matches();
				if (classBytypeStar) {
					starTvList.add(Channel);
				}
			}
			break;
		case 3:
			localTvList.clear();
			String regExLocal = "CDTV|SCTV|" + getResources().getString(R.string.rongcheng) + "|"
					+ getResources().getString(R.string.jingniu) + "|" + getResources().getString(R.string.qingyang)
					+ "|" + getResources().getString(R.string.wuhou) + "|" + getResources().getString(R.string.chenghua)
					+ "|" + getResources().getString(R.string.jinjiang) + "|"
					+ getResources().getString(R.string.chengdu) + "|" + getResources().getString(R.string.sichuan);
			java.util.regex.Pattern patternLocal = java.util.regex.Pattern.compile(regExLocal);
			for (ChannelInfo Channel : channelsAll) {
				java.util.regex.Matcher matcherLocal = patternLocal.matcher(Channel.getChannelName());
				boolean classBytypeLocal = matcherLocal.find();
				if (classBytypeLocal) {
					localTvList.add(Channel);
				}
			}
			break;
		case 4:
			HDTvList.clear();
			String regExHD = getResources().getString(R.string.hd_dtv) + "|"
					+ getResources().getString(R.string.xinyuan_hdtv1) + "|"
					+ getResources().getString(R.string.xinyuan_hdtv2) + "|"
					+ getResources().getString(R.string.xinyuan_hdtv3) + "|"
					+ getResources().getString(R.string.xinyuan_hdtv4);
			java.util.regex.Pattern patternHD = java.util.regex.Pattern.compile("3D|" + regExHD + "|.*HD$");
			for (ChannelInfo Channel : channelsAll) {
				java.util.regex.Matcher matcherHD = patternHD.matcher(Channel.getChannelName());
				boolean classBytypeHD = matcherHD.find();
				if (classBytypeHD) {
					HDTvList.add(Channel);
				}
			}
			break;
		case 5:
			// favTvList.clear();
			// for (ChannelInfo Channel : Channels) {
			// if (Channel.favorite == 1) {
			// favTvList.add(Channel);
			// }
			//
			// }
			break;
		case 6:
			otherTvList.clear();
			String regExOther = "CDTV|SCTV|CCTV|" + getResources().getString(R.string.weishi) + "|"
					+ getResources().getString(R.string.rongcheng) + "|" + getResources().getString(R.string.jingniu)
					+ "|" + getResources().getString(R.string.qingyang) + "|" + getResources().getString(R.string.wuhou)
					+ "|" + getResources().getString(R.string.chenghua) + "|"
					+ getResources().getString(R.string.jinjiang) + "|" + getResources().getString(R.string.chengdu)
					+ "|" + getResources().getString(R.string.sichuan);
			java.util.regex.Pattern patternOther = java.util.regex.Pattern.compile(regExOther);
			for (ChannelInfo Channel : channelsAll) {
				java.util.regex.Matcher matcherOther = patternOther.matcher(Channel.getChannelName());
				boolean classBytypeOther = matcherOther.find();
				if (!classBytypeOther) {
					otherTvList.add(Channel);
				}
			}
			break;
		}

	}

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
		if (mCurChannels.size() <= 0) {
			focusView.setVisibility(View.INVISIBLE);
		}
	}

	// 获取下一个频道的频道信息
	private ChannelInfo toNextChannel(int curType, ChannelInfo preChannel) {
		List<ChannelInfo> channels = null;
		switch (curType) {
		case 0:
			channels = channelsAll;
			break;

		case 1:
			channels = CCTVList;
			break;
		case 2:
			channels = starTvList;
			break;
		case 3:
			channels = localTvList;
			break;
		case 4:
			channels = HDTvList;
			break;
		case 5:
			channels = favTvList;
			break;
		case 6:
			channels = otherTvList;
			break;
		default:
			break;
		}
		ChannelInfo curChannel = null;
		if (channels != null && channels.size() > 0) {
			if (channels.size() == 1) {
				return curChannel;
			}
			for (int i = 0; i < channels.size(); i++) {

				if (channels.get(i).getChannelNumber() == preChannel.getChannelNumber()) {

					if (i == channels.size() - 1) {
						curChannel = channels.get(i - 1);
					} else {
						curChannel = channels.get(i + 1);
					}
					break;

				}
			}
		}
		return curChannel;
	}

	private void showTime() {
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		int focusLocation = 0;// 频道加减时选中的频道
		int playIndex = 0;
		ChannelInfo channel;
		TextView chanView;
		String dialogButtonTextOk = MainActivity.this.getString(R.string.str_zhn_yes);
		String dialogButtonTextCancel = MainActivity.this.getString(R.string.str_zhn_no);
		// String dialogSkipTitle =
		// MainActivity.this.getString(R.string.str_zhn_skiptitle);
		// String dialogSkipMess =
		// MainActivity.this.getString(R.string.str_zhn_skipmessage);

		channelListLinear.setVisibility(View.VISIBLE);
		focusView.setVisibility(View.VISIBLE);
		linear_vertical_line.setVisibility(View.VISIBLE);
		if (shelterListView) {
			mhandler.removeCallbacks(runnable);
		}
		mhandler.postDelayed(runnable, 8000);
		switch (keyCode) {
		case Class_Constant.KEYCODE_RIGHT_ARROW_KEY:

			if (curType == 6) {
				curType = 0;
			} else {
				curType++;
			}
			getAllTVtype(curType);
			showChannelList();
			break;
		case Class_Constant.KEYCODE_LEFT_ARROW_KEY:

			if (curType == 0) {
				curType = 6;
			} else {
				curType--;
			}
			getAllTVtype(curType);
			showChannelList();
			break;
		case Class_Constant.KEYCODE_UP_ARROW_KEY:
		case Class_Constant.KEYCODE_PAGE_UP:
			if (curListIndex == 0) {
				getAllTVtype(curType);
				showChannelList();
				chListView.setSelection(chListView.getCount() - 1);
			}
			break;
		case Class_Constant.KEYCODE_DOWN_ARROW_KEY:
		case Class_Constant.KEYCODE_PAGE_DOWN:
			if (curListIndex == (chListView.getCount() - 1)) {
				getAllTVtype(curType);
				showChannelList();
				chListView.setSelection(0);
			}
			break;
		case Class_Constant.KEYCODE_CHANNEL_UP:
			getAllTVtype(curType);
			showChannelList();
			if (curListIndex == (chListView.getCount() - 1)) {
				chListView.setSelection(0);
				focusLocation = 0;
			} else {
				chListView.setSelection(curListIndex + 1);
				focusLocation = curListIndex + 1;
			}

			ChannelInfo chanPlus = (ChannelInfo) CacheData.allChannelMap.get(String.valueOf(curId));
			int iPlus = mCurChannels.indexOf(chanPlus);
			playChannel(Integer.parseInt(mCurChannels.get(iPlus + 1).getChannelNumber()), true);
			// Log.i("zyt", "location is + " + i);

			break;
		case Class_Constant.KEYCODE_CHANNEL_DOWN:
			getAllTVtype(curType);
			showChannelList();
			if (curListIndex == 0) {
				chListView.setSelection(chListView.getCount() - 1);
				focusLocation = chListView.getCount() - 1;
			} else {
				chListView.setSelection(curListIndex - 1);
				focusLocation = curListIndex - 1;
			}
			// playIndex =
			// Integer.parseInt(mCurChannels.get(focusLocation).getChannelNumber());
			// curId = focusLocation;
			// playChannel(focusLocation, true);

			ChannelInfo chanMinu = (ChannelInfo) CacheData.allChannelMap.get(String.valueOf(curId));
			int iMinu = mCurChannels.indexOf(chanMinu);
			playChannel(Integer.parseInt(mCurChannels.get(iMinu - 1).getChannelNumber()), true);

			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	// // ============show banner=========================================
	public void showBanner(int channelId, ProgramInfo pgmInfo) {
		ChannelInfo curChannel = (ChannelInfo) CacheData.allChannelMap.get(String.valueOf(channelId));
		Banner ban = new Banner(this, curChannel, pgmInfo);
		ban.show();
	}

	// ============play video=========================================
	public int playChannel(int channelId, boolean isCheckPlaying) {

		if (channelId == curId && isCheckPlaying) {
			return channelId;
		}

		ChannelInfo curChannel = (ChannelInfo) CacheData.allChannelMap.get(String.valueOf(channelId));
		if (curChannel == null) {
			return -1;
		}

		videoView.stopPlayback();

		/*--------------- If it is audio channel, blank the screen------------- */
		// if (curChannel.sortId == 2 || curChannel.videoPid == 0x0
		// || curChannel.videoPid == 0x1fff) {
		// dvbPlayer.blank();
		// showAudioPlaying(true);
		// } else {
		// showAudioPlaying(false);
		// }

		// PlayVideo.getInstance().playLiveProgram(videoView, curChannel);
		// //live program
		// String replayUrl =
		// "http://ott.yun.gehua.net.cn:8080/msis/getPlayURL?version=V002&resourceCode=8406&providerID=gehua&assetID=8406&resolution=1280*768&playType=4&terminalType=4&shifttime=1459019"
		// +
		// "820000&shiftend=1459023000000&authKey=c7e278212b81aff1992ac5e0017757d7";

		String replayUrl = "http://ott.yun.gehua.net.cn:8080/msis/getPlayURL?version=V002&resourceCode=8406&providerID=gehua&assetID=8406&resolution=1280*768&playType=4&terminalType=4&shifttime=1459038060000&shiftend=1459040640000&delay=200000&authKey=c7e278212b81aff1992ac5e0017757d7";
		String replayUrl1 = "http://ott.yun.gehua.net.cn:8080/msis/getPlayURL?version=V002&resourceCode=8245&providerID=gehua&assetID=8245&resolution=1280*768&playType=4&terminalType=4&shifttime=1459037700000&shiftend=1459043340000&delay=200000&authKey=c7e278212b81aff1992ac5e0017757d7";

		PlayVideo.getInstance().playReplayProgram(videoView, replayUrl1);
		// 获取回看播放串，进行播放
		PlayVideo.getInstance().getProgramInfo(mhandler, curChannel);// 显示banner信息
		CacheData.curChannelNum = curChannel.getChannelNumber();

		curId = channelId;

		return 0;
	}

	// =============onKeydown timer====================================
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			shelterListView = true;

			channelListLinear.setVisibility(View.INVISIBLE);
			focusView.setVisibility(View.INVISIBLE);
			linear_vertical_line.setVisibility(View.INVISIBLE);
			mhandler.postDelayed(this, 8000);
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

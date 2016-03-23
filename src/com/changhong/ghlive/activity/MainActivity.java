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
import com.changhong.gehua.common.Class_Constant;
import com.changhong.gehua.common.PlayVideo;
import com.changhong.gehua.common.ProcessData;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends BaseActivity {

	private String TAG = "mmmm";

	// view
	private ImageView focusView; // foucus image
	private TextView epgListTitleView;// chanellist title
	private String[] TVtype;// all tv type
	private VideoView videoView;
	private LinearLayout channelListLinear;// channellist layout

	// private View curView;
	private boolean lockSwap = false;

	private VolleyTool volleyTool;
	private RequestQueue mReQueue;

	private ProcessData processData;

	// kinds of channel list
	private List<ChannelInfo> mCurChannels= new ArrayList<ChannelInfo>();//当前频道列表清单
	private List<ChannelInfo> channelsAll = new ArrayList<ChannelInfo>();
	private List<ChannelInfo> CCTVList = new ArrayList<ChannelInfo>();
	private List<ChannelInfo> starTvList = new ArrayList<ChannelInfo>();
	private List<ChannelInfo> favTvList = new ArrayList<ChannelInfo>();
	private List<ChannelInfo> localTvList = new ArrayList<ChannelInfo>();
	private List<ChannelInfo> HDTvList = new ArrayList<ChannelInfo>();
	private List<ChannelInfo> otherTvList = new ArrayList<ChannelInfo>();
	

	private ListView chListView;
	private int curListIndex = 0;
	private int new_ChanId;
	private int old_chanId;
	private int curType = 0;

	private ChannelListAdapter chLstAdapter;
	private Handler mhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String content = null;
			switch (msg.what) {
			case Class_Constant.PLAY_LIVE:// 直播
//				content = (String) msg.obj;
//				Log.i(TAG, "playURL:" + content);
//				videoView.setVideoPath(content);
//				videoView.start();
				break;
			case 2:

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

		// videoView.setMediaController(new MediaController(this));
		videoView.setFocusable(false);

		chLstAdapter = new ChannelListAdapter(MainActivity.this);
		Log.i("mmmm", "chListView" + chListView);
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
			//// getAllTVtype(curType);
			//// showChannelList();
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
						// setadapter
						chLstAdapter.setData(channelsAll);
						Log.i(TAG, "HttpService=channelsAll:" + channelsAll.size());
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
			Log.i(TAG, "HttpService=error：" + arg0);
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

	private void showTime() {
	}

	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// // TODO Auto-generated method stub
	// ChannelInfo channel;
	// TextView chanView;
	// String dialogButtonTextOk = MainActivity.this
	// .getString(R.string.str_zhn_yes);
	// String dialogButtonTextCancel = MainActivity.this
	// .getString(R.string.str_zhn_no);
	// String dialogSkipTitle = MainActivity.this
	// .getString(R.string.str_zhn_skiptitle);
	// String dialogSkipMess = MainActivity.this
	// .getString(R.string.str_zhn_skipmessage);
	// switch (keyCode) {
	// case KeyEvent.KEYCODE_F4: {
	// // 交换节目排序
	// chanView = (TextView) curView.findViewById(R.id.chanId);
	// Toast.makeText(MainActivity.this,
	// MainActivity.this.getString(R.string.str_swap),
	// Toast.LENGTH_LONG).show();
	// lockSwap = true;
	// old_chanId = Integer.parseInt(chanView.getText().toString());
	// break;
	// }
	//
	// case Class_Constant.KEYCODE_RIGHT_ARROW_KEY:
	//
	// if (curType == 6) {
	// curType = 0;
	// } else {
	// curType++;
	// }
	// getAllTVtype(curType);
	// showChannelList();
	// break;
	// case Class_Constant.KEYCODE_LEFT_ARROW_KEY:
	//
	// if (curType == 0) {
	// curType = 6;
	// } else {
	// curType--;
	// }
	// getAllTVtype(curType);
	// showChannelList();
	// break;
	// }
	// return super.onKeyDown(keyCode, event);
	// }

	// ============play video=========================================
	public int playChannel(int channelId, boolean isCheckPlaying) {

		if (channelId == CacheData.curChannelNum && isCheckPlaying) {
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

		PlayVideo.getInstance().playLiveProgram(videoView, curChannel);

		// mo_Ca.channelNotify(curChannel);

		CacheData.curChannelNum = channelId;

		Banner ban = new Banner(this, curChannel);
		ban.show();
		
		return 0;
	}

	// =============play video over====================================

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

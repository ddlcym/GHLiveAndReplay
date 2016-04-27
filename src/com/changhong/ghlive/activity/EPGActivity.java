package com.changhong.ghlive.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.changhong.gehua.common.CacheData;
import com.changhong.gehua.common.ChannelInfo;
import com.changhong.gehua.common.Class_Constant;
import com.changhong.gehua.common.ProcessData;
import com.changhong.gehua.common.ProgramInfo;
import com.changhong.gehua.common.VolleyTool;
import com.changhong.ghlive.datafactory.HandleLiveData;
import com.changhong.ghlive.service.HttpService;
import com.changhong.ghliveandreplay.R;
import com.changhong.replay.datafactory.ChannelRepListAdapter;
import com.changhong.replay.datafactory.DayMonthAdapter;
import com.changhong.replay.datafactory.EpgListview;
import com.changhong.replay.datafactory.ProgramsAdapter;
import com.changhong.replay.datafactory.ResolveEPGInfoThread;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EPGActivity extends BaseActivity {

	private boolean channelListFoucus = false;
	private static LinearLayout channelCurSelect = null;
	private static LinearLayout channelLastSelect = null;
	private static EpgListview channelListview;
	private static EpgListview epgEventListview;
	private static LinearLayout channelListLinear;
	private static GridView epgWeekInfoView;
	private static TextView chanListTitleButton;
	private ImageView focusView;

	private LinearLayout EventLastSelect = null;
	private LinearLayout EventCurSelect = null;

	private static int curType = 0;
	private static boolean firsIn = true;
	boolean bEventFocus = false;

	private VolleyTool volleyTool;
	private RequestQueue mReQueue;
	private ProcessData processData;

	private static final int MSG_NO_CHANNEL = 0x000;
	private static final int MSG_TITLE_FOCUSABLE = 0x001;
	private static final int MSG_SHOW_CHANNELLIST = 0x101;
	public static final int MSG_CHANNEL_CHANGE = 0x102;
	public static final int MSG_WEEKDAY_CHANGE = 0x202;
	public static final int MSG_SHOW_WEEKDAY = 0x201;
	public static final int MSG_BOOK_NEW = 0x301;
	private static final String TAG = "mmmm";

	private static String curChannelNum;
	private int channelCount;
	private ChannelRepListAdapter channelAdapter;
	private DayMonthAdapter dayMonthAdapter;
	private ProgramsAdapter programsAdapter;
	// EpgEvent 控制
	private static int EventlitItemindex = 0;

	private static String[] tvType;
	// WeekInfo 控制
	// List<Map<String, Object>> SimpleAdapterWeekdata = null;
	private static String curDay = null;
	private static ChannelInfo curChannel;
	private ResolveEPGInfoThread resolveProJsonThread = null;

	// all type channel
	List<ChannelInfo> allTvList = new ArrayList<ChannelInfo>();
	List<ChannelInfo> CCTVList = new ArrayList<ChannelInfo>();
	List<ChannelInfo> starTvList = new ArrayList<ChannelInfo>();
	List<ChannelInfo> favTvList = new ArrayList<ChannelInfo>();
	List<ChannelInfo> localTvList = new ArrayList<ChannelInfo>();
	List<ChannelInfo> HDTvList = new ArrayList<ChannelInfo>();
	List<ChannelInfo> otherTvList = new ArrayList<ChannelInfo>();

	List<ChannelInfo> curChannelList = new ArrayList<ChannelInfo>();
	List<ProgramInfo> curProgramList = new ArrayList<ProgramInfo>();
	private Handler uiHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case MSG_NO_CHANNEL:
				break;
			case MSG_TITLE_FOCUSABLE:
				// channelListLinear.setFocusable(true);
				// channelListLinear.setFocusableInTouchMode(true);
				// chanListTitleButton.setFocusable(true);
				// chanListTitleButton.setFocusableInTouchMode(true);
				// epgWeekInfoView.setFocusable(true);
				// epgWeekInfoView.setFocusableInTouchMode(true);
				// epgWeekInfoView.setClickable(true);
				break;
			case MSG_SHOW_CHANNELLIST:
				showChannelList(curType);
				if (firsIn) {
					// epg.ChannelClassifyScale(channelListLinear, true);
					channelListview.setSelection(getIndexInList(curChannelNum, curChannelList));
					// epg.channelListview.setSelection(curChannelNum-1);
					// this.sendEmptyMessageDelayed(MSG_TITLE_FOCUSABLE, 200);
					firsIn = false;
				}
				break;
			case MSG_CHANNEL_CHANGE:

				EpgEventListRefresh(curDay);
				break;
			case MSG_SHOW_WEEKDAY:

				curDay = CacheData.getDayMonths().get(0);
				CacheData.setReplayCurDay(curDay);
				EventlitItemindex = 0;
				showWeekDay();
				// epg.getEpgEventData(curChannelNum, curDayIndex);
				EpgEventListRefresh(curDay);
				break;
			case MSG_WEEKDAY_CHANGE:
				EpgEventListRefresh(curDay);
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		// startHttpSer();
		super.onCreate(savedInstanceState);
	}

	private void startHttpSer() {
		Intent intent = new Intent(this, HttpService.class);
		startService(intent);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.epg);

		channelListLinear = (LinearLayout) findViewById(R.id.epg_chan_classifylayout);
		focusView = (ImageView) findViewById(R.id.set_repfocus_id);

		chanListTitleButton = (TextView) findViewById(R.id.epg_chanlistTitle);
		// chanListTitleButton.setOnKeyListener(epg_Listener_Classify_OnKey);
		chanListTitleButton.setOnFocusChangeListener(epg_Listener_Classify_OnfocusChange);
		chanListTitleButton.requestFocus();

		channelListview = (EpgListview) findViewById(R.id.ChanlIstView);
		channelListview.setFocusable(true);
		channelListview.setFocusableInTouchMode(true);
		channelListview.setOnFocusChangeListener(ChanListOnfocusChange);
		channelListview.setOnItemSelectedListener(ChanListOnItemSelected);
		channelListview.setOnItemClickListener(ChanListOnItemClick);

		epgWeekInfoView = (GridView) findViewById(R.id.EpgWeekInfo);
		epgWeekInfoView.setOnItemSelectedListener(WeekInfoItemSelected);

		epgEventListview = (EpgListview) findViewById(R.id.EpgEventInfo);
		epgEventListview.setOnFocusChangeListener(epgEventChangeListener);
		epgEventListview.setOnItemSelectedListener(epgEventSelectedListener);
		epgEventListview.setOnItemClickListener(epgEventClickListener);

		channelAdapter = new ChannelRepListAdapter(EPGActivity.this);
		channelListview.setAdapter(channelAdapter);
		dayMonthAdapter = new DayMonthAdapter(EPGActivity.this);
		epgWeekInfoView.setAdapter(dayMonthAdapter);
		programsAdapter = new ProgramsAdapter(EPGActivity.this);
		epgEventListview.setAdapter(programsAdapter);
		epgWeekInfoView.setFocusable(false);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

		tvType = getResources().getStringArray(R.array.tvtype);
		firsIn = true;
		volleyTool = VolleyTool.getInstance();
		mReQueue = volleyTool.getRequestQueue();
		if (null == processData) {
			processData = new ProcessData();
		}
		channelCount = CacheData.getAllChannelInfo().size();
		if (channelCount <= 0) {
			Log.i("mmmm", "无节目！");
			// 无节目时的处理----------------------------------待完成-----------------------------
			getChannelList();
		} else {
			curType = 0;
			getAllTVtype();
			showChannelList();
			uiHandler.sendEmptyMessage(MSG_SHOW_CHANNELLIST);
		}
		curChannelNum = "1";

	}

	// private OnKeyListener epg_Listener_Classify_OnKey = new OnKeyListener() {
	//
	// @Override
	// public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
	// if (arg2.getAction() == KeyEvent.ACTION_DOWN) {
	// switch (arg1) {
	// case Class_Constant.KEYCODE_LEFT_ARROW_KEY:
	// if (curType > 0)
	// curType--;
	// else if (curType == 0) {
	// curType = 6;
	// }
	// uiHandler.sendEmptyMessage(MSG_SHOW_CHANNELLIST);
	// break;
	// case Class_Constant.KEYCODE_RIGHT_ARROW_KEY:
	// if (curType == 6)
	// curType = 0;
	// else if (curType >= 0) {
	// curType++;
	// }
	// uiHandler.sendEmptyMessage(MSG_SHOW_CHANNELLIST);
	// break;
	// default:
	// break;
	// }
	// }
	// return false;
	// }
	// };
	private OnFocusChangeListener epg_Listener_Classify_OnfocusChange = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View arg0, boolean arg1) {
			if (arg0 != null) {
				// ChannelClassifyScale(channelListLinear, arg1);
			}
		}
	};

	// 周一...周日改变时触发
	private OnItemSelectedListener WeekInfoItemSelected = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			if (CacheData.dayMonths.isEmpty())
				return;
			curDay = CacheData.dayMonths.get(arg2);
			CacheData.setReplayCurDay(curDay);
			uiHandler.sendEmptyMessage(MSG_WEEKDAY_CHANGE); // 更新下面的列表

		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}
	};

	private View.OnClickListener bookButtonClick = new View.OnClickListener() {

		@Override
		public void onClick(View arg0) {

			// 节目预订相关信息--------------待完成--------------
			// Intent mintent = new Intent(EPGActivity.this, EpgManage.class);
			// startActivity(mintent);
		}
	};

	private OnItemClickListener ChanListOnItemClick = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			switch (arg0.getId()) {
			case R.id.ChanlIstView:
				// Point point = new Point();
				// getWindowManager().getDefaultDisplay().getSize(point);
				// DVB_RectSize.Builder builder = DVB_RectSize.newBuilder()
				// .setX(0).setY(0).setW(point.x).setH(point.y);
				// objApplication.dvbPlayer.setSize(builder.build());
				// finish();
				break;

			default:
				break;
			}
		}
	};
	private OnItemSelectedListener ChanListOnItemSelected = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			if (arg1 == null) {
				return;
			}
			TextView channelIdText = (TextView) arg1.findViewById(R.id.channelId);
			curChannelNum = channelIdText.getText().toString();
			curChannel = (ChannelInfo) CacheData.getAllChannelMap().get(curChannelNum);

			getPointProList(curChannel);

			channelCurSelect = (LinearLayout) arg1.findViewById(R.id.epg_chan_itemlayout);
			if (channelListFoucus == false) {
				return;
			}

			if (channelLastSelect != null) {
				// ChannelItemScale(channelLastSelect, false, false);
				channelLastSelect = null;
			}

			if (channelCurSelect != null) {
				// ChannelItemScale(channelCurSelect, true, true);
				channelLastSelect = channelCurSelect;
			}
			// dislistfocus((ViewGroup)arg1);

		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}
	};

	private OnFocusChangeListener ChanListOnfocusChange = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View arg0, boolean arg1) {
			channelListFoucus = arg1;

			if (arg0 != null) {
				if (channelCurSelect != null) {
					// ChannelItemScale(channelCurSelect, true,
					// channelListFoucus);
					channelLastSelect = channelListLinear;
				}
			}
		}
	};

	private OnFocusChangeListener epgEventChangeListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View arg0, boolean arg1) {

			bEventFocus = arg1;
			if (arg0.getId() == R.id.EpgEventInfo) {
				// if (true == arg1) {
				// bookButton.setFocusable(false);
				// bookButton.setFocusableInTouchMode(false);
				// } else if (false == arg1) {
				// bookButton.setFocusable(true);
				// bookButton.setFocusableInTouchMode(true);
				// }
			}
			if (arg0 != null) {
				if (EventCurSelect != null) {
					// epgEventItemScale(EventCurSelect, bEventFocus);
					EventLastSelect = EventCurSelect;
				}
			}
		}
	};

	private OnItemSelectedListener epgEventSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

			if (arg1 == null) {
				return;
			}

			EventCurSelect = (LinearLayout) arg1.findViewById(R.id.eventitem_Linearlayout);
			if (bEventFocus == false) {
				return;
			}

			if (EventLastSelect != null) {
				// epgEventItemScale(EventLastSelect, false);
				EventLastSelect = null;
			}

			if (EventCurSelect != null) {
				// epgEventItemScale(EventCurSelect, true);
				EventLastSelect = EventCurSelect;
				// EventlitItemindex = (int) arg3;
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}
	};

	private OnItemClickListener epgEventClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

			// ChannelInfo mChannel = (ChannelInfo) CacheData.getAllChannelMap()
			// .get(curChannelNum);
			ProgramInfo mEvent = (ProgramInfo) arg1.getTag();
			// 启动回看播放界面--------------------------------待完成---------------------------
			playChannel(arg2);
			// String startHour =
			// String.valueOf(mEvent.getBeginTime().getHours());
			// if (startHour.length() < 2)
			// startHour = "0" + startHour;
			// String startMinute = String.valueOf(mEvent.getBeginTime()
			// .getMinutes());
			// if (startMinute.length() < 2)
			// startMinute = "0" + startMinute;
			// Calendar c = Calendar.getInstance();
			// c.setTimeZone(TimeZone.getTimeZone("GMT+8"));
			// c.setTime(new Date());
			// c.add(Calendar.DAY_OF_MONTH, curDayIndex);
			// SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
			// String tmpDateName = dateFormat.format(c.getTime());
			// String startTime = startHour + ":" + startMinute;
			// if (curDayIndex == 0) {
			// int startH = mEvent.getBeginTime().getHours();
			// Date date = new Date();
			// int nowH = c.get(Calendar.HOUR_OF_DAY);
			// if (startH < nowH) {
			// Toast.makeText(context, R.string.time_out,
			// Toast.LENGTH_SHORT).show();
			// return;
			// }
			// int startM = Integer.parseInt(startMinute);
			// int nowM = c.get(Calendar.MINUTE);
			// if (startH == nowH && startM <= nowM) {
			// Toast.makeText(context, R.string.time_out,
			// Toast.LENGTH_SHORT).show();
			// return;
			// }
			// }

		}
	};

	public void EpgEventListRefresh(String curday) {
		// 根据当前保存的数据刷新EventList
		curProgramList = (List<ProgramInfo>) CacheData.getAllProgramMap().get(curday);
		programsAdapter.setData(curProgramList);
		// 记住上次的位置，如果是第一个日期则记住，其他日期否则都不记住
		View v = epgEventListview.getChildAt(0);
		int top = (v == null) ? 0 : v.getTop();
		epgEventListview.setSelectionFromTop(0, top);

	}

	public void showWeekDay() {
		epgWeekInfoView.setNumColumns(CacheData.getDayMonths().size());
		dayMonthAdapter.setData(CacheData.getDayMonths());

		// epgWeekInfoView.setSelection(EventlitItemindex);
	}

	public void showChannelList(int channelType) {

		switch (channelType) {
		case 0:
			curChannelList = allTvList;
			chanListTitleButton.setText(tvType[0]);
			break;
		case 1:
			curChannelList = CCTVList;
			chanListTitleButton.setText(tvType[1]);
			break;
		case 2:
			chanListTitleButton.setText(tvType[2]);
			curChannelList = starTvList;
			break;
		case 3:
			chanListTitleButton.setText(tvType[3]);
			curChannelList = localTvList;
			break;
		case 4:
			chanListTitleButton.setText(tvType[4]);
			curChannelList = HDTvList;
			break;
		case 5:
			chanListTitleButton.setText(tvType[5]);
			curChannelList = favTvList;
			break;
		case 6:
			chanListTitleButton.setText(tvType[6]);
			curChannelList = otherTvList;
			break;

		default:
			break;
		}
		channelAdapter.setData(curChannelList);
		// if (curChannelList.size() <= 0) {
		// focusView.setVisibility(View.INVISIBLE);
		// }
	}

	private void getAllTVtype() {
		// fill all type tv

		// clear all tv type;
		allTvList.clear();
		CCTVList.clear();
		starTvList.clear();
		favTvList.clear();
		localTvList.clear();
		HDTvList.clear();
		otherTvList.clear();
		List<ChannelInfo> channels = CacheData.allChannelInfo;
		for (ChannelInfo dvbChannel : channels) {
			allTvList.add(dvbChannel);

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
			String regExStar;
			regExStar = getResources().getString(R.string.weishi);
			java.util.regex.Pattern patternStar = java.util.regex.Pattern.compile(".*" + regExStar + "$");
			java.util.regex.Matcher matcherStar = patternStar.matcher(dvbChannel.getChannelName());
			boolean classBytypeStar = matcherStar.matches();
			if (classBytypeStar) {
				starTvList.add(dvbChannel);
			}
			String regExLocal = "CDTV|SCTV|" + getResources().getString(R.string.rongcheng) + "|"
					+ getResources().getString(R.string.jingniu) + "|" + getResources().getString(R.string.qingyang)
					+ "|" + getResources().getString(R.string.wuhou) + "|" + getResources().getString(R.string.chenghua)
					+ "|" + getResources().getString(R.string.jinjiang) + "|"
					+ getResources().getString(R.string.chengdu) + "|" + getResources().getString(R.string.sichuan);
			java.util.regex.Pattern patternLocal = java.util.regex.Pattern.compile(regExLocal);
			java.util.regex.Matcher matcherLocal = patternLocal.matcher(dvbChannel.getChannelName());
			boolean classBytypeLocal = matcherLocal.find();
			if (classBytypeLocal) {
				localTvList.add(dvbChannel);
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

	// private void epgEventItemScale(LinearLayout ItemLayout, boolean
	// IsScalebig) {
	// final Animation ani_EventScaleBig = AnimationUtils.loadAnimation(this,
	// R.anim.epg_eventitem_big);
	// final Animation ani_EventScaleSmall = AnimationUtils.loadAnimation(
	// this, R.anim.epg_eventitem_small);
	// RelativeLayout.LayoutParams FocusItemLayout = new
	// RelativeLayout.LayoutParams(
	// 10, 10);
	// TextView itembackTview = null;
	// Rect imgRect = new Rect();
	// if (ItemLayout == null) {
	// System.out
	// .println("epg_method_Event_itemScale invalid param(ItemLayout is
	// null)!");
	// return;
	// }
	//
	// itembackTview = (TextView) findViewById(R.id.item_SelectbackTview);
	// System.out.println("-----epg_method_Event_itemScale IsScalebig: "
	// + IsScalebig + "------");
	//
	// /* listview焦点变化处理 */
	// if (IsScalebig) {
	// ItemLayout.getGlobalVisibleRect(imgRect);
	//
	// FocusItemLayout.leftMargin = imgRect.left;
	// FocusItemLayout.topMargin = imgRect.top;
	// FocusItemLayout.width = imgRect.width();
	// FocusItemLayout.height = imgRect.height();
	//
	// itembackTview.setBackgroundResource(R.drawable.eventselect);
	// itembackTview.setLayoutParams(FocusItemLayout);
	// itembackTview.startAnimation(ani_EventScaleBig);
	// itembackTview.setVisibility(View.VISIBLE);
	// } else {
	// itembackTview.setVisibility(View.INVISIBLE);
	// itembackTview.startAnimation(ani_EventScaleSmall);
	// itembackTview.clearAnimation();
	// }
	// }

	private String getStartEndTime(int startHour, int startMinute, int endHour, int endMinute) {
		String formatString = "  :   ~   :  ";
		String startTimeString = timeFormat(startHour, startMinute);
		String endTimeString = timeFormat(endHour, endMinute);
		formatString = startTimeString + " ~ " + endTimeString;
		return formatString;
	}

	private String timeFormat(int hour, int minute) {
		String startHourString = String.valueOf(hour);
		String startMinuteString = String.valueOf(minute);
		if (startHourString.length() < 2)
			startHourString = "0" + startHourString;
		if (startMinuteString.length() < 2)
			startMinuteString = "0" + startMinuteString;
		return startHourString + ":" + startMinuteString;
	}

	// private List<Map<String, Object>> GetWeekDate() {
	// List<Map<String, Object>> WeekDateInfo = new ArrayList<Map<String,
	// Object>>();
	// Map<String, Object> item = new HashMap<String, Object>();
	// List<String> dayList = CacheData.getDayMonth();

	// String tmpDayName = null;
	// String tmpDateName = null;
	// int curDayWeek = 0;
	// int tmpDayWeek = 0;
	//
	// Calendar c = Calendar.getInstance();
	// c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
	// curDayWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
	//
	// tmpDayWeek = curDayWeek;
	// for (int i = 0; i < 7; i++) {
	// c.setTime(new Date());
	// c.add(Calendar.DAY_OF_MONTH, i);
	// SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
	// tmpDateName = dateFormat.format(c.getTime());
	// item = new HashMap<String, Object>();
	// if (tmpDayWeek < 6) {
	// tmpDayName = EpgWeek[tmpDayWeek];
	// tmpDayWeek++;
	// } else {
	// tmpDayName = EpgWeek[tmpDayWeek];
	// tmpDayWeek = 0;
	// }
	// item.put("DayWeek", tmpDayName);
	// item.put("DayMonth", tmpDateName);
	// WeekDateInfo.add(item);
	// }
	// for (int i = 0; i < dayList.size(); i++) {
	// String day = dayList.get(i);
	// item.put("dayMonth", day);
	// WeekDateInfo.add(item);
	// }
	// return WeekDateInfo;
	//
	// }

	public int getIndexInList(String chanNO, List<ChannelInfo> channels) {
		int index = -1;
		if (channels == null || channels.size() <= 0) {
			return -1;
		}
		for (int i = 0; i < channels.size(); i++) {
			if (chanNO == channels.get(i).getChannelNumber()) {
				index = i;
			}
		}
		return index;
	}

	public void playChannel(int position) {

		ProgramInfo program = curProgramList.get(position);
		Intent mIntent = new Intent(EPGActivity.this, ReplayPlayActivity.class);

		CacheData.setCurProgram(program);
		CacheData.setCurChannel(curChannel);

		startActivity(mIntent);
	}

	private void getChannelList() {
		// 传入URL请求链接
		String URL = processData.getChannelList();
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
				new Response.Listener<org.json.JSONObject>() {

					@Override
					public void onResponse(JSONObject arg0) {
						// TODO Auto-generated method stub
						// 相应成功
						// Log.i(TAG, "HttpService=channle:" + arg0);
						for (ChannelInfo channel : HandleLiveData.getInstance().dealChannelJson(arg0)) {
							allTvList.add(channel);
						}

						// first set adapter
						curType = 0;
						curChannel = CacheData.getAllChannelInfo().get(0);
						getAllTVtype();
						showChannelList();
						uiHandler.sendEmptyMessage(MSG_CHANNEL_CHANGE);
						// Log.i(TAG,
						// "HttpService=channelsAll:" + channelsAll.size());
						// if (allTvList.size() <= 0) {
						// channelListLinear.setVisibility(View.INVISIBLE);
						// }
					}
				}, null);
		jsonObjectRequest.setTag(EPGActivity.class.getSimpleName());// 设置tag,cancelAll的时候使用
		mReQueue.add(jsonObjectRequest);
	}

	// get programs in the channel
	private void getPointProList(ChannelInfo channel) {
		mReQueue.cancelAll("program");
		String realurl = processData.getChannelProgramList(channel);

		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, realurl, null,
				new Response.Listener<org.json.JSONObject>() {

					@Override
					public void onResponse(org.json.JSONObject arg0) {
						// TODO Auto-generated method stub
						// 相应成功
						// Log.i(TAG, "getPointProList:" + arg0);
						resolveProJsonThread = ResolveEPGInfoThread.getInstance();
						resolveProJsonThread.addData(uiHandler, arg0);
						resolveProJsonThread.startRes();

					}
				}, null);
		jsonObjectRequest.setTag("program");// 设置tag,cancelAll的时候使用
		mReQueue.add(jsonObjectRequest);
	}

	private void showChannelList() {
		// TODO show channellist
		List<ChannelInfo> curChannels = null;
		switch (curType) {
		case 0:
			chanListTitleButton.setText(tvType[0]);
			curChannels = allTvList;
			break;
		case 1:
			chanListTitleButton.setText(tvType[1]);
			curChannels = CCTVList;
			break;
		case 2:
			chanListTitleButton.setText(tvType[2]);
			curChannels = starTvList;
			break;
		case 3:
			chanListTitleButton.setText(tvType[3]);
			curChannels = localTvList;
			break;
		case 4:
			chanListTitleButton.setText(tvType[4]);
			curChannels = HDTvList;
			break;
		case 5:
			chanListTitleButton.setText(tvType[5]);
			curChannels = favTvList;
			break;
		case 6:
			chanListTitleButton.setText(tvType[6]);
			curChannels = otherTvList;
			break;
		}
		curChannelList = curChannels;
		channelAdapter.setData(curChannelList);
		// if (curChannelList.size() <= 0) {
		// focusView.setVisibility(View.INVISIBLE);
		// }
	}

	private void dislistfocus(ViewGroup selected) {
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		switch (keyCode) {
		case Class_Constant.KEYCODE_UP_ARROW_KEY:
			if (channelListview.hasFocus()) {
				if (0 == channelListview.getSelectedItemPosition()) {
					channelListview.setSelection(channelAdapter.getCount() - 1);
				}
			}
			break;
		case Class_Constant.KEYCODE_DOWN_ARROW_KEY:
			if (channelListview.hasFocus()) {
				if ((channelAdapter.getCount() - 1) == channelListview.getSelectedItemPosition()) {
					channelListview.setSelection(0);
				}
			}
			break;
		case Class_Constant.KEYCODE_RIGHT_ARROW_KEY:
			epgWeekInfoView.setFocusable(true);
			break;
		case Class_Constant.KEYCODE_CHANNEL_UP://回看列表page +操作
			if(channelListview.hasFocus()){
				int pageUpIndex = channelListview.getSelectedItemPosition() + 8;
				if(pageUpIndex > (channelAdapter.getCount()-1)){
					pageUpIndex = pageUpIndex - (channelAdapter.getCount());	
				}
				if((pageUpIndex>(channelAdapter.getCount()-8))&&(pageUpIndex<=(channelAdapter.getCount()-1))){
					pageUpIndex = channelAdapter.getCount() - 8;
				}
				if((pageUpIndex > 0)&&(pageUpIndex < 7)){
					pageUpIndex = 0;
				}
				channelListview.setSelection(pageUpIndex);
			}
			break;
		case Class_Constant.KEYCODE_CHANNEL_DOWN://回看列表 page -操作
			if(channelListview.hasFocus()){
				int pageDownIndex = channelListview.getSelectedItemPosition() - 8;
				if(pageDownIndex < 0){
					pageDownIndex = channelAdapter.getCount() + pageDownIndex;
				}
				if((pageDownIndex>(channelAdapter.getCount()-8))&&(pageDownIndex<=(channelAdapter.getCount()-1))){
					pageDownIndex = channelAdapter.getCount() - 8;
				}
				if((pageDownIndex > 0)&&(pageDownIndex < 7)){
					pageDownIndex = 0;
				}
				channelListview.setSelection(pageDownIndex);
			}	
			break;
		// next process
		}

		return super.onKeyDown(keyCode, event);
	}

	// -------------------system function------------------
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
		mReQueue.cancelAll(EPGActivity.class.getSimpleName());
	}

}

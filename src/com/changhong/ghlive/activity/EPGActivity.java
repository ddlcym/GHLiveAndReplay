// package com.changhong.ghlive.activity;
//
// import java.lang.ref.WeakReference;
// import java.text.SimpleDateFormat;
// import java.util.ArrayList;
// import java.util.Calendar;
// import java.util.Date;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.TimeZone;
//
// import com.android.volley.Request;
// import com.android.volley.RequestQueue;
// import com.android.volley.Response;
// import com.android.volley.toolbox.JsonObjectRequest;
// import com.changhong.gehua.common.CacheData;
// import com.changhong.gehua.common.ChannelInfo;
// import com.changhong.gehua.common.Class_Constant;
// import com.changhong.gehua.common.PlayVideo;
// import com.changhong.gehua.common.ProcessData;
// import com.changhong.gehua.common.VideoView;
// import com.changhong.gehua.common.VolleyTool;
// import com.changhong.ghlive.datafactory.HandleLiveData;
// import com.changhong.ghlive.service.HttpService;
// import com.changhong.ghliveandreplay.R;
// import com.changhong.replay.datafactory.EpgListview;
//
// import android.content.Context;
// import android.content.Intent;
// import android.content.SharedPreferences;
// import android.graphics.Point;
// import android.graphics.Rect;
// import android.os.Bundle;
// import android.os.Handler;
// import android.os.Message;
// import android.util.Log;
// import android.view.KeyEvent;
// import android.view.LayoutInflater;
// import android.view.View;
// import android.view.ViewGroup;
// import android.view.View.OnFocusChangeListener;
// import android.view.View.OnKeyListener;
// import android.view.animation.Animation;
// import android.view.animation.AnimationUtils;
// import android.widget.AdapterView;
// import android.widget.BaseAdapter;
// import android.widget.Button;
// import android.widget.FrameLayout;
// import android.widget.GridView;
// import android.widget.ImageView;
// import android.widget.LinearLayout;
// import android.widget.RelativeLayout;
// import android.widget.SimpleAdapter;
// import android.widget.TextView;
// import android.widget.Toast;
// import android.widget.AdapterView.OnItemClickListener;
// import android.widget.AdapterView.OnItemSelectedListener;
//
// public class EPGActivity extends BaseActivity{
//
// private boolean channelListFoucus = false;
// private static LinearLayout channelCurSelect = null;
// private static LinearLayout channelLastSelect = null;
// private static VideoView replayVideoView;
// private static EpgListview channelListview;
// private static EpgListview epgEventListview;
// private static TextView epgListTitleView;
// private static ImageView focusView;
// private static LinearLayout channelListLinear;
// private static GridView epgWeekInfoView;
// private static Button bookButton;
// private static Button chanListTitleButton;
// private static TextView curChannelText;
// private static TextView curProgramText;
//
// private static int curType = 0;
// private static Context context;
// private static boolean firsIn = true;
//
// private VolleyTool volleyTool;
// private RequestQueue mReQueue;
// private ProcessData processData;
//
// private UI_Handler uiHandler = new UI_Handler(this);
// private static final int MSG_NO_CHANNEL = 0x000;
// private static final int MSG_TITLE_FOCUSABLE = 0x001;
// private static final int MSG_SHOW_CHANNELLIST = 0x101;
// public static final int MSG_CHANNEL_CHANGE = 0x102;
// public static final int MSG_WEEKDAY_CHANGE = 0x202;
// public static final int MSG_SHOW_WEEKDAY = 0x201;
// public static final int MSG_BOOK_NEW = 0x301;
//
// private static String channelNO;
// private int channelCount;
//
// private static int WeeklistItemindex = 0; // 从当前开始第几天，0-6
// private static String[] tvType;
//
// static List<Map<String, Object>> SimpleAdapterEventdata = new
// ArrayList<Map<String, Object>>();
//
// // all type channel
// List<ChannelInfo> allTvList = new ArrayList<ChannelInfo>();
// List<ChannelInfo> CCTVList = new ArrayList<ChannelInfo>();
// List<ChannelInfo> starTvList = new ArrayList<ChannelInfo>();
// List<ChannelInfo> favTvList = new ArrayList<ChannelInfo>();
// List<ChannelInfo> localTvList = new ArrayList<ChannelInfo>();
// List<ChannelInfo> HDTvList = new ArrayList<ChannelInfo>();
// List<ChannelInfo> otherTvList = new ArrayList<ChannelInfo>();
//
// List<ChannelInfo> curChannelList = new ArrayList<ChannelInfo>();
//
// private static class UI_Handler extends Handler {
// WeakReference<EPGActivity> mActivity;
//
// public UI_Handler(EPGActivity activity) {
// mActivity = new WeakReference<EPGActivity>(activity);
// }
//
// @Override
// public void handleMessage(Message msg) {
// EPGActivity epg = mActivity.get();
// switch (msg.what) {
// case MSG_NO_CHANNEL:
// break;
// case MSG_TITLE_FOCUSABLE:
// // channelListLinear.setFocusable(true);
// // channelListLinear.setFocusableInTouchMode(true);
// // chanListTitleButton.setFocusable(true);
// // chanListTitleButton.setFocusableInTouchMode(true);
// epgWeekInfoView.setFocusable(true);
// epgWeekInfoView.setFocusableInTouchMode(true);
// epgWeekInfoView.setClickable(true);
// break;
// case MSG_SHOW_CHANNELLIST:
// epg.showChannelList(curType);
// epg.showCurChannelInfo(channelNO);
// if (firsIn) {
// epg.ChannelClassifyScale(channelListLinear, true);
// epg.channelListview.setSelection(epg.getIndexInList(
// epg.channelNO, epg.curChannelList));
// // epg.channelListview.setSelection(channelNO-1);
// this.sendEmptyMessageDelayed(MSG_TITLE_FOCUSABLE, 200);
// firsIn = false;
// }
// break;
// case MSG_CHANNEL_CHANGE:
// WeeklistItemindex = 0;
// epg.showCurChannelInfo(channelNO);
// epg.playChannel(replayVideoView, channelNO);
// epg.getEpgEventData(channelNO, WeeklistItemindex);
// epg.EpgEventListRefresh(SimpleAdapterEventdata);
// break;
// case MSG_SHOW_WEEKDAY:
// epg.showWeekDay();
// epg.getEpgEventData(channelNO, WeeklistItemindex);
// epg.EpgEventListRefresh(SimpleAdapterEventdata);
// case MSG_WEEKDAY_CHANGE:
// epg.getEpgEventData(channelNO, WeeklistItemindex);
// epg.EpgEventListRefresh(SimpleAdapterEventdata);
// break;
// case MSG_BOOK_NEW:
// epg.getEpgEventData(channelNO, WeeklistItemindex);
// epg.EpgEventListRefresh(SimpleAdapterEventdata);
// default:
// break;
// }
// super.handleMessage(msg);
// }
// }
//
// @Override
// protected void onCreate(Bundle savedInstanceState) {
// // TODO Auto-generated method stub
// super.onCreate(savedInstanceState);
// playChannel(replayVideoView, channelNO);
// }
//
//
// @Override
// protected void initView() {
// // TODO Auto-generated method stub
// setContentView(R.layout.epg);
//
// replayVideoView= (VideoView) findViewById(R.id.epg_VideosurfaceView);
// channelListLinear = (LinearLayout)
// findViewById(R.id.epg_chan_classifylayout);
//
// chanListTitleButton = (Button) findViewById(R.id.epg_chanlistTitle);
// chanListTitleButton.setOnKeyListener(epg_Listener_Classify_OnKey);
// chanListTitleButton
// .setOnFocusChangeListener(epg_Listener_Classify_OnfocusChange);
// chanListTitleButton.requestFocus();
//
// channelListview = (EpgListview) findViewById(R.id.ChanlIstView);
// channelListview.setFocusable(true);
// channelListview.setFocusableInTouchMode(true);
// channelListview.setOnFocusChangeListener(ChanListOnfocusChange);
// channelListview.setOnItemSelectedListener(ChanListOnItemSelected);
// channelListview.setOnItemClickListener(ChanListOnItemClick);
//
// curChannelText = (TextView) findViewById(R.id.epg_videoTvinfo_chan1);
// curProgramText = (TextView) findViewById(R.id.epg_videoTvinfo_program1);
//
// epgWeekInfoView = (GridView) findViewById(R.id.EpgWeekInfo);
// epgWeekInfoView.setOnItemSelectedListener(WeekInfoItemSelected);
// bookButton = (Button) findViewById(R.id.epg_timerbt0);
// bookButton.setOnClickListener(bookButtonClick);
//
// epgEventListview = (EpgListview) findViewById(R.id.EpgEventInfo);
// epgEventListview.setOnFocusChangeListener(epgEventChangeListener);
// epgEventListview.setOnItemSelectedListener(epgEventSelectedListener);
// epgEventListview.setOnItemClickListener(epgEventClickListener);
//
// }
//
// private boolean initValue() {
// context = EPGActivity.this;
// tvType = getResources().getStringArray(R.array.tvtype);
// firsIn = true;
// volleyTool = VolleyTool.getInstance();
// mReQueue = volleyTool.getRequestQueue();
// if (null == processData) {
// processData = new ProcessData();
// }
// channelCount = CacheData.getAllChannelInfo().size();
// if (channelCount <= 0) {
// Log.i("mmmm", "无节目！");
//// 无节目时的处理----------------------------------待完成-----------------------------
//// Intent mIntent1 = new Intent("chots.action.system_dialog");
//// mIntent1.putExtra("extra.show", false);
//// sendBroadcast(mIntent1);
////
//// Intent mIntent = new Intent(Epg.this, DialogNotice.class);
//// mIntent.putExtra(Class_Constant.DIALOG_TITLE, R.string.no_channel);
//// mIntent.putExtra(Class_Constant.DIALOG_DETAILS, R.string.no_channel);
//// mIntent.putExtra(Class_Constant.DIALOG_BUTTON_NUM, 1);
//// startActivity(mIntent);
//// finish();
//// return false;
//// // uiHandler.sendEmptyMessage(MS_NO_CHANNEL);
// }
// channelNO = CacheData.curChannelNum;
// getAllTVtype();
// EpgWeek = getResources().getStringArray(R.array.epgweek);
//
// SimpleAdapterWeekdata = new ArrayList<Map<String, Object>>();
// SimpleAdapterWeekdata = GetWeekDate();
// return true;
// }
//
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
// private OnFocusChangeListener epg_Listener_Classify_OnfocusChange = new
// OnFocusChangeListener() {
//
// @Override
// public void onFocusChange(View arg0, boolean arg1) {
// if (arg0 != null) {
// ChannelClassifyScale(channelListLinear, arg1);
// }
// }
// };
//
// // 周一...周日改变时触发
// private OnItemSelectedListener WeekInfoItemSelected = new
// OnItemSelectedListener() {
//
// @Override
// public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
// long arg3) {
// WeeklistItemindex = arg2;
// uiHandler.sendEmptyMessage(MSG_WEEKDAY_CHANGE); // 更新下面的列表
// }
//
// @Override
// public void onNothingSelected(AdapterView<?> arg0) {
//
// }
// };
//
// private View.OnClickListener bookButtonClick = new View.OnClickListener() {
//
// @Override
// public void onClick(View arg0) {
//
// Intent mintent = new Intent(EPGActivity.this, EpgManage.class);
// startActivity(mintent);
// }
// };
//
// private OnItemClickListener ChanListOnItemClick = new OnItemClickListener() {
// @Override
// public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
// long arg3) {
// switch (arg0.getId()) {
// case R.id.ChanlIstView:
//// Point point = new Point();
//// getWindowManager().getDefaultDisplay().getSize(point);
//// DVB_RectSize.Builder builder = DVB_RectSize.newBuilder()
//// .setX(0).setY(0).setW(point.x).setH(point.y);
//// objApplication.dvbPlayer.setSize(builder.build());
//// finish();
// break;
//
// default:
// break;
// }
// }
// };
// private OnItemSelectedListener ChanListOnItemSelected = new
// OnItemSelectedListener() {
//
// @Override
// public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
// long arg3) {
// if (arg1 == null) {
// return;
// }
// TextView channelIdText = (TextView) arg1
// .findViewById(R.id.channelId);
// channelNO = Integer.parseInt(channelIdText.getText().toString());
// uiHandler.removeMessages(MSG_CHANNEL_CHANGE);
// uiHandler.sendEmptyMessageDelayed(MSG_CHANNEL_CHANGE, 3000);// send
// // a
// // message
// // delay
// // 3s,
// // to
// // play
// // the
// // channel
// channelCurSelect = (LinearLayout) arg1
// .findViewById(R.id.epg_chan_itemlayout);
// if (channelListFoucus == false) {
// return;
// }
//
// if (channelLastSelect != null) {
// ChannelItemScale(channelLastSelect, false, false);
// channelLastSelect = null;
// }
//
// if (channelCurSelect != null) {
// ChannelItemScale(channelCurSelect, true, true);
// channelLastSelect = channelCurSelect;
// }
//
// }
//
// @Override
// public void onNothingSelected(AdapterView<?> arg0) {
//
// }
// };
//
// private OnFocusChangeListener ChanListOnfocusChange = new
// OnFocusChangeListener() {
//
// @Override
// public void onFocusChange(View arg0, boolean arg1) {
// channelListFoucus = arg1;
//
// if (arg0 != null) {
// if (channelCurSelect != null) {
// ChannelItemScale(channelCurSelect, true, channelListFoucus);
// channelLastSelect = channelListLinear;
// }
// }
// }
// };
//
// private OnFocusChangeListener epgEventChangeListener = new
// OnFocusChangeListener() {
//
// @Override
// public void onFocusChange(View arg0, boolean arg1) {
//
// bEventFocus = arg1;
// if (arg0.getId() == R.id.EpgEventInfo) {
// if (true == arg1) {
// bookButton.setFocusable(false);
// bookButton.setFocusableInTouchMode(false);
// } else if (false == arg1) {
// bookButton.setFocusable(true);
// bookButton.setFocusableInTouchMode(true);
// }
// }
// if (arg0 != null) {
// if (EventCurSelect != null) {
// epgEventItemScale(EventCurSelect, bEventFocus);
// EventLastSelect = EventCurSelect;
// }
// }
// }
// };
//
// private OnItemSelectedListener epgEventSelectedListener = new
// OnItemSelectedListener() {
//
// @Override
// public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
// long arg3) {
//
// if (arg1 == null) {
// return;
// }
//
// EventCurSelect = (LinearLayout) arg1
// .findViewById(R.id.eventitem_Linearlayout);
// if (bEventFocus == false) {
// return;
// }
//
// if (EventLastSelect != null) {
// epgEventItemScale(EventLastSelect, false);
// EventLastSelect = null;
// }
//
// if (EventCurSelect != null) {
// epgEventItemScale(EventCurSelect, true);
// EventLastSelect = EventCurSelect;
// // EventlitItemindex = (int) arg3;
// }
// }
//
// @Override
// public void onNothingSelected(AdapterView<?> arg0) {
//
// }
// };
//
// private OnItemClickListener epgEventClickListener = new OnItemClickListener()
// {
//
// @Override
// public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
// long arg3) {
//
// Channel mChannel = objApplication.dvbDatabase.getChannel(channelID);
// DVB_EPG_Event mEvent = (DVB_EPG_Event) arg1.getTag();
//
// String startHour = String.valueOf(mEvent.getStartTime().getHour());
// if (startHour.length() < 2)
// startHour = "0" + startHour;
// String startMinute = String.valueOf(mEvent.getStartTime()
// .getMinute());
// if (startMinute.length() < 2)
// startMinute = "0" + startMinute;
// Calendar c = Calendar.getInstance();
// c.setTimeZone(TimeZone.getTimeZone("GMT+8"));
// c.setTime(new Date());
// c.add(Calendar.DAY_OF_MONTH, WeeklistItemindex);
// SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
// String tmpDateName = dateFormat.format(c.getTime());
// String startTime = startHour + ":" + startMinute;
// TextView bookView = (TextView) arg1
// .findViewById(R.id.epg_event_Tview_timer);
// if (bookView.getText().toString().equals((R.string.str_booked))) {
// objApplication.dvbBookDataBase.RemoveOneBookInfo(tmpDateName,
// startTime);
// bookView.setText("");
// mBookInfos = objApplication.dvbBookDataBase.GetBookInfo();
// return;
// }
// if (WeeklistItemindex == 0) {
// int startH = mEvent.getStartTime().getHour();
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
//
// BookInfo mBookInfo = new BookInfo();
// mBookInfo.bookEnventName = mEvent.getName();
// mBookInfo.bookChannelName = mChannel.name;
// mBookInfo.bookTimeStart = startTime;
// mBookInfo.bookChannelIndex = mChannel.chanId;
// mBookInfo.bookDay = tmpDateName;
// if (null != mBookInfos && mBookInfos.size() != 0) {
// for (int i = 0; i < mBookInfos.size(); i++) {
// if (mBookInfos.get(i).bookTimeStart.equals(startTime)
// && mBookInfos.get(i).bookDay.equals(tmpDateName)) {
// Toast.makeText(context, R.string.str_book_dialog,
// Toast.LENGTH_SHORT).show();
// return;
// }
// }
// }
// if (objApplication.dvbBookDataBase.BookInfoCommit(mBookInfo)) {
//
// SharedPreferences sharedPre = getSharedPreferences("id",
// MODE_PRIVATE);
// SharedPreferences.Editor editor = sharedPre.edit();
// int flag = sharedPre.getInt("id", 0);
//
// Intent myBookIntent = new Intent(
// "android.intent.action.SmartTVBook");
// myBookIntent.putExtra("bookinfo", mBookInfo);
// myBookIntent.putExtra("SmartTV_BookFlag", flag);
//
// sendBroadcast(myBookIntent);
//
// editor.putInt("id", flag + 1);
// editor.commit();
// EventlitItemindex = arg2;
// uiHandler.sendEmptyMessage(MSG_BOOK_NEW);
// } else {
// Common.LOGE("bookInfo添加失败");
// }
//
// }
// };
//
// private void ChannelItemScale(LinearLayout ItemLayout,
// boolean isFocusChangeAction, boolean IsScalebig) {
// final Animation ani_ScaleBig = AnimationUtils.loadAnimation(this,
// R.anim.epg_chanitem_big);
// final Animation ani_ScaleSmall = AnimationUtils.loadAnimation(this,
// R.anim.epg_chanitem_small);
// RelativeLayout.LayoutParams FocusChanItemLayout = new
// RelativeLayout.LayoutParams(
// 10, 10);
// TextView itembackTview = null;
// Rect imgRect = new Rect();
//
// if (ItemLayout == null) {
// System.out.println("ItemLayout invalid param(ItemLayout is null)!");
// return;
// }
//
// itembackTview = (TextView) findViewById(R.id.item_SelectbackTview);
// System.out.println("-----epg_method_Chan_itemScale IsScalebig: "
// + IsScalebig + "------");
//
// /* listview焦点变化处理 */
// if (IsScalebig) {
// ItemLayout.getGlobalVisibleRect(imgRect);
// System.out
// .println("-----chapp_epg_chanitemScale scaelbig-imgRect(x:"
// + imgRect.left + ",y:" + imgRect.top + ",w:"
// + imgRect.width() + ",h:" + imgRect.height() + ")");
//
// FocusChanItemLayout.leftMargin = imgRect.left;
// FocusChanItemLayout.topMargin = imgRect.top;
// FocusChanItemLayout.width = imgRect.width();
// FocusChanItemLayout.height = imgRect.height();
//
// itembackTview.setBackgroundResource(R.drawable.chanselect);
// itembackTview.setLayoutParams(FocusChanItemLayout);
// itembackTview.startAnimation(ani_ScaleBig);
// itembackTview.setVisibility(View.VISIBLE);
// } else {
// itembackTview.setVisibility(View.INVISIBLE);
// itembackTview.startAnimation(ani_ScaleSmall);
// itembackTview.clearAnimation();
//
// if (isFocusChangeAction) {
// ItemLayout.getGlobalVisibleRect(imgRect);
// System.out
// .println("-----chapp_epg_chanitemScale scaelsmall-imgRect(x:"
// + imgRect.left
// + ",y:"
// + imgRect.top
// + ",w:"
// + imgRect.width()
// + ",h:"
// + imgRect.height()
// + ")");
//
// FocusChanItemLayout.leftMargin = imgRect.left;
// FocusChanItemLayout.topMargin = imgRect.top;
// FocusChanItemLayout.width = imgRect.width();
// FocusChanItemLayout.height = imgRect.height();
//
// // itembackTview.setBackgroundResource(R.drawable.chanunfocus);
// itembackTview.setBackgroundDrawable(null);
// itembackTview.setLayoutParams(FocusChanItemLayout);
// itembackTview.setVisibility(View.VISIBLE);
// }
// }
// }
//
// private void ChannelClassifyScale(LinearLayout itemLayout,
// boolean isScalebig) {
// final Animation anim_ScaleBig = AnimationUtils.loadAnimation(context,
// R.anim.scale_big);
// final Animation anim_ScaleSmall = AnimationUtils.loadAnimation(context,
// R.anim.scale_small);
// FrameLayout.LayoutParams FocusItemLayout = new FrameLayout.LayoutParams(
// 10, 10);
// TextView itembackTview = null;
// Rect imgRect = new Rect();
//
// if (itemLayout == null) {
// return;
// }
//
// itembackTview = (TextView) findViewById(R.id.title_SelectbackTview);
// if (isScalebig) {
// FocusItemLayout.leftMargin = 11;
// FocusItemLayout.topMargin = 0;
// FocusItemLayout.height = 80;
// FocusItemLayout.width = 221;
//
// itembackTview.setBackgroundResource(R.drawable.chancalssfify);
// itembackTview.setLayoutParams(FocusItemLayout);
// itembackTview.startAnimation(anim_ScaleBig);
// itembackTview.setVisibility(View.VISIBLE);
// } else {
// itembackTview.setBackgroundDrawable(null);
// itembackTview.setVisibility(View.INVISIBLE);
// itembackTview.startAnimation(anim_ScaleSmall);
// itembackTview.clearAnimation();
// }
// }
// public void EpgEventListRefresh(List<Map<String, Object>> EventdataList) {
// // 根据当前保存的数据刷新EventList
// epgEventListview.setAdapter(new EventListAdapter(context,
// SimpleAdapterEventdata));
// epgEventListview.setSelection(EventlitItemindex);
//
// }
// public void showWeekDay() {
// SimpleAdapter adapterweek = new SimpleAdapter(this,
// SimpleAdapterWeekdata, R.layout.epg_main_weekitem,
// new String[] { "DayMonth", "DayWeek" }, new int[] {
// R.id.epg_week_Tview_date, R.id.epg_week_Tview_week });
// epgWeekInfoView.setAdapter(adapterweek);
// epgWeekInfoView.setNumColumns(adapterweek.getCount());
// epgWeekInfoView.setFocusable(false);
// epgWeekInfoView.setFocusableInTouchMode(false);
// // epgWeekInfoView.setSelection(WeeklistItemindex);
// }
//
// public void showCurChannelInfo(String chanNO) {
// ChannelInfo channel = (ChannelInfo) CacheData.getAllChannelMap().get(chanNO);
// DVB_EPG_PF pfInfo = Epg.objApplication.dvbEpg.getPfInfo(channel);
// String PF_enventName_P = null;
// if (pfInfo != null && pfInfo.hasPresent()) {
// PF_enventName_P = pfInfo.getPresent().getName();
// // PF_enventName_P = pfInfo.get(0).mstr_EventName;
// } else {
// Common.LOGE("TVEpg obj_EpgEventInfo == null");
// return;
// }
// curChannelText.setText(channel.name);
// curProgramText.setText(PF_enventName_P);
// }
//
//
// public void getEpgEventData(String Channel, int WeekIndex) {
// // 根据频道ID和第几天得到EpgEventData
// // SimpleAdapterEventdata
// SimpleAdapterEventdata = new ArrayList<Map<String, Object>>();
// DVB_EPG_SCH schInfo = null;
// Channel curChannel = objApplication.dvbDatabase.getChannel(Channel);
// schInfo = objApplication.dvbEpg.getSchInfo(curChannel);
// schInfo = objApplication.dvbEpg.getSchInfo(curChannel);
// if (null == schInfo || schInfo.getSchEventCount() == 0) {
// Common.LOGD("schInfo null");
// return;
// }
// Map<String, Object> item = null;
// int startHour = 0;
// int startMinute = 0;
// int durationHour = 0;
// int durationMinute = 0;
// Calendar c = Calendar.getInstance();
// c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
// int curDayWeek = c.get(Calendar.DAY_OF_WEEK);
// for (int i = 0; i < schInfo.getSchEventCount(); i++) {
// if (schInfo.getSchEvent(i).getStartTime().getWeekday() == (WeekIndex
// + curDayWeek - 1)) {
// item = new HashMap<String, Object>();
// item.put("time", schInfo.getSchEvent(i).getStartTime()
// .getHour()
// + ":"
// + schInfo.getSchEvent(i).getStartTime().getMinute());
// // int startMonth = schInfo.get(i).mo_StartTime.mi_Month;
// // int startDay = schInfo.get(i).mo_StartTime.mi_Day;
// startHour = schInfo.getSchEvent(i).getStartTime().getHour();
// startMinute = schInfo.getSchEvent(i).getStartTime().getMinute();
// durationHour = schInfo.getSchEvent(i).getDurationHour();
// durationMinute = schInfo.getSchEvent(i).getDurationMinute();
// item.put("tag", schInfo.getSchEvent(i));
// item.put(
// "time",
// getStartEndTime(startHour, startMinute, durationHour,
// durationMinute));
// item.put("event", schInfo.getSchEvent(i).getName());
// SimpleAdapterEventdata.add(item);
// }
// }
//
// }
//
// public void showChannelList(int channelType) {
//
// switch (channelType) {
// case 0:
// channelListview.setAdapter(new ChannelListAdapter(context,
// allTvList));
// curChannelList = allTvList;
// chanListTitleButton.setText(tvType[0]);
// break;
// case 1:
// channelListview
// .setAdapter(new ChannelListAdapter(context, CCTVList));
// curChannelList = CCTVList;
// chanListTitleButton.setText(tvType[1]);
// break;
// case 2:
// chanListTitleButton.setText(tvType[2]);
// curChannelList = starTvList;
// channelListview.setAdapter(new ChannelListAdapter(context,
// starTvList));
// break;
// case 3:
// chanListTitleButton.setText(tvType[3]);
// curChannelList = localTvList;
// channelListview.setAdapter(new ChannelListAdapter(context,
// localTvList));
// break;
// case 4:
// chanListTitleButton.setText(tvType[4]);
// curChannelList = HDTvList;
// channelListview
// .setAdapter(new ChannelListAdapter(context, HDTvList));
// break;
// case 5:
// chanListTitleButton.setText(tvType[5]);
// curChannelList = favTvList;
// channelListview.setAdapter(new ChannelListAdapter(context,
// favTvList));
// break;
// case 6:
// chanListTitleButton.setText(tvType[6]);
// curChannelList = otherTvList;
// channelListview.setAdapter(new ChannelListAdapter(context,
// otherTvList));
// break;
//
// default:
// break;
// }
// }
//
// private void getAllTVtype() {
// // fill all type tv
//
// // clear all tv type;
// allTvList.clear();
// CCTVList.clear();
// starTvList.clear();
// favTvList.clear();
// localTvList.clear();
// HDTvList.clear();
// otherTvList.clear();
// allTvList=CacheData.allChannelInfo;
// for (ChannelInfo dvbChannel : allTvList) {
// allTvList.add(dvbChannel);
//
// //喜爱频道列表---------------待完成---------------
//// if (dvbChannel.favorite == 1) {
//// favTvList.add(dvbChannel);
//// }
// String regExCCTV;
// regExCCTV = getResources().getString(R.string.zhongyang);
// java.util.regex.Pattern pattern = java.util.regex.Pattern
// .compile("CCTV|" + regExCCTV);
// java.util.regex.Matcher matcher = pattern.matcher(dvbChannel.name);
// boolean classBytype = matcher.find();
// if (classBytype) {
// CCTVList.add(dvbChannel);
// }
// String regExStar;
// regExStar = getResources().getString(R.string.weishi);
// java.util.regex.Pattern patternStar = java.util.regex.Pattern
// .compile(".*" + regExStar + "$");
// java.util.regex.Matcher matcherStar = patternStar
// .matcher(dvbChannel.name);
// boolean classBytypeStar = matcherStar.matches();
// if (classBytypeStar) {
// starTvList.add(dvbChannel);
// }
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
// java.util.regex.Matcher matcherLocal = patternLocal
// .matcher(dvbChannel.name);
// boolean classBytypeLocal = matcherLocal.find();
// if (classBytypeLocal) {
// localTvList.add(dvbChannel);
// }
// String regExHD = getResources().getString(R.string.hd_dtv) + "|"
// + getResources().getString(R.string.xinyuan_hdtv1) + "|"
// + getResources().getString(R.string.xinyuan_hdtv2) + "|"
// + getResources().getString(R.string.xinyuan_hdtv3) + "|"
// + getResources().getString(R.string.xinyuan_hdtv4);
// java.util.regex.Pattern patternHD = java.util.regex.Pattern
// .compile("3D|" + regExHD + "|.*HD$");
//
// java.util.regex.Matcher matcherHD = patternHD
// .matcher(dvbChannel.name);
// boolean classBytypeHD = matcherHD.find();
// if (classBytypeHD) {
// HDTvList.add(dvbChannel);
// }
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
// java.util.regex.Matcher matcherOther = patternOther
// .matcher(dvbChannel.name);
// boolean classBytypeOther = matcherOther.find();
// if (!classBytypeOther) {
// otherTvList.add(dvbChannel);
// }
// }
// }
//
//
// public int getIndexInList(String chanNO, List<ChannelInfo> channels) {
// int index = -1;
// if (channels == null || channels.size() <= 0) {
// return -1;
// }
// for (int i = 0; i < channels.size(); i++) {
// if (chanNO == channels.get(i).getChannelNumber()) {
// index = i;
// }
// }
// return index;
// }
//
// protected class ChannelListAdapter extends BaseAdapter {
// private Context context;
// private List<ChannelInfo> repChannels;
// private LayoutInflater inflater;
//
// @Override
// public int getCount() {
// return repChannels.size();
// }
//
// public ChannelListAdapter(Context context, List<ChannelInfo> curChannels) {
// this.context = context;
// this.repChannels = curChannels;
// inflater = LayoutInflater.from(context);
// }
//
// public void setData(List<ChannelInfo> curChannels){
// this.repChannels=curChannels;
// }
//
// @Override
// public Object getItem(int arg0) {
// return null;
// }
//
// @Override
// public long getItemId(int arg0) {
// return 0;
// }
//
// @Override
// public View getView(int position, View convertView, ViewGroup viewGroup) {
//
// if (convertView == null) {
// convertView = inflater
// .inflate(R.layout.epg_main_chanitem, null);
// }
// ChannelInfo channel = repChannels.get(position);
// TextView channelIndex = (TextView) convertView
// .findViewById(R.id.epg_chan_Tview_chanindex);
// TextView channelName = (TextView) convertView
// .findViewById(R.id.epg_chan_Tview_channame);
// TextView channelIDView = (TextView) convertView
// .findViewById(R.id.channelId);
//
//
// channelIDView.setText(channel.getChannelNumber());
//
// channelIDView.setTextColor(0xffffff00);
//
//
// channelIndex.setText(position);
//
// channelIndex.setTextColor(0xffffff00);
//
// channelName.setText(channel.getChannelName());
// return convertView;
// }
//
// }
//
// public void playChannel(VideoView view ,String channelNO){
// PlayVideo.getInstance().playLiveProgram(view,
// (ChannelInfo)CacheData.getAllChannelMap().get(channelNO));
// }
//
// private void getChannelList() {
// // 传入URL请求链接
// String URL = processData.getChannelList();
// JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
// Request.Method.GET, URL, null,
// new Response.Listener<org.json.JSONObject>() {
//
// @Override
// public void onResponse(org.json.JSONObject arg0) {
// // TODO Auto-generated method stub
// // 相应成功
//// Log.i(TAG, "HttpService=channle:" + arg0);
// allTvList = HandleLiveData.getInstance()
// .dealChannelJson(arg0);
// // first set adapter
// curType = 0;
// showChannelList();
//// Log.i(TAG,
//// "HttpService=channelsAll:" + channelsAll.size());
// if (allTvList.size() <= 0) {
// channelListLinear.setVisibility(View.INVISIBLE);
// }
// }
// }, null);
// jsonObjectRequest.setTag(HttpService.class.getSimpleName());//
// 设置tag,cancelAll的时候使用
// mReQueue.add(jsonObjectRequest);
// }
//
// private void showChannelList() {
// // TODO show channellist
// List<ChannelInfo> curChannels = null;
// switch (curType) {
// case 0:
// epgListTitleView.setText(TVtype[0]);
// curChannels = channelsAll;
// break;
// case 1:
// epgListTitleView.setText(TVtype[1]);
// curChannels = CCTVList;
// break;
// case 2:
// epgListTitleView.setText(TVtype[2]);
// curChannels = starTvList;
// break;
// case 3:
// epgListTitleView.setText(TVtype[3]);
// curChannels = localTvList;
// break;
// case 4:
// epgListTitleView.setText(TVtype[4]);
// curChannels = HDTvList;
// break;
// case 5:
// epgListTitleView.setText(TVtype[5]);
// curChannels = favTvList;
// break;
// case 6:
// epgListTitleView.setText(TVtype[6]);
// curChannels = otherTvList;
// break;
// }
// mCurChannels = curChannels;
// chLstAdapter.setData(mCurChannels);
// if (mCurChannels.size() <= 0) {
// focusView.setVisibility(View.INVISIBLE);
// }
// }
//
//
// //-------------------system function------------------
// @Override
// protected void onResume() {
// // TODO Auto-generated method stub
// super.onResume();
// }
//
// @Override
// protected void onPause() {
// // TODO Auto-generated method stub
// super.onPause();
// }
//
// @Override
// protected void onDestroy() {
// // TODO Auto-generated method stub
// super.onDestroy();
// }
//
//
// }

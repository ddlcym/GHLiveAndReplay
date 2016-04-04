package com.changhong.ghlive.datafactory;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import com.changhong.gehua.common.ChannelInfo;
import com.changhong.gehua.common.ProgramInfo;
import com.changhong.ghliveandreplay.R;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author OscarChang
 *
 */
public class Banner {

	private static Banner banner;
	private static Toast bannerToast = null;
	// private static SysApplication sysApplication;
	private Context mContext;
	private View bannerView;

	ChannelInfo channelInfo;
	ProgramInfo programInfo;

	HashMap<String, Integer> hs = null;
	String[] sWeek;
	String sMonth;
	String sDay;

	String PF_timeshiftsupport = new String();

	ImageView adv_image = null;
	TextView channel_name = null;
	TextView channel_number = null;
	TextView PF_dtw = null;// 时间
	TextView PF_P = null;
	// TextView PF_F = null;
	TextView textview_timeshift_support = null; // 节目名称
	LineProgressView progress = null;

	public Banner(Context context, ChannelInfo outterChannelInfo, ProgramInfo outterProgramInfo) {
		mContext = context.getApplicationContext();
		channelInfo = outterChannelInfo;
		programInfo = outterProgramInfo;
	}

	public void show() {
		if (bannerToast == null) {
			bannerToast = new Toast(mContext);
			bannerToast.setGravity(Gravity.BOTTOM, 0, 0);
		}
		if (bannerView == null) {
			LayoutInflater mInflater = LayoutInflater.from(mContext);
			bannerView = mInflater.inflate(R.layout.banner, null);
			// bannerView.setLeft(1300);
			findView();
		}
		// updatePFInfo();
		updateBanner();
		updateDateTime();
		bannerToast.setView(bannerView);
		bannerToast.setGravity(Gravity.RIGHT | Gravity.BOTTOM, 0, 2);
		// bannerToast.setGravity(Gravity.BOTTOM, 0, 2);
		bannerToast.setDuration(Toast.LENGTH_LONG);
		bannerToast.show();
//		bannerToast.cancel();
		// showMyToast(bannerToast, 16);// 测试显示toast
	}
	
	public void cancelBanner(){
		bannerToast.cancel();
	}
	
	private void findView() {
		adv_image = (ImageView) bannerView.findViewById(R.id.banner_adv_id);
		channel_name = (TextView) bannerView.findViewById(R.id.banner_channel_name_id);
		channel_number = (TextView) bannerView.findViewById(R.id.banner_service_id);
		progress = (LineProgressView) bannerView.findViewById(R.id.banner_progress_view);
		PF_dtw = (TextView) bannerView.findViewById(R.id.banner_DTW_id);
		PF_P = (TextView) bannerView.findViewById(R.id.banner_tshift_support);// playtime
		// PF_F = (TextView) bannerView.findViewById(R.id.banner_PF_F_id);
		textview_timeshift_support = (TextView) bannerView.findViewById(R.id.banner_PF_P_id);// 名称
	}

	private void updateBanner() {
		// TODO channelname PF_channel_name diffs?
		if (hs == null) {
			hs = new HashMap<String, Integer>();
			// CH_TVlogo_Mapping();
		}
		progress.setProgress(getPlayingProgress());

		channel_name.setText(channelInfo.getChannelName());
		channel_number.setText(channelInfo.getChannelNumber());
		textview_timeshift_support.setText(programInfo.getEventName());
		PF_P.setText(programInfo.getBeginTime().toString().substring(11, 16));// 取固定格式时间
		Log.i("zyt", "Date time is" + programInfo.getBeginTime().toString().substring(11, 16));
		// PF_F.setText(programInfo.getEndTime().toString());
		// adv_image.set
		// textview_timeshift_support.setText(getTimeShiftSupportString(channel.chanId));

	}

	private int getPlayingProgress() {
		int startTime = 0;

		int endTime = 0;

		Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		int nowH = c.get(Calendar.HOUR_OF_DAY);
		int nowM = c.get(Calendar.MINUTE);
		int nowS = c.get(Calendar.SECOND);
		int nowTime = nowH * 60 * 60 + nowM * 60 + nowS;

		int duration = getDuration(startTime, endTime);
		int played = getDuration(startTime, nowTime);
		if (duration == 0) {
			return 0;
		}
		return played * 100 / duration;
	}

	private int getDuration(int start, int end) {
		int duration = end - start;
		if (duration < 0) {// start 23:23 end 00:15
			duration = end + 24 * 60 * 60 - start;
		}
		return duration;
	}

	private void updateDateTime() {
		String mYear;
		String mMonth;
		String mDay;
		String mWeek;
		String mHour, mMinute;
		String[] week = mContext.getResources().getStringArray(R.array.str_dtv_epg_week_name);

		Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		mYear = String.valueOf(c.get(Calendar.YEAR));
		mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);
		mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
		mWeek = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
		if (c.get(Calendar.HOUR_OF_DAY) < 10) {
			mHour = "0" + String.valueOf(c.get(Calendar.HOUR_OF_DAY));
		} else {
			mHour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
		}
		if (c.get(Calendar.MINUTE) < 10) {
			mMinute = "0" + String.valueOf(c.get(Calendar.MINUTE));
		} else {
			mMinute = String.valueOf(c.get(Calendar.MINUTE));
		}
		if ("1".equals(mWeek)) {
			mWeek = week[6];
		} else if ("2".equals(mWeek)) {
			mWeek = week[0];
		} else if ("3".equals(mWeek)) {
			mWeek = week[1];
		} else if ("4".equals(mWeek)) {
			mWeek = week[2];
		} else if ("5".equals(mWeek)) {
			mWeek = week[3];
		} else if ("6".equals(mWeek)) {
			mWeek = week[4];
		} else if ("7".equals(mWeek)) {
			mWeek = week[5];
		}

		PF_dtw.setText(mMonth + mContext.getResources().getString(R.string.moon) + mDay
				+ mContext.getResources().getString(R.string.day) + "  " + mWeek + " " + mHour + ":" + mMinute);
	}

	/* 自定义显示Toast的时间 */
	private void showMyToast(Toast toast, int cnt) {
		if (cnt < 0)
			return;
		toast.show();
		execToast(toast, cnt);
	}

	private void execToast(final Toast toast, final int cnt) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				showMyToast(toast, cnt - 1);
			}
		}, 2000);
	}
}

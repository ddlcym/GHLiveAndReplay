package com.changhong.ghlive.datafactory;

import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import com.changhong.gehua.common.ChannelInfo;
import com.changhong.ghliveandreplay.R;

import android.content.Context;
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

	// channelInfo怎么传入?
	private static Banner banner;
	private static Toast bannerToast = null;
	// private static SysApplication sysApplication;
	private Context mContext;
	private View bannerView;

	ChannelInfo channelInfo;
	HashMap<String, Integer> hs = null;
	String[] sWeek;
	String sMonth;
	String sDay;

	// String PF_channel_name = new String();

	// String PF_time_P = new String();
	// String PF_time_F = new String();
	// String PF_enventName_P = new String();
	// String PF_enventName_F = new String();

	String PF_timeshiftsupport = new String();

	ImageView adv_image = null;
	TextView channel_name = null;
	TextView channel_number = null;
	TextView PF_dtw = null;// 时间
	// TextView PF_P = null;
	// TextView PF_F = null;
	// TextView textview_timeshift_support = null;
	LineProgressView progress = null;

	private Banner(Context context, ChannelInfo outterChannelInfo) {
		mContext = context.getApplicationContext();
		channelInfo = outterChannelInfo;
	}

	public void show() {
		// if (sysApplication == null) {
		// sysApplication = SysApplication.getInstance();
		// sysApplication.initBookDatabase(mContext);
		// }
		if (bannerToast == null) {
			bannerToast = new Toast(mContext);
			bannerToast.setGravity(Gravity.BOTTOM, 0, 0);
		}
		if (bannerView == null) {
			LayoutInflater mInflater = LayoutInflater.from(mContext);
			bannerView = mInflater.inflate(R.layout.banner, null);
			findView();
		}
		// channel = sysApplication.dvbDatabase.getChannel(chanId);
		// updatePFInfo();
		updateBanner();
		updateDateTime();
		bannerToast.setView(bannerView);
		bannerToast.setDuration(Toast.LENGTH_LONG);
		bannerToast.show();
	}

	// private void updatePFInfo() {

	// PF_time_P = "";
	// PF_enventName_P =
	// mContext.getResources().getString(R.string.noprogrampfinfo);

	// PF_time_F = "";
	// PF_enventName_F =
	// mContext.getResources().getString(R.string.noprogrampfinfo);
	// DVB_EPG_PF pfInfo =
	// DVB.getManager().getEpgInstance().getPfInfo(channel);

	// if (pfInfo != null) {
	// if (pfInfo.hasPresent()) {
	// PF_time_P =
	// format0Right(pfInfo.getPresent().getStartTime().getHour(), 2) + ":"
	// + format0Right(pfInfo.getPresent().getStartTime().getMinute(), 2);
	// PF_enventName_P = pfInfo.getPresent().getName();
	// }
	// if (pfInfo.hasFollowing()) {
	// PF_time_F =
	// format0Right(pfInfo.getFollowing().getStartTime().getHour(), 2) + ":"
	// + format0Right(pfInfo.getFollowing().getStartTime().getMinute(), 2);
	// PF_enventName_F = pfInfo.getFollowing().getName();
	// }
	//
	// if (PF_time_P == null || PF_time_P.equals("")) {
	// PF_time_P = mContext.getResources().getString(R.string.notimeinfo);
	// }
	// if (PF_time_F == null || PF_time_F.equals("")) {
	// PF_time_F = mContext.getResources().getString(R.string.notimeinfo);
	// }
	// if (PF_enventName_P == null || PF_enventName_P.equals("")) {
	// PF_enventName_P =
	// mContext.getResources().getString(R.string.noprogrampfinfo);
	// }
	// if (PF_enventName_F == null || PF_enventName_F.equals("")) {
	// PF_enventName_F =
	// mContext.getResources().getString(R.string.noprogrampfinfo);
	// }
	//
	// } else {
	// Log.i("mmmm", "info is == null");
	// return;
	// }
	// }

	private void findView() {
		adv_image = (ImageView) bannerView.findViewById(R.id.banner_adv_id);
		channel_name = (TextView) bannerView.findViewById(R.id.banner_channel_name_id);
		channel_number = (TextView) bannerView.findViewById(R.id.banner_service_id);
		progress = (LineProgressView) bannerView.findViewById(R.id.banner_progress_view);
		PF_dtw = (TextView) bannerView.findViewById(R.id.banner_DTW_id);

		// PF_P = (TextView) bannerView.findViewById(R.id.banner_PF_P_id);
		// PF_F = (TextView) bannerView.findViewById(R.id.banner_PF_F_id);
		// textview_timeshift_support = (TextView)
		// bannerView.findViewById(R.id.banner_tshift_support);

		// param
		// channel =
		// sysApplication.dvbDatabase.getChannel(SysApplication.iCurChannelId);

	}

	private void updateBanner() {
		// String channelname = new String();
		// TODO channelname PF_channel_name diffs?
		// channelname = channel.name; ---zyt
		if (hs == null) {
			hs = new HashMap<String, Integer>();
			// CH_TVlogo_Mapping();
		}
		progress.setProgress(getPlayingProgress());

		// 序列号
		// if (channel.logicNo < 10) {
		// service_id.setText("00" + channel.logicNo);
		// } else if (channel.logicNo <) {
		// service_id.setText("0" + channel.logicNo);
		// } else {
		// service_id.setText("" + channel.logicNo);
		// }

		channel_name.setText(channelInfo.getChannelName());
		channel_number.setText(channelInfo.getChannelNumber());
		// textview_timeshift_support.setText(getTimeShiftSupportString(channel.chanId));
		// PF_P.setText(PF_time_P + " " + PF_enventName_P);
		// PF_F.setText(PF_time_F + " " + PF_enventName_F);
	}

	private int getPlayingProgress() {
		// DVB_EPG_PF pfInfo =
		// DVB.getManager().getEpgInstance().getPfInfo(channel);
		int startTime = 0;
		// if (pfInfo == null) {
		// startTime = 0;
		// }
		// else {
		// int startH = pfInfo.getPresent().getStartTime().getHour();
		// int startM = pfInfo.getPresent().getStartTime().getMinute();
		// int startS = pfInfo.getPresent().getStartTime().getSecond();
		// startTime = startH * 60 * 60 + startM * 60 + startS;// Start
		// // time:second
		// }

		int endTime = 0;
		// if (pfInfo == null) {
		// endTime = 0;
		// } else {
		// int endH = pfInfo.getFollowing().getStartTime().getHour();
		// int endM = pfInfo.getFollowing().getStartTime().getMinute();
		// int endS = pfInfo.getFollowing().getStartTime().getSecond();
		// endTime = endH * 60 * 60 + endM * 60 + endS;// End time:second
		// }

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
}

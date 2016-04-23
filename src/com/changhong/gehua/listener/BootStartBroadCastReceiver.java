package com.changhong.gehua.listener;

import com.changhong.gehua.common.Class_Constant;
import com.changhong.gehua.common.CommonMethod;
import com.changhong.ghlive.activity.MainActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author OscarChang
 *
 */
public class BootStartBroadCastReceiver extends BroadcastReceiver {
	// start boot receiver
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stufb
		
		CommonMethod.saveChannelLastTime(Class_Constant.DEFAULT_CHANNEL_NUMBER, context);
	}
}

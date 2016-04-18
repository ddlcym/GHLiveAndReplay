package com.changhong.gehua.common;

import android.content.Context;
import android.content.Intent;

public class CommonMethod {

	public static void startSettingPage(Context outterContext) {
		Intent intent = new Intent("com.bestv.ott.action.settings");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		outterContext.startActivity(intent);
	}
}

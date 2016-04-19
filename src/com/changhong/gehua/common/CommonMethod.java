package com.changhong.gehua.common;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CommonMethod {
	
	public static String cmdFreeze = "setprop sys.media.hiplayer.freezemode " + "\""+ "freeze" + "\"";
	public static String cmdBlack = "setprop sys.media.hiplayer.freezemode " + "\""+ "black" + "\"";

	public static void startSettingPage(Context outterContext) {
		Intent intent = new Intent("com.bestv.ott.action.settings");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		outterContext.startActivity(intent);
	}
	
	 public static void excuteCmd(String cmd) throws Exception {
		Process process = Runtime.getRuntime().exec(
				new String[] { "/system/bin/sh", "-c", cmd });
		process.waitFor();

		InputStream is = process.getInputStream();
		InputStreamReader ir = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(ir);

		String line = "";
		StringBuilder sb = new StringBuilder(line);
		while ((line = br.readLine()) != null) {
			sb.append(line);
			sb.append('\n');
		}
		String result = sb.toString();
	}

	 
}

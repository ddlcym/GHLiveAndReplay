package com.changhong.gehua.update;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.JsonReader;
import android.util.Log;

public class UpdateThread implements Runnable {

	// 1:updateReceiver 2:userupdateservice
	private UpdateReceiver mUpdateReceiver;
	private Context mContext;
	private static final String TAG = "zyt";

	/**
	 * 升级信息
	 */
	private String updateVersion;

	/**
	 * 更新方式，是否强制
	 */
	private String updateWay;

	/**
	 * 更新信息
	 */
	private String updateMsgContent;

	/**
	 * 更新APK，服务器地址
	 */
	private String serverApkAddress;

	public UpdateThread(Context outterContext, UpdateReceiver outterUpdateReceiver) {
		this.mUpdateReceiver = outterUpdateReceiver;
		this.mContext = outterContext;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		// Log.i(TAG, "process is here 0-0");
		initUpdateThread();
	}

	public void initUpdateThread() {

		// Log.i(TAG, "process is inner updatethread");
		/**
		 * 注册广播
		 */
		IntentFilter homefilter = new IntentFilter();
		homefilter.addAction("MAIN_UPDATE_DOWNLOAD");
		homefilter.addAction("MAIN_UPDATE_INSTALL");
		mContext.registerReceiver(mUpdateReceiver, homefilter);

		/**
		 * 更新的时间检 如果当前更新过了就不用在更新
		 */
		UpdateLogService preferenceService = new UpdateLogService(mContext);
		String updateDate = preferenceService.getUpdateDate();

		/* zyt 手动更改上次更新的时间 ，测试代码，需要删除 */
		// GregorianCalendar gc = new GregorianCalendar();
		// gc.setTime(new Date());
		// gc.add(5, -6);
		// Date backSevenDate = gc.getTime();
		// String updateDate = (DateUtils.to10String(backSevenDate));

		if (!updateDate.equals("") && updateDate.compareTo(DateUtils.to10String(new Date())) >= 0) {
			Log.i(TAG, updateDate);
			Log.i(TAG, "process is return and  null turn out 无需更新");
			return;
		} else {
			preferenceService.saveUpdateDate();
			Log.i("zyt", "this time the update date is  " + DateUtils.to10String(new Date()));
		}

		if (UserUpdateService.updateFile.exists()) {
			/**
			 * 本地APK已存在
			 * 
			 */
			// Log.i(TAG, "apk file is exist");
			fileExistFlow();
		} else {
			/**
			 * 本地文件不存在，从服务器获得更新流程
			 */
			// Log.i(TAG, "apk file is not exist");
			fileNotExistFlow();
		}
	}

	@SuppressLint("NewApi")
	private void fileExistFlow() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Log.i(TAG, "apk file  is  exist");
					// 先比较本地下载APK和服务端最新的版本
					PackageManager pm = mContext.getPackageManager();
					PackageInfo instalPMInfo = pm.getPackageArchiveInfo(
							UserUpdateService.updateFile.getAbsolutePath().toString(), PackageManager.GET_ACTIVITIES);
					final String updateMsg = getUpdateInfo();
					if (updateMsg != null) {
						JsonReader reader = new JsonReader(new StringReader(updateMsg));
						try {
							reader.beginObject();
							while (reader.hasNext()) {
								String name = reader.nextName();
								if (name.equals("version")) {
									updateVersion = reader.nextString();
								} else if (name.equals("force")) {
									updateWay = reader.nextString();
								} else if (name.equals("updatecontent")) {
									updateMsgContent = reader.nextString();
								} else if (name.equals("url")) {
									serverApkAddress = reader.nextString();
								} else {
									reader.skipValue();
								}
							}
							reader.endObject();
						} catch (IOException e) {
							e.printStackTrace();
						}

						// 先比较本地程序和服务器的版本
						if (instalPMInfo != null) {
							int installVersionCode = instalPMInfo.versionCode;
							if (updateVersion != null && !updateVersion.equals("") && !updateVersion.equals("null")) {
								if (Integer.parseInt(updateVersion) > installVersionCode) {
									// 有更新弹框提示下载更新
									UserUpdateService.updateFile.delete();
									fileNotExistFlow();
									return;
								}
							}
						} else {
							// 文件包存在，但是又得不到信息，证明下载的文件又问题，重新下载
							UserUpdateService.updateFile.delete();
							fileNotExistFlow();
							return;
						}
					}

					// 在比较本地程序和安装APK的版本
					PackageInfo localPMInfo = pm.getPackageInfo(mContext.getPackageName(),
							PackageManager.GET_CONFIGURATIONS);
					if (localPMInfo != null) {
						int localVersionCode = localPMInfo.versionCode;
						Log.e(TAG, "安装的versionCode  " + localVersionCode);

						// 获取本地apk的versionCode
						if (instalPMInfo != null) {
							int installVersionCode = instalPMInfo.versionCode;
							Log.e(TAG, "未安装的versionCode  " + installVersionCode);

							if (installVersionCode <= localVersionCode) {
								Log.e(TAG, "本地版本号更大，下载文件删除");
								UserUpdateService.updateFile.delete();
							} else {
								// 弹框提示安装
								Intent intent = new Intent("MAIN_UPDATE_INSTALL");
								intent.putExtra("UPDATE_WAY", updateWay);// 升级的安装方式
								mContext.sendBroadcast(intent);
							}
						}
					}
				} catch (PackageManager.NameNotFoundException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	@SuppressLint("NewApi")
	private void fileNotExistFlow() {
		Log.i(TAG, "apk file  is  not exist");
		new Thread(new Runnable() {
			@Override
			public void run() {
				// 服务器获得最新的版本信息
				String updateMsg = getUpdateInfo();
				if (updateMsg != null) {
					JsonReader reader = new JsonReader(new StringReader(updateMsg));
					try {
						reader.beginObject();
						while (reader.hasNext()) {
							String name = reader.nextName();
							if (name.equals("version")) {
								updateVersion = reader.nextString();
								Log.i(TAG, "updateVersion " + updateVersion);
							} else if (name.equals("force")) {
								updateWay = reader.nextString();
							} else if (name.equals("updatecontent")) {
								updateMsgContent = reader.nextString();
								Log.i(TAG, "updateMsgContent " + updateMsgContent);
							} else if (name.equals("url")) {
								serverApkAddress = reader.nextString();
							} else {
								reader.skipValue();
							}
						}
						reader.endObject();
					} catch (IOException e) {
						e.printStackTrace();
					}

					// 比较本地的版本和服务器端的版本
					try {
						PackageManager pm = mContext.getPackageManager();
						PackageInfo localPMInfo = pm.getPackageInfo(mContext.getPackageName(),
								PackageManager.GET_CONFIGURATIONS);
						if (localPMInfo != null) {
							int versionCode = localPMInfo.versionCode;
							if (updateVersion != null && !updateVersion.equals("") && !updateVersion.equals("null")) {
								if (Integer.parseInt(updateVersion) <= versionCode) {
									// 本地版本大于等于服务器版本，无更新
								} else {
									// 本地版本小于等于服务器版本有更新弹框提示下载更新
									Log.i(TAG, "本地版本小于等于服务器版本有更新弹框提示下载更新");
									Intent intent = new Intent("MAIN_UPDATE_DOWNLOAD");
									intent.putExtra("SERVER_APK_ADDRESS", serverApkAddress);
									intent.putExtra("UPDATE_WAY", updateWay);
									mContext.sendBroadcast(intent);
								}
							}
						}
					} catch (PackageManager.NameNotFoundException e) {
						e.printStackTrace();
					}
				} else {
					// 没得到升级信息，不做处理
				}
			}
		}).start();
	}

	private String getUpdateInfo() {
		String retSrc = null;

		/**
		 * 没有连接网络，提示用户，流程结束
		 */
		if (!NetworkUtils.isWifiConnected(mContext)) {
			return null;
		}

		/**
		 * 下载最新的版本信息
		 */
		try {
			URI url = URI.create(UserUpdateService.JSON_URL);
			HttpParams params = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(params, 5000);
			HttpConnectionParams.setSoTimeout(params, 8000);
			HttpClient httpclient = new DefaultHttpClient(params);
			HttpGet httpRequest = new HttpGet(url);
			HttpResponse httpResponse = httpclient.execute(httpRequest);

			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				retSrc = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
				Log.i(TAG, "getJsonData get Json  OK !");
				Log.i(TAG, "getJsonData get Json  OK ! " + retSrc);
			} else {
				Log.e(TAG, "getJsonData  get Json  ERROR !");
				return null;
			}

			retSrc = removeBOM(retSrc);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return retSrc;
	}

	public static final String removeBOM(String data) {
		if (TextUtils.isEmpty(data)) {
			return data;
		}
		if (data.startsWith("\ufeff")) {
			return data.substring(1);
		} else {
			return data;
		}
	}
}

package com.changhong.gehua.update;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import com.changhong.gehua.update.DialogUtil.DialogBtnOnClickListener;
import com.changhong.gehua.update.DialogUtil.DialogMessage;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class UpdateReceiver extends BroadcastReceiver {

	private Context mContext;

	public UpdateReceiver(Context outterContext) {
		mContext = outterContext;
	}

	public void onReceive(final Context mContext, Intent mIntent) {
		/**
		 * 升级文件下载
		 */
		if (mIntent.getAction().equals("MAIN_UPDATE_DOWNLOAD")) {

			// 如果用户不是连接的WIFI网络，直接返回不处理
			if (!NetworkUtils.isWifiConnected(mContext)) {
				return;
			}

			final String apkAddress = mIntent.getStringExtra("SERVER_APK_ADDRESS");
			final String apkUpdateWay = mIntent.getStringExtra("UPDATE_WAY");
			// 直接开始下载程序不经过用户确认
			new Thread(new Runnable() {
				@Override
				public void run() {
					HttpURLConnection connection = null;
					try {
						UserUpdateService.downloading = true;

						/**
						 * 设置网络连接
						 */
						URL url = new URL(apkAddress);
						connection = (HttpURLConnection) url.openConnection();
						connection.setUseCaches(false);
						connection.setRequestMethod("GET");
						connection.setConnectTimeout(10000);

						/**
						 * 开始下载文件
						 */
						if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
							connection.connect();
							InputStream instream = connection.getInputStream();
							RandomAccessFile rasf = new RandomAccessFile(UserUpdateService.updateFile, "rwd");

							byte[] b = new byte[1024 * 24];
							int length = -1;
							while ((length = instream.read(b)) != -1) {
								rasf.write(b, 0, length);
								// Log.i("zyt", "apk file size is + " +
								// rasf.length());
							}

							Log.i("zyt", "apk file download is finished");
							rasf.close();
							instream.close();
							// 下载完成安装
							Intent install = new Intent("MAIN_UPDATE_INSTALL");
							install.putExtra("UPDATE_WAY", apkUpdateWay);
							mContext.sendBroadcast(install);
						}

						/**
						 * 下载完成处理
						 */
						UserUpdateService.downloading = false;
					} catch (Exception e) {
						// 异常处理
						e.printStackTrace();
						if (UserUpdateService.updateFile.exists()) {
							UserUpdateService.updateFile.delete();
						}
						UserUpdateService.downloading = false;
					} finally {
						connection.disconnect();
					}
				}
			}).start();

		} else if (mIntent.getAction().equals("MAIN_UPDATE_INSTALL")) {
			// 弹出对话框，用户选择安装最新的apk文件
			if (!UserUpdateService.updateFile.exists()) {
				return;
			}

			boolean forceInstall = Boolean.parseBoolean(mIntent.getStringExtra("UPDATE_WAY"));

			try {
				Runtime.getRuntime().exec("chmod 0777  " + UserUpdateService.updateFile.getAbsolutePath().toString());
			} catch (IOException e) {
				e.printStackTrace();
			}

			/**
			 * 如果用户点击的是直接下载，下载后直接更新，如果下载文件已经存在，就询问用户时候安
			 */
			if (!forceInstall) {
				Dialog dialog = DialogUtil.showAlertDialog(mContext, "已经为您准备好更新", "最新的版本已经下载完成,是否安装更新？",
						new DialogBtnOnClickListener() {

							@Override
							public void onSubmit(DialogMessage dialogMessage) {
								// 休息1秒安装
								// try {
								// Thread.sleep(1000);
								// } catch (InterruptedException e) {
								// e.printStackTrace();
								// }
								//
								// // 安装新的APK
								// Uri uri =
								// Uri.fromFile(UserUpdateService.updateFile);
								// Intent intent = new
								// Intent(Intent.ACTION_VIEW);
								// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								// intent.setDataAndType(uri,
								// "application/vnd.android.package-archive");
								// mContext.startActivity(intent);
								// if (dialogMessage.dialog != null &&
								// dialogMessage.dialog.isShowing()) {
								// dialogMessage.dialog.cancel();
								// }
								installAPKDirect();
							}

							@Override
							public void onCancel(DialogMessage dialogMessage) {
								if (dialogMessage.dialog != null && dialogMessage.dialog.isShowing()) {
									dialogMessage.dialog.cancel();
								}
							}
						});
			} else {
				installAPKDirect();
			}

		}
	}

	/* 直接安装APK */
	public void installAPKDirect() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// 安装新的APK
		Uri uri = Uri.fromFile(UserUpdateService.updateFile);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(uri, "application/vnd.android.package-archive");
		mContext.startActivity(intent);
	}

}

package com.changhong.ghlive.common;

import android.util.Log;

/*return encrypt url adress*/
/*Author:OscarChang*/

public class ProcessData {

	private MD5Encrypt MD5;
	private static final String serverAdress = "http://ott.yun.gehua.net.cn:8080/";
	private String MD5Key = "aidufei";
	private String conStr = "&authKey=";

	/* pdlb:频道列表 param of must */
	private String pdlbPendingStr = "msis/getChannels?";
	private String pdlbVersion = "V001";
	private String pdlbChannelVersion = "0";
	private String pdlbResolution = "800*600";
	private String pdlbTerminalType = "1";

	/* pfc:播放串 param of must */
	private String bfcPendingStr = "msis/getPlayURL?";
	private String bfcVersion = "V002";
	private String bfcResourceCode = "8061";// CCTV1
	// private String bfcProviderID = "gehua";
	// private String bfcAssetID = "8061";
	private String bfcResolution = "800*600";
	private String bfcPlayType = "2";
	private String bfcTerminalType = "4";

	/* generate channel list */
	public String getChannelList() {
		String rawPlainStr = serverAdress + pdlbPendingStr + "version=" + pdlbVersion + "&channelVersion="
				+ pdlbChannelVersion + "&resolution=" + pdlbResolution + "&terminalType=" + pdlbTerminalType;

		String encryptStr = MD5Encrypt.MD5EncryptExecute(rawPlainStr);
		String generateStr = rawPlainStr + conStr + encryptStr;
		return generateStr;
	}

	/* generate play url string */
	public String getPlayUrlString(String outterProviderID, String outterAssetID) {
		String rawPlainStr = serverAdress + bfcPendingStr + "version=" + bfcVersion + "&resourceCode=" + bfcResourceCode
				+ "&providerID=" + outterProviderID + "&assetID=" + outterAssetID + "&resolution=" + bfcResolution
				+ "&playType=" + bfcPlayType + "&terminalType=" + bfcTerminalType;
		String encryptStr = MD5Encrypt.MD5EncryptExecute(serverAdress + "msis/getPlayURL");

		String generateStr = rawPlainStr + conStr + encryptStr;

		Log.i("mmmm", "afterEncrypt:" + generateStr);

		return generateStr;
	}

}

package com.changhong.gehua.common;

import android.util.Log;

/*return encrypt url adress*/
/*Author:OscarChang*/

public class ProcessData {

	private MD5Encrypt MD5;
	private static final String serverAdress = "http://ott.yun.gehua.net.cn:8080/";
	private String MD5Key = "aidufei";
	private String conStr = "&authKey=";

	/* pdlb:Ƶ���б� param of must */
	private String pdlbPendingStr = "msis/getChannels?";
	private String pdlbVersion = "V001";
	private String pdlbChannelVersion = "0";
	private String pdlbResolution = "800*600";
	private String pdlbTerminalType = "1";

	/* for test */
	private String pgSize = "&pageSize=20";
	/*********************************************************************************/
	/* pdjmlb:Ƶ����Ŀ�б� param of must */
	private String pdjmlbPendingStr = "msis/getChannelProgram?";
	private String pdjmlbVersion = "V001";
	private String pdjmbResolution = "800*600";
	private String pdjmlbChannelResourceCode = "8061"; // not known
	private String pdjmlbTerminalType = "3";
	/*********************************************************************************/
	/* pdjmlb:��Ŀ��Ϣ : param of must */
	private String jmxxPendingStr = "msis/getCurrentProgramInfo?";
	private String jmxxVersion = "V001";
	private String jmxxChannelId = "8061";

	/*********************************************************************************/
	/* bfc:���Ŵ� param of must */
	private String bfcPendingStr = "msis/getPlayURL?";
	private String bfcVersion = "V002";
	private String bfcResourceCode = "8414";// CCTV1
	// private String bfcProviderID = "gehua";
	// private String bfcAssetID = "8061";
	private String bfcResolution = "800*600";
	private String bfcPlayType = "2";
	private String bfcTerminalType = "4";

	/* generate channel list */
	public String getChannelList() {
		String rawPlainStr = serverAdress + pdlbPendingStr + "version=" + pdlbVersion + "&channelVersion="
				+ pdlbChannelVersion + (pgSize) + "&resolution=" + pdlbResolution + "&terminalType=" + pdlbTerminalType;

		return strGETReturn(rawPlainStr);
	}

	/* generate channel's program list */
	public String getChannelProgramList() {
		String rawPlainStr = serverAdress + pdjmlbPendingStr + "version=" + pdjmlbVersion + "&resolution="
				+ pdjmbResolution + "&channelResourceCode=" + pdjmlbChannelResourceCode + "&terminalType="
				+ bfcTerminalType;

		return strGETReturn(rawPlainStr);
	}

	/* generate current channel's program info */
	public String getCurrentProgramInfo() {
		String rawPlainStr = serverAdress + jmxxPendingStr + "&version=" + jmxxVersion + "&channelId=" + jmxxChannelId;

		return strPOSTReturn(rawPlainStr, "msis/getCurrentProgramInfo");
	}

	/* generate play url string */
	public String getPlayUrlString(ChannelInfo outterchanInfo) {
		String rawPlainStr = serverAdress + bfcPendingStr + "version=" + bfcVersion + "&resourceCode="
				+ outterchanInfo.getResourceCode() + "&providerID=" + outterchanInfo.getProviderID() + "&assetID="
				+ outterchanInfo.getAssetID() + "&resolution=" + bfcResolution + "&playType=" + bfcPlayType
				+ "&terminalType=" + bfcTerminalType;

		return strPOSTReturn(rawPlainStr, "msis/getPlayURL");
	}

	/* Http GET String return */
	public String strGETReturn(String arg) {

		return (arg + conStr + MD5Encrypt.MD5EncryptExecute(arg));
	}

	public String strPOSTReturn(String arg0, String arg1) {

		return (arg0 + conStr + MD5Encrypt.MD5EncryptExecute(serverAdress + arg1));
	}

}

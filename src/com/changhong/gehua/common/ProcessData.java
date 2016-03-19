package com.changhong.gehua.common;

/*return encrypt url adress*/
/*Author:OscarChang*/

public class ProcessData {

	private MD5Encrypt MD5;
	private static final String serverAdress = "http://ott.yun.gehua.net.cn:8080/";
	private String MD5Key = "aidufei";
	private String conStr = "&authKey=";

	/* chnList:频道列表 param of must */
	private String chListPendingStr = "msis/getChannels?";
	private String chListVersion = "V001";
	private String chListChannelVersion = "0";
	private String chListResolution = "800*600";
	private String chListTerminalType = "1";
	/* for test */
	private String chListpgSize = "&pageSize=20";

	/*********************************************************************************/
	/* chPgmList:频道节目列表 param of must */
	private String chPgmListPendingStr = "msis/getChannelProgram?";
	private String chPgmListVersion = "V001";
	private String chPgmListResolution = "800*600";
	private String chPgmListChannelResourceCode = "8061"; // not known
	private String chPgmListTerminalType = "3";

	/*********************************************************************************/
	/* pgmInfo:节目信息 : param of must */
	private String pgmInfoPendingStr = "msis/getCurrentProgramInfo?";
	private String pgmInfoVersion = "V001";
	private String pgmInfoChannelId = "8061";

	/*********************************************************************************/
	/* crntPgmList：频道的当前节目单 param of must */
	private String crntPgmListVersion = "V001";
	private String crntPgmListchannelResourceCode = "8061";
	private String crntPgmListPendingStr = "msis/getChannelCurrentPrograms?";
	/*********************************************************************************/
	/* playUrl:播放串 param of must */
	private String playUrlPendingStr = "msis/getPlayURL?";
	private String playUrlVersion = "V002";
	private String playUrlResolution = "800*600";
	private String playUrlPlayType = "2";
	private String playUrlTerminalType = "4";

	/* generate channel list ： 获取频道列表 */
	public String getChannelList() {
		String rawPlainStr = serverAdress + chListPendingStr + "version=" + chListVersion + "&channelVersion="
				+ chListChannelVersion + (chListpgSize) + "&resolution=" + chListResolution + "&terminalType="
				+ chListTerminalType;

		return strGETReturn(rawPlainStr);
	}

	/* generate channel's program list */
	public String getChannelProgramList(ChannelInfo outterchanInfo) {
		String rawPlainStr = serverAdress + chPgmListPendingStr + "version=" + chPgmListVersion + "&resolution="
				+ chPgmListResolution + "&channelResourceCode=" + outterchanInfo.getResourceCode() + "&terminalType="
				+ chPgmListTerminalType;

		return strGETReturn(rawPlainStr);
	}

	/* generate current channel's program info 获取节目信息 */
	public String getCurrentProgramInfo(ChannelInfo outterchanInfo) {
		String rawPlainStr = serverAdress + pgmInfoPendingStr + "&version=" + pgmInfoVersion + "&channelId="
				+ outterchanInfo.getChannelID();

		return strPOSTReturn(rawPlainStr, "msis/getCurrentProgramInfo");
	}

	/* generate current channel's program list 获取频道的当前节目单 */
	public String getCurrentChannelProgramList(ChannelInfo outterchanInfo) {
		String rawPlainStr = serverAdress + crntPgmListPendingStr + "&version=" + crntPgmListVersion
				+ "&channelResourceCode=" + outterchanInfo.getResourceCode();

		return strGETReturn(rawPlainStr);
	}

	/* generate live play url string 获取直播播放串 */
	public String getLivePlayUrlString(ChannelInfo outterchanInfo) {
		String rawPlainStr = serverAdress + playUrlPendingStr + "version=" + playUrlVersion + "&resourceCode="
				+ outterchanInfo.getResourceCode() + "&providerID=" + outterchanInfo.getProviderID() + "&assetID="
				+ outterchanInfo.getAssetID() + "&resolution=" + playUrlResolution + "&playType=" + playUrlPlayType
				+ "&terminalType=" + playUrlTerminalType;

		return strPOSTReturn(rawPlainStr, "msis/getPlayURL");
	}
	
	/* generate replay url string 获取回看播放串 */
	public String getReplayPlayUrlString(ChannelInfo outterchanInfo, String outtershifttime, String outterShiftEnd) {

		String rawPlainStr = serverAdress + playUrlPendingStr + "version=" + playUrlVersion + "&resourceCode="
				+ outterchanInfo.getResourceCode() + "&providerID=" + outterchanInfo.getProviderID() + "&assetID="
				+ outterchanInfo.getAssetID() + "&resolution=" + playUrlResolution + "&playType=" + playUrlPlayType
				+ "&terminalType=" + playUrlTerminalType + "&shifttime=" + outtershifttime + "&shiftend="
				+ outterShiftEnd;

		return strPOSTReturn(rawPlainStr, "msis/getPlayURL");
	}

	/* Http GET String return */
	public String strGETReturn(String arg) {

		return (arg + conStr + MD5Encrypt.MD5EncryptExecute(arg));
	}

	/* Http POST String return */
	public String strPOSTReturn(String arg0, String arg1) {

		return (arg0 + conStr + MD5Encrypt.MD5EncryptExecute(serverAdress + arg1));
	}

}

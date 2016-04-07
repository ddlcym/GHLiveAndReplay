package com.changhong.gehua.common;

import java.io.Serializable;

/*Channel Infomation*/
/*Author: OscarChang*/
public class ChannelInfo implements Serializable{

	/* information about channels */
	private String channelID;
	private String channelName;
	private String channelCode;
	private String description;
	private int videoType;
	private int feeType;
	private int resourceOrder;
	private int resourceCode;
	private String channelType;
	private String cityCode;
	private String gradeCode;
	private String channelSpec;
	private int networkId;
	private int TSID;
	private int serviceid;
	private String assetID;
	private String providerID;
	private PosterInfo posterInfo;
	private String channelTypes;
	private String channelNumber;
	private String frequency;
	private String symbolRate;
	private String modulation;
	private int platform;
	
	//频道其他信息
	private String channelLogo;
	private String isTTV;
	private String isBTV;

	public String getChannelLogo() {
		return channelLogo;
	}

	public void setChannelLogo(String channelLogo) {
		this.channelLogo = channelLogo;
	}

	public String getIsTTV() {
		return isTTV;
	}

	public void setIsTTV(String isTTV) {
		this.isTTV = isTTV;
	}

	public String getIsBTV() {
		return isBTV;
	}

	public void setIsBTV(String isBTV) {
		this.isBTV = isBTV;
	}

	public String getChannelID() {
		return channelID;
	}

	public void setChannelID(String channelID) {
		this.channelID = channelID;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getChannelCode() {
		return channelCode;
	}

	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getVideoType() {
		return videoType;
	}

	public void setVideoType(int videoType) {
		this.videoType = videoType;
	}

	public int getFeeType() {
		return feeType;
	}

	public void setFeeType(int feeType) {
		this.feeType = feeType;
	}

	public int getResourceOrder() {
		return resourceOrder;
	}

	public void setResourceOrder(int resourceOrder) {
		this.resourceOrder = resourceOrder;
	}

	public int getResourceCode() {
		return resourceCode;
	}

	public void setResourceCode(int resourceCode) {
		this.resourceCode = resourceCode;
	}

	public String getChannelType() {
		return channelType;
	}

	public void setChannelType(String channelType) {
		this.channelType = channelType;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getGradeCode() {
		return gradeCode;
	}

	public void setGradeCode(String gradeCode) {
		this.gradeCode = gradeCode;
	}

	public String getChannelSpec() {
		return channelSpec;
	}

	public void setChannelSpec(String channelSpec) {
		this.channelSpec = channelSpec;
	}

	public int getNetworkId() {
		return networkId;
	}

	public void setNetworkId(int networkId) {
		this.networkId = networkId;
	}

	public int getTSID() {
		return TSID;
	}

	public void setTSID(int tSID) {
		TSID = tSID;
	}

	public int getServiceid() {
		return serviceid;
	}

	public void setServiceid(int serviceid) {
		this.serviceid = serviceid;
	}

	public String getAssetID() {
		return assetID;
	}

	public void setAssetID(String assetID) {
		this.assetID = assetID;
	}

	public String getProviderID() {
		return providerID;
	}

	public void setProviderID(String providerID) {
		this.providerID = providerID;
	}

	public PosterInfo getPosterInfo() {
		return posterInfo;
	}

	public void setPosterInfo(PosterInfo posterInfo) {
		this.posterInfo = posterInfo;
	}

	public String getChannelTypes() {
		return channelTypes;
	}

	public void setChannelTypes(String channelTypes) {
		this.channelTypes = channelTypes;
	}

	public String getChannelNumber() {
		return channelNumber;
	}

	public void setChannelNumber(String channelNumber) {
		this.channelNumber = channelNumber;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getSymbolRate() {
		return symbolRate;
	}

	public void setSymbolRate(String symbolRate) {
		this.symbolRate = symbolRate;
	}

	public String getModulation() {
		return modulation;
	}

	public void setModulation(String modulation) {
		this.modulation = modulation;
	}

	public int getPlatform() {
		return platform;
	}

	public void setPlatform(int platform) {
		this.platform = platform;
	}

}

package com.changhong.gehua.common;

import java.util.ArrayList;
import java.util.List;

/** 
 * @author  cym  
 * @date 创建时间：2016年9月19日 下午2:31:45 
 * @version 1.0 
 * @parameter   
 */
public class ChannelType {

	private int ID=-1;
	private String pramValue;//分类名称
	private String pramKey;//分类ID
	private int rank;//序列
	private List<ChannelInfo> channelList =new ArrayList<ChannelInfo>();
	
	public String getPramValue() {
		return pramValue;
	}
	public void setPramValue(String pramValue) {
		this.pramValue = pramValue;
	}
	public String getPramKey() {
		return pramKey;
	}
	public void setPramKey(String pramKey) {
		this.pramKey = pramKey;
	}
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public List<ChannelInfo> getChannelList() {
		return channelList;
	}
	public void setChannelList(List<ChannelInfo> channelList) {
		this.channelList = channelList;
	}
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	
	
	
	
}

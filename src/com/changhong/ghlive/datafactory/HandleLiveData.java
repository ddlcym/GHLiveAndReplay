package com.changhong.ghlive.datafactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.changhong.gehua.common.CacheData;
import com.changhong.gehua.common.ChannelInfo;
import com.changhong.gehua.common.ChannelType;
import com.changhong.ghlive.activity.MyApp;

import android.content.Context;

public class HandleLiveData {
	private JsonResolve JsonResolve = null;
	private Context con;

	private static HandleLiveData handliveData;

	public static HandleLiveData getInstance() {
		if (null == handliveData) {
			handliveData = new HandleLiveData();
		}
		return handliveData;
	}

	public HandleLiveData() {
		if (null == JsonResolve) {
			JsonResolve = JsonResolve.getInstance();
		}

		if (null == con) {
			con = MyApp.getContext();
		}
	}
	
	public List<ChannelType> dealChannelTypes(JSONObject json){
		List<ChannelType> list=JsonResolve.jsonToTypes(json);
		
		
		return sortChannels(list);
	}

	public List<ChannelInfo> dealChannelJson(JSONObject json) {
		List<ChannelInfo> channels = JsonResolve.jsonToChannels(json);
		for (ChannelInfo channel : channels) {
			CacheData.allChannelMap.put(channel.getChannelNumber(), channel);
			CacheData.allChannelInfo.add(channel);
		}

		// sort
//		Collections.sort(channels, new Comparator<ChannelInfo>() {
//
//			public int compare(ChannelInfo o1, ChannelInfo o2) {
//				int result = Integer.parseInt(o1.getChannelNumber()) - Integer.parseInt(o2.getChannelNumber());
//				if (result == 0) {
//					result = o1.getChannelName().compareTo(o2.getChannelName());
//				}
//				return result;
//			}
//		});

		return channels;
	}
	
	public void dealChannelExtra(JSONObject json){
		CacheData.setAllChannelExtraInfo(JsonResolve.jsonToChannelExtra(json));
	}

	
	public List<ChannelType> sortChannels(List<ChannelType> outterList) {

		Collections.sort(outterList, new Comparator<ChannelType>() {

			public int compare(ChannelType o1, ChannelType o2) {
				int result = o1.getRank() - o2.getRank();
//				if (result == 0) {
//					result = o1.getChannelName().compareTo(o2.getChannelName());
//				}
				return result;
			}
		});

		return outterList;
	}
}

package com.changhong.ghlive.datafactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONObject;

import com.changhong.gehua.common.CacheData;
import com.changhong.gehua.common.ChannelInfo;
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

	public List<ChannelInfo> dealChannelJson(JSONObject json) {
		List<ChannelInfo> channels = JsonResolve.jsonToChannels(json);
		List<Integer> channelNums = new ArrayList<Integer>();

		for (ChannelInfo channel : channels) {
			CacheData.allChannelMap.put(channel.getChannelNumber(), channel);
			CacheData.allChannelInfo.add(channel);
		}

		// 排序
		Collections.sort(channels, new Comparator<ChannelInfo>() {

			public int compare(ChannelInfo o1, ChannelInfo o2) {
				int result = Integer.parseInt(o1.getChannelNumber()) - Integer.parseInt(o2.getChannelNumber());
				if (result == 0) {
					result = o1.getChannelName().compareTo(o2.getChannelName());
				}
				return result;
			}
		});

		// for (ChannelInfo channel : channels) {
		// channelNums.add(Integer.parseInt(channel.getChannelNumber()));
		// }
		//
		// for (int i = 0; i < channelNums.size(); i++) {
		// Log.i("zyt-channenum", channelNums.get(i) + "");
		// }

		return channels;
	}

}

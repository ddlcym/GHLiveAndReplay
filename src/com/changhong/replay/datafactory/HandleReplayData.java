package com.changhong.replay.datafactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.changhong.gehua.common.CacheData;
import com.changhong.gehua.common.ChannelInfo;
import com.changhong.gehua.common.ProgramInfo;
import com.changhong.gehua.common.Utils;
import com.changhong.gehua.sqlite.DBManager;
import com.changhong.ghlive.activity.MyApp;
import com.changhong.ghlive.datafactory.HandleLiveData;
import com.changhong.ghlive.datafactory.JsonResolve;

public class HandleReplayData {
	private JsonResolve jsonResolve = null;
	private Context con;

	private static HandleReplayData handleReplayData;

	public static HandleReplayData getInstance() {
		if (null == handleReplayData) {
			handleReplayData = new HandleReplayData();
		}
		return handleReplayData;
	}

	public HandleReplayData() {
		if (null == jsonResolve) {
			jsonResolve = JsonResolve.getInstance();
		}

		if (null == con) {
			con = MyApp.getContext();
		}
	}

	public Map<String, List<ProgramInfo>> dealChannelJson(JSONObject json) {

		List<ProgramInfo> pointChannelList =  jsonResolve.jsonToPrograms(json);
		// Log.i("mmmm", "programs"+programs.toString());
		if(pointChannelList!=null&&pointChannelList.size()>0){
			DBManager.getInstance(MyApp.getContext()).clearChannelEPG(pointChannelList.get(0).getChannelID());
			DBManager.getInstance(MyApp.getContext()).insertPrograms(pointChannelList);
			return sortPrograms(pointChannelList);
		}else{
			return null;
		}
		
	}

	public Map<String, List<ProgramInfo>> sortPrograms(List<ProgramInfo> list) {
		Map<String, List<ProgramInfo>> mapPros = new HashMap<String, List<ProgramInfo>>();
		LinkedList<String> dayMonth = new LinkedList<String>();
		for (ProgramInfo pro : list) {
			Date dt = pro.getEventDate();
			String date = Utils.dateToString(dt);

			if (!dayMonth.contains(date)) {
				dayMonth.addFirst(date);
				mapPros.put(date, new ArrayList<ProgramInfo>());

			}
			if (Utils.isOutOfDate(pro)) {
				mapPros.get(date).add(pro);
			}
		}
		CacheData.setAllProgramMap(mapPros);
		CacheData.setDayMonths(dayMonth);
		return mapPros;
	}
}

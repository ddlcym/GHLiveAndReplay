package com.changhong.gehua.sqlite;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebChromeClient.CustomViewCallback;

import com.changhong.gehua.common.ChannelInfo;
import com.changhong.gehua.common.ChannelType;
import com.changhong.gehua.common.ProgramInfo;
import com.changhong.gehua.common.Utils;

/*
 * 读写数据库
 */

public class DBManager {

	public static DBManager dBManager;
	public static SQLiteDatabase db;
	private Context mcontext;

	private final String DB_TABLE_COL = "_id," + "channelID," + "channelName,"
			+ "channelCode," + "description," + "videoType," + "feeType,"
			+ "resourceOrder," + "resourceCode," + "channelType," + "cityCode,"
			+ "gradeCode," + "channelSpec," + "networkId," + "TSID,"
			+ "serviceid," + "assetID," + "providerID,"
			// +"posterInfo,"
			+ "channelTypes," + "channelNumber," + "frequency," + "symbolRate,"
			+ "modulation," + "platform," + "channelLogo," + "isTTV,"// 是否支持时移
			+ "isBTV";// 时移时长

	public static DBManager getInstance(Context context) {
		if (dBManager == null) {
			dBManager = new DBManager(context);
		}
		return dBManager;
	}

	public DBManager() {
	}

	public DBManager(Context context) {
		mcontext = context;
		db = DatabaseContainer.getInstance(context).getWritableDatabase();
	}

	public void checkUserSQLiteDatabase() {
		if (null == db) {
			db = DatabaseContainer.getInstance(mcontext).getWritableDatabase();
		}
	}

	public void insertChannelList(List<ChannelInfo> list) {
		if (null == list || list.isEmpty()) {
			return;
		}
		checkUserSQLiteDatabase();
		db.beginTransaction();
		try {
			for (int i = 0; i < list.size(); i++) {
				ChannelInfo channel = list.get(i);

				insertChannels(channel);

			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
	}

	public List<ChannelInfo> queryChannelList() {
		List<ChannelInfo> list = new ArrayList<ChannelInfo>();
		checkUserSQLiteDatabase();
		Cursor cursor = queryTheCursor(DatabaseContainer.TABLE_CHANNELS);

		while (cursor.moveToNext()) {
			ChannelInfo channel = convertToChannel(cursor);
			if(!TextUtils.isEmpty(channel.getChannelID())){ 
				list.add(channel);
			}
		}
		cursor.close();
		return list;
	}

	private void insertChannels(ChannelInfo channel) {
		checkUserSQLiteDatabase();
		ContentValues cv = new ContentValues();
		cv.put("channelID", channel.getChannelID());
		cv.put("channelName", channel.getChannelName());
		cv.put("channelCode", channel.getChannelCode());
		cv.put("description", channel.getDescription());
		cv.put("videoType", channel.getVideoType());
		cv.put("feeType", channel.getFeeType());
		cv.put("resourceOrder", channel.getResourceOrder());
		cv.put("resourceCode", channel.getResourceCode());
		cv.put("channelType", channel.getChannelType());
		cv.put("cityCode", channel.getCityCode());
		cv.put("gradeCode", channel.getGradeCode());
		cv.put("channelSpec", channel.getChannelSpec());
		cv.put("networkId", channel.getNetworkId());
		cv.put("TSID", channel.getTSID());
		cv.put("serviceid", channel.getServiceid());
		cv.put("assetID", channel.getAssetID());
		cv.put("providerID", channel.getProviderID());
		cv.put("channelTypes", Utils.listToString(channel.getChannelTypes()));
		cv.put("channelNumber", channel.getChannelNumber());
		cv.put("frequency", channel.getFrequency());
		cv.put("symbolRate", channel.getSymbolRate());
		cv.put("modulation", channel.getModulation());
		cv.put("platform", channel.getPlatform());
		cv.put("channelLogo", channel.getChannelLogo());
		cv.put("isTTV", channel.getIsTTV());
		cv.put("isBTV", channel.getIsBTV());
		db.insert(DatabaseContainer.TABLE_CHANNELS, null, cv);
	}

	// 更新频道里的某个值
	public int updateChannel(int ri_chanId, String rstr_Key, String rstr_Value) {
		checkUserSQLiteDatabase();
		ContentValues cv = new ContentValues();
		String args = "" + ri_chanId;
		if (db == null) {
			Log.e("mmmm", "maybe database is not created.");
			return 1;
		}

		cv.put(rstr_Key, rstr_Value);

		String whereClause = "id=?";
		String[] whereArgs = { args };

		db.update(DatabaseContainer.TABLE_CHANNELS, cv, whereClause, whereArgs);

		return 0;
	}

	/**
	 * get channel by logicNo
	 * 
	 */
	public ChannelInfo queryChannelByLogicNo(int logicNo) {
		Cursor cur;
		String[] col = { DB_TABLE_COL };
		String selection = "channelNumber" + "=" + logicNo;

		// Log.i(TAG, "getChannel-->channel id: " + ri_chanId);
		checkUserSQLiteDatabase();
		cur = db.query(DatabaseContainer.TABLE_CHANNELS, col, selection, null,
				null, null, null);

		if (cur == null || !cur.moveToFirst()) {
			Log.e("mmmm", "can't find valid channel.");
			if (cur != null && !cur.isClosed()) {
				cur.close();
			}
			return null;
		}

		ChannelInfo mChannel = convertToChannel(cur);

		if (cur != null && !cur.isClosed()) {
			cur.close();
		}

		return mChannel;
	}

	/*
	 * clear all channels
	 */
	public void clearTableChannels() {
		checkUserSQLiteDatabase();
		db.delete(DatabaseContainer.TABLE_CHANNELS, null, null);
	}

	/**
	 * query all channel_info, return cursor
	 * 
	 * @return Cursor
	 */
	public Cursor queryTheCursor(String table) {
		checkUserSQLiteDatabase();
		Cursor c = db.rawQuery("SELECT * FROM " + table, null);
		return c;
	}

	/**
	 * close database
	 */
	public void closeDB() {
		if (db != null) {
			db.close();
		}
	}

	private ChannelInfo convertToChannel(Cursor cur) {
		int index = 0;

		ChannelInfo channel = new ChannelInfo();

		index = cur.getColumnIndex("_id");
		if (index >= 0) {
			channel.setID(cur.getInt(index));
		}

		index = cur.getColumnIndex("channelID");
		if (index >= 0) {
			channel.setChannelID(cur.getString(index));
		}
		index = cur.getColumnIndex("channelName");
		if (index >= 0) {
			channel.setChannelName(cur.getString(index));
		}
		index = cur.getColumnIndex("channelCode");
		if (index >= 0) {
			channel.setChannelCode(cur.getString(index));
		}

		index = cur.getColumnIndex("description");
		if (index >= 0) {
			channel.setDescription(cur.getString(index));
		}

		index = cur.getColumnIndex("videoType");
		if (index >= 0) {
			channel.setVideoType(cur.getInt(index));
		}

		index = cur.getColumnIndex("feeType");
		if (index >= 0) {
			channel.setFeeType(cur.getInt(index));
		}

		index = cur.getColumnIndex("resourceOrder");
		if (index >= 0) {
			channel.setResourceOrder(cur.getInt(index));
		}

		index = cur.getColumnIndex("resourceCode");
		if (index >= 0) {
			channel.setResourceCode(cur.getInt(index));
		}

		index = cur.getColumnIndex("channelType");
		if (index >= 0) {
			channel.setChannelType(cur.getString(index));
		}

		index = cur.getColumnIndex("cityCode");
		if (index >= 0) {
			channel.setCityCode(cur.getString(index));
		}

		index = cur.getColumnIndex("gradeCode");
		if (index >= 0) {
			channel.setGradeCode(cur.getString(index));
		}

		index = cur.getColumnIndex("channelSpec");
		if (index >= 0) {
			channel.setChannelSpec(cur.getString(index));
		}

		index = cur.getColumnIndex("networkId");
		if (index >= 0) {
			channel.setNetworkId(cur.getInt(index));
		}

		index = cur.getColumnIndex("TSID");
		if (index >= 0) {
			channel.setTSID(cur.getInt(index));
		}

		index = cur.getColumnIndex("serviceid");
		if (index >= 0) {
			channel.setServiceid(cur.getInt(index));
		}

		index = cur.getColumnIndex("assetID");
		if (index >= 0) {
			channel.setAssetID(cur.getString(index));
		}

		index = cur.getColumnIndex("providerID");
		if (index >= 0) {
			channel.setProviderID(cur.getString(index));
		}

		// index = cur.getColumnIndex("posterInfo");
		// if (index >= 0)
		// {
		// channel.setPosterInfo(cur.getInt(index));
		// }

		index = cur.getColumnIndex("channelTypes");
		if (index >= 0) {
			channel.setChannelTypes(Utils.stringToList(cur.getString(index)));
		}

		index = cur.getColumnIndex("channelNumber");
		if (index >= 0) {
			channel.setChannelNumber(cur.getString(index));
		}

		index = cur.getColumnIndex("frequency");
		if (index >= 0) {
			channel.setFrequency(cur.getString(index));
		}

		index = cur.getColumnIndex("symbolRate");
		if (index >= 0) {
			channel.setSymbolRate(cur.getString(index));
		}

		index = cur.getColumnIndex("modulation");
		if (index >= 0) {
			channel.setModulation(cur.getString(index));
		}

		index = cur.getColumnIndex("platform");
		if (index >= 0) {
			channel.setPlatform(cur.getInt(index));
		}

		index = cur.getColumnIndex("channelLogo");
		if (index >= 0) {
			channel.setChannelLogo(cur.getString(index));
		}

		index = cur.getColumnIndex("isTTV");
		if (index >= 0) {
			channel.setIsTTV(cur.getString(index));
		}

		index = cur.getColumnIndex("isBTV");
		if (index >= 0) {
			channel.setIsBTV(cur.getString(index));
		}

		return channel;
	}

	/*
	 * 
	 * 
	 * =====================================EPG相关方法==============================
	 * ========================
	 */

	public List<ProgramInfo> queryEPGByChannelID(String channelID) {
		checkUserSQLiteDatabase();
		List<ProgramInfo> list = new ArrayList<ProgramInfo>();
		Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseContainer.TABLE_EPG+" WHERE channelID= ?", new String[]{channelID});

		while (cursor.moveToNext()) {
			ProgramInfo pro = convertToProgram(cursor);
			list.add(pro);
		}
		cursor.close();
		return list;
	}
	
	public void insertPrograms(List<ProgramInfo> list){
		checkUserSQLiteDatabase();
		if(null==list||list.isEmpty()){
			return;
		}
		
		db.beginTransaction();
		try {
			for (int i = 0; i < list.size(); i++) {
				ProgramInfo program = list.get(i);
				insertProgram(program);

			}
			db.setTransactionSuccessful();
		}

		catch (Exception e) {
			e.printStackTrace();
		}

		finally {
			db.endTransaction();
		}
	}

	public void insertProgram(ProgramInfo program) {
		checkUserSQLiteDatabase();

		ContentValues cv = new ContentValues();
		cv.put("programId", program.getProgramId());
		cv.put("channelID", program.getChannelID());
		cv.put("eventDate", Utils.dateToFullStr(program.getEventDate()));
		cv.put("beginTime", Utils.dateToFullStr(program.getBeginTime()));
		cv.put("endTime", Utils.dateToFullStr(program.getEndTime()));
		cv.put("eventName", program.getEventName());
		cv.put("eventDesc", program.getEventDesc());
//		cv.put("keyWord", program.getKeyWord());
//		cv.put("isBook", program.getIsBook());
//		cv.put("isRecommend", program.getIsRecommend());
//		cv.put("playCount", program.getPlayCount());
		cv.put("assetID", program.getAssetID());
		// cv.put("poster", program.getPoster());
//		cv.put("playtime", program.getPlaytime());
//		cv.put("volumeName", program.getVolumeName());
		cv.put("channelResourceCode", program.getChannelResourceCode());
		cv.put("videoType", program.getVideoType());
		cv.put("providerID", program.getProviderID());
		cv.put("channelName", program.getChannelName());
//		cv.put("status", program.getStatus());
//		cv.put("viewLevel", program.getViewLevel());
		db.insert(DatabaseContainer.TABLE_EPG, null, cv);
	}

	public void clearTableEPG() {
		checkUserSQLiteDatabase();
		db.delete(DatabaseContainer.TABLE_EPG, null, null);
	}
	
	public void clearChannelEPG(String channelID){
		checkUserSQLiteDatabase();
		String[] args={channelID};
		db.delete(DatabaseContainer.TABLE_EPG, "channelID=?", args);
	}

	private ProgramInfo convertToProgram(Cursor cur) {
		ProgramInfo pro = new ProgramInfo();
		int index = -1;

		index = cur.getColumnIndex("_id");
		if (index >= 0) {
			pro.setID(cur.getInt(index));
		}

		index = cur.getColumnIndex("programId");
		if (index >= 0) {
			pro.setProgramId(cur.getInt(index));
		}

		index = cur.getColumnIndex("channelID");
		if (index >= 0) {
			pro.setChannelID(cur.getString(index));
		}

		index = cur.getColumnIndex("eventDate");
		if (index >= 0) {
			try {
				pro.setEventDate(Utils.strToFullDate(cur.getString(index)));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		index = cur.getColumnIndex("beginTime");
		if (index >= 0) {
			try {
				pro.setBeginTime(Utils.strToFullDate(cur.getString(index)));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		index = cur.getColumnIndex("endTime");
		if (index >= 0) {
			try {
				pro.setEndTime(Utils.strToFullDate(cur.getString(index)));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		index = cur.getColumnIndex("eventName");
		if (index >= 0) {
			pro.setEventName(cur.getString(index));
		}

		index = cur.getColumnIndex("eventDesc");
		if (index >= 0) {
			pro.setEventDesc(cur.getString(index));
		}

//		index = cur.getColumnIndex("keyWord");
//		if (index >= 0) {
//			pro.setKeyWord(cur.getString(index));
//		}
//
//		index = cur.getColumnIndex("isBook");
//		if (index >= 0) {
//			pro.setIsBook(cur.getInt(index));
//		}
//
//		index = cur.getColumnIndex("isRecommend");
//		if (index >= 0) {
//			pro.setIsRecommend(cur.getInt(index));
//		}
//
//		index = cur.getColumnIndex("playCount");
//		if (index >= 0) {
//			pro.setPlayCount(cur.getInt(index));
//		}

		index = cur.getColumnIndex("assetID");
		if (index >= 0) {
			pro.setAssetID(cur.getString(index));
		}

		// index = cur.getColumnIndex("poster");
		// if (index >= 0) {
		// pro.set(cur.getString(index));
		// }

//		index = cur.getColumnIndex("playtime");
//		if (index >= 0) {
//			pro.setPlaytime(cur.getInt(index));
//		}
//
//		index = cur.getColumnIndex("volumeName");
//		if (index >= 0) {
//			pro.setVolumeName(cur.getString(index));
//		}

		index = cur.getColumnIndex("channelResourceCode");
		if (index >= 0) {
			pro.setChannelResourceCode(cur.getInt(index));
		}

		index = cur.getColumnIndex("videoType");
		if (index >= 0) {
			pro.setVideoType(cur.getString(index));
		}

		index = cur.getColumnIndex("providerID");
		if (index >= 0) {
			pro.setProviderID(cur.getString(index));
		}

		index = cur.getColumnIndex("channelName");
		if (index >= 0) {
			pro.setChannelName(cur.getString(index));
		}

//		index = cur.getColumnIndex("status");
//		if (index >= 0) {
//			pro.setStatus(cur.getInt(index));
//		}
//
//		index = cur.getColumnIndex("viewLevel");
//		if (index >= 0) {
//			pro.setViewLevel(cur.getString(index));
//		}

		return pro;
	}
	
	public boolean isNeedUpdateEPG(ChannelInfo channel){
		checkUserSQLiteDatabase();
		String date =Utils.dateToStringSQL(new Date());
		String cmd="SELECT * FROM " + DatabaseContainer.TABLE_EPG+" WHERE eventDate>'"+date+"' and channelID="+channel.getChannelID();
		Log.i("mmmm", "dbmanager-cmd: "+cmd);
		Cursor cursor=db.rawQuery(cmd, null);
		boolean flag=(0==cursor.getCount());
		cursor.close();
		return flag;
	}

	/*
	 * =====================================channel types=======================
	 */
	public void insertChannelType(ChannelType type) {
		checkUserSQLiteDatabase();
		ContentValues cv = new ContentValues();
		cv.put("pramValue", type.getPramValue());
		cv.put("pramKey", type.getPramKey());
		cv.put("rank", type.getPramKey());
		db.insert(DatabaseContainer.TABLE_CHANNEL_TYPES, null, cv);

	}

	public void insertChannelType(List<ChannelType> list) {
		checkUserSQLiteDatabase();
		db.beginTransaction();
		try {
			for (int i = 0; i < list.size(); i++) {
				ChannelType type = list.get(i);
				insertChannelType(type);

			}
			db.setTransactionSuccessful();
		}

		catch (Exception e) {
			e.printStackTrace();
		}

		finally {
			db.endTransaction();
		}
	}

	public List<ChannelType> queryChannelType() {
		List<ChannelType> list = new ArrayList<ChannelType>();
		checkUserSQLiteDatabase();
		Cursor cursor = queryTheCursor(DatabaseContainer.TABLE_CHANNEL_TYPES);

		while (cursor.moveToNext()) {
			ChannelType ct = convertToChannelType(cursor);
			list.add(ct);
		}
		cursor.close();

		return list;
	}

	private ChannelType convertToChannelType(Cursor cur) {
		ChannelType ct = new ChannelType();
		int index = -1;

		index = cur.getColumnIndex("_id");
		if (index >= 0) {
			ct.setID(cur.getInt(index));
		}

		index = cur.getColumnIndex("pramValue");
		if (index >= 0) {
			ct.setPramValue(cur.getString(index));
		}

		index = cur.getColumnIndex("pramKey");
		if (index >= 0) {
			ct.setPramKey(cur.getString(index));
		}

		index = cur.getColumnIndex("rank");
		if (index >= 0) {
			ct.setRank(cur.getInt(index));
		}
		return ct;
	}

}

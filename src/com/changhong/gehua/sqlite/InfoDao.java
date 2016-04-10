package com.changhong.gehua.sqlite;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;

import com.changhong.gehua.common.ChannelInfo;
import com.changhong.ghlive.activity.MyApp;

public class InfoDao {
	
	public static InfoDao infoDao;
	public static  SQLiteDatabase database;
	
	public static InfoDao getInstance() {
        if (infoDao == null) {
        	infoDao = new InfoDao();
        }
        return infoDao;
    }
	
	public InfoDao(){
		database = MyApp.getDatabaseContainer().openEPGDatabase();
	}

	public static void insertChannel(ChannelInfo channelInfo){

		database.execSQL("insert into person(name, age) values(?,?)", new Object[]{"特", 4});   
		  
		database.close();  
		
	}
	
	public List<ChannelInfo> getChannelList(){
		List<ChannelInfo> list=new ArrayList<ChannelInfo>();
//		Cursor cursor = database.rawQuery("select i_ChannelIndex, str_eventName, str_startTime, str_endTime,str_ChannelName from epg_information where i_weekIndex = ? and str_startTime < ? and str_endTime >= ?",
//                new String[]{weekIndex, currentTime, currentTime});
//
//        while (cursor.moveToNext()) {
//            String channelIndex = cursor.getString(0);
//            String eventName = cursor.getString(1);
//            String startTime = cursor.getString(2);
//            String endTime = cursor.getString(3);
//            String channelName = cursor.getString(4);
//            // 获取该图片的父路径名
//            Program program = new Program(channelIndex, eventName, startTime, endTime,channelName);
//            currentPlaying.put(channelName, program);
//        }
//        cursor.close();
		return list;
	}
	
}

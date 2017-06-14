package com.changhong.gehua.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by cym
 *
 * 数据库支持的数据类型，TEXT，VARCHAR, INTEGER, REAL, BLOG,
 */
public class DatabaseContainer extends SQLiteOpenHelper {
	
	/*
	 * table name 
	 */
	public final static String TABLE_CHANNELS="channel_info";
	public final static String TABLE_EPG="epg_info";
	public final static String TABLE_CHANNEL_TYPES="channel_type";
	

    private final static String DATABASE_NAME = "iptv_info.db";
    


    private static int CURRENT_VERSION = 1;

    private SQLiteDatabase epgDatabase;
    private static DatabaseContainer databaseContainer = null;

    public DatabaseContainer(Context context) {
        super(context, DATABASE_NAME, null, CURRENT_VERSION);
    }
    
    public static DatabaseContainer getInstance(Context context) {
        if (databaseContainer == null) {
            databaseContainer = new DatabaseContainer(context);
        }
        return databaseContainer;
    }

//    public SQLiteDatabase openEPGDatabase() {
//        try {
//            File epgDBFile = new File(MyApp.epgDBCachePath.getAbsolutePath(), EPG_DATABASE_NAME);
//            if (epgDBFile.exists() && epgDatabase == null) {
//                epgDatabase = SQLiteDatabase.openDatabase(epgDBFile.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return epgDatabase;
//    }

    /**
     * 当更新了EPG的时候，都需要重新打开一次DB
     */
//    public void reopenEPGDatabase() {
//        try {
//            File epgDBFile = new File(MyApp.epgDBCachePath.getAbsolutePath(), EPG_DATABASE_NAME);
//            if (epgDBFile.exists()) {
//                epgDatabase = SQLiteDatabase.openDatabase(epgDBFile.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 数据库第一次创建的时候别调用的
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_EPG+
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "programId VARCHAR(50), " +
                "channelID VARCHAR(50), " +
                "eventDate DATETIME, " +
                "beginTime DATETIME, " +
                "endTime DATETIME, " +
                "eventName VARCHAR(100), " +
                "eventDesc VARCHAR(100), " +
                "keyWord VARCHAR(100), " +
                "isBook VARCHAR(100), " +
                "isRecommend VARCHAR(100), " +
                "playCount VARCHAR(100), " +
                "assetID VARCHAR(100), " +
                "poster VARCHAR(100), " +
                "playtime VARCHAR(100), " +
                "volumeName VARCHAR(100), " +
                "channelResourceCode VARCHAR(100), " +
                "videoType VARCHAR(100), " +
                "providerID VARCHAR(100), " +
                "channelName VARCHAR(100), " +
                "status VARCHAR(100), " +
                "viewLevel VARCHAR(100))");
        
        db.execSQL("CREATE TABLE  " +TABLE_CHANNELS+
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "channelID VARCHAR(100), " +
                "channelName VARCHAR(100), " +
                "channelCode VARCHAR(100), " +
                "description VARCHAR(100), " +
                "videoType INTEGER, " +
                "feeType INTEGER, " +
                "resourceOrder INTEGER, " +
                "resourceCode INTEGER, " +
                "channelType VARCHAR(100), " +
                "cityCode VARCHAR(100), " +
                "gradeCode VARCHAR(100), " +
                "channelSpec VARCHAR(100), " +
                "networkId INTEGER, " +
                "TSID INTEGER, " +
                "serviceid INTEGER, " +
                "assetID VARCHAR(100), " +
                "providerID VARCHAR(100), " +
//                "posterInfo VARCHAR(100), " +
                "channelTypes VARCHAR(100), " +
                "channelNumber VARCHAR(100), " +
                "frequency VARCHAR(100), " +
                "symbolRate VARCHAR(100), " +
                "modulation VARCHAR(100), " +
                "platform INTEGER, " +
                "channelLogo VARCHAR(100), " +
                "isTTV VARCHAR(100), " +//是否支持时移
                "isBTV VARCHAR(100)) " );//时移时长
                
        db.execSQL("CREATE TABLE  " +TABLE_CHANNEL_TYPES+
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
        		"pramValue VARCHAR(50),"+
                "pramKey VARCHAR(50),"+
        		"rank INTEGER)");
        
    }

    /**
     * 数据库版本更新的时候被调用
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//    	db.execSQL("ALTER TABLE channel_info ADD COLUMN other STRING"); 
    }
}

package com.changhong.gehua.sqlite;

import java.io.File;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.changhong.ghlive.activity.MyApp;

/**
 * Created by cym
 *
 * 数据库支持的数据类型，TEXT，VARCHAR, INTEGER, REAL, BLOG,
 */
public class DatabaseContainer extends SQLiteOpenHelper {

    private final static String DATABASE_NAME = "ghlive_replay.db";

    private final static String EPG_DATABASE_NAME = "epg_database.db";

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

    public SQLiteDatabase openEPGDatabase() {
        try {
            File epgDBFile = new File(MyApp.epgDBCachePath.getAbsolutePath(), EPG_DATABASE_NAME);
            if (epgDBFile.exists() && epgDatabase == null) {
                epgDatabase = SQLiteDatabase.openDatabase(epgDBFile.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return epgDatabase;
    }

    /**
     * 当更新了EPG的时候，都需要重新打开一次DB
     */
    public void reopenEPGDatabase() {
        try {
            File epgDBFile = new File(MyApp.epgDBCachePath.getAbsolutePath(), EPG_DATABASE_NAME);
            if (epgDBFile.exists()) {
                epgDatabase = SQLiteDatabase.openDatabase(epgDBFile.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 数据库第一次创建的时候别调用的
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE epg_info" +
                "(programId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "channelID VARCHAR(50), " +
                "eventDate VARCHAR(50), " +
                "beginTime VARCHAR(50), " +
                "endTime VARCHAR(50), " +
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
        
        db.execSQL("CREATE TABLE  channel_info" +
                "(channelID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "channelName VARCHAR(100), " +
                "channelCode VARCHAR(100), " +
                "description VARCHAR(100), " +
                "videoType VARCHAR(100), " +
                "feeType VARCHAR(100), " +
                "resourceOrder VARCHAR(100), " +
                "resourceCode VARCHAR(100), " +
                "channelType VARCHAR(100), " +
                "cityCode VARCHAR(100), " +
                "gradeCode VARCHAR(100), " +
                "channelSpec VARCHAR(100), " +
                "networkId VARCHAR(100), " +
                "TSID VARCHAR(100), " +
                "serviceid VARCHAR(100), " +
                "assetID VARCHAR(100), " +
                "providerID VARCHAR(100), " +
                "posterInfo VARCHAR(100), " +
                "channelTypes VARCHAR(100), " +
                "channelNumber VARCHAR(100), " +
                "frequency VARCHAR(100), " +
                "symbolRate VARCHAR(100), " +
                "modulation VARCHAR(100), " +
                "platform VARCHAR(100), " +
                "channelLogo VARCHAR(100), " +
                "isTTV VARCHAR(100), " +//是否支持时移
                "isBTV VARCHAR(100), " +//是否支持回看
                "isNpvr VARCHAR(100), " +//是否支持nPVR
                "liveOrderFlag VARCHAR(100), " +//直播频道是否已订购
        		 "TTVLen VARCHAR(30))");//时移时长

    }

    /**
     * 数据库版本更新的时候被调用
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}

package com.changhong.ghlive.activity;

import java.io.File;

import com.changhong.gehua.sqlite.PathGenerateUtils;

import android.app.Application;

public class MyApp extends Application {
	 private static MyApp instance;  
	 public static File epgDBCachePath;
     
	    public static MyApp getContext(){  
	        return instance;  
	    }  
	  
	    @Override  
	    public void onCreate() {  
	        super.onCreate();  
	        instance=this;  
	        epgDBCachePath = PathGenerateUtils.getEPGDirectory(this);
	    }  
}

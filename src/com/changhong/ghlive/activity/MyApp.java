package com.changhong.ghlive.activity;

import java.io.File;

import android.app.Application;
import android.content.Context;

import com.changhong.gehua.sqlite.DatabaseContainer;
import com.changhong.gehua.sqlite.PathGenerateUtils;

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
	    
	    
	    public static DatabaseContainer getDatabaseContainer() {
	        return DatabaseContainer.getInstance(instance);
	    }
}

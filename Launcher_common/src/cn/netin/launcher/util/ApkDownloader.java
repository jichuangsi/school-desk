package cn.netin.launcher.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;

public class ApkDownloader {
	private static ApkDownloader sInstance = null ;
	private static Context mContext = null ;
	private static List sDownloadList = new ArrayList() ;
	
	private ApkDownloader(Context context){
		mContext = context ;
	}
	
	public ApkDownloader getInstance(Context context){
		if (sInstance == null){
			sInstance = new ApkDownloader(context) ;
		}
		
		return sInstance ;
	}
	
	public void download(String url){
		
	}
	
	
	
}

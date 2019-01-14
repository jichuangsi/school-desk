package com.hanceedu.common;

import java.util.HashMap;
import java.util.Map;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class HanceApplication extends Application {

	private Handler mAppHandler = null ;
	private Map<String, Object> mAppData = new HashMap<String, Object>() ;
	private Map<String, Handler> mHandlerMap = new HashMap<String, Handler>() ;
	public static final String DEFAULT_HANDLER = "DEFAULT_HANDLER" ;
	
	public Handler getHandler() {
		return mAppHandler;
	}
	public void setHandler(Handler handler) {
		//System.out.println("setHandler") ;
		this.mAppHandler = handler;
	}
	
	public void setHandler(String key, Handler handler) {
		mHandlerMap.put(key, handler) ;
	}

	public Map<String, Object> getAppDataMap() {
		return mAppData ;
	}

	public Object getAppData(String key) {
		return mAppData.get(key) ;
	}

	public void putAppData(String key, Object value) {
		mAppData.put(key, value) ;
	}

	public void removeAppData(String key) {
		mAppData.remove(key) ;
	}

	public void clearAppData() {
		mAppData.clear() ;
	}

	public void sendMessage( int what, int arg1, int arg2, String... stringArray) {
		sendMessage(DEFAULT_HANDLER,  what,  arg1,  arg2, 0,  stringArray) ;
	}
	public void sendMessage(String handlerKey,   int what, int arg1, int arg2, String... stringArray) {
		sendMessage(handlerKey,  what,  arg1,  arg2, 0,  stringArray) ;
	}
	
	public void sendMessage(String handlerKey, int what, int arg1, int arg2, int delayMillis, String... stringArray) {
		Handler h = null ;
		if (DEFAULT_HANDLER.equals(handlerKey) ){
			h = mAppHandler ;
		}else{
			h = mHandlerMap.get(handlerKey) ;
		}
		if (h == null) {
			System.out.println("############## HanceApplication appHandler is null ! ######################");
			return ;
		}
		Message msg = Message.obtain() ;
		msg.what = what ;
		msg.arg1 = arg1 ;
		msg.arg2 = arg2 ;
		if (stringArray.length > 0) {
			Bundle data = new Bundle() ;
			for (int i = 0 ; i < stringArray.length; i++)  {
				data.putString("str" + i, stringArray[i]) ;
			}

			msg.setData(data) ; 			
		}
		if (delayMillis > 0) {
			h.sendMessageDelayed(msg, delayMillis) ;
		}else{
			h.sendMessage(msg) ;
		}

	}

	public void release() {
		//mAppHandler = null ;
		mAppData.clear() ;
		//mAppData = null ;
		mHandlerMap.clear();
	}
	

} 
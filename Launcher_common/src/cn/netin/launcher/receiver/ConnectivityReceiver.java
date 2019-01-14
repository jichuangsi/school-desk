package cn.netin.launcher.receiver;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.util.Log;



public  class ConnectivityReceiver extends BroadcastReceiver {
	private static final String TAG = "EL ConnectivityReceiver" ;
	private final Application mApplication;

	public static final int TYPE_MOBILE = 0 ;
	public static final int TYPE_WIFI = 1 ;

	ConnectivityListener mListener = null ;


	public interface ConnectivityListener {
		void onChange(int type,  boolean isConnected) ;
	}

	public ConnectivityReceiver(Application application, ConnectivityListener listener) {
		mApplication = application;
		mListener = listener ;

		IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		application.registerReceiver(this, filter);			
		//Log.d(TAG, "ConnectivityReceiver : registerReceiver ") ;	
	}

	public boolean isConnected(int type) {
	
		boolean connected = false;

		ConnectivityManager connectivityManager = (ConnectivityManager)  mApplication.getSystemService(Context.CONNECTIVITY_SERVICE);  
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();    
		if(info != null && info.isAvailable()) {  
			String name = info.getTypeName();  
			Log.d(TAG, "当前网络名称：" + name); 
			connected = true;
		} else {  
			Log.d(TAG, "没有可用网络");  
		}  	

		return connected ;
	}


	@Override
	public void onReceive(Context context, Intent intent) {
		//Log.d(TAG, "onReceive") ;	
		if (mListener != null) {
			mListener.onChange(TYPE_WIFI, isConnected(TYPE_WIFI));
			//mListener.onChange(TYPE_MOBILE, isConnected(TYPE_MOBILE));
		}
	}

	public void release() {
		mApplication.unregisterReceiver(this);  
	}
}


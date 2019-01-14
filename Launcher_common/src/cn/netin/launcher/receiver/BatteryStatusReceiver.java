package cn.netin.launcher.receiver;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;



public  class BatteryStatusReceiver extends BroadcastReceiver {
	private static final String TAG = "EL BatteryStateReceiver" ;
	private final Application mApplication;

	BatteryStatusListener mListener = null ;
	
	
	public interface BatteryStatusListener {
		void onChange(int status, int level, boolean overHeat) ;
	}

	public BatteryStatusReceiver(Application application, BatteryStatusListener listener) {
		mApplication = application;
		mListener = listener ;
		
		IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		application.registerReceiver(this, filter);			
		Log.d(TAG, "BatteryStatusReceiver : registerReceiver ") ;
		
	}
	
	public int[] getBatteryStatus() {
	    Intent intent = mApplication.registerReceiver(null,  
	            new IntentFilter(Intent.ACTION_BATTERY_CHANGED));  
	    
		int rawlevel = intent.getIntExtra("level", -1);  
		int scale = intent.getIntExtra("scale", -1);  
		int status = intent.getIntExtra("status", -1);  
		//int health = intent.getIntExtra("health", -1);  
		int level = -1; // percentage, or -1 for unknown  
		if (rawlevel >= 0 && scale > 0) {  
			level = (rawlevel * 100) / scale;  
		}  

	    int[] ret = new int[2] ;
	    ret[0] = status ;
	    ret[1] = level ;
	    
	    //Log.d(TAG, "getBatteryStatus status=" + status + " level=" + level) ;
	    
	    return ret ;
	}


	@Override
	public void onReceive(Context context, Intent intent) {
		StringBuilder sb = new StringBuilder();  
		boolean overHeat = false ;
		int rawlevel = intent.getIntExtra("level", -1);  
		int scale = intent.getIntExtra("scale", -1);  
		int status = intent.getIntExtra("status", -1);  
		int health = intent.getIntExtra("health", -1);  
		int level = -1; // percentage, or -1 for unknown  
		if (rawlevel >= 0 && scale > 0) {  
			level = (rawlevel * 100) / scale;  
		}  
		sb.append("The phone");  
		if (BatteryManager.BATTERY_HEALTH_OVERHEAT == health) {  
			overHeat = true ;
			sb.append("'s battery feels very hot!");  
		} else {  
			switch (status) {  
			case BatteryManager.BATTERY_STATUS_UNKNOWN:  
				sb.append("no battery.");  
				break;  
			case BatteryManager.BATTERY_STATUS_CHARGING:  
				sb.append("'s battery");  
				if (level <= 33)  
					sb.append(" is charging, battery level is low"  
							+ "[" + level + "]");  
				else if (level <= 84)  
					sb.append(" is charging." + "[" + level + "]");  
				else  
					sb.append(" will be fully charged.");  
				break;  
			case BatteryManager.BATTERY_STATUS_DISCHARGING:  
			case BatteryManager.BATTERY_STATUS_NOT_CHARGING:  
				if (level == 0)  
					sb.append(" needs charging right away.");  
				else if (level > 0 && level <= 33)  
					sb.append(" is about ready to be recharged, battery level is low"  
							+ "[" + level + "]");  
				else  
					sb.append("'s battery level is" + "[" + level + "]");  
				break;  
			case BatteryManager.BATTERY_STATUS_FULL:  
				sb.append(" is fully charged.");  
				break;  
			default:  
				sb.append("'s battery is indescribable!");  
				break;  
			}  
		}  
		sb.append(' ');  
		
		//Log.d(TAG, sb.toString()) ;
		if (mListener != null) {
			mListener.onChange(status, level, overHeat);
		}
		//batterLevel.setText(sb.toString());  
	}
	
	public void release() {
		mApplication.unregisterReceiver(this);  
	}
}


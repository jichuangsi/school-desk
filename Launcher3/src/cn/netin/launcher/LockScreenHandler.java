package cn.netin.launcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import cn.netin.launcher.service.ServiceContract;

public class LockScreenHandler {
	private static final String TAG = "EL LockScreenHandler" ;
	private Context mContext ;
	private static boolean sIsLocked = false ;
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			if (action.equals(ServiceContract.ACTION_LOCK_SCREEN)) {			
				startLock() ;
			}
			else if (action.equals(ServiceContract.ACTION_UNLOCK_SCREEN)) {			
				removeLock() ;
			}		
		}        
	};
	
	public LockScreenHandler(Context context){
		mContext = context ;
		registerReceiver() ;
	}
	
	private void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ServiceContract.ACTION_LOCK_SCREEN);
		filter.addAction(ServiceContract.ACTION_UNLOCK_SCREEN);
		mContext.registerReceiver(mReceiver, filter); 
	}

	
	private void startLock() {
		//Log.d(TAG, "startLock") ;
		Intent newIntent = new Intent(mContext, RestActivity.class) ;
		//newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK) ;
		mContext.startActivity(newIntent);	
		sIsLocked = true ;
	}

	private void removeLock() {
		//Log.d(TAG, "removeLock") ;
		Intent newIntent = new Intent(mContext, LauncherActivity.class) ;
		//newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) ;
		mContext.startActivity(newIntent);	
		sIsLocked = false ;
	}

	public void mayLockScreen() {
		if (sIsLocked) {
			startLock() ;
		}
	}
	
	public void release() {
		mContext.unregisterReceiver(mReceiver) ;
		mContext = null ;
	}
}

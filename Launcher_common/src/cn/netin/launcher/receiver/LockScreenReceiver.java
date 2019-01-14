package cn.netin.launcher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import cn.netin.launcher.service.ServiceContract;

public class LockScreenReceiver extends BroadcastReceiver {

	private static final String TAG = "EL Launcher Receiver" ;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "onReceive ServiceContract ACTION_LOCK_SCREEN") ;
		String action = intent.getAction() ;
		if (action.equals(ServiceContract.ACTION_LOCK_SCREEN)) {
			Intent newIntent = new Intent() ;
			newIntent.setAction(ServiceContract.ACTION_LOCK_SCREEN) ;
			newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK) ;
			context.startActivity(newIntent);			
		}
	}

}


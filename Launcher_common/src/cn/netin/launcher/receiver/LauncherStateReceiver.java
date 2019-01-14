package cn.netin.launcher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import cn.netin.launcher.data.LauncherState;
import cn.netin.launcher.service.AppStatService;
import cn.netin.launcher.service.ServiceContract;

public class LauncherStateReceiver extends BroadcastReceiver {

	private static final String TAG = "EL Service Receiver" ;	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		String action = intent.getAction() ;
		//动态注册才能收到
		if (action.equals("android.intent.action.SCREEN_ON")) {
			Log.d(TAG, "@@@@ onReceive SCREEN_ON start AppStatService ") ;
			Intent service = new Intent(context, AppStatService.class) ;
			context.startService(service);			
		}
		else if (action.equals("android.intent.action.USER_PRESENT")) {
			Log.d(TAG, "@@@@ onReceive USER_PRESENT start AppStatService ") ;
			Intent service = new Intent(context, AppStatService.class) ;
			context.startService(service);			
		}
		
		else if (action.equals(ServiceContract.ACTION_LAUNCHER_ON)) {
			Log.d(TAG, "@@@ onReceive ACTION_LAUNCHER_ON") ;
			LauncherState.setAlive(true);
		}
		else if (action.equals(ServiceContract.ACTION_LAUNCHER_OFF)) {
			Log.d(TAG, "@@@@ onReceive ACTION_LAUNCHER_OFF") ;
			LauncherState.setAlive(false);
		}

	}

}

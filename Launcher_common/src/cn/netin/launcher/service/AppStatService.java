package cn.netin.launcher.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import cn.netin.launcher.R;

public class AppStatService extends Service {
	private AppStat mAppStat ;
	private static final String TAG = "EL AppStatService" ;
	//private KeyguardManager.KeyguardLock mKeyguardLock ;
	//private DatabaseHelper mDatabaseHelper ;
	private static final int NOTIFICATION_ID = 1201 ;

	@Override
	public void onCreate() {
		Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@ AppStatService onCreate") ;
		super.onCreate();
		mAppStat = new AppStat(this.getApplicationContext()) ;
		mAppStat.start();

		//KeyguardManager keyguardManager = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);  
		//mKeyguardLock = keyguardManager.newKeyguardLock("AppStatService Lock");   

		//mDatabaseHelper = new DatabaseHelper(this) ;
		Intent intent = new Intent() ;	  
		intent.setComponent(new ComponentName("cn.netin.launcher", "cn.netin.launcher.LauncherActivity") );  

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);     

		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);  
		builder.setContentTitle(getString(R.string.service_name)) ;
		builder.setContentText(getString(R.string.service_text)) ;
		builder.setSmallIcon(R.drawable.icon_notification) ;
		builder.setContentIntent(contentIntent) ;
		builder.setOngoing(true) ;
		Notification notification = builder.build() ;

		startForeground(NOTIFICATION_ID, notification);  

		registerReceiver() ;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "AppStatService onDestroy") ;
		mAppStat.interrupt();
		unregisterReceiver(mReceiver);  
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter();  
		filter.addAction("android.intent.action.SCREEN_ON");  
		filter.addAction("android.intent.action.SCREEN_OFF");  	              
		registerReceiver(mReceiver, filter);  
	}

	//屏幕变暗/变亮的广播 ， 我们要调用KeyguardManager类相应方法去解除屏幕锁定  
	private BroadcastReceiver mReceiver = new BroadcastReceiver(){  
		@Override  
		public void onReceive(Context context , Intent intent) {  
			String action = intent.getAction() ;  

			if(action.equals("android.intent.action.SCREEN_OFF") ){  
				Log.i(TAG, "########### AppStatService SCREEN_OFF" ); 
				//mKeyguardLock.disableKeyguard();  
				//Log.i(TAG, "########### mScreenOffReceiver disableKeyguard" ); 
			}  
			else if  (action.equals("android.intent.action.SCREEN_ON") ) {
				Log.i(TAG, "########### AppStatService SCREEN_ON" ); 

				//mKeyguardLock.disableKeyguard();  
				//Log.i(TAG, "########### mScreenOffReceiver disableKeyguard" ); 


			}
		}  

	};  



}

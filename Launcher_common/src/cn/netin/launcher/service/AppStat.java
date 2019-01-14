package cn.netin.launcher.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.hanceedu.common.App;
import com.hanceedu.common.util.DateUtil;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import cn.netin.launcher.data.Constants;
import cn.netin.launcher.data.LauncherData;
import cn.netin.launcher.data.LauncherState;
import cn.netin.launcher.data.Protection;
import cn.netin.launcher.data.ProtectionData;

public class AppStat extends Thread {
	private static final int INTERVAL = 40 ; //多久算下应用的使用时间
	private static final String TAG = "EL AppStat" ;
	private static final int MS_PER_DAY = 24 * 60 * 60 * 1000 ; 
	private PackageManager mPackageManager;
	private ActivityManager mActivityManager ;
	private Context mContext ;
	private DatabaseHelper mDatabaseHelper ;
	private PowerManager mPowerManager ;
	private KeyguardManager mKeyguardManager ; 
	private ProtectionData mProtectionData ;
	private static int mRestSeconds = 0 ;
	private static int mPlaySeconds = 0 ;
	private static boolean mIsLocked = false ;
	private static boolean mProtectionEnabled = false ;

	public static void setProtection(boolean enabled){
		mProtectionEnabled = enabled ;
	}

	public static void setInstallerAllowed(boolean allowed){
		nativeSetInstallerAllowed(allowed) ;
	}
	private ContentObserver mLauncherDataObserver = new ContentObserver(new Handler()) {  

		@Override  
		public void onChange(boolean selfChange) {  
			super.onChange(selfChange);  
			Log.i(TAG, "app onChange");  
		}  
	};  

	private ContentObserver mProtectionObserver = new ContentObserver(new Handler()) {  

		@Override  
		public void onChange(boolean selfChange) {  
			super.onChange(selfChange);  
			Log.d(TAG, "############ protection onChange");  
			Protection protection = mProtectionData.getProtection() ;
			if (protection != null) {
				mProtectionEnabled = protection.enable ;
				Log.d(TAG, "mProtectionObserver onChange protection mProtectionEnabled= " + protection.enable) ;
				
			}else{
				Log.e(TAG, "mProtectionObserver onChange protection is null ") ;
			}
		}  
	};


	public static boolean isLocked() {
		return mIsLocked ;
	}

	public AppStat(Context context) {
		mContext = context ;
		mProtectionData = new ProtectionData(context) ;
		Protection protection = mProtectionData.getProtection() ;
		if (protection != null) {
			mProtectionEnabled = protection.enable ;
		}
		Log.d(TAG, "mProtectionEnabled=" + mProtectionEnabled ) ;
		mDatabaseHelper = new DatabaseHelper(context) ;
		//获得包管理器
		mPackageManager = context.getPackageManager();
		//获得ActivityManager对象
		mActivityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE); 	
		mPowerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
		mKeyguardManager = (KeyguardManager) mContext.getSystemService(Context.KEYGUARD_SERVICE);

		context.getContentResolver().registerContentObserver(Uri.parse(ServiceContract.PROTECTION_URI), true, mProtectionObserver);
		context.getContentResolver().registerContentObserver(Uri.parse(ServiceContract.APPS_URI), true, mLauncherDataObserver);

	}

	private boolean isScreenOff() {
		return (!mPowerManager.isScreenOn()) ;
	}

	private boolean isScreenLocked() {
		return mKeyguardManager.inKeyguardRestrictedInputMode();
	}

	/** 不使用的时候会间隔调用这个函数 */
	private void handleRest() {
		mRestSeconds += INTERVAL / 4 ;
		//Log.d(TAG, "$$$$$$$$$$ handleRest isInPlayPeriod=" + isInPlayPeriod() 
		//	+ " isRestEnough=" + isRestEnough() 
		//+ " mIsLocked=" + mIsLocked ) ;
		if ( isInPlayPeriod() && isRestEnough()) {
			if (mIsLocked) {
				unlockScreen() ;
			}
		}
	}

	/** 在玩的时候会间隔调用这个函数 */
	private void handlePlay() {

		//只有专用launcer启用时才用这个功能
		if (!LauncherState.isAlive()) {
			return ;
		}

		//只要一玩，就把休息时间清零
		resetRestTime() ;
		mPlaySeconds += INTERVAL / 4 ;
		//Log.d(TAG, "$$$$$$$$$$ handlePlay isInPlayPeriod=" + isInPlayPeriod() 
		//	+ " isPlayEnough=" + isPlayEnough() 
		//+ " mIsLocked=" + mIsLocked ) ;
		if (  !isInPlayPeriod() || isPlayEnough() ) {
			if (!mIsLocked) {
				lockScreen() ;
			}
		}
	}

	private void resetRestTime() {
		mRestSeconds = 0 ;
	}
	private void resetPlayTime() {
		mPlaySeconds = 0 ;
	}


	/** 是否休息够时间了 */
	public boolean isRestEnough() {
		RestTime restTime = mDatabaseHelper.getRestTime() ;
		//Log.d(TAG, "isRestEnough enableRest=" + restTime.enableRest 
		//	+ " mRestSeconds=" + mRestSeconds 
		//+ " rest=" + restTime.rest) ;
		//如果不设定休息
		if (!restTime.enableRest) {
			return true ;
		}
		if (mRestSeconds >  restTime.rest * 60 ) {
			return true ;
		}
		return false ;
	}

	/** 是否玩够时间了 */
	public boolean isPlayEnough() {
		RestTime restTime = mDatabaseHelper.getRestTime() ;
		//Log.d(TAG, "isPlayEnough enableRest=" + restTime.enableRest 
		//	+ " mPlaySeconds=" + mPlaySeconds 
		//+ " play=" + restTime.play) ;
		//如果不设定休息
		if (!restTime.enableRest) {
			return false ;
		}
		if (mPlaySeconds >  restTime.play * 60) {
			return true ;
		}
		return false ;
	}

	/** 是否在设定的允许使用时段内 */
	public boolean isInPlayPeriod() {
		RestTime restTime = mDatabaseHelper.getRestTime() ;
		if (!restTime.enablePeriod) {
			return true ;
		}
		int currentMinutesInDay = DateUtil.getCurrentMinutesInDay() ;
		//Log.d(TAG, "isInPlayPeriod startTime=" + restTime.startTime 
		//	+ " endTime=" + restTime.endTime 
		//+ " currentMinutesInDay=" + currentMinutesInDay 
		//) ;
		if (currentMinutesInDay >= restTime.startTime && currentMinutesInDay <=  restTime.endTime) {
			return true ;
		}
		//Log.d(TAG, "NOT isInPlayPeriod") ; 
		return false ;
	}


	/** 每隔一段时间运行一次，取当前正在运行的应用 */
	private void updateRunningTasks(int times)
	{

		//如果屏幕处于关闭或者锁定状态，就不统计
		//Log.d(TAG, "isScreenOff()=" + isScreenOff() + " isScreenLocked()=" + isScreenLocked()) ;
		if (isScreenOff() || isScreenLocked() ) {
			//肯定处于休息状态
			if (times == INTERVAL) {
				Log.d(TAG, "handleRest") ; 
				handleRest() ;
			}
			return ;
		}			

		if (!LauncherState.isCurrentUserOwner(mContext) && !LauncherState.isBlocking()) {
			block(null) ;
		}

		//如果不用专用Launcher，就不统计
		if (!LauncherState.isAlive()) {
			if (times == INTERVAL) {
				Log.d(TAG, "launcher is Not alive") ; 
			}
			return ;
		}

		ComponentName topComp = TopActivityUtils.getTopActivity(mContext, mActivityManager) ;
		String pkg = "";
		String cls = "";
		if (topComp != null) {
			pkg = topComp.getPackageName() ;
			cls = topComp.getClassName() ;
		}

		if (times == INTERVAL) {
			Log.d(TAG, "top Activity=" + pkg + " " + cls );	
			//如果RestActivity在顶端，计算休息时间，不统计
			//if (cls.equals("cn.netin.launcher.RestActivity")) {
			if (pkg.equals("cn.netin.launcher") && mIsLocked) {
				Log.d(TAG, "mIsLocked") ;
				handleRest() ;
				//不统计
				return ;
			}else{		
				//处理玩的时间
				handlePlay() ;
			}
		}

		if (pkg.isEmpty()) {
			return ;
		}

		if (times == INTERVAL) {
			Date date = new Date() ;
			date.getTime() ;
			long ms = date.getTime() ;
			//把毫秒计算的时间转成按天计算
			long days = ms / MS_PER_DAY ;	
			//更新该应用当前日期的使用时间
			mDatabaseHelper.insertOrUpdateStat(pkg, (int)days, (int) (INTERVAL / 4));
			//mDatabaseHelper.debugStat();
		}

		if (!isAllowed(pkg, cls)) {
			Log.d(TAG, "block " + pkg + " " + cls) ;
			block(pkg) ;
		}

		/*
		List<RunningAppProcessInfo> runningTasks = mActivityManager.getRunningAppProcesses(); 
		for(int i=0;i<runningTasks.size();i++)
		{
			RunningAppProcessInfo processInfo = ((RunningAppProcessInfo)runningTasks.get(i)) ;
			String processName = processInfo.processName ;
			String[] pkgList = processInfo.pkgList ;
			for (int j = 0 ; j < pkgList.length; j++) {
				String pkgName =  pkgList[j] ;
				Log.d(TAG, "==== processName=" + processName + " pkgName=" + pkgName);			
				ApplicationInfo appInfo = getAppInfo(pkgName) ;
				if (appInfo == null) {
					Log.e(TAG, "appInfo == null :" + pkgName) ;
					continue ;
				}
				String label = (String)appInfo.loadLabel(mPackageManager) ;
				if( (appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 ) {
					Log.d(TAG, "++++++++++++ pkgName=" + pkgName + " label=" + label );							
				}else{
					Log.d(TAG, "------------ pkgName=" + pkgName + " label=" + label );				
				}

			}//for pkgList

		}//for runningTasks
		 */		

		/*
		List<RecentTaskInfo> recentTaskInfoList = mActivityManager.getRecentTasks(10, ActivityManager.RECENT_WITH_EXCLUDED) ;
		RecentTaskInfo rt = recentTaskInfoList.get(0) ;
		cm = rt.origActivity ;
		//The original Intent used to launch the task. You can use this Intent to re-launch the task 
		//(if it is no longer running) or bring the current task to the front. 
		Intent intent = rt.baseIntent ;
		 */
	}
	@Override
	public void run() {
		Log.d(TAG, "start updateRunningTasks") ;
		int times = 0 ;
		while (true) {
			times++ ;
			if (this.isInterrupted()) {
				release() ;
				break ;
			}
			updateRunningTasks(times) ;
			if (times == INTERVAL) {
				times = 0 ;
			}
			try {

				Thread.sleep(250);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

	}

	private void release() {
		mDatabaseHelper.release();
		mDatabaseHelper = null ;
		mContext.getContentResolver().unregisterContentObserver(mLauncherDataObserver);
	}

	public void unlockScreen() {
		Log.i(TAG, "########### AppStat unlockScreen" ); 
		mIsLocked = false ;
		resetRestTime() ;
		resetPlayTime() ;

		Intent intent = new Intent();  
		intent.setAction(ServiceContract.ACTION_UNLOCK_SCREEN);  
		mContext.sendBroadcast(intent);  
	}

	public void lockScreen() {
		Log.i(TAG, "########### AppStat lockScreen" ); 
		mIsLocked = true ;
		resetRestTime() ;
		resetPlayTime() ;

		Intent intent = new Intent();  
		intent.setAction(ServiceContract.ACTION_LOCK_SCREEN);  
		mContext.sendBroadcast(intent);  

		//Intent launcherIntent = new Intent();
		//launcherIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);	
		//launcherIntent.setComponent(new ComponentName("com.hanceedu.launcher", "com.hanceedu.launcher.RestActivity")) ;
		//mContext.startActivity(launcherIntent);  

	}

	public static  boolean isAllowed(String pkg, String cls) {
		//Log.d(TAG, "isAllowed mProtectionEnabled=" + mProtectionEnabled) ;
		if (!mProtectionEnabled) {
			//Log.d(TAG, "isAllowed: protection not enabled ") ;
			return true ;
		}
		if (!LauncherState.isAlive()) {
			//Log.d(TAG, "isAllowed: launcher is not alive") ;
			return true ;
		}
		return nativeIsAllowed(pkg, cls, Constants.BAN_INSTALLER) ;
	
	}

	private void block(String pkg) {

		if (pkg != null) {
			ActivityManager am = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);  
			am.killBackgroundProcesses(pkg);
			Log.d(TAG, "killBackgroundProcesses:" + pkg) ;
		}

		//Intent intent = new Intent(mContext, BlockActivity.class);
		//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//mContext.startActivity(intent);

		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(intent);
	}
	
	private native static void nativeSetInstallerAllowed(boolean allowed) ;
	private native static boolean nativeIsAllowed(String pkg, String cls, boolean banInstaller) ;
}

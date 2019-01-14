package cn.netin.launcher;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import cn.netin.launcher.data.LauncherState;
import cn.netin.launcher.data.StatPermissionData;
import cn.netin.launcher.service.AppStat;
import cn.netin.launcher.service.DatabaseHelper;

public class LauncherPermissions {
	private static final String TAG = "EL LauncherPermissions" ;
	private static boolean mStatPermissionChecked = false ;
	private static boolean mInstallerChecked = false ;
	private AlertDialog mUsageDialog = null;
	private AlertDialog mAppDialog = null;
	private Context mContext ;
	private int mCheckCount = 0 ;

	public LauncherPermissions(Context context) {
		mContext = context ;

		mUsageDialog = new AlertDialog.Builder(mContext)
				.setTitle(R.string.dialog_title)
				.setMessage(R.string.dialog_message)
				.setOnCancelListener(new OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						Log.d(TAG, "dialog canceled") ;
						check() ;
					}

				})
				.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						//some rom has removed the newly introduced android.settings.USAGE_ACCESS_SETTINGS
						startUsageSetting() ;
						dialog.dismiss();
					}
				}).create();


		mAppDialog = new AlertDialog.Builder(mContext)
				.setTitle("需要重置应用偏好设置")
				.setMessage("请在应用设置页面点菜单，选“重置应用偏好设置”")
				.setOnCancelListener(new OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						Log.d(TAG, "dialog canceled") ;
						check() ;
					}

				})
				.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						//some rom has removed the newly introduced android.settings.USAGE_ACCESS_SETTINGS
						startAppSetting() ;
						dialog.dismiss();
					}
				}).create();
	}

	public void check() {

		if (!mInstallerChecked) {
			if (mCheckCount < 3){
				Log.d(TAG, "mCheckCount=" + mCheckCount) ;
				//检查专用akp安装器是否已经设为默认
				mInstallerChecked = LauncherState.checkInstaller(mContext) ;
				mCheckCount++ ; 
			}else{
				mAppDialog.show();
				mCheckCount = 0 ;
			}
			return ;
		}
		

		if (!mStatPermissionChecked){
			if (!StatPermissionData.isGranted(mContext)) {
				mUsageDialog.show();
			}else{
				mUsageDialog.dismiss();		
				mStatPermissionChecked = true ;
				DatabaseHelper helper = new DatabaseHelper(mContext) ;
				helper.enableProtection();
				AppStat.setProtection(true);
			}
		}

	}

	@SuppressLint("InlinedApi")
	private void startUsageSetting() {
		try {
			Intent intent = new Intent() ;
			intent.setAction(Settings.ACTION_USAGE_ACCESS_SETTINGS) ;
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) ;
			mContext.startActivity(intent);
		} catch (Exception e) {

		}
	}

	private void startAppSetting() {
		try {
			Intent intent = new Intent() ;
			intent.setAction(Settings.ACTION_APPLICATION_SETTINGS) ; ;
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) ;
			mContext.startActivity(intent);
		} catch (Exception e) {

		}
	}


}

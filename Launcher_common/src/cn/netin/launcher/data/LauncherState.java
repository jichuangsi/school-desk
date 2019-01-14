package cn.netin.launcher.data;

import java.io.File;
import java.lang.reflect.Method;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.UserManager;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

public class LauncherState {

	private static final String TAG = "EL LauncherState";

	private static boolean sIsOwner = true ;
	private static boolean sIsAlive = false ;
	private static boolean sIsBlocking = false ;
	private static boolean sInstallerChecked = false ;

	public static boolean isBlocking() {
		return sIsBlocking;
	}
	public static void setBlocking(boolean flag) {	
		sIsBlocking = flag;
	}

	public static boolean isAlive() {
		return sIsAlive;
	}

	public static void setAlive(boolean flag) {	
		sIsAlive = flag;
	}

	public static boolean isCurrentUserOwner(Context context)  
	{  
		return sIsOwner ;
	} 

	//LauncherActivity启动时调用
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	public static void checkOwner(Context context)  
	{  
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
			sIsOwner = true ;
			return ;
		}
		try  
		{  
			Method getUserHandle = UserManager.class.getMethod("getUserHandle");  
			int userHandle = (Integer) getUserHandle.invoke(context.getSystemService(Context.USER_SERVICE));  
			sIsOwner = (userHandle == 0);  
			if (!sIsOwner) {
				Log.d(TAG, "sIsOwner=" + sIsOwner) ;
			}		
		}  
		catch (Exception ex)  
		{  		
			sIsOwner = false; 
			Log.d(TAG, "getUserHandle fail.") ;
		}  
	}

	private static Intent getInstallerIntent(Context context) {
		File parentFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) ;
		String dir = parentFile.getAbsolutePath() ;
		Log.d(TAG, "dir=" + dir) ;
		File file = new File(parentFile, "INSTALLER_TEST.apk");
		Intent intent = new Intent(Intent.ACTION_VIEW);
		// 由于没有在Activity环境下启动Activity,设置下面的标签
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if(Build.VERSION.SDK_INT>=24) { //判读版本是否在7.0以上
			//参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
			Uri providerUri = FileProvider.getUriForFile(context, "cn.netin.launcher.installer.fileprovider", file);
			//添加这一句表示对目标应用临时授权该Uri所代表的文件
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			intent.setDataAndType(providerUri, "application/vnd.android.package-archive");
		}else{
			intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
		}

		return intent ;
	}

	public static boolean checkInstaller(Context context) {
		if (sInstallerChecked ) {
			return true;
		}
		//如果不管控安装器，就返回
		if (!Constants.BAN_INSTALLER) {
			sInstallerChecked = true ;
			return true;
		}
		PackageManager pm = context.getPackageManager();
		Intent intent = getInstallerIntent(context) ; 
		ResolveInfo info = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
		String pkg = info.activityInfo.packageName ;
		Log.d(TAG, "checkInstaller pkg = " + pkg);

		if (!"cn.netin.launcher".equals(pkg)) {
			Toast.makeText(context, "请选择“专用APK安装器”，并选择“总是”", Toast.LENGTH_SHORT).show() ;
			context.startActivity(intent);
			return false ;
		}
		Toast.makeText(context, "已经将专用安装器设为默认", Toast.LENGTH_SHORT).show() ;
		sInstallerChecked = true ;
		return true ;

	}


}

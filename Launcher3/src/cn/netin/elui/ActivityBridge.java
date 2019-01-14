package cn.netin.elui;


import java.io.File;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Environment;

public class ActivityBridge {

	private static final String TAG = "EL ActivityBridge";

	private static Context sContext  = null ;

	public static void init(Context context) {
		sContext = context ;

		ApplicationInfo applicationInfo = context.getApplicationInfo() ;
		String packageName = applicationInfo.packageName ;
		String apkPath = applicationInfo.sourceDir ;
		String dataDir = context.getFilesDir().getAbsolutePath();

		File f =  context.getExternalCacheDir() ;
		if (f == null) {
			return ;
		}

		String extCacheDir = f.getAbsolutePath() ;
		String extStorageDir = Environment.getExternalStorageDirectory().getAbsolutePath() ;
		String picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES ).getAbsolutePath() ;
		//String documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS ).getAbsolutePath() ;
		String musicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC ).getAbsolutePath() ;
		String moviesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES ).getAbsolutePath() ;
		String downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS ).getAbsolutePath() ;
		String dcimDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM ).getAbsolutePath() ;
		String removableSDDir = getRemovableSD() ;
		String usbStorageDir = getUsbStorage() ;
		/*
		Log.d(TAG, "init extCacheDir=" + extCacheDir 
				+ " extStorageDir=" + extStorageDir
				+ " picturesDir=" + picturesDir
				//+ " documentsDir=" + documentsDir
				+ " musicDir=" + musicDir
				+ " moviesDir=" + moviesDir
				+ " downloadsDir=" + downloadsDir
				+ " dcimDir=" + dcimDir
				+ " removableSDDir=" + removableSDDir
				+ " usbStorageDir=" + usbStorageDir

				) ;
				*/

		/*
		File[] dirs = context.getExternalFilesDirs(Environment.DIRECTORY_DCIM) ;
		for (File file : dirs) {
			Log.d(TAG, "ExternalFilesDirs:" + file.getAbsolutePath() + " total space=" + file.getTotalSpace()) ;
		}
		 */

		AssetManager assetManager = context.getAssets();
		
		setRemovables(removableSDDir, usbStorageDir) ;
		setContext(context, assetManager, packageName, apkPath, dataDir, extStorageDir,  Build.VERSION.SDK_INT);
		DeviceId.setContext(context);
	}
	

	public static String getRemovableSD(){
		List<Storage> storages = Storage.getStorageList(sContext) ;
		for (Storage s : storages) {
			if (s.isRemovable && s.isMounted) {
				return s.path ;
			}
		}
		return "" ;
	}
	
	public static String getUsbStorage(){
		List<Storage> storages = Storage.getStorageList(sContext) ;
		for (Storage s : storages) {
			if (s.isRemovable && s.isMounted) {
				String lower = s.path.toLowerCase(Locale.ENGLISH) ;
				if (lower.indexOf("usb") != -1) {
					return s.path ;
				}
				if (lower.indexOf("uhost") != -1) {
					return s.path ;
				}
			}
		}
		return "" ;
	}
	

	public static native void setContext(final Context sContext, final AssetManager assetManager, final String  packageName, final String  apkPath, final String  dataDir, final String extStorageDir,  int sdkInt);
	public static native void setRemovables(final String removableSDDir, final String usbStorageDir);
	
}


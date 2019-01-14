package cn.netin.parentalcontrol;

import java.util.ArrayList;
import java.util.List;

import com.hanceedu.common.App;
import com.hanceedu.common.App.AppColumns;
import com.hanceedu.common.util.DrawableUtil;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import cn.netin.launcher.data.LauncherData;
import cn.netin.launcher.service.ServiceContract;

public class AppData {

	private static final String TAG = "EL AppData" ; 
	private Context mContext;
	private Uri mUri ;
	private static int sIconSize = 36 ;

	public AppData(Context context) {
		mContext = context;
		mUri = Uri.parse(ServiceContract.APPS_URI) ;
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		float density = dm.density ;
		sIconSize = (int) (Constants.ICON_SIZE_SMALL * density) ;
	}

	//返回定制桌面还没有的APP
	public List<App> getAvailableAppList() {

		//取系统中已经安装的APP
		List<App> installedAppList = getInstalledAppList() ;
		if (installedAppList == null || installedAppList.size() == 0) {
			return null ;
		}
		//Log.i(TAG, "getInstalledAppList size=" + installedAppList.size()) ;
		//取定制桌面上已经放置的APP
		List<App> launcherAppList = getLauncherAppListWithoutGroups() ;
		if (launcherAppList == null || launcherAppList.size() == 0) {
			//Log.i(TAG, "#########getLauncherAppList size=" + launcherAppList.size()) ;
			return installedAppList ;
		}

		for (int i = 0 ; i < installedAppList.size(); i++) {
			App app = installedAppList.get(i) ;
			//Log.d(TAG, "getAvailableAppList " + app.getPkg() + "  " + app.getCls()) ;
			for (App app1 : launcherAppList) {
				//if (app.getPkg().equals(app1.getPkg()) && app.getCls().equals(app1.getCls()) ) {			
				if (app.getPkg().equals(app1.getPkg()) ) {
					//Log.d(TAG, "getAvailableAppList match " + app.getCls()) ;
					installedAppList.remove(app) ;
					if (i > -1) {
						i-- ;
					}
					break ;
				}
			}

		}

		/*		for (int i = 0 ; i < appList.size(); i++) {
			App app = appList.get(i) ;
			Log.d(TAG, "getAvailableAppList " + app.getPkg() + "  " + app.getCls()) ;
		}*/

		//Log.i(TAG, "getAvailableAppList size=" + installedAppList.size()) ;

		return installedAppList ;
	}


	/**
	 * Loads the list of installed applications in mApplications.
	 */
	@SuppressWarnings("deprecation")
	private List<App> getInstalledAppList() {

		PackageManager pm = mContext.getPackageManager();

		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

		final List<ResolveInfo> apps = pm.queryIntentActivities(mainIntent, 0);
		if (apps == null) {
			return null ;
		}
		//Collections.sort(apps, new ResolveInfo.DisplayNameComparator(pm));

		final int count = apps.size();
		List<App> appList = new ArrayList<App>(count);

		for (int i = 0; i < count; i++) {
			App app = new App();
			ResolveInfo info = apps.get(i);
			app.setId(i);
			app.setCategory(1); //表示可以删除
			app.setName(info.loadLabel(pm).toString());
			app.setPkg(info.activityInfo.applicationInfo.packageName);
			app.setCls(info.activityInfo.name);
			//Log.i(TAG, "-----------------------------------" ) ;
			//Log.i(TAG, "applicationInfo.packageName:" + info.activityInfo.applicationInfo.packageName) ; //packageName
			//Log.i(TAG, "activityInfo.packageName:" + info.activityInfo.packageName) ;//和上一句等效
			//Log.i(TAG, "applicationInfo.name:" + info.activityInfo.applicationInfo.name ) ; //application的名字,一般是null
			//Log.i(TAG, "applicationInfo.className:" + info.activityInfo.applicationInfo.className ) ;//application的类名，比如HanceApplication
			//Log.i(TAG, "activityInfo.name:" + info.activityInfo.name) ;//activity的类名
			Drawable drawable = info.activityInfo.loadIcon(pm) ;
			if (drawable == null) {
				drawable = mContext.getResources().getDrawable(R.drawable.ic_launcher);
			}
			drawable = DrawableUtil.resizeDrawable(mContext, drawable, sIconSize, sIconSize) ;
			app.setSmallDrawable(drawable);
			appList.add(app);
		}

		return appList ;

	}

	

	public List<App>  getRemovableLauncherAppList() {

		List<App> launcherList = LauncherData.getLauncherAppList() ;
		List<App> appList = new ArrayList<App>();
		if (launcherList == null || launcherList.size() == 0) {
			Log.e(TAG, "getRemovableLauncherAppList launcherList is empty") ;
			return appList ;
		}
		//Log.e(TAG, "getRemovableLauncherAppList launcherList count=" + launcherList.size()) ;
		Drawable drawable = null;	
		for (App app : launcherList) {
			//cate = 0 的不可以移除
			if (app.getCategory() == 0) {
				//Log.i(TAG, "getRemovableLauncherAppList category == 0 :" + app.getPkg());
				continue ;
			}
			drawable = getAppIcon(app) ;
			if (drawable == null) {
				//Log.i(TAG, "getRemovableLauncherAppList drawable fail :" + app.getPkg());
				continue ;
			}
			app.setSmallDrawable(drawable);
			appList.add(app);
		}
		//Log.e(TAG, "getRemovableLauncherAppList size=" + appList.size()) ;
		
		return appList ;
	}

	
	public List<App>  getLauncherAppListWithoutGroups() {

		List<App> launcherList = LauncherData.getLauncherAppList() ;
		if (launcherList == null) {
			return null ;
		}
		List<App> appList = new ArrayList<App>();

		Drawable drawable = null;
		
		for (App app : launcherList) {
			//不要程序组
			if (app.getGroupId() > 0) {
				//Log.i(TAG, "****** app.getCategory() app.getGroupId() > 0 :" + app.getName());
				continue ;
			}
			drawable = getAppIcon(app) ;
			if (drawable == null) {
				continue ;
			}
			app.setSmallDrawable(drawable);
			appList.add(app);
		}

		return appList ;
	}
	
	private Drawable getAppIcon(App app){
		Drawable drawable = DrawableUtil.getIconDrawable(mContext, app.getPkg(), app.getCls(), 0) ;
		if (drawable == null) {
			return null ;
		}
		return DrawableUtil.resizeDrawable(mContext, drawable, sIconSize,  sIconSize) ;
		
	}

	
	public boolean addApp(App app) {
		int id = LauncherData.getInstance(mContext).insertApp(app) ;
		if (id > 0) {
			return true ;
		}
		return false ;
		
	}

	public boolean removeApp(App app) { 

		 LauncherData.getInstance(mContext).removeApp(app.getId());
		
		return true ;

	}

}

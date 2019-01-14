package cn.netin.launcher.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.hanceedu.common.App;
import com.hanceedu.common.HanceApplication;
import com.hanceedu.common.data.DataInterface;
import com.hanceedu.common.util.DrawableUtil;
import com.hanceedu.common.util.FileUtil;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;
import cn.netin.launcher.service.ServiceContract;

public class LauncherData implements DataInterface {
	private static final String TAG = "EL LauncherData";
	private Context mContext = null;
	private PackageManager mPackageManager = null ;
	private static HanceApplication sApp;
	private static DataListener sListener;
	private static int sIconSize = 72 ;
	private static boolean sValid = false ;
	private static List<App> sAppList = new ArrayList<App>() ;
	private static List<App> sVerifiedList = new ArrayList<App>() ;
	private static LauncherData sInstance = null ;
	

	static {
		System.loadLibrary("launcher");
	}

	private LauncherData(Context context) {
		mContext = context;
		sApp = (HanceApplication) context.getApplicationContext();
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		float density = dm.density ;
		sIconSize = (int) (Constants.ICON_SIZE * density) ;
		//Log.d(TAG, "sIconSize=" + sIconSize) ;
		//clearAll();
		mPackageManager = context.getPackageManager() ;

	}
	
	public static LauncherData getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new LauncherData(context);
		}
		return sInstance ;
	}

	public static boolean isValid(){
		return sValid ;
	}

	private static void clearAll() {
		sAppList.clear();
		sVerifiedList.clear();
		//sApp.clearAppData();
		Log.e(TAG, "clear all!") ;
	}

	public static List<App> getLauncherAppList(){
		return sVerifiedList ;
	}

	public void verifyList(){
		if (sAppList.size() == 0) {
			Log.e(TAG, "verifyList size=0") ;
			return  ;
		}
		if (mContext == null) {
			Log.e(TAG, "mContext is null") ;
			return  ;
		}
		boolean isElsInstalled = true;
		try {
			mPackageManager.getApplicationInfo(Constants.ELS_PKG, 0) ;
		} catch (NameNotFoundException e) {
			isElsInstalled = false ;
		}
		Resources res = mContext.getResources();
		Drawable drawable = null;
		for (int i = 0; i < sAppList.size(); i++) {
			App app = sAppList.get(i);
			String icon = app.getIcon();
			String pkg = app.getPkg() ;
			String cls = app.getCls() ;
			// Log.i(TAG, "app parentId=" + app.getParentId() + "
			// category=" + app.getCategory()) ;
			if (Constants.HIDE_ELS 
					&& Constants.ELS_PKG.equals(pkg) 
					&& !isElsInstalled ) {
				continue ;
			}
			if (isPkgInList(app, sVerifiedList)) {
				Log.e(TAG, "pass duplicated app:" + app.getName() + " : " + pkg) ;
				continue ;
			}
			if (pkg != null && !pkg.equals("")) {
				drawable = DrawableUtil.getLargeIconDrawable(mContext, pkg, cls);
				if (drawable == null) {
					Log.e(TAG, "pass no Icon app:" + pkg) ;
					continue ;
				}
			}
			
			if (icon != null && !icon.equals("")) {
				icon = Constants.ICON_FOLDER + icon;
				// 从Assets中找Icon图标。
				try {
					InputStream is = res.getAssets().open(icon);
					//drawable = Drawable.createFromStream(is, null);
					TypedValue typedValue = new TypedValue();
					// DENSITY_NONE:按原图大小。DENSITY_DEFAULT: 按系统密度 *
					typedValue.density = TypedValue.DENSITY_NONE;
					//typedValue.density = TypedValue.DENSITY_DEFAULT;
					drawable = Drawable.createFromResourceStream(res,typedValue,is, null);
					//Log.d(TAG, "width=" + drawable.getIntrinsicWidth() + " height=" + drawable.getIntrinsicHeight());
				} catch (IOException e) {
					//e.printStackTrace();
					//Log.e(TAG, "createFromResourceStream fail: " + icon);
				}
			}

			if (drawable == null) {
				Log.e(TAG, "no icon: " + pkg + " / " + cls) ;
				continue ;
			}
			
			// Log.d(TAG, "LauncherData Icon getIntrinsicWidth=" +
			// drawable.getIntrinsicWidth()) ;

			drawable = DrawableUtil.resizeDrawable(mContext, drawable, sIconSize,sIconSize);
			app.setDrawable(drawable);
			sVerifiedList.add(app) ;
			Log.d(TAG, "sVerifiedList add " + app.getName()) ; 
		} // for
		
		Log.d(TAG, "sVerifiedList size=" +sVerifiedList.size()) ; 
	}



	@Override
	public Param execute(Param param) {
		return null;
	}

	@Override
	public boolean prepare(Param param) {
		DataTask dataTask = new DataTask() ;
		dataTask.execute(param);
		return true;
	}

	private static boolean isPkgInList(App app, List<App> list){
		String pkg = app.getPkg() ;
		boolean isGroup = app.getGroupId() > 0 ;
		boolean isElsApp = pkg.equals("cn.netin.els") ;
		boolean isLauncherApp = pkg.equals("cn.netin.launcher") ;

		for (App a : list) {
			if (isGroup) {
				if (a.getGroupId() == app.getGroupId()) {
					Log.d(TAG, "has group:" + a.getName()) ;
					return true ;
				}
			}
		
			else if (isElsApp || isLauncherApp ) {
				if (a.getCls().equals(app.getCls()) && a.getData().equals(app.getData())) {
					Log.d(TAG, "has els app:" + a.getName()) ;
					return true ;
				}
			}
			
			else if (a.getPkg().equals(pkg)) {
				return true ;
			}
		}
		return false ;
	}

	@Override
	public void setListener(DataListener dataListener) {
		sListener = dataListener;
	}

	public void release() {
		Log.e(TAG, "release") ;
		sInstance = null ;
		mContext = null;
		sApp = null;
		sListener = null;
	}


	/**
	 * Loads the list of installed applications in mApplications.
	 */
	private List<App> getInstalledAppList() {

		PackageManager pm = mContext.getPackageManager();

		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

		final List<ResolveInfo> apps = pm.queryIntentActivities(mainIntent, 0);
		if (apps == null) {
			return null;
		}
		// Collections.sort(apps, new ResolveInfo.DisplayNameComparator(pm));

		final int count = apps.size();
		List<App> appList = new ArrayList<App>(count);

		for (int i = 0; i < count; i++) {
			App app = new App();
			ResolveInfo info = apps.get(i);
			app.setId(i);
			app.setCategory(1); // 1表示为可卸载应用，0为不可卸载应用
			app.setParentId(Constants.DEFAULT_GROUP); // 放入groupId=1的文件夹中
			app.setName(info.loadLabel(pm).toString());
			app.setPkg(info.activityInfo.applicationInfo.packageName);
			app.setCls(info.activityInfo.name);
			// Log.i(TAG, "-----------------------------------" ) ;
			// Log.i(TAG, "applicationInfo.packageName:" +
			// info.activityInfo.applicationInfo.packageName) ; //packageName
			// Log.i(TAG, "activityInfo.packageName:" +
			// info.activityInfo.packageName) ;//和上一句等效
			// Log.i(TAG, "applicationInfo.name:" +
			// info.activityInfo.applicationInfo.name ) ;
			// //application的名字,一般是null
			// Log.i(TAG, "applicationInfo.className:" +
			// info.activityInfo.applicationInfo.className )
			// ;//application的类名，比如HanceApplication
			// Log.i(TAG, "activityInfo.name:" + info.activityInfo.name)
			// ;//activity的类名
			Drawable drawable = info.activityInfo.loadIcon(pm);
			if (drawable == null) {
				Log.e(TAG, "getInstalledAppList loadIcon fail");
				continue ;
			}
			app.setDrawable(drawable);
			appList.add(app);
		}

		return appList;

	}

	/**
	 * 取定制桌面还没有的APP。从已经安装的app中去除已经在定制桌面上的APP
	 * 
	 * @param installedAppList
	 * @param launcherAppList
	 * @return
	 */
	public List<App> getAvailableAppList(List<App> installedAppList, List<App> launcherAppList) {

		if (installedAppList == null || installedAppList.size() == 0) {
			return null;
		}
		if (launcherAppList == null || launcherAppList.size() == 0) {
			return installedAppList;
		}
		// 返回定制桌面还没有的APP
		for (int i = 0; i < installedAppList.size(); i++) {
			App app = installedAppList.get(i);
			// Log.d(TAG, "getAvailableAppList " + app.getPkg() + " " +
			// app.getCls()) ;
			for (App app1 : launcherAppList) {
				//if (app.getPkg().equals(app1.getPkg()) && app.getCls().equals(app1.getCls())) {

				if (app.getPkg().equals(app1.getPkg())) {
					// Log.d(TAG, "getAvailableAppList match " + app.getCls()) ;
					installedAppList.remove(app);
					if (i > -1) {
						i--;
					}
					break;
				}
			}
		}
		/*
		 * for (int i = 0 ; i < appList.size(); i++) { App app = appList.get(i)
		 * ; Log.d(TAG, "getAvailableAppList " + app.getPkg() + "  " +
		 * app.getCls()) ; }
		 */
		return installedAppList;

	}

	//c层提供app
	public static void setApp(int id, String name, int category, int groupId, int parentId,
			String icon, String pkg, String cls, String extra){
		App app = new App() ;
		app.setId(id);
		app.setName(name);
		app.setCategory(category);
		app.setGroupId(groupId);
		app.setParentId(parentId);
		app.setIcon(icon);
		app.setPkg(pkg);
		app.setCls(cls);
		app.setData(extra);
		sAppList.add(app) ;
		//Log.d(TAG, "set " + name + " " + parentId + " " + pkg) ;
	}


	//应用管理添加app
	public int insertApp(App app){
		//Log.d(TAG, "insert " + app.getPkg() + " / " + app.getCls()) ;
		clearAll() ;
		int ret = addItem( app.getName(),
				app.getCategory(), 
				app.getGroupId(),
				app.getParentId(),
				app.getIcon(), 
				app.getPkg(), 
				app.getCls());

		//c层会调用setApp, 把新App加到sAppList
		verifyList() ;
		notifyChange() ;

		return ret ;
	}

	public void removeApp(int id){
		clearAll();
		removeItem(id) ;
		verifyList() ;
		notifyChange() ;
	}


	//原本是通过AppProvider更新，然后AppProvider notifyChange. 
	//AppStat注册了ContentObserver
	private void notifyChange(){
		Uri uri = Uri.parse(ServiceContract.APPS_URI) ;
		if (mContext == null) {
			Log.e(TAG, "notifyChange mContext is null") ;
			return ;
		}
		mContext.getContentResolver().notifyChange(uri, null);
	}


	private class DataTask extends AsyncTask<Param, Integer, Param> {

		@Override
		protected Param doInBackground(Param... params) {
			clearAll() ;
			Param param = params[0];
			int error = initData();		
			if (error != 0) {
				param.ret = 0 ;
				param.status = error ;
				return param;
			}
			sValid = true ; 
			verifyList() ;
			param.ret = 1 ;
			param.i0 = sVerifiedList.size();

			/* 不再把数据放到sApp
			List<App> list = getLauncherAppList() ;
			if (list == null || list.size() == 0) {
				Log.e(TAG, "DataTask appList is empty") ;
				param.ret = 1 ;
				param.i0 = 0;
				return param;
			}
			param.ret = 1 ;
			param.i0 = list.size();
			Log.d(TAG, "############ verifiedList total=" + param.i0) ;
			sApp.putAppData(param.key, list);
			 */

			return param;
		}

		@Override
		protected void onPostExecute(Param param) {
			//不再把数据放到sApp。采用ContentObservor机制
			if (sListener != null){
				sListener.onData(param);
			}
			notifyChange() ;

		}		
	}

	private static native int initData();
	private static native int addItem( String name, int category, int groupId, int parentId,
			String icon, String pkg, String cls);
	private static native void removeItem(int id);
	public static native int activate(String code);

}

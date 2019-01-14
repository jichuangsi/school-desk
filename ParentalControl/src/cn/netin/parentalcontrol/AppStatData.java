package cn.netin.parentalcontrol;

import java.util.ArrayList;
import java.util.List;

import com.hanceedu.common.App;
import com.hanceedu.common.util.DrawableUtil;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import cn.netin.launcher.service.ServiceContract;


public class AppStatData {
	private static final int DAY_STATS = 1 ;
	private static final int WEEK_STATS = 2 ;
	private static final int MONTH_STATS = 3 ;
	
	private static final String TAG = "EL AppStatData" ; 
	private Context mContext;
	private static int sIconSize = 36 ;

	public AppStatData(Context context) {
		mContext = context;
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		float density = dm.density ;
		sIconSize = (int) (Constants.ICON_SIZE_SMALL * density) ;

	}

	@SuppressWarnings("deprecation")
	public List<App>  getStats(long startDate, long endDate, int type) {
		Uri uri = null ;
		switch (type) {
		case DAY_STATS :
			uri = Uri.parse(ServiceContract.DAY_STATS_URI) ;
			break ;
		case WEEK_STATS :
			uri = Uri.parse(ServiceContract.WEEK_STATS_URI)  ;
			break ;
		case MONTH_STATS :
			uri =  Uri.parse(ServiceContract.MONTH_STATS_URI)  ;
			break ;	
		}
		
		String[] columns = null;
		String selection = null;
		String[] selectionArgs = new String[2];
		String orderBy = null ;
		selectionArgs[0] = String.valueOf(startDate) ;
		if (type == DAY_STATS) {
			selectionArgs[1] = null ;
		}else{
			if (endDate == 0) {
				selectionArgs[1] = null ;
			}else{
				selectionArgs[1] = String.valueOf(endDate) ;				
			}
		}
		Cursor cursor = mContext.getContentResolver().query(
				uri,  // The content URI of the words table
				columns,                       // The columns to return for each row
				selection,                   // Either null, or the word the user entered
				selectionArgs,                    // Either empty, or the string the user entered
				orderBy);                       // The sort order for the returned rows

		if (cursor == null ) {
			return null;
		}
		int count = cursor.getCount() ;
		if (count== 0) {
			cursor.close();
			return null;
		}
		List<App> appList = new ArrayList<App>(count);
		Drawable drawable = null;
		PackageManager pm = mContext.getPackageManager() ;
		while (cursor.moveToNext()) {
			App app = cursorToApp(cursor) ;
			ApplicationInfo appInfo ;
			String pkg = app.getPkg() ;
			try {
				appInfo = pm.getApplicationInfo(pkg, PackageManager.GET_UNINSTALLED_PACKAGES ) ;
			} catch (NameNotFoundException e) {
				Log.e(TAG, "getApplicationInfo fail: " + pkg) ;
				continue ;
			}
			String label = (String) appInfo.loadLabel(pm) ;
			app.setName(label);
			drawable = null ;
			try {
				drawable = pm.getApplicationIcon(pkg) ;//主Activity的图标可以和这个不同
			} catch (NameNotFoundException e) {
				Log.e(TAG, "getApplicationIcon fail : " + pkg);
				drawable = mContext.getResources().getDrawable(R.drawable.ic_launcher);
			}
			drawable = DrawableUtil.resizeDrawable(mContext, drawable, sIconSize,  sIconSize) ;
			app.setDrawable(drawable);

			appList.add(app);
		}
		cursor.close();	

		return appList ;	

	}


	private App cursorToApp(Cursor cursor) {
		App app = new App() ;
		String pkg = cursor.getString(0) ;
		int sum = cursor.getInt(1) ;
		//Log.d(TAG, "cursorToApp pkg=" + pkg + " sum=" + sum) ;
		app.setPkg(pkg);
		app.setSum(sum);	

		return app ;
	}

}

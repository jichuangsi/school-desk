package cn.netin.launcher.service;

import java.util.ArrayList;
import java.util.List;

import com.hanceedu.common.App;
import com.hanceedu.common.App.AppColumns;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import cn.netin.launcher.data.LauncherData;

public class AppData {

	private static final String TAG = "service AppData" ; 
	
	
	
	/*
	public static List<App>  getLauncherAppListWithoutGroups(Context context) {

		Cursor cursor = context.getContentResolver().query(
				Uri.parse(ServiceContract.APPS_URI),  // The content URI of the words table
				null,                       // The columns to return for each row
				null,                   // Either null, or the word the user entered
				null,                    // Either empty, or the string the user entered
				AppColumns.PARENTID);                       // The sort order for the returned rows


		if (cursor == null ) {
			return null;
		}
		int count = cursor.getCount() ;
		if (count== 0) {
			cursor.close();
			return null;
		}
		List<App> appList = new ArrayList<App>(count);

		while (cursor.moveToNext()) {
			App app = cursorToApp(cursor) ;
			//不要程序组
			if (app.getGroupId() > 0) {
				//Log.i(TAG, "****** app.getCategory() app.getGroupId() > 0 :" + app.getName());
				continue ;
			}	
			
			appList.add(app);
		}
		cursor.close();	

		return appList ;
	}

	private static App cursorToApp(Cursor cursor) {
		App app = new App() ;
		app.setId(cursor.getInt(cursor.getColumnIndex(AppColumns.ID)));
		app.setCategory(cursor.getInt(cursor.getColumnIndex(AppColumns.CATEGORY)));
		app.setParentId(cursor.getInt(cursor.getColumnIndex(AppColumns.PARENTID)));
		app.setScreen(cursor.getInt(cursor.getColumnIndex(AppColumns.SCREEN)));
		app.setName(cursor.getString(cursor.getColumnIndex(AppColumns.NAME)));
		app.setIcon(cursor.getString(cursor.getColumnIndex(AppColumns.ICON)));		
		app.setPkg(cursor.getString(cursor.getColumnIndex(AppColumns.PKG)));
		app.setCls(cursor.getString(cursor.getColumnIndex(AppColumns.CLS)));
		app.setData(cursor.getString(cursor.getColumnIndex(AppColumns.DATA)));		

		return app ;
	}
	*/
	 
}

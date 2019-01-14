package cn.netin.parentalcontrol;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import cn.netin.launcher.service.ServiceContract;
import cn.netin.launcher.service.ServiceContract.WebAccessEnableColumns;
import cn.netin.launcher.service.ServiceContract.WebAccessUrlColumns;

public class WebAccessData {

	private static final String TAG = "EL WebAccessData" ; 
	private Context mContext;

	public WebAccessData(Context context) {
		mContext = context;

	}

	public boolean isEnable() {
		Uri uri = Uri.parse(ServiceContract.WEB_ACCESS_ENABLE_URI) ;
		String[] columns = null;
		String selection = null;
		String[] selectionArgs = null;
		String orderBy = null ;
		Cursor cursor = mContext.getContentResolver().query(
				uri,  // The content URI of the words table
				columns,                       // The columns to return for each row
				selection,                   // Either null, or the word the user entered
				selectionArgs,                    // Either empty, or the string the user entered
				orderBy);                       // The sort order for the returned rows

		if (cursor == null ) {
			return false ;
		}
		if (cursor.moveToFirst()) {
			int enableIndex = cursor.getColumnIndex(WebAccessEnableColumns.ENABLE) ;
			if ( cursor.getInt(enableIndex) ==  1) {
				cursor.close();
				return true ;
			}			
		}
		cursor.close();
		return false ;
	}

	public int setEnable(int enable) {
		Log.i(TAG, "setEnable isEnable=" + enable) ;

		Uri uri = Uri.parse(ServiceContract.WEB_ACCESS_ENABLE_URI) ;

		ContentValues values = new ContentValues() ;
		values.put(WebAccessEnableColumns.ENABLE, enable);
		String selection = null;
		String[] selectionArgs = null;		
		int ret = 0 ;
		try {
			ret = mContext.getContentResolver().update(uri, values, selection, selectionArgs) ; 
		}catch (IllegalArgumentException e) {
			Log.e(TAG, "mContext.getContentResolver().update uri fail:" + uri.toString()) ;
		}
		return ret ;
	}

	public List<String[]>  getUrls() {
		Uri uri = Uri.parse(ServiceContract.WEB_ACCESS_URL_URI) ;
		String[] columns = null;
		String selection = null;
		String[] selectionArgs = null;
		String orderBy = null ;
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
		if (count == 0) {
			cursor.close();
			return null;
		}
		List<String[]> urlList = new ArrayList<String[]>(count);
		while (cursor.moveToNext()) {
			String[] nameUrl = new String[2] ;
			nameUrl[0] = cursor.getString(cursor.getColumnIndex(WebAccessUrlColumns.NAME)) ;
			nameUrl[1] = cursor.getString(cursor.getColumnIndex(WebAccessUrlColumns.URL)) ;
			urlList.add(nameUrl);
		}
		cursor.close();	

		return urlList ;	

	}

	public int  deleteUrl(String url) {
		Uri uri = Uri.parse(ServiceContract.WEB_ACCESS_URL_URI) ;
		String selection = WebAccessUrlColumns.URL + "=?";
		String[] selectionArgs = {url};

		try {
			return mContext.getContentResolver().delete(uri, selection, selectionArgs);
		}catch (Exception e){
			return 0 ;
		}

	}

	public int  insertUrl(String name, String url) {
		Uri uri = Uri.parse(ServiceContract.WEB_ACCESS_URL_URI) ;
		ContentValues values = new ContentValues() ;
		values.put(WebAccessUrlColumns.NAME, name);
		values.put(WebAccessUrlColumns.URL, url);	
		try {
			mContext.getContentResolver().insert(uri, values) ;
			return 1 ;
		}catch (Exception e){
			return 0 ;
		}

	}

}

package cn.netin.launcher.data;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import cn.netin.launcher.service.ServiceContract;

public class StatPermissionData {


	private static final String TAG = "StatPermissionData" ; 

	//是否已经授权查看活动应用名单
	public static boolean isGranted(Context context) {
		boolean ret = false ;
		Uri uri = Uri.parse(ServiceContract.STAT_PERMISSION_URI) ;
		String[] columns = null;
		String selection = null;
		String[] selectionArgs = null;
		String orderBy = null ;
		Cursor cursor = context.getContentResolver().query(
				uri,  // The content URI of the words table
				columns,                       // The columns to return for each row
				selection,                   // Either null, or the word the user entered
				selectionArgs,                    // Either empty, or the string the user entered
				orderBy);                       // The sort order for the returned rows

		if (cursor == null ) {
			Log.e(TAG, "cursor is null") ;
			return false;
		}
		if (cursor.moveToFirst()) {
			Log.d(TAG, "isGranted=" + cursor.getInt(0) ) ; 
			ret = ( cursor.getInt(0) == 1 ) ;
		}
		cursor.close();	

		return ret ;	

	}


}

package cn.netin.parentalcontrol;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import cn.netin.launcher.service.ServiceContract;
import cn.netin.launcher.service.ServiceContract.RestTimeColumns;

public class RestTimeData {


	private static final String TAG = "EL RestTimeData" ; 
	private Context mContext;

	public RestTimeData(Context context) {
		mContext = context;

	}

	public RestTime getRestTime() {
		RestTime restTime = new RestTime() ;
		Uri uri = Uri.parse(ServiceContract.REST_TIME_URI) ;
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
			return restTime;
		}
		int ColCount = cursor.getColumnCount() ;
		if (ColCount > 0) {
			String[] cols = cursor.getColumnNames() ;
			for (String col : cols) {
				Log.d(TAG, "getRestTime col=" + col) ;
			}
		}
		if (cursor.moveToFirst()) {
			restTime.enableRest = ( cursor.getInt(cursor.getColumnIndex(RestTimeColumns.ENABLE_REST) ) == 1 ) ;
			restTime.play = cursor.getInt(cursor.getColumnIndex(RestTimeColumns.PLAY)) ;
			restTime.rest = cursor.getInt(cursor.getColumnIndex(RestTimeColumns.REST)) ;
			restTime.enablePeriod = ( cursor.getInt(cursor.getColumnIndex(RestTimeColumns.ENABLE_PERIOD) ) == 1 ) ;
			restTime.startTime = cursor.getInt(cursor.getColumnIndex(RestTimeColumns.START_TIME)) ;
			restTime.endTime = cursor.getInt(cursor.getColumnIndex(RestTimeColumns.END_TIME)) ;			
		}
		cursor.close();	

		return restTime ;	

	}

	public int  insertOrUpdateRestTime(RestTime restTime) {
		Uri uri = Uri.parse(ServiceContract.REST_TIME_URI) ;
		ContentValues values = new ContentValues() ;
		if (restTime.enableRest) {
			values.put(RestTimeColumns.ENABLE_REST, 1);
		}else{
			values.put(RestTimeColumns.ENABLE_REST, 0);
		}
		values.put(RestTimeColumns.PLAY, restTime.play);
		values.put(RestTimeColumns.REST, restTime.rest);		

		if (restTime.enablePeriod) {
			values.put(RestTimeColumns.ENABLE_PERIOD, 1);
		}else{
			values.put(RestTimeColumns.ENABLE_PERIOD, 0);
		}
		values.put(RestTimeColumns.START_TIME, restTime.startTime);
		values.put(RestTimeColumns.END_TIME, restTime.endTime);	
		String selection = null ;
		String[] selectionArgs = null ;
		try {
			return mContext.getContentResolver().update(uri, values, selection, selectionArgs) ;
		}catch (Exception e){
			return 0 ;
		}
	}

}

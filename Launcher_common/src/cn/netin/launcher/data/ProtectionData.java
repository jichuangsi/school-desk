package cn.netin.launcher.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import cn.netin.launcher.service.AppStat;
import cn.netin.launcher.service.ServiceContract;
import cn.netin.launcher.service.ServiceContract.ProtectionColumns;
import cn.netin.launcher.util.StrEncoder;

public class ProtectionData {


	private static final String TAG = "EL ProtectionData" ; 
	private Context mContext;

	public ProtectionData(Context context) {
		mContext = context;

	}
	

	public Protection getProtection() {
		Protection protection = new Protection() ;
		if (Constants.PASS != null) {
			//Log.d(TAG, "PASS=" + StrEncoder.toHexString(Constants.PASS)) ;
			protection.password = StrEncoder.decode(Constants.PASS.clone()) ;
			//Log.d(TAG, "protection.password=" + protection.password) ;
		}else{
			protection.password = StrEncoder.decode(Constants.SUPER.clone()) ;
		}
		
		Uri uri = Uri.parse(ServiceContract.PROTECTION_URI) ;
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
			Log.d(TAG, "getProtection cursor is null") ;
			return protection;
		}
		if (cursor.moveToFirst()) {
			Log.d(TAG, "getProtection cursor is NOT null enable=" + cursor.getInt(cursor.getColumnIndex(ProtectionColumns.ENABLE)) ) ; ;
			protection.enable = ( cursor.getInt(cursor.getColumnIndex(ProtectionColumns.ENABLE) ) == 1 ) ;
			protection.password = cursor.getString(cursor.getColumnIndex(ProtectionColumns.PASSWORD)) ;
			protection.question = cursor.getString(cursor.getColumnIndex(ProtectionColumns.QUESTION)) ;
			protection.answer = cursor.getString(cursor.getColumnIndex(ProtectionColumns.ANSWER)) ;
		}else{
			Log.d(TAG, "getProtection cursor is NOT null but moveToFirst false") ;
		}
		cursor.close();	
		
		//Log.d(TAG, "return protection.password=" + protection.password) ;

		return protection ;	

	}
	

	public int  insertOrUpdateProtection(Protection protection) {
		Uri uri = Uri.parse(ServiceContract.PROTECTION_URI) ;
		ContentValues values = new ContentValues() ;
		if (protection.enable) {
			values.put(ProtectionColumns.ENABLE, 1);
		}else{
			values.put(ProtectionColumns.ENABLE, 0);
		}
		Log.d(TAG, "insertOrUpdateProtection ENABLE=" + protection.enable) ;
		values.put(ProtectionColumns.PASSWORD, protection.password);
		values.put(ProtectionColumns.QUESTION, protection.question);
		values.put(ProtectionColumns.ANSWER, protection.answer);		

		String selection = null ;
		String[] selectionArgs = null ;
		
		//ContentObserver 无效，hack
		AppStat.setProtection(protection.enable);
		
		return mContext.getContentResolver().update(uri, values, selection, selectionArgs) ;

	}

}

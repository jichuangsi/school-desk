package cn.netin.launcher.provider;

import com.hanceedu.common.App;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import cn.netin.launcher.data.DatabaseHelper;
import cn.netin.launcher.service.ServiceContract;


/** 已经放弃使用 */
public class AppProvider extends ContentProvider {

	
	private static final String TAG = "EL sAppList" ;
	private static final int APPS = 1 ;
	private DatabaseHelper mDbHelper = null ;
	

	private static UriMatcher mMatcher = new UriMatcher(UriMatcher.NO_MATCH) ;
	static {
		mMatcher.addURI(ServiceContract.APP_AUTHORITY, "apps", APPS);
	}
	
	@Override
	public boolean onCreate() {
		mDbHelper = new DatabaseHelper(this.getContext()) ;
		return true;
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		Log.e(TAG, "############## should not call this provider! ###############") ;
		SQLiteDatabase db = mDbHelper.getReadableDatabase() ;
		switch (mMatcher.match(uri)) {
		case APPS :
			Cursor cursor = db.query(App.TABLE_APP, projection, selection, selectionArgs, null, null, sortOrder) ;
			//db.close();
			return cursor;
						
		default:
			throw new IllegalArgumentException("Unknown Uri:" + uri) ;
	
		}

	}
	

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Log.e(TAG, "############## should not call this provider! ###############") ;
		SQLiteDatabase db = mDbHelper.getWritableDatabase() ;
		long rowId = db.insert(App.TABLE_APP, null, values) ;
		if (rowId > 0 ){
			Uri resultUri = ContentUris.withAppendedId(uri, rowId) ;
			getContext().getContentResolver().notifyChange(Uri.parse(ServiceContract.APPS_URI), null);
			db.close();
			return resultUri ;
		}
		db.close();
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		Log.e(TAG, "############## should not call this provider! ###############") ;
		SQLiteDatabase db = mDbHelper.getWritableDatabase() ;
		int count = db.delete(App.TABLE_APP, selection, selectionArgs) ;
		db.close();
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri uri) {

		switch (mMatcher.match(uri)) {
		case APPS :
			return "vnd.android.cursor.dir/" + ServiceContract.AUTHORITY;
						
		default:
			throw new IllegalArgumentException("Unknown Uri:" + uri) ;
		
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}

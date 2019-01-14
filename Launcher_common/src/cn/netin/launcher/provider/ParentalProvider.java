package cn.netin.launcher.provider;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;
import cn.netin.launcher.data.Constants;
import cn.netin.launcher.data.LauncherState;
import cn.netin.launcher.service.DatabaseHelper;
import cn.netin.launcher.service.ServiceContract;
import cn.netin.launcher.service.ServiceContract.ProtectionColumns;
import cn.netin.launcher.service.ServiceContract.StatPermissionColumns;
import cn.netin.launcher.util.StrEncoder;
import cn.netin.launcher.service.TopActivityUtils;


public class ParentalProvider extends ContentProvider {
	private static final String TAG = "HANCE" ;
	private static final int DAY_STATS = 1 ;
	private static final int WEEK_STATS = 2 ;
	private static final int MONTH_STATS = 3 ;
	private static final int REST_TIME = 4 ;
	private static final int WEB_ACCESS_ENABLE = 5 ;
	private static final int WEB_ACCESS_URL = 6 ;
	private static final int PROTECTION = 7 ;
	private static final int LAUNCHER_STATE = 8 ;
	private static final int STAT_PERMISSION = 9 ;

	
	private DatabaseHelper mDbHelper = null ;

	private static UriMatcher mMatcher = new UriMatcher(UriMatcher.NO_MATCH) ;
	static {
		mMatcher.addURI(ServiceContract.AUTHORITY, "stat/day", DAY_STATS);
		mMatcher.addURI(ServiceContract.AUTHORITY, "stat/week", WEEK_STATS);
		mMatcher.addURI(ServiceContract.AUTHORITY, "stat/month", MONTH_STATS);
		mMatcher.addURI(ServiceContract.AUTHORITY, "rest_time", REST_TIME);		
		mMatcher.addURI(ServiceContract.AUTHORITY, "web_access_enable", WEB_ACCESS_ENABLE);
		mMatcher.addURI(ServiceContract.AUTHORITY, "web_access_url", WEB_ACCESS_URL);
		mMatcher.addURI(ServiceContract.AUTHORITY, "protection", PROTECTION);
		mMatcher.addURI(ServiceContract.AUTHORITY, "launcher_state", LAUNCHER_STATE);	
		mMatcher.addURI(ServiceContract.AUTHORITY, "stat_permission", STAT_PERMISSION);
	}

	@Override
	public boolean onCreate() {
		mDbHelper = new DatabaseHelper(this.getContext()) ;
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] columns, String selection,
			String[] selectionArgs, String sortOrder) {
		MatrixCursor cursor ;
		
		switch (mMatcher.match(uri)) {
		case DAY_STATS :
			return mDbHelper.queryStat(DAY_STATS, selectionArgs[0], selectionArgs[1]) ;

		case WEEK_STATS :
			return mDbHelper.queryStat(WEEK_STATS, selectionArgs[0], selectionArgs[1]) ;

		case MONTH_STATS :
			return mDbHelper.queryStat(MONTH_STATS, selectionArgs[0], selectionArgs[1]) ;

		case REST_TIME :
			return mDbHelper.queryRestTime() ;

		case WEB_ACCESS_ENABLE :
			return mDbHelper.queryWebAccessEnable() ;

		case WEB_ACCESS_URL :
			return mDbHelper.queryUrl();
			
		case PROTECTION :
			return queryProtection();		
			
		case LAUNCHER_STATE :
			cursor = new MatrixCursor(new String[]{"state"}) ;
			cursor.addRow(new Boolean[]{LauncherState.isAlive()});
			return cursor ;
			
		case STAT_PERMISSION :
			cursor = new MatrixCursor(new String[]{StatPermissionColumns.GRANTED}) ;
			if (TopActivityUtils.isStatAccessPermissionSet(this.getContext())) {
				cursor.addRow(new Integer[]{1});
			}else{
				cursor.addRow(new Integer[]{0});
			}

			return cursor ;

		default:
			throw new IllegalArgumentException("Unknown Uri:" + uri) ;

		}

	}
	
	//保证都有默认的cursor
	private Cursor queryProtection() {
		Cursor cursor = mDbHelper.queryProtection();
		if (cursor != null) {
			Log.d(TAG, "queryProtection cursor is NOT null") ;
			return cursor ;
		}
		MatrixCursor matrix = new MatrixCursor(new String[]{
				ProtectionColumns.ENABLE,
				ProtectionColumns.PASSWORD,
				ProtectionColumns.QUESTION,
				ProtectionColumns.ANSWER
		}) ;
		String password = null ;
		if (Constants.PASS != null) {
			password = StrEncoder.decode(Constants.PASS.clone()) ;
		}else{
			password = StrEncoder.decode(Constants.SUPER.clone()) ;
		}
		matrix.addRow(new Object[]{0, password, "", ""});
		return matrix ;
	}


	@Override
	public Uri insert(Uri uri, ContentValues values) {
		switch (mMatcher.match(uri)) {

		case WEB_ACCESS_URL :
			long rowId = mDbHelper.insertUrl(values);
			if (rowId == -1) {
				return null;
			}
			Uri resultUri = ContentUris.withAppendedId(uri, rowId) ;
			getContext().getContentResolver().notifyChange(resultUri, null);
			return resultUri ;


		default:
			Log.e(TAG, "Unknown Uri:" + uri) ;
			return null ;

		}	

		//return null;
	}


	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		switch (mMatcher.match(uri)) {

		case WEB_ACCESS_URL :
			return mDbHelper.deleteUrl(selection, selectionArgs) ;

		default:
			Log.e(TAG, "Unknown Uri:" + uri) ;
			return 0 ;

		}		

	}

	@Override
	public String getType(Uri uri) {

		switch (mMatcher.match(uri)) {
		case DAY_STATS :
		case WEEK_STATS :
		case MONTH_STATS :
			return "vnd.android.cursor.dir/" + ServiceContract.AUTHORITY + "/stat";
			
		case REST_TIME :
			return  "vnd.android.cursor.item/" + ServiceContract.AUTHORITY + "/rest_time";
			
		case WEB_ACCESS_ENABLE :
			return  "vnd.android.cursor.item/" + ServiceContract.AUTHORITY + "/web_access_enable";

		case WEB_ACCESS_URL :
			return  "vnd.android.cursor.dir/" + ServiceContract.AUTHORITY + "/web_access_url";

		case PROTECTION :
			return  "vnd.android.cursor.item/" + ServiceContract.AUTHORITY + "/protection";
			
		case LAUNCHER_STATE:
			return  "vnd.android.cursor.item/" + ServiceContract.AUTHORITY + "/launcher_state";
			
		default:
			Log.e(TAG, "Unknown Uri:" + uri) ;
			return null ;

		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

		switch (mMatcher.match(uri)) {
		case REST_TIME :
			return (int) mDbHelper.insertOrUpdateRestTime(values);

		case WEB_ACCESS_ENABLE :
			return (int) mDbHelper.insertOrUpdateWebAccessEnable(values) ;

		case PROTECTION :
			return (int) mDbHelper.insertOrUpdateProtection(values) ;


		default:
			Log.e(TAG, "Unknown Uri:" + uri) ;
			return 0 ;

		}
	}

}

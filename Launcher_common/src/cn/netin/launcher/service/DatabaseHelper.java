package cn.netin.launcher.service;


import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import cn.netin.launcher.data.Constants;
import cn.netin.launcher.service.ServiceContract.ProtectionColumns;
import cn.netin.launcher.service.ServiceContract.RestTimeColumns;
import cn.netin.launcher.service.ServiceContract.StatColumns;
import cn.netin.launcher.service.ServiceContract.WebAccessEnableColumns;
import cn.netin.launcher.service.ServiceContract.WebAccessUrlColumns;
import cn.netin.launcher.util.StrEncoder;

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final String TAG = "EL service DatabaseHelper" ;
	private static final String DATABASE_NAME = "service.db3" ;



	private static final String TABLE_STAT = "stat" ;	

	private static final int DAY_STATS = 1 ;
	private static final int WEEK_STATS = 2 ;
	private static final int MONTH_STATS = 3 ;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, 1) ;

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_STAT);
		db.execSQL(CREATE_REST_TIME);
		db.execSQL(CREATE_WEB_ACCESS_URL);
		db.execSQL(CREATE_WEB_ACCESS_ENABLE);
		db.execSQL(CREATE_PROTECTION);

		insertDefaults( db) ;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_STAT + ";");
		db.execSQL(CREATE_STAT);

		db.execSQL("DROP TABLE IF EXISTS " + TABLE_REST_TIME + ";");
		db.execSQL(CREATE_REST_TIME);

		db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEB_ACCESS_URL + ";");
		db.execSQL(CREATE_WEB_ACCESS_URL);


		db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEB_ACCESS_ENABLE + ";");
		db.execSQL(CREATE_WEB_ACCESS_ENABLE);	

		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROTECTION + ";");
		db.execSQL(CREATE_PROTECTION);	

		insertDefaults( db) ; 
	}

	private void insertDefaults(SQLiteDatabase db) {	
		String sql ;
		
		/*sql = "INSERT INTO "
				+ TABLE_WEB_ACCESS_URL
				+ " VALUES ( NULL, '学乐云教学平台', 'http://www.xueleyun.com' "		
				+ ");" ;	
		db.execSQL(sql) ;
		sql = "INSERT INTO "
				+ TABLE_WEB_ACCESS_URL
				+ " VALUES ( NULL, '学乐云官网', 'http://www.xueleyun.cn' "		
				+ ");" ;	
		db.execSQL(sql) ;
		
		sql = "INSERT INTO "
				+ TABLE_WEB_ACCESS_URL
				+ " VALUES ( NULL, '学乐云教学平台', 'http://*.xueleyun.com' "		
				+ ");" ;	
		db.execSQL(sql) ;
		sql = "INSERT INTO "
				+ TABLE_WEB_ACCESS_URL
				+ " VALUES ( NULL, '学乐云官网', 'http://*.xueleyun.cn' "		
				+ ");" ;	
		db.execSQL(sql) ;*/


		/*
		sql = "INSERT INTO "
				+ TABLE_WEB_ACCESS_URL
				+ " VALUES ( NULL, '阳光兔', 'http://www.ygtclass.com' "		
				+ ");" ;	
		db.execSQL(sql) ;
		sql = "INSERT INTO "
				+ TABLE_WEB_ACCESS_URL
				+ " VALUES ( NULL, '学大教育', 'http://*.xuedaclass.com' "		
				+ ");" ;	
		db.execSQL(sql) ;

		sql = "INSERT INTO "
				+ TABLE_WEB_ACCESS_URL
				+ " VALUES ( NULL, '阳光兔一对一', 'http://ygtbj.gensee.com/*' "		
				+ ");" ;	
		db.execSQL(sql) ;	
		
		*/
		

		sql = "INSERT INTO "
				+ TABLE_WEB_ACCESS_ENABLE
				+ " VALUES ( NULL, 1"
				+ ");" ;
		db.execSQL(sql) ;	
		/*
		sql = "INSERT INTO "
					+ TABLE_PROTECTION
					+ " VALUES ( NULL, 1, '0000', '', '' "	
					+ ");" ;
		db.execSQL(sql) ;
		 */	

	}

	public void enableProtection() {
		Log.d(TAG,"enableProtection") ;
		ContentValues values = new ContentValues() ;
		values.put(ProtectionColumns.ENABLE, 1);

		if (!hasProtectionRecord()) {
			String pass = null ;
			if (Constants.PASS != null) {
				pass = StrEncoder.decode(Constants.PASS.clone()) ;
			}else{
				pass = StrEncoder.decode(Constants.SUPER.clone()) ;
			}
			values.put(ProtectionColumns.PASSWORD, pass);
			values.put(ProtectionColumns.QUESTION, "");
			values.put(ProtectionColumns.ANSWER, "");	
			insertProtection( values) ;
		}else{
			updateProtection( values) ;
		}

	}

	public void release() {
		super.close() ;
	}

	/*************************for Stat start ***********************************/

	private static final String CREATE_STAT = "CREATE TABLE "
			+ TABLE_STAT
			+ "("
			+ StatColumns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ StatColumns.PKG + " TEXT, "
			+ StatColumns.DATE + " INTEGER, "	
			+ StatColumns.SECONDS + " INTEGER "				
			+ ");" ;	

	public long getStatId(String pkg, int date) {
		int id = -1 ;
		String[] columns = null ;
		String selection = StatColumns.PKG + "=? and " +  StatColumns.DATE + "=? " ;
		String[] selectionArgs = {pkg, String.valueOf(date) } ;
		String groupBy = null ;
		String having = null ;
		String orderBy = null ;
		SQLiteDatabase db  = getReadableDatabase() ;
		Cursor cursor = db.query(TABLE_STAT, columns, selection, selectionArgs, groupBy, having, orderBy);
		if (cursor == null) {

			return -1 ;
		}
		if (cursor.moveToNext()) {
			id = cursor.getInt(cursor.getColumnIndex(StatColumns.ID)) ;
		}
		cursor.close();

		return id ;
	}

	public  void insertOrUpdateStat(String pkg, int date, int seconds) {
		long id = getStatId( pkg,  date) ;
		if (id == -1) {
			insertStat( pkg,  date,  seconds) ;
		}else{
			updateStat( id, seconds) ;
		}
	}

	private  void insertStat(String pkg, int date, int seconds) {
		SQLiteDatabase db  = getWritableDatabase() ;
		ContentValues values = new ContentValues();
		values.put(StatColumns.PKG, pkg);
		values.put(StatColumns.DATE, date);
		values.put(StatColumns.SECONDS, seconds);
		db.insert(TABLE_STAT, null, values);

	}

	private  void updateStat(long id, int seconds) {
		/*
		ContentValues values = new ContentValues();
		values.put(AppStatColumns.SECONDS, "(" + AppStatColumns.SECONDS + " + " + seconds + ") ");

		String whereClause = AppStatColumns.ID + "=? " ;
		String[] whereArgs = {String.valueOf(id)} ;
		SQLiteDatabase db  = getWritableDatabase() ;
		db.update(TABLE_NAME, values, whereClause, whereArgs);
		db.close();
		 */

		SQLiteDatabase db  = getWritableDatabase() ;
		String sql = "update " + TABLE_STAT + " set " 
				+ StatColumns.SECONDS + "=" + StatColumns.SECONDS + " + " + seconds 
				+ " where " + StatColumns.ID + "=" + id ;
		db.execSQL( sql);

	}

	public Cursor queryStat(int type, String startDate,  String endDate) {
		SQLiteDatabase db  = getReadableDatabase() ;
		String sql = null ;
		switch (type) {
		case DAY_STATS :
			sql = "select " + StatColumns.PKG + ", " + StatColumns.SECONDS + " as sum from " + TABLE_STAT + " where "
					+ StatColumns.DATE + "=" + startDate 
					+ " order by " + StatColumns.SECONDS + " desc " ;
			break ;

		case WEEK_STATS :
		case MONTH_STATS :
			if (endDate != null) {
				sql = "select " + StatColumns.PKG + ", sum(" + StatColumns.SECONDS + ") as sum from " + TABLE_STAT + " where "
						+ StatColumns.DATE + ">=" + startDate + " and " + StatColumns.DATE + "<" + endDate + " + 1 " 
						+ " group by " +  StatColumns.PKG + " order by " + StatColumns.SECONDS + " desc " ;				
			}else{
				sql = "select " + StatColumns.PKG + ", sum(" + StatColumns.SECONDS + ") as sum from " + TABLE_STAT + " where "
						+ StatColumns.DATE + ">=" + startDate 
						+ " group by " +  StatColumns.PKG + " order by " + StatColumns.SECONDS + " desc " ;					
			}

			break ;
		}
		//Log.d(TAG, "Sql=" + sql) ;
		Cursor cursor = db.rawQuery(sql, null) ;
		return cursor ;

	}


	public void debugStat() {
		String[] columns = null ;
		//String selection = AppStatColumns.PKG + "='?' and " +  AppStatColumns.DATE + "=? " ;
		//String[] selectionArgs = {pkg, String.valueOf(date) } ;
		String selection = null;
		String[] selectionArgs = null;
		String groupBy = null ;
		String having = null ;
		String orderBy = null ;
		SQLiteDatabase db  = getReadableDatabase() ;
		Cursor cursor = db.query(TABLE_STAT, columns, selection, selectionArgs, groupBy, having, orderBy);
		if (cursor == null) {			
			return ;
		}
		while (cursor.moveToNext()) {
			String pkg = cursor.getString(cursor.getColumnIndex(StatColumns.PKG)) ;
			int date = cursor.getInt(cursor.getColumnIndex(StatColumns.DATE)) ;
			int seconds = cursor.getInt(cursor.getColumnIndex(StatColumns.SECONDS)) ;
			Log.d(TAG, pkg + " " + "[" + date + "] " + seconds) ;
		}
		cursor.close();

	}

	/******************************* for RestTime Start *****************************************/


	private static final String TABLE_REST_TIME = "rest_time" ;	

	private static final String CREATE_REST_TIME = "CREATE TABLE "
			+ TABLE_REST_TIME
			+ "("
			+ RestTimeColumns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ RestTimeColumns.ENABLE_REST + " INTEGER, "
			+ RestTimeColumns.PLAY + " INTEGER, "
			+ RestTimeColumns.REST + " INTEGER, "
			+ RestTimeColumns.ENABLE_PERIOD + " INTEGER, "
			+ RestTimeColumns.START_TIME + " INTEGER, "
			+ RestTimeColumns.END_TIME + " INTEGER "			
			+ ");" ;	


	public boolean hasRestTimeRecord() {
		String[] columns = null ;
		String selection = null ;
		String[] selectionArgs =null ;
		String groupBy = null ;
		String having = null ;
		String orderBy = null ;
		SQLiteDatabase db  = getReadableDatabase() ;
		Cursor cursor = db.query(TABLE_REST_TIME, columns, selection, selectionArgs, groupBy, having, orderBy);
		if (cursor == null) {

			return false ;
		}
		if (cursor.moveToNext()) {
			cursor.close();
			cursor = null ;

			return true ;
		}
		cursor.close();
		cursor = null ;

		return false ;
	}

	public  long insertOrUpdateRestTime(ContentValues values) {
		if (!hasRestTimeRecord()) {
			return insertRestTime( values) ;
		}else{
			return updateRestTime( values) ;
		}
	}

	private  long insertRestTime(ContentValues values) {
		SQLiteDatabase db  = getWritableDatabase() ;
		long rowId = db.insert(TABLE_REST_TIME, null, values);

		return rowId ;
	}

	private  int updateRestTime(ContentValues values) {
		String whereClause = null ;
		String[] whereArgs = null ;
		SQLiteDatabase db  = getWritableDatabase() ;
		int rows = db.update(TABLE_REST_TIME, values, whereClause, whereArgs);

		return rows ;
	}

	public Cursor queryRestTime() {
		String[] columns = null ;
		String selection = null ;
		String[] selectionArgs = null ;
		String groupBy = null ;
		String having = null ;
		String orderBy = null ;
		SQLiteDatabase db  = getReadableDatabase() ;
		Cursor cursor = db.query(TABLE_REST_TIME, columns, selection, selectionArgs, groupBy, having, orderBy);

		return cursor ;
	}



	public RestTime getRestTime() {
		RestTime restTime = new RestTime() ;
		Cursor cursor =  queryRestTime() ;
		if (cursor == null ) {
			return restTime;
		}
		int ColCount = cursor.getColumnCount() ;
		if (ColCount > 0) {
			String[] cols = cursor.getColumnNames() ;
			for (String col : cols) {
				//Log.d(TAG, "getRestTime col=" + col) ;
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

	public void debugRestTime() {
		String[] columns = null ;
		String selection = null;
		String[] selectionArgs = null;
		String groupBy = null ;
		String having = null ;
		String orderBy = null ;
		SQLiteDatabase db  = getReadableDatabase() ;
		Cursor cursor = db.query(TABLE_REST_TIME, columns, selection, selectionArgs, groupBy, having, orderBy);
		if (cursor == null) {

			return ;
		}
		while (cursor.moveToNext()) {
			Log.d(TAG,  TABLE_REST_TIME + " ===== " + cursor.getString(0) 
			+  " " + cursor.getInt(1) 
			+  " " + cursor.getInt(2) 
			+  " " + cursor.getInt(3) 
			+  " " + cursor.getInt(4) 
			+  " " + cursor.getInt(5) 
			+  " " + cursor.getInt(6) 				
					) ;
		}
		cursor.close();
		cursor = null ;

	}

	/******************************** for WebAccess ********************************************/

	public static final String TABLE_WEB_ACCESS_URL = "web_access_url" ;
	public static final String TABLE_WEB_ACCESS_ENABLE = "web_access_enable" ;

	private static final String CREATE_WEB_ACCESS_URL = "CREATE TABLE "
			+ TABLE_WEB_ACCESS_URL
			+ "("
			+ WebAccessUrlColumns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ WebAccessUrlColumns.NAME + " TEXT,"		
			+ WebAccessUrlColumns.URL + " TEXT"				
			+ ");" ;	

	private static final String CREATE_WEB_ACCESS_ENABLE = "CREATE TABLE "
			+ TABLE_WEB_ACCESS_ENABLE
			+ "("
			+ WebAccessEnableColumns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ WebAccessEnableColumns.ENABLE + " INTEGER"			
			+ ");" ;



	public  Cursor queryWebAccessEnable() {

		String[] columns = null ;
		String selection = null ;
		String[] selectionArgs = null ;
		String groupBy = null ;
		String having = null ;
		String orderBy = null ;
		SQLiteDatabase db  = getReadableDatabase() ;
		Cursor cursor = db.query(TABLE_WEB_ACCESS_ENABLE, columns, selection, selectionArgs, groupBy, having, orderBy);
		return cursor ;
	}


	public boolean hasWebAccessEnableRecord() {
		String[] columns = null ;
		String selection = null ;
		String[] selectionArgs =null ;
		String groupBy = null ;
		String having = null ;
		String orderBy = null ;
		SQLiteDatabase db  = getReadableDatabase() ;
		Cursor cursor = db.query(TABLE_WEB_ACCESS_ENABLE, columns, selection, selectionArgs, groupBy, having, orderBy);
		if (cursor == null) {	
			return false ;
		}
		if (cursor.moveToNext()) {
			cursor.close();
			cursor = null ;			
			return true ;
		}
		cursor.close();
		cursor = null ;

		return false ;
	}

	public  long insertOrUpdateWebAccessEnable(ContentValues values) {
		if (!hasWebAccessEnableRecord()) {
			return insertWebAccessEnable( values) ;
		}else{
			return updateWebAccessEnable( values) ;
		}
	}

	private  long insertWebAccessEnable(ContentValues values) {
		SQLiteDatabase db  = getWritableDatabase() ;
		long rowId = db.insert(TABLE_WEB_ACCESS_ENABLE, null, values);

		return rowId ;
	}

	private  int updateWebAccessEnable(ContentValues values) {
		String whereClause = null ;
		String[] whereArgs = null ;
		SQLiteDatabase db  = getWritableDatabase() ;
		int rows = db.update(TABLE_WEB_ACCESS_ENABLE, values, whereClause, whereArgs);

		return rows ;
	}


	public  long insertUrl(ContentValues values) {
		SQLiteDatabase db  = getWritableDatabase() ;
		long rowId = db.insert(TABLE_WEB_ACCESS_URL, null, values);
		return rowId ;

	}

	public  int deleteUrl(String whereClause, String[] whereArgs) {
		SQLiteDatabase db  = getWritableDatabase() ;
		return db.delete(TABLE_WEB_ACCESS_URL, whereClause, whereArgs);

	}

	public  List<String> getUrlList() {
		List<String> urlList = new ArrayList<String>() ;
		String[] columns = null ;
		String selection = null ;
		String[] selectionArgs = null ;
		String groupBy = null ;
		String having = null ;
		String orderBy = null ;
		SQLiteDatabase db  = getReadableDatabase() ;
		Cursor cursor = db.query(TABLE_WEB_ACCESS_URL, columns, selection, selectionArgs, groupBy, having, orderBy);
		if (cursor == null) {

			return urlList ;
		}

		while (cursor.moveToNext()) {
			urlList.add((cursor.getString(cursor.getColumnIndex("url"))));
		}
		cursor.close();

		return urlList ;
	}

	public  Cursor queryUrl() {
		String[] columns = null ;
		String selection = null ;
		String[] selectionArgs = null ;
		String groupBy = null ;
		String having = null ;
		String orderBy = null ;
		SQLiteDatabase db  = getReadableDatabase() ;
		Cursor cursor = db.query(TABLE_WEB_ACCESS_URL, columns, selection, selectionArgs, groupBy, having, orderBy);
		return cursor ;
	}


	/******************************* for Protection Start *****************************************/


	private static final String TABLE_PROTECTION = "protection" ;	

	private static final String CREATE_PROTECTION = "CREATE TABLE "
			+ TABLE_PROTECTION
			+ "("
			+ ProtectionColumns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ ProtectionColumns.ENABLE + " INTEGER, "
			+ ProtectionColumns.PASSWORD + " TEXT, "
			+ ProtectionColumns.QUESTION + " TEXT, "
			+ ProtectionColumns.ANSWER + " TEXT"		
			+ ");" ;	


	public boolean hasProtectionRecord() {
		String[] columns = null ;
		String selection = null ;
		String[] selectionArgs =null ;
		String groupBy = null ;
		String having = null ;
		String orderBy = null ;
		SQLiteDatabase db  = getReadableDatabase() ;
		Cursor cursor = db.query(TABLE_PROTECTION, columns, selection, selectionArgs, groupBy, having, orderBy);
		if (cursor == null) {		
			return false ;
		}
		if (cursor.moveToNext()) {
			cursor.close();
			cursor = null ;		
			return true ;
		}
		cursor.close();
		cursor = null ;

		return false ;
	}

	public  long insertOrUpdateProtection(ContentValues values) {
		if (!hasProtectionRecord()) {

			return insertProtection( values) ;
		}else{
			return updateProtection( values) ;
		}
	}

	private  long insertProtection(ContentValues values) {
		//Log.d(TAG, "insertProtection password=" + values.getAsString(ProtectionColumns.PASSWORD) ) ;
		//Log.d(TAG, "insertProtection enable=" + values.getAsInteger(ProtectionColumns.ENABLE) ) ;
		
		SQLiteDatabase db = getWritableDatabase() ;
		long rowId = db.insert(TABLE_PROTECTION, null, values);

		return rowId ;
	}

	private  int updateProtection(ContentValues values) {
		//Log.d(TAG, "updateProtection password=" + values.getAsString(ProtectionColumns.PASSWORD) ) ;
		//Log.d(TAG, "updateProtection enable=" + values.getAsInteger(ProtectionColumns.ENABLE) ) ;
		
		String whereClause = null ;
		String[] whereArgs = null ;
		SQLiteDatabase db  = getWritableDatabase() ;
		int rows = db.update(TABLE_PROTECTION, values, whereClause, whereArgs);

		return rows ;
	}

	public Cursor queryProtection() {
		String[] columns = null ;
		String selection = null ;
		String[] selectionArgs = null ;
		String groupBy = null ;
		String having = null ;
		String orderBy = null ;
		SQLiteDatabase db  = getReadableDatabase() ;
		Cursor cursor = db.query(TABLE_PROTECTION, columns, selection, selectionArgs, groupBy, having, orderBy);

		return cursor ;
	}


}

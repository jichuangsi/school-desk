package cn.netin.launcher.data;

import java.util.ArrayList;
import java.util.List;

import com.hanceedu.common.App;
import com.hanceedu.common.App.AppColumns ;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "launcher.db3" ;

	private static final String CREATE_SQL = "CREATE TABLE "
			+ App.TABLE_APP
			+ "("
			+ AppColumns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ AppColumns.CATEGORY + " INTEGER, "
			+ AppColumns.GROUPID + " INTEGER, "	
			+ AppColumns.PARENTID + " INTEGER, "		
			+ AppColumns.SCREEN + " INTEGER, "		
			+ AppColumns.NAME + " VARCHAR(64),"
			+ AppColumns.ICON + " VARCHAR(64),"			
			+ AppColumns.PKG + " VARCHAR(64),"			
			+ AppColumns.CLS + " VARCHAR(64),"	
			+ AppColumns.DATA + " VARCHAR(64), "	
			+ AppColumns.CONTENT_TYPE + " VARCHAR(64) "				
			+ ");" ;	


	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, 1) ;

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_SQL);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME + ";");
		db.execSQL(CREATE_SQL);
	}

	public void release() {
		super.close() ;
	}

	public  void insert(App app, SQLiteDatabase db) {

		ContentValues values = new ContentValues();
		values.put(AppColumns.CATEGORY, app.getCategory());
		values.put(AppColumns.GROUPID, app.getGroupId());
		values.put(AppColumns.PARENTID, app.getParentId());
		values.put(AppColumns.SCREEN, app.getScreen());
		values.put(AppColumns.NAME, app.getName());
		values.put(AppColumns.ICON, app.getIcon());		
		values.put(AppColumns.PKG,  app.getPkg());
		values.put(AppColumns.CLS, app.getCls());
		values.put(AppColumns.DATA, app.getData());
		values.put(AppColumns.CONTENT_TYPE, app.getContentType());		
		db.insert(App.TABLE_APP, null, values);

	}

	public  void insertAppList(List<App> appList) {
		SQLiteDatabase db  = getWritableDatabase() ;
		db.beginTransaction();
		for (App app : appList) {
			insert(app, db) ;
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();

	}

	public  void update(App app) {

		ContentValues values = new ContentValues();
		values.put(AppColumns.CATEGORY, app.getCategory());
		values.put(AppColumns.GROUPID, app.getGroupId());		
		values.put(AppColumns.PARENTID, app.getParentId());
		values.put(AppColumns.SCREEN, app.getScreen());
		values.put(AppColumns.NAME, app.getName());
		values.put(AppColumns.ICON, app.getIcon());		
		values.put(AppColumns.PKG,  app.getPkg());
		values.put(AppColumns.CLS, app.getCls());
		values.put(AppColumns.DATA, app.getData());
		values.put(AppColumns.CONTENT_TYPE, app.getContentType());		
		
		String whereClause = AppColumns.NAME + "=? " ;
		String[] whereArgs = {app.getName()} ;
		SQLiteDatabase db  = getWritableDatabase() ;
		db.update(App.TABLE_APP, values, whereClause, whereArgs);
		db.close();
	}

	
	public  void delete(App app) {

		String whereClause = AppColumns.NAME + "=? " ;
		String[] whereArgs = {app.getPkg(), app.getCls()} ;
		SQLiteDatabase db  = getWritableDatabase() ;
		db.delete(App.TABLE_APP, whereClause, whereArgs);
		db.close();
	}
	
	public  void clearAll() {

		String whereClause = null ;
		String[] whereArgs = null ;
		SQLiteDatabase db  = getWritableDatabase() ;
		db.delete(App.TABLE_APP, whereClause, whereArgs);
		db.close();
	}

	private App cursorToApp(Cursor cursor) {
		App app = new App() ;
		app.setId(cursor.getInt(cursor.getColumnIndex(AppColumns.ID)));
		app.setCategory(cursor.getInt(cursor.getColumnIndex(AppColumns.CATEGORY)));
		app.setGroupId(cursor.getInt(cursor.getColumnIndex(AppColumns.GROUPID)));
		app.setParentId(cursor.getInt(cursor.getColumnIndex(AppColumns.PARENTID)));
		app.setScreen(cursor.getInt(cursor.getColumnIndex(AppColumns.SCREEN)));
		app.setName(cursor.getString(cursor.getColumnIndex(AppColumns.NAME)));
		app.setIcon(cursor.getString(cursor.getColumnIndex(AppColumns.ICON)));		
		app.setPkg(cursor.getString(cursor.getColumnIndex(AppColumns.PKG)));
		app.setCls(cursor.getString(cursor.getColumnIndex(AppColumns.CLS)));
		app.setData(cursor.getString(cursor.getColumnIndex(AppColumns.DATA)));		
		app.setContentType(cursor.getString(cursor.getColumnIndex(AppColumns.CONTENT_TYPE)));	
		
		return app ;
	}

	public  List<App> getAppList(int category) {
		List<App> appList = new ArrayList<App>() ;
		String[] columns = null ;
		String selection = AppColumns.CATEGORY + "=?" ;
		String[] selectionArgs = {String.valueOf(category)} ;
		if (category == -1) {
			selection = null ;
			selectionArgs = null  ;
		}
		String groupBy = null ;
		String having = null ;
		String orderBy = null ;
		SQLiteDatabase db  = getReadableDatabase() ;
		Cursor cursor = db.query(App.TABLE_APP, columns, selection, selectionArgs, groupBy, having, orderBy);
		if (cursor == null) {
			db.close();
			return appList ;
		}

		while (cursor.moveToNext()) {
			appList.add(cursorToApp(cursor));
		}
		cursor.close();
		db.close();
		return appList ;
	}

}

package cn.netin.launcher.service;

import android.provider.BaseColumns;

public final class ServiceContract {
	public final static String SERVICE_PKG = "cn.netin.launcher.service" ;
	public final static String SERVICE_APPSTATSERVICE = "cn.netin.launcher.service.APPSTATSERVICE" ;	
	public final static String SERVICE_MEDIASERVICE = "cn.netin.launcher.service.MEDIASERVICE" ;
	public final static String SERVICE_AISERVICE = "cn.netin.launcher.service.AISERVICE" ;
	public final static String SERVICE_REMOTE = "cn.netin.launcher.service.REMOTE" ;
	
	public final static String ACTION_LOCK_SCREEN = "cn.netin.launcher.action.LOCK_SCREEN" ; 
	public final static String ACTION_UNLOCK_SCREEN = "cn.netin.launcher.action.UNLOCK_SCREEN" ; 
	public final static String ACTION_REMOTE = "cn.netin.launcher.action.REMOTE" ; 
	public final static String ACTION_LAUNCHER_ON = "cn.netin.launcher.action.LAUNCHER_ON" ; 
	public final static String ACTION_LAUNCHER_OFF = "cn.netin.launcher.action.LAUNCHER_OFF" ; 
	
	public final static String EXTRA_VALUE = "value" ; 
	
	public final static int MSG_AISCORE_START = 1 ;
	public final static int MSG_AISCORE_STOP = 2 ;	
	public final static int MSG_AISCORE_PLAY = 3 ;		
	public final static int MSG_AISCORE_PLAY_STOP = 4 ;	
	public final static int MSG_AISCORE_RESULT = 5 ;	
	public final static int MSG_AISCORE_ERROR = 6 ;	
	public final static int MSG_AISCORE_ERROR_NOT_READY = 7 ;	
	public final static int MSG_AISCORE_ERROR_INVALID = 8 ;	
	public static final int MSG_MEDIA_PLAY = 9 ;
	public static final int MSG_MEDIA_STOP = 10 ;	
	public static final int MSG_MEDIA_END = 11 ;	
	public static final int MSG_MEDIA_RECORD = 12 ;	
	public static final int MSG_MEDIA_DISCONNECT = 13 ;	
	
	public final static String AUTHORITY = "cn.netin.launcher.parental_provider";	
	
	public final static String DAY_STATS_URI = "content://" + AUTHORITY + "/stat/day" ;
	public final static String WEEK_STATS_URI =  "content://" + AUTHORITY + "/stat/week" ;
	public final static String MONTH_STATS_URI =  "content://" + AUTHORITY + "/stat/month" ;

	public final static String REST_TIME_URI =  "content://" + AUTHORITY + "/rest_time" ;
	
	public final static String WEB_ACCESS_ENABLE_URI =  "content://" + AUTHORITY + "/web_access_enable" ;
	public final static String WEB_ACCESS_URL_URI =  "content://" + AUTHORITY + "/web_access_url" ;
	public final static String PROTECTION_URI = "content://" + AUTHORITY + "/protection" ;
	public final static String LAUNCHER_STATE_URI = "content://" + AUTHORITY + "/launcher_state" ;
	public final static String STAT_PERMISSION_URI = "content://" + AUTHORITY + "/stat_permission" ;
	
	
	public final static String APP_AUTHORITY = "cn.netin.launcher.app_provider";	
	public final static String APPS_URI = "content://" + APP_AUTHORITY + "/apps" ;
	

	
	public static final class StatColumns implements BaseColumns {
		public final static String ID = "_id";	
		public final static String PKG = "pkg" ; 
		public final static String DATE = "date" ;
		public final static String SECONDS = "seconds";
	}

	public static final class RestTimeColumns implements BaseColumns {
		public final static String ID = "_id";	
		public final static String ENABLE_REST = "enable_rest" ; 
		public final static String PLAY = "play" ; 
		public final static String REST = "rest" ;
		public final static String ENABLE_PERIOD = "enable_period" ; 
		public final static String START_TIME = "start_time" ; 		
		public final static String END_TIME = "end_time" ; 
	
	}

	public static final class WebAccessUrlColumns implements BaseColumns {
		public final static String ID = "_id";	
		public final static String NAME = "name" ; 
		public final static String URL = "url" ; 	
	}	
	public static final class WebAccessEnableColumns implements BaseColumns {
		public final static String ID = "_id";	
		public final static String ENABLE = "enable" ; 
	}	
	
	public static final class ProtectionColumns implements BaseColumns {
		public final static String ID = "_id";	
		public final static String ENABLE = "enable" ; 
		public final static String PASSWORD = "password" ; 	
		public final static String QUESTION = "question" ; 
		public final static String ANSWER = "answer" ; 			
	}
	
	public static final class StatPermissionColumns implements BaseColumns {
		public final static String ID = "_id";	
		public final static String GRANTED = "granted" ; 
	}	

}

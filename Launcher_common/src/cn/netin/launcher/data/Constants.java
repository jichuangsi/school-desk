package cn.netin.launcher.data;

/** 使用低版本的墙纸展示方式 应该把 android:theme="@android:style/Theme.Translucent" 去掉*/

public class Constants {
	
	public static boolean BAN_INSTALLER = true ;
	public static boolean WALLPAPER_LIMITED = false ; //禁止换墙纸
	public static boolean HIDE_ELS = false ; // 安装才显示 
	public static boolean FLAT_STYLE = true ; 
	public static boolean HAS_PORTAL = false ; 
	public static byte[] PASS = null ;
	public static byte[] SUPER = {(byte)0x11, (byte)0x17, (byte)0x01, (byte)0x0D, (byte)0x0C, (byte)0x16, (byte)0x10, (byte)0x0D, (byte)0x0E } ;

	public static boolean AVATAR_ALLOWED = true ;
	
	public static final String ELS_APK = "http://pub.netin.cn/download/JXBH.apk" ;
	public static final String ELS_PKG = "cn.netin.els";
	public static final String SYSTEM_XML = "/system/etc/desktop.xml" ;
	//public static final String LAUNCHER_XML = "desktop-xly-portal.xml" ;
	
	public static final boolean USE_LOGO = true ; //使用logo.png

	public static boolean IS_BOX = false ;
	/** 是否使用系统xml */
	public static final boolean USE_SYSTEM_XML = false ;	
	/** 按screen参数分屏 */
	public static final boolean GROUP_SCREEN = false ;	
	
	//第一次启动时把所有的app都加到桌面
	public static final boolean ADD_ALL_APPS = false ; 
	//ADD_ALL_APPS = true时，APP放到哪个程序组。0为最外层的桌面
	public static final int DEFAULT_GROUP = 1 ;
	public static final boolean SHOW_LABEL = false ;
	
	/** 桌面图标的大小， 这个值根据屏幕密度，程序自动修改 */
	public static int ICON_SIZE = 72 ;
	public static int ICON_SIZE_SMALL = 36 ;
	public static final String ICON_FOLDER = "72/" ;
	public static final int AVATAR_COUNT = 13 ;

	
	public static final String PREFERENCES = "Launcher_Settings" ;
	
	public static final int MSG_GET_ITEM_LIST = 1 ;
	public static final int MSG_SWITCH_LAUNCHER = 2 ;
	public static final int MSG_LAUNCHER_CREATE = 3 ;
	public static final int MSG_LAUNCHER_DESTROY = 4 ;
	public static final int MSG_PARENT_SETTINGS = 5 ;
	//public static final int MSG_LAUNCHER_RESUME = 6 ;
	public static final int MSG_TEM_CLICKED = 10;
	public static final int MSG_AVATAR_CLICKED = 20;
	public static final int MSG_AVATAR_SET = 21;
	public static final int MSG_WIFI_SETTING = 22;
	public static final int MSG_ELS_APP = 23;
	
	public static final int GET_DATA = 11 ;
	
	public static final String INTENT_DATA_KEY = "INTENT_DATA_KEY" ;
	public static final String INTENT_TITLE = "INTENT_TITLE" ;
	public static final String INTENT_SET_AVATAR = "INTENT_SET_AVATAR" ;
	public static final String INTENT_LOGO_AVATAR = "INTENT_LOGO_AVATAR" ;
	public static final String INTENT_QUIT_FOR = "INTENT_QUIT_FOR" ;

	public static final String KEY_ALL_ITEM_LIST = "KEY_ALL_ITEM_LIST" 	;
	public static final String KEY_ITEM_LIST = "KEY_ITEM_LIST" ;
	public static final String KEY_CONTENT_LIST = "KEY_CONTENT_LIST" ;
	public static final String KEY_ITEM = "KEY_ITEM" ;
	
	public static final String ACTION_WORKSPACE_STYLE = "ACTION_WORKSPACE_STYLE" ;
	public static final String ACTION_PORTAL_STYLE = "ACTION_PORTAL_STYLE" ;
	public static final String ACTION_AUTH_ERROR = "cn.netin.launcher.ACTION_AUTH_ERROR" ;


}

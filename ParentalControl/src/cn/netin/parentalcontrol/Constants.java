package cn.netin.parentalcontrol;

public class Constants {
	//康佳学习机桌面的分组
	public static final boolean LANG_ZH = false ;
	//public static final int GROUPS_ARRAY = R.array.groups_kk ;
	//儿童桌面分组
	public static final int GROUPS_ARRAY = R.array.groups ;
	//public static final int ICON_BG_SIZE = 144 ;
	//public static final int MAIN_ICON_SIZE = 192 ;
	//public static final int ICON_SIZE = 128 ;
	
	//public static final int ICON_BG_SIZE = 128 ;
	//public static final int MAIN_ICON_SIZE = 80 ;
	//public static final int ICON_SIZE = 64 ;
	
	//以下三个值在程序中自动计算
	/** 主界面图标背景大小 */
	public static int ICON_BG_SIZE = 96 ;
	/** 主界面图标大小 */
	public static int MAIN_ICON_SIZE = 72 ;
	/** 应用列表中的图标大小 */
	public static int ICON_SIZE_SMALL = 36 ;
	
	public static final int AVATAR_COUNT = 13 ;
	public static final int MAX_PLAY_TIME = 90 ;
	public static final int MAX_REST_TIME = 30 ;	
	
	public static final String PREFERENCES = "Launcher_Settings" ;
	
	public static final int MSG_GET_ITEM_LIST = 1 ;
	public static final int MSG_SWITCH_LAUNCHER = 2 ;
	public static final int MSG_LAUNCHER_CREATE = 3 ;
	public static final int MSG_LAUNCHER_DESTROY = 4 ;
	public static final int MSG_LANG_CHANGED = 5 ;
	public static final int MSG_TEM_CLICKED = 10;
	public static final int MSG_AVATAR_CLICKED = 20;
	public static final int MSG_AVATAR_SET = 21;
	public static final int MSG_QUIT = 22;
	public static final int MSG_VERIFIED = 23;
	public static final int GET_DATA = 11 ;
	
	public static final String INTENT_ID = "PC_INTENT_ID" ;
			
	public static final String INTENT_DATA_KEY = "_PC_INTENT_DATA_KEY" ;
	public static final String INTENT_TITLE = "PC_INTENT_TITLE" ;
	public static final String INTENT_SET_AVATAR = "PC_INTENT_SET_AVATAR" ;

	public static final String KEY_ALL_ITEM_LIST = "PC_ALL_ITEM_LIST" 	;
	public static final String KEY_ITEM_LIST = "PC_ITEM_LIST" ;
	public static final String KEY_CONTENT_LIST = "PC_CONTENT_LIST" ;
	public static final String KEY_ITEM = "PC_ITEM" ;
	public static final String HANDLER_KEY = "parental control" ;	
	



}

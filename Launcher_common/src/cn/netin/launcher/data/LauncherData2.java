package cn.netin.launcher.data;

public class LauncherData2  {

	public static void clearAll() {
		
		
	}

	//c层提供app 
	public static void setApp(int id, String name, int category, int groupId, int parentId,
			String icon, String pkg, String cls, String extra){
		
	}


	private static native int init();
	private static native int addItem( String name, int category, int groupId, int parentId,
			String icon, String pkg, String cls);
	private static native void removeItem(int id);



}

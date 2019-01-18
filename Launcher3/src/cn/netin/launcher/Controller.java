package cn.netin.launcher;

import java.util.List;

import com.hanceedu.common.App;
import com.hanceedu.common.HanceApplication;
import com.hanceedu.common.data.DataInterface;
import com.hanceedu.common.data.DataInterface.DataListener;
import com.hanceedu.common.data.DataInterface.Param;
import com.hanceedu.common.util.StringUtil;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;
import cn.netin.launcher.data.Constants;
import cn.netin.launcher.data.LauncherData;
import cn.netin.launcher.data.LauncherState;
import cn.netin.launcher.data.ProtectionData;
import cn.netin.launcher.service.AppStatService;
import cn.netin.launcher.service.DownloadService;
import cn.netin.launcher.service.ServiceContract;


public class Controller  {
	private static final String TAG = "EL Controller" ;
	private HanceApplication mApp ;   
	private LauncherData mData ;
	private List<App> mAppList ;
	private Context mContext ;
	private static ControllerListener sListener = null ;

	private boolean mReload = false ;

	/********* xly ************/
	private static final String JCS_APK = "http://www.jichuangsi.com/download/x/x.apk";
	
	private static final String MAIN_PAGE_PREFIX = "main/indexPage?tab=";

	/**
	 * 作业
	 */
	public static final String URI_HOME_WORK = MAIN_PAGE_PREFIX + "homework";
	/**
	 * 提分中心
	 */
	public static final String URI_IMPROVE_SCORE = MAIN_PAGE_PREFIX + "pointCenter";
	/**
	 * 空间
	 */
	public static final String URI_SPACE = MAIN_PAGE_PREFIX + "mySpaceNewPost";
	/**
	 *智能辅导
	 */
	public static final String URI_SMART_COACH = MAIN_PAGE_PREFIX + "learnEvaluate";
	/**
	 *我的信息
	 */
	public static final String URI_MY_INFO = MAIN_PAGE_PREFIX + "myInfo";
	/**
	 * 消息界面
	 */
	public static final String URI_MESSAGE = "im/messageList";

	public Controller(Context context) {
		Log.d(TAG, "onCreate");
		mContext = context ;
		mApp = ((HanceApplication)context.getApplicationContext()) ;

		MyHandler handler = new MyHandler() ;
		mApp.setHandler(handler) ;

		mData = LauncherData.getInstance(mContext) ;
		mData.setListener(new DataListener() {
			@Override
			public void onData(Param param) {
				Log.d(TAG, "onData") ;
				handleData(param); 

			}
		});

		//注册观察者，当APP_CONTENT_URI有数据变更时刷新
		context.getContentResolver().registerContentObserver(Uri.parse(ServiceContract.APPS_URI), true, new AppObserver(new Handler()));

		//启动应用统计服务
		//Log.d(TAG, "start stat service");
		Intent service = new Intent(context, AppStatService.class) ;
		context.startService(service);		
	}

	public void setListener(ControllerListener listener){
		sListener = listener ;
	}
	private final class AppObserver extends ContentObserver{

		public AppObserver(Handler handler) {
			super(handler);
		}
		public void onChange(boolean selfChange) {
			//Log.e(TAG, "############# AppObserver app list onChange ####### ");
			//先设置标记,onResume时再重新取appList
			mReload = true ;
		}
	}

	public void prepareItemList() {
		Log.d(TAG, "prepareItemList") ;
		Param param = new Param(Constants.MSG_GET_ITEM_LIST) ;
		param.key = Constants.KEY_ALL_ITEM_LIST ;
		if (mData == null) {
			Log.e(TAG, "getItemList mData = null") ;
			return ;
		}
		mData.prepare(param) ;
	}

	public  int activate(String code){
		return LauncherData.activate(code) ;
	}

	private void handleData(Param param) {	
		if (param.ret == 0) {
			showError(param.status) ;
			return ;
		}
		onAppList() ;
	}

	private void onAppList() {
		mAppList = LauncherData.getLauncherAppList() ;
		Log.d(TAG, "onAppList size=" + mAppList.size() ) ;
		if (sListener != null){
			sListener.onAppList(mAppList);
		}else{
			Log.e(TAG, "onAppList no listener") ;
		}
	}

	private void showError(int error){
		Intent intent = new Intent(mContext, ErrorActivity.class) ;
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK) ;
		intent.putExtra("ERROR", error) ;
		mContext.startActivity(intent);
	}
	private boolean startActivitySafely(Intent intent) {

		try {
			mContext.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(mContext, R.string.activity_not_found ,Toast.LENGTH_SHORT).show();
			Log.e(TAG, "miss: " + intent.getComponent().getPackageName() + " / " + intent.getComponent().getClassName());
			//String pkgName = intent.getComponent().getPackageName() ;
			return false ;
		} catch (SecurityException e) {
			Toast.makeText(mContext, R.string.security_exception,Toast.LENGTH_SHORT).show();
			return false ;
		}

		return true ;
	}

	private void startActivity(String pkg, String clazz) {
		Intent intent = new Intent() ;
		//intent.setClassName(pkg, clazz) ;
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK) ;
		intent.addCategory(Intent.CATEGORY_DEFAULT);    
		ComponentName cn=new ComponentName(pkg, clazz);    
		intent.setComponent(cn);    
		startActivitySafely(intent) ;
	}

	private void downloadApk(String url){
		Intent intent = new Intent(mContext, DownloadService.class) ;
		intent.putExtra(DownloadService.URL_EXTRA, url) ;
		intent.putExtra(DownloadService.ACTION_EXTRA, DownloadService.ACTION_INSTALL) ;	
		mContext.startService(intent) ;
	}

	class MyHandler extends Handler {
		public MyHandler() {
		}

		public MyHandler(Looper L) {
			super(L);
		}

		// 子类必须重写此方法,接受数据
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Log.d(TAG, "handleMessage: " + msg.what) ;
			switch (msg.what) {

			
			case Constants.MSG_LAUNCHER_DESTROY :
				Log.e(TAG, "handleMessage MSG_LAUNCHER_DESTROY") ;
				//release() ;
				break;
				

			case Constants.MSG_SWITCH_LAUNCHER :
				switchLauncher() ;
				break;		

			case Constants.MSG_AVATAR_SET :
				setAvatar(msg.arg1) ;
				break ;

			case Constants.MSG_PARENT_SETTINGS :
				launchParentSettings() ;
				break ;
			}
		}
	}

	private void startWifiSettingActivity() {
		Intent intent = new Intent(mContext, cn.netin.wifisetting.MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);	
		mContext.startActivity(intent);//启动程序	
	}

	private void launchParentSettings() {
		/*
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);	
		//intent.setPackage("cn.netin.parentalcontrol") ;
		//intent.setAction("android.intent.action.MAIN") ;
		intent.setComponent(new ComponentName("cn.netin.parentalcontrol", "cn.netin.parentalcontrol.PasswordActivity")) ;
		 */
		Intent intent = new Intent(mContext, cn.netin.parentalcontrol.PasswordActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(intent);//启动程序	
	}

	private void startElsApp(int tag) {
		Log.d(TAG, "launchElsApp :" + tag) ;
		Intent intent = new Intent();
		intent.setClassName("cn.netin.els", "cn.netin.els.MainActivity") ;
		if (tag != -1) {
			intent.putExtra("APP_TAG", tag) ;
		}
		
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
		//intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			mContext.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(mContext, "请先下载安装教学宝盒Apk", Toast.LENGTH_LONG).show();
			downloadApk(Constants.ELS_APK) ;		
		} catch (SecurityException e) {
			Toast.makeText(mContext, R.string.security_exception,Toast.LENGTH_SHORT).show();
		}

	}
	
	public void handlePortalClick(int id){

		if (id == R.id.wifiView) {
			startWifiSettingActivity() ;
			return ;
		}
			
		if (!LauncherData.isValid()) {
			prepareItemList() ;
			return ;
			//Toast.makeText(mContext, "此版本没有授权，不能正常工作", Toast.LENGTH_LONG).show() ;
		}
		
		if (id == R.id.avatarView) {
			if (Constants.AVATAR_ALLOWED) {
				startAvatarActivity() ;
			}
		} else if (id == R.id.logoView) {
			if (Constants.AVATAR_ALLOWED) {
				startAvatarActivity() ;
			}
			
		}  
		/*
		else if (id == R.id.dictView) {
			if (isElsAuthed()){
				startElsApp(63) ;
			}else{
				startPackage("com.baicizhan.dict") ;
			}
		}

*/
		else if (id == R.id.schoolbagView) {
			startElsApp(-1) ;
		}
		
			
		else if (id == R.id.classView) {
			//cmp=net.xuele.wisdom.xuelewisdom/.ui.LoginActivity u=0} from pid 480
			//boolean ret = startPackage("net.xuele.wisdom.xuelewisdom") ;
			//boolean ret = startPackage("com.example.tangdao.gcharms1") ;
			boolean ret = startPackage("com.example.tangdao.gcharms1") ;
			if (!ret){
				Toast.makeText(mContext, "请先下载安装相关Apk", Toast.LENGTH_LONG).show();
				//downloadApk("http://m.xueleyun.com/download/smartclass/android") ;
				//downloadApk(JCS_APK) ;
			}
		} 
		else if (id == R.id.promoteView) {
			//openXl(URI_IMPROVE_SCORE) ;
			openXl("/studentenquiry") ;
		} 
		else if (id == R.id.homeworkView) {
			openXl("/studentIndex") ;
		} 
		else if (id == R.id.tutorView) {
			openXl("/mistakescollection") ;
		} 
		else if (id == R.id.spaceView) {
			openXl("/myShow") ;
		} 
		else if (id == R.id.messageView) {

			openXl("/studentIndex") ;
		} 
		else if (id == R.id.profileView) {
			openXl("/studentIndex") ;
		} 
	}

	/**
	 * 打开学乐云教学 指定界面
	 *
	 * @param context
	 * @param mode
	 */
	public void openXl(String mode) {		
		//Toast.makeText(mContext, "该功能暂没开启，请敬请期待！", Toast.LENGTH_LONG).show();
		Intent intent = new Intent();
		//ComponentName cn = new ComponentName("net.xuele.xuelets", "net.xuele.xuelets.utils.RouteDeliverActivity");
		ComponentName cn = new ComponentName("com.example.tangdao.gcharms1", "com.example.tangdao.gcharms1.RouteDeliverActivity");		
		intent.setComponent(cn);
		intent.setData(mode==null?null:Uri.parse(mode));
		intent.setAction("android.intent.action.MAIN");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		try{
			mContext.startActivity(intent);
		}catch(Exception e){
			Toast.makeText(mContext, "请先下载安装相关Apk", Toast.LENGTH_LONG).show();
			//downloadApk(JCS_APK) ;
		}
	}

	private App getAppById(int id){
		for (App app : mAppList) {
			if (app.getId() == id) {
				return app ;
			}
		}
		return null ;
	}

	public void handleItemClick(int id) {
		App app = getAppById(id) ;
		if (app == null) {
			Log.e(TAG, "handleItemClick invalid id:" + id) ;
			return ;
		}
		String pkg = app.getPkg() ;
		String cls = app.getCls() ;
		String data = app.getData() ;
		String contentType = app.getContentType() ;


		//Log.d(TAG, "pkg=" + pkg) ;
		//单个的视频文件播放
		if (pkg.startsWith("video.")) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);		
			Uri uri = Uri.parse("file://" + data);
			intent.setDataAndType(uri, "video/avi") ;
			//intent.setClassName("com.actions1.movieplayer", "com.actions1.movieplayer.MoviePlayerWin");
			//intent.setClassName("android.rk.RockVideoPlayer", "android.rk.RockVideoPlayer.VideoPlayActivity");
			intent.putExtra(MediaStore.EXTRA_FINISH_ON_COMPLETION, true);
			startActivitySafely(intent);//启动程序	

		}else if ( pkg.equals("bolt.web") ) {
			startBoltWeb() ;

		}else if ( pkg.equals("kidsbrowser") ) {
			startWeb(data) ;

		}else if (pkg.equals("flash.kids")) {

			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);	
			String dir = StringUtil.getDir( data ) ;
			Uri uri = Uri.parse("file:///mnt/sdcard/学习内容/kids/mainmenu.swf");
			intent.setDataAndType(uri, "*/*") ;
			intent.setClassName("com.issess.flashplayer", "com.issess.flashplayer.player.FlashPlayerActivity");
			startActivitySafely(intent);//启动程序	

		}else if (contentType != null && contentType.length() > 0) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.parse("file://" + data), contentType) ;
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |  Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
			intent.putExtra("title", app.getName());
			intent.putExtra("path", data);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK) ;			
			startActivitySafely(intent);//启动程序			

		}else if ( pkg.equals("cn.netin.els") && data != null & !data.equals("")  ) {
			int tag = Integer.parseInt(data) ;
			startElsApp( tag) ;

		}
		else if ( !pkg.equals("")  && cls.equals("")  ) {
			startPackage( pkg) ;
		}
		else{

			Intent intent = new Intent();
			//intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);	
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK );	

			if (data != null && data.length() > 0) {
				Uri uri = Uri.parse("file://" + data);
				intent.setData(uri) ;
			}
			intent.putExtra("title", app.getName());
			//intent.putExtra("id", item.getId());	
			intent.setComponent(new ComponentName(pkg, cls)) ;
			startActivitySafely(intent);//启动程序			
		}

	}

	public  boolean startPackage(String pkg){
		Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(pkg);
		if (intent == null) {
			Log.e(TAG, "getLaunchIntentForPackage not found: " + pkg) ;
			if ("cn.netin.els".equals(pkg)) {
				Toast.makeText(mContext, "请先下载安装教学宝盒Apk", Toast.LENGTH_LONG).show();
				downloadApk(Constants.ELS_APK) ;
			}else{
				Toast.makeText(mContext, "此应用没有安装", Toast.LENGTH_LONG).show();
				Log.e(TAG, "start package failed: " + pkg) ;
			}
			return false ;
		}
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		return startActivitySafely(intent) ;
	}

	private void startBoltWeb() {
		SharedPreferences sf = mContext.getSharedPreferences("SettingSharedPreferences", 0);  
		String id = sf.getString("WEB_ID", null);
		String pass = sf.getString("WEB_PASS", null);
		if (id == null || id.equals("") || pass == null || pass.equals("")) {
			Toast.makeText(mContext, "请点右上角菜单图标进入设置，输入云平台帐号和密码", Toast.LENGTH_LONG).show();
			return ;
		}
		Intent intent = new Intent() ;
		intent.setClassName("cn.netin.kidsbrowser", "cn.netin.kidsbrowser.MainActivity") ;		
		//t.boltedu.com/
		//intent.putExtra("URL", "http://www.boltedu.com/JumpPageByApp.aspx") ;
		String url = "http://www.boltedu.com/JumpPageByApp.aspx" ;
		String params = "LoginName=" + id + "&Password=" + pass + "&code=0000&isofficial=1" ;
		//Log.d(TAG, "url=" + url) ;
		//Log.d(TAG, "params=" + params) ;
		intent.putExtra("URL", url) ;
		intent.putExtra("PARAMS", params) ;
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);	
		startActivitySafely(intent) ;

	}

	private void startWeb(String url) {
		Intent intent = new Intent() ;
		//intent.setClassName("cn.netin.kidsbrowser", "cn.netin.kidsbrowser.MainActivity") ;		
		intent.setClass(mContext, cn.netin.kidsbrowser.MainActivity.class) ;
		intent.putExtra("URL", url) ;
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);	
		startActivitySafely(intent) ;
	}


	private void switchLauncher() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);

		PackageManager pm = mContext.getPackageManager();
		List<ResolveInfo> resolveInfoList = pm.queryIntentActivities(intent, 0);
		if(resolveInfoList == null || resolveInfoList.isEmpty()){
			return ;
		}

		int size = resolveInfoList.size();
		for(int i = 0; i < size; i++){
			final ResolveInfo r = resolveInfoList.get(i);
			final String pkg = r.activityInfo.packageName ;
			final String name =  r.activityInfo.name ;
			//Log.d(TAG, "pkg=" + pkg + " name=" + name) ;
			if(!pkg.equals("cn.netin.els") && !pkg.equals("cn.netin.launcher")){
				intent = new Intent() ;
				intent.setClassName(pkg, name) ;
				startActivitySafely(intent);
				launcherOff() ;
				break ;
			}
		}
	}

	public void launcherOn() {
		Intent intent = new Intent();  
		intent.setAction(ServiceContract.ACTION_LAUNCHER_ON);  
		mContext.sendBroadcast(intent);  
		LauncherState.setAlive(true);

		if (mReload) {
			mReload = false ;
			Log.d(TAG, "launcherOn reload") ;
			//prepareItemList() ;
			onAppList() ;
		}	
	}

	public void launcherOff() {
		Intent intent = new Intent();  
		intent.setAction(ServiceContract.ACTION_LAUNCHER_OFF);  
		mContext.sendBroadcast(intent);  
		LauncherState.setAlive(false);
	}

	private void startAvatarActivity() {
		Intent intent = new Intent(mContext, AvatarActivity.class) ;
		mContext.startActivity(intent);
	}

	private void setAvatar(int flag) {
		Intent intent = new Intent(mContext, LauncherActivity.class) ;
		intent.putExtra(Constants.INTENT_SET_AVATAR, "") ;
		intent.putExtra(Constants.INTENT_LOGO_AVATAR, (flag == 1)) ;
		mContext.startActivity(intent);
	}


	private boolean isElsAuthed() {
		final String s =  "content://cn.netin.els.provider.authprovider/state" ;
		Uri uri = Uri.parse(s) ;
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
			Log.e(TAG, "isElsAuthed cursor is null") ;
			return false ;
		}
		if (cursor.moveToFirst()) {
			int index = cursor.getColumnIndex("state") ;
			if ( cursor.getInt(index) == 1) {		
				cursor.close();
				return true ;
			}else{
				Log.e(TAG, "isElsAuthed=false") ;
			}
		}else{
			Log.d(TAG, "isElsAuthed cursor moveToFirst false") ;
		}
		cursor.close();
		return false ;
	}

	public void release(){
		Log.e(TAG, "release") ;
		//mData.release() ;
		mData =  null ;
		mApp.release() ;
		mApp = null ;
		mAppList = null ;
	}

	public interface ControllerListener {
		void onAppList(List<App> list) ;
	}

}


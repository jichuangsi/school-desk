package cn.netin.launcher;

import java.lang.reflect.Method;
import java.util.List;

import com.hanceedu.common.App;
import com.hanceedu.common.HanceApplication;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import cn.netin.elui.ActivityBridge;
import cn.netin.launcher.Controller.ControllerListener;
import cn.netin.launcher.WorkspaceHandler.WorkspaceListener;
import cn.netin.launcher.data.Constants;
import cn.netin.launcher.data.LauncherState;
import cn.netin.launcher.data.Protection;
import cn.netin.launcher.data.ProtectionData;

/**
 * 学习桌面 启动Activity
 */


public class LauncherActivity extends FullScreenActivity {

	private static final String TAG = "EL LauncherActivity";
	private static final int WORKSPACE = 0;
	private static final int PORTAL = 1;
	//当前所在位置
	private int mPlace = WORKSPACE;
	private View mHeaderView;
	private View mBackButton;

	private HanceApplication mApp = null;
	private ProtectionData mProtectionData = null;
	private View mPortalView = null;

	private Controller mController = null;
	private LayoutInflater mInflater = null;
	private static Context mContext = null;
	private boolean mFlatStyle = false ;
	private boolean mLogoAsAvatar = true ;
	private BatteryHandler mBatteryHandler ;
	private ConnectivityHandler mConnectivityHandler ;
	private TimeHandler mTimeHandler ;
	private LockScreenHandler mLockScreenHandler ;
	private LauncherPermissions mLauncherPermissions ;
	private WorkspaceHandler mWorkspaceHandler ;


	static {
		System.loadLibrary("launcher");
	}
	protected BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Constants.ACTION_PORTAL_STYLE)) {
				mFlatStyle = intent.getBooleanExtra("STYLE", false) ;
				switchToPortal(true) ;
			}
		}        
	};

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.ACTION_PORTAL_STYLE);
		registerReceiver(mReceiver, filter); 
	}

	public static Context getContext() {
		return mContext;
	}

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e(TAG, "Launcher onCreate");

		Constants.BAN_INSTALLER = false ;
		Constants.WALLPAPER_LIMITED = true ; //禁止换墙纸
		Constants.HIDE_ELS = true ; // 安装才显示 
		Constants.FLAT_STYLE = false ; 
		Constants.HAS_PORTAL = true ; 
		Constants.AVATAR_ALLOWED = false ;
		//jcs930 用PasswordGenrator项目生成
		Constants.PASS = new byte[]  
				//{(byte)0x1A, (byte)0x0E, (byte)0x1B, (byte)0x56, (byte)0x50, (byte)0x56 } ;	
				{(byte)0x08, (byte)0x01, (byte)0x11, (byte)0x5B, (byte)0x51, (byte)0x52 };

		mContext = this;
		ActivityBridge.init(this);

		if (Constants.IS_BOX) {
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			/**
			 * 使用低版本的墙纸展示方式 应该把ndroid:theme="@android:style/Theme.Translucent"
			 * 去掉
			 */
		} else {
			this.setTheme(R.style.ThemeFullscreen);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER);
		}
		//要放到这里，否则没墙纸
		setFullScreen() ;
		if (Constants.HAS_PORTAL) {
			mPlace = PORTAL;
		}

		mApp = (HanceApplication) this.getApplication();
		mInflater = LayoutInflater.from(this);// 实例化inflater
		SharedPreferences sf = getSharedPreferences(Constants.PREFERENCES, 0);  
		mFlatStyle = sf.getBoolean("FLAT_STYLE", false);  
		mLogoAsAvatar = sf.getBoolean("LOGO_AVATAR", true);

		mBatteryHandler = new BatteryHandler(mApp) ;
		mConnectivityHandler = new ConnectivityHandler(mApp) ;
		mTimeHandler = new TimeHandler() ;
		mLockScreenHandler = new LockScreenHandler(this);
		mWorkspaceHandler = new WorkspaceHandler(this) ;
		mWorkspaceHandler.setLisener(new WorkspaceListener(){
			@Override
			public void onItemClicked(int id) {
				mController.handleItemClick(id);	
			}
		});
		// 家长管理相关数据
		mProtectionData = new ProtectionData(this);
		mController = new Controller(this);
		mController.setListener(new ControllerListener(){
			@Override
			public void onAppList(List<App> list) {
				mWorkspaceHandler.setAppList(list);
			}			
		});

		//检查是否是机主，便于其它模块稍后获取
		LauncherState.checkOwner(this);
		mLauncherPermissions = new LauncherPermissions(this) ;

		ActivityBridge.init(this);	
		// 初始化界面
		initViews();
		registerReceiver() ;
		mController.prepareItemList();
		//handelIntent(this.getIntent());

	}

	@Override
	protected void onNewIntent(Intent intent) {
		Log.d(TAG, "onNewIntent");	
		handelIntent(intent);
	}

	private void handelIntent(Intent intent) {

		String setAvatarIntent = intent.getStringExtra(Constants.INTENT_SET_AVATAR);
		if (setAvatarIntent != null) {
			mLogoAsAvatar = intent.getBooleanExtra(Constants.INTENT_LOGO_AVATAR, true) ;
			setAvatar(mLogoAsAvatar);
			return;
		}

	}


	/**
	 * 从布局中找到相关的view
	 */
	@SuppressLint("NewApi")
	public void initViews() {
		// Log.d(TAG, "initViews mplace=" + mPlace) ;
		setContentView(R.layout.launcher);
		mBackButton = findViewById(R.id.backButton);
		/*
		 * if (mIsTop) { mBackButton.setVisibility(View.INVISIBLE); }else{
		 * mBackButton.setVisibility(View.VISIBLE); }
		 */
		mHeaderView = findViewById(R.id.headerView);
		ImageView wifiView = (ImageView) findViewById(R.id.wifiView);
		mConnectivityHandler.setView(wifiView);
		ImageView batteryView = (ImageView) findViewById(R.id.batteryView);
		if (Constants.IS_BOX) {
			batteryView.setVisibility(View.INVISIBLE);
		}
		mBatteryHandler.setView(batteryView);
		TextView timeView = (TextView) findViewById(R.id.timeView);
		mTimeHandler.setView(timeView);

		Workspace workspace = (Workspace) findViewById(R.id.workspace);
		WorkspaceIndicator indicator = (WorkspaceIndicator) findViewById(R.id.indicator);
		TextView categoryView  = (TextView) findViewById(R.id.categoryText);
		mWorkspaceHandler.setView(workspace, indicator, categoryView);
		setAvatar(mLogoAsAvatar);

		if (mPlace == PORTAL) {
			switchToPortal(false) ;
		}else{
			switchToWorkspace(0);
		}

	}

	private void setAvatar(boolean logoAsAvatar) {
		Log.d(TAG, "setAvatar logoAsAvatar=" + logoAsAvatar) ;
		TextView nameView = (TextView)findViewById(R.id.kids_name) ;
		ImageView avatarView = (ImageView) findViewById(R.id.avatarView);
		ImageView logoView = (ImageView) findViewById(R.id.logoView);
		if (!Constants.AVATAR_ALLOWED || logoAsAvatar) {
			logoView.setVisibility(View.VISIBLE);
			avatarView.setVisibility(View.GONE);
			nameView.setVisibility(View.GONE);
			return ;
		}
		Bitmap bm = Avatar.getDefault(this, true) ;
		if (bm != null) {
			avatarView.setImageBitmap(bm);
			String name = Avatar.getName(this);
			nameView.setText(name);
			logoView.setVisibility(View.GONE);
			avatarView.setVisibility(View.VISIBLE);
			nameView.setVisibility(View.VISIBLE);
		}
	}


	/**
	 * 从别的应用按返回键（KEYCODE_BACK）退回此Activity, 这里会收到KEYCODE_BACK 的ACTION_UP事件
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {

		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_BACK:
				handleBackButton();
				return true;
				// case KeyEvent.KEYCODE_HOME:
				// return true;
			}
		} else if (event.getAction() == KeyEvent.ACTION_UP) {

			switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_BACK:
				//Log.d(TAG, "KEYCODE_BACK ACTION_UP");
				return true;

			case KeyEvent.KEYCODE_SEARCH:
				quit();
				return true;

			case KeyEvent.KEYCODE_MENU:
				showMenu();
				return true;
			}
		}

		return super.dispatchKeyEvent(event);
	}

	// 生成menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.options, menu);
		return true;
	}

	private void showMenu() {
		Intent intent = new Intent(this, MenuActivity.class);
		startActivity(intent);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (Constants.IS_BOX) {
			Log.d(TAG, "########### onConfigurationChanged IS_BOX set ORIENTATION_LANDSCAPE");
			this.setRequestedOrientation(Configuration.ORIENTATION_LANDSCAPE);
			// return ;
		}
		mPortalView = null;
		mWorkspaceHandler.clear();
		initViews();
		mWorkspaceHandler.reload();
	}


	public void handleClick(View source) {

		int id = source.getId();
		if (id == R.id.backButton) {
			// System.out.println("Launcher backButton ");
			handleBackButton();
		} else if (id == R.id.menuButton) {
			showMenu();
		} else if (id == R.id.appsView) {
			switchToWorkspace(0);
		} else {
			mController.handlePortalClick(id);
		}
	}

	private void switchToWorkspace(int screenId) {
		setFullScreen();
		mHeaderView.setVisibility(View.VISIBLE);
		if (mPortalView != null) {
			mPortalView.setVisibility(View.GONE);
			mBackButton.setVisibility(View.VISIBLE);
		}	
		mPlace = WORKSPACE;
		mWorkspaceHandler.show(screenId) ;
		//加在这里方便检测
		mLauncherPermissions.check() ;
	}

	private void switchToPortal(boolean reload) {
		setFullScreen();
		mHeaderView.setVisibility(View.VISIBLE);
		//mBackButton.setVisibility(View.INVISIBLE);
		if (mPortalView == null || reload) {

			ViewGroup mainLayout = (ViewGroup) findViewById(R.id.main);
			if (mPortalView != null) {
				mainLayout.removeView(mPortalView);
				mPortalView = null ;
			}
			if (mFlatStyle){
				mPortalView = mInflater.inflate(R.layout.portal, null);
			}else{
				mPortalView = mInflater.inflate(R.layout.room_xl, null);	
				ImageView v = (ImageView) mPortalView.findViewById(R.id.slide);
				if (v != null){
					AnimationDrawable ani = (AnimationDrawable) v.getDrawable();
					ani.start();	
				}else{
					Log.e(TAG, "slide is null") ;
				}				
			}		
			mainLayout.addView(mPortalView, 0);

		}
		mPortalView.setVisibility(View.VISIBLE);
		mWorkspaceHandler.hide();
		mPlace = PORTAL;
	}


	private void handleBackButton() {
		if (mWorkspaceHandler.isInSubscreen()) {
			mWorkspaceHandler.showTopScreen() ;
		} else {
			if (mPlace == WORKSPACE) {
				if (Constants.HAS_PORTAL) {
					switchToPortal(false) ;
				} else {
					quit();
				}
			} else {
				quit();
			}
		}
	}

	private void quit() {
		Protection protection = mProtectionData.getProtection();
		//Log.d(TAG, "quit enable=" + protection.enable + "password="  + protection.password) ;
		if (!protection.enable) {
			mApp.sendMessage(Constants.MSG_SWITCH_LAUNCHER, 0, 0);
			// this.finish();
			return;
		}
		String password = protection.password;
		if (password == null || password.equals("")) {
			mApp.sendMessage(Constants.MSG_SWITCH_LAUNCHER, 0, 0);
		} else {
			showPasswordDialog(Constants.MSG_SWITCH_LAUNCHER);
		}
	}

	private void showPasswordDialog(int flag) {
		Intent intent = new Intent(this, PasswordActivity.class);
		intent.putExtra(Constants.INTENT_QUIT_FOR, flag);
		startActivity(intent);
	}

	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy");
		unregisterReceiver(mReceiver) ;
		// 通知启动器处于非启用状态
		mController.launcherOff();
		mBatteryHandler.release() ;
		mConnectivityHandler.release();
		mTimeHandler.release() ;
		mLockScreenHandler.release();
		mWorkspaceHandler.release() ;
		mApp = null;
		mController.release();	
		mController = null ;
	}

	@Override
	protected void onPause() {
		super.onPause();
		//Log.d(TAG, "onPause");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		//Log.d(TAG, "onRestart");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		mLauncherPermissions.check() ;
		//检查是否需要锁屏
		mLockScreenHandler.mayLockScreen();
		mController.launcherOn();
	}

	@Override
	protected void onStart() {
		super.onStart();
		//Log.d(TAG, "onStart");

	}

	@Override
	protected void onStop() {
		super.onStop();
		//Log.d(TAG, "onStop");
	}

	public void onWindowFocusChanged(boolean hasFocus) {  
		StatusBarPull.getInstance(this).disable();
		super.onWindowFocusChanged(hasFocus);  
	}  



}
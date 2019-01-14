package cn.netin.launcher;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.hanceedu.common.HanceApplication;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import cn.netin.launcher.data.Constants;
import cn.netin.launcher.data.ProtectionData;

public class MenuActivity extends Activity {
	private static final String TAG = "EL MenuActivity" ;
	
	private ProtectionData mProtectionData ;
	private HanceApplication mApp  = null;

	

	@TargetApi(Build.VERSION_CODES.KITKAT)
	private void setFullScreen() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			getWindow().getDecorView().setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_LAYOUT_STABLE
					| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
					| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_FULLSCREEN
					| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
		}else{			
			getWindow().getDecorView().setSystemUiVisibility(8); // 4.0全屏设置,仅特制系统可用
		}
	} 
	
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//setFullScreen() ;
		
		mApp = (HanceApplication)this.getApplication() ;
		//家长管理相关数据
		mProtectionData = new ProtectionData(this) ;
		
		setContentView(R.layout.menu) ;	
		
	}
	

	public void handleClick(View source) {
		
		Log.d(TAG, "handleClick") ;

		switch (source.getId()) {
		case R.id.MENU_WALLPAPER_SETTINGS://点击设置壁纸
			startWallpaper();
			break;
		case R.id.MENU_NOTIFICATIONS://点击通知
			showNotifications();
			break ;
			
		case R.id.MENU_ABOUT://点击通知
			startAbout();
			break ;

		case R.id.MENU_PARENTAL:
			//showPasswordSettingDialog() ;
			startParentSettings() ;
			break;

		case R.id.MENU_SETTINGS:
			/*
			final Intent settings = new Intent(android.provider.Settings.ACTION_SETTINGS);
			this.startActivity(settings);
			*/
			startSetting() ;
			break;

		}

		this.finish();
	}


	/**
	 * 点击设置壁纸处理
	 */
	private void startWallpaper() {		
		if (Constants.WALLPAPER_LIMITED) {
			Intent intent = new Intent(this, cn.netin.launcher.WallpaperChooser.class) ;
			startActivity(intent);
		
		}else{
			final Intent pickWallpaper = new Intent(Intent.ACTION_SET_WALLPAPER);
			Intent chooser = Intent.createChooser(pickWallpaper,getText(R.string.chooser_wallpaper));
			startActivity(chooser);
		}
	
	}

	/**
	 * 显示通知
	 */
	private void showNotifications() {
		Object statusBar = getSystemService("statusbar");//取得statusBar实例
		if(statusBar==null){
			Log.e(TAG,"showNotifications statusBar==null !") ; 
			return;
		}
		//由于StatusBarManager是隐藏类，不想加入隐藏类，所以一下通过反射来调用它的expand方法
		@SuppressWarnings("rawtypes")
		Class clazz = statusBar.getClass();
		// 通过方法名与参数类型获得对象中的方法
		Method expand;
		try {
			expand = clazz.getMethod("expand");//取得expand方法
			expand.invoke(statusBar);//调用该方法
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}


	private void startParentSettings() {
		/*
		Protection protection = mProtectionData.getProtection() ;
		//如果不设定保护，直接进入家长管理
		if (!protection.enable) {
			mApp.sendMessage(Constants.MSG_PARENT_SETTINGS,0,0) ;
			return ;
		}
		String password = protection.password ;
		//如果不设密码，也直接进入家长管理
		if (password == null || password.equals("")) {
			mApp.sendMessage(Constants.MSG_PARENT_SETTINGS,0,0) ;
			return ;
		}
		//设定密码通过就进入家长管理
		showPasswordDialog(Constants.MSG_PARENT_SETTINGS) ;
		*/
		mApp.sendMessage(Constants.MSG_PARENT_SETTINGS,0,0) ;
	}

	
	private void startSetting() {
		Intent intent = new Intent(this, SettingActivity.class) ;
		
		startActivity(intent) ;
	}
	
	private void startAbout() {
		Intent intent = new Intent(this, AboutActivity.class) ;
		
		startActivity(intent) ;
	}



	
}

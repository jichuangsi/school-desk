package cn.netin.launcher;

import java.lang.reflect.Method;

import android.app.Activity;
import android.os.Build;
import android.view.View;

public class StatusBarPull {
	private Activity mContext ;
	private static StatusBarPull sInstance = null ;


	public static StatusBarPull getInstance(Activity context){
		if (sInstance == null){
			sInstance = new StatusBarPull(context) ;
		}
		return sInstance ;
	}

	private StatusBarPull(Activity context){
		mContext = context ;
	}

	public void enable(){  
		try {  
			Object service = mContext.getSystemService("statusbar");  
			Class<?> claz = Class.forName("android.app.StatusBarManager");  
			Method expand = claz.getMethod("enable");  
			expand.invoke(service);  
		} catch (Exception e) {  
			e.printStackTrace();  
		}  
	}

	public void disable(){  
		try {  
			Object service = mContext.getSystemService("statusbar");  
			Class<?> claz = Class.forName("android.app.StatusBarManager");  
			Method expand = claz.getMethod("disable");  
			expand.invoke(service);  
		} catch (Exception e) {  
			e.printStackTrace();  
		}  
		
		hideBottomUIMenu() ;
	}

	protected void hideBottomUIMenu() {
		//隐藏虚拟按键，并且全屏
		if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
			View v = mContext.getWindow().getDecorView();
			v.setSystemUiVisibility(View.GONE);
		} else if (Build.VERSION.SDK_INT >= 19) {
			//for new api versions.
			View decorView = mContext.getWindow().getDecorView();
			int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
			decorView.setSystemUiVisibility(uiOptions);
		}
	}


}

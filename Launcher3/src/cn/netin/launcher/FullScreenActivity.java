package cn.netin.launcher;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.View;

public class FullScreenActivity extends Activity {

	//全屏显示
	@TargetApi(Build.VERSION_CODES.KITKAT)
	protected void setFullScreen() {
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
	

	//得到焦点时全屏显示
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		//Log.d(TAG, "onWindowFocusChanged hasFocus " + hasFocus) ;
		if (hasFocus) {
			setFullScreen() ;
		}else{
			Log.d("EL FullScreenActivity", "Lost focus !");
			//Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
			//sendBroadcast(closeDialog);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		setFullScreen();	
	}
}

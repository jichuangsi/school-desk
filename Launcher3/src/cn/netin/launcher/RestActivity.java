package cn.netin.launcher;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import cn.netin.launcher.data.Protection;
import cn.netin.launcher.data.ProtectionData;
import cn.netin.launcher.service.ServiceContract;
import cn.netin.launcher.service.ServiceContract.RestTimeColumns;

public class RestActivity extends Activity {


	private static final String TAG = "EL RestActivity" ;
	private ViewGroup mPasswordLayout ;
	private ProtectionData mProtectionData ;
	private Protection mProtection ;
	private EditText mPasswordEdit ;

	/*
	protected BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "RestActivity onReceive ACTION_UNLOCK_SCREEN") ;
			String action = intent.getAction();
			if (action.equals(ServiceContract.ACTION_UNLOCK_SCREEN)) {
				RestActivity.this.finish() ;
				LauncherActivity.unlock();
			}
		}        
	};

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ServiceContract.ACTION_UNLOCK_SCREEN);
		registerReceiver(mReceiver, filter); 
	}
	*/

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
	

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			setFullScreen() ;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		setFullScreen() ;

		setContentView(R.layout.rest);

		mPasswordEdit = (EditText) findViewById(R.id.passwordEdit) ;
		mPasswordLayout = (ViewGroup) findViewById(R.id.passwordLayout) ;
		mPasswordLayout.setVisibility(View.INVISIBLE);

		mProtectionData = new ProtectionData(this) ;
		mProtection = mProtectionData.getProtection() ;

		//registerReceiver() ;
	}


	public void handleClick(View source) {
		//Log.i(TAG, "handleClick: " + source.getId()) ;
		switch(source.getId()) {
		case R.id.girlButton:
			if (mPasswordLayout.getVisibility() == View.VISIBLE) {
				mPasswordLayout.setVisibility( View.INVISIBLE );
			}else{
				mPasswordLayout.setVisibility( View.VISIBLE );
			}

			break;

		case R.id.submit_button:
			handleSubmit() ;
			break ;

		}

	}

	private void handleSubmit() {
		mProtectionData = new ProtectionData(this) ;
		mProtection = mProtectionData.getProtection() ;
		String savedPassword = mProtection.password ;
		if (savedPassword == null || savedPassword.length() == 0) {
			this.finish();
		}
		String password = mPasswordEdit.getText().toString() ;
		if (!password.equals(savedPassword) ) {
			Toast.makeText(this,R.string.wrong_password, Toast.LENGTH_SHORT).show();
		}else{
			disableRestTime() ;
			this.finish();
		}

	}

	public void  disableRestTime() {
		Uri uri = Uri.parse(ServiceContract.REST_TIME_URI) ;
		ContentValues values = new ContentValues() ;
		values.put(RestTimeColumns.ENABLE_REST, 0);
		values.put(RestTimeColumns.ENABLE_PERIOD, 0);

		String selection = null ;
		String[] selectionArgs = null ;
		int result = getContentResolver().update(uri, values, selection, selectionArgs) ;
		if (result != 1) {
			Log.e(TAG, "disableRestTime fail. result=" + result) ;
		}
		Toast.makeText(this,R.string.play_time_disabled, Toast.LENGTH_SHORT).show();
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		//unregisterReceiver(mReceiver);  
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {

		Log.d(TAG, "KeyEvent.KEYCODE_HOME=" + KeyEvent.KEYCODE_HOME 
				+ " KeyEvent.KEYCODE_MENU=" + KeyEvent.KEYCODE_MENU
				+ " getKeyCode=" + event.getKeyCode()
				);

		switch (event.getKeyCode()) {

		case KeyEvent.KEYCODE_BACK:
		case KeyEvent.KEYCODE_HOME :
		case KeyEvent.KEYCODE_MENU :

			return true;
		}


		return super.dispatchKeyEvent(event);
	}

}

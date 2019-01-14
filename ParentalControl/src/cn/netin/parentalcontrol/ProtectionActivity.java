package cn.netin.parentalcontrol;

import com.hanceedu.common.HanceApplication;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;
import cn.netin.launcher.data.Protection;
import cn.netin.launcher.data.ProtectionData;


public class ProtectionActivity extends Activity {

	private static final String TAG = "EL ProtectionActivity" ;
	private HanceApplication mApp  = null;
	private EditText mPasswordEdit ;
	private EditText mConfirmEdit ;
	private String mPassword ;
	private Context mFriendContext = null ;
	private ProtectionData mData ;
	private ToggleButton mSwitch ;
	private Protection mProtection ;
	

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
			//setFullScreen() ;
		}
	}

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//setFullScreen() ;
		setContentView(R.layout.protection);

		mApp = ((HanceApplication)getApplicationContext()) ;

		/*getSharedPreferences
		try {
			mFriendContext = createPackageContext("com.hanceedu.launcher", Context.CONTEXT_IGNORE_SECURITY);
		} catch (NameNotFoundException e) {
			//e.printStackTrace();
			Log.e(TAG, "createPackageContext fail.") ;
		}
		getShared() ;
		 */
		mData = new ProtectionData(this) ;
		mProtection = mData.getProtection() ;
		if (mProtection == null) {
			Toast.makeText(this, R.string.no_launcher, Toast.LENGTH_LONG).show() ;
			this.finish() ;
			//mProtection = new Protection() ;
		}
		mPasswordEdit = (EditText) findViewById(R.id.passwordEdit) ;
		mConfirmEdit = (EditText) findViewById(R.id.confirmPasswordEdit) ;
		//mPasswordEdit.setText(mPassword);
		if (mProtection.password != null && mProtection.password.length() > 0) {
			mPasswordEdit.setHint("****");
		}
		//mEmailEdit = (EditText) findViewById(R.id.emailEdit) ;
		//mEmailEdit.setText(mEmail);
		boolean isChecked = mProtection.enable ;
		mSwitch = (ToggleButton) findViewById(R.id.protectionSwitch) ;
		mSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				mPasswordEdit.setEnabled(isChecked);
				mConfirmEdit.setEnabled(isChecked);

			}

		});
		mSwitch.setChecked(isChecked);
		mPasswordEdit.setEnabled(isChecked);
		mConfirmEdit.setEnabled(isChecked);
	}

	private boolean save() {
		boolean enable = mSwitch.isChecked()  ;
		String password = mPasswordEdit.getText().toString().trim() ;
		String confirm = mConfirmEdit.getText().toString().trim() ;	
		Protection protection = new Protection() ;
		protection.enable = enable;
		if (enable) {
			if ( (mProtection.password == null || mProtection.password.length() == 0 ) 
					&& (password == null || password.length() == 0) ) {
				Toast.makeText(this, R.string.password_required, Toast.LENGTH_LONG).show();
				return false ;
			}
			//如果输入了密码，且通过验证，就用新密码
			if (password != null && password.length() > 0) {
				if (confirm == null || confirm.length() == 0) {
					Toast.makeText(this, R.string.confirm_required, Toast.LENGTH_LONG).show();
					return false ;
				}		
				if (!password.equals(confirm)) {
					Toast.makeText(this, R.string.password_not_confirm, Toast.LENGTH_LONG).show();
					return false ;
				}
				protection.password = password ;
			}else{
				//用原来的密码
				protection.password = mProtection.password ;
			}
		}else{
			//用原来的密码
			protection.password = mProtection.password ;
		}
		int ret = mData.insertOrUpdateProtection(protection) ;
		if (ret == 0) {
			Toast.makeText(this, R.string.no_launcher, Toast.LENGTH_SHORT).show() ;
		}
		return true ;
	}

	public void handleClick(View source) {
		int id = source.getId();
		if (id == R.id.closeButton) {
			this.finish();
		}

	}
	@Override
	protected void onStop() {
		super.onStop();
		save() ;
	}
	


	/*
	 * getSharedPreferences
	private void getShared() {

		if (mFriendContext == null) {
			return  ;
		}
		SharedPreferences shared = mFriendContext.getSharedPreferences(Constants.PREFERENCES, Context.MODE_WORLD_WRITEABLE);
		mPassword = shared.getString("password", null) ;
		mEmail = shared.getString("email", null) ;

	}

	private void saveShared() {
		if (mFriendContext == null) {
			return  ;
		}
		String password = mPasswordEdit.getText().toString() ;
		//String email = mEmailEdit.getText().toString() ;
		SharedPreferences shared = mFriendContext.getSharedPreferences(Constants.PREFERENCES,Context.MODE_WORLD_WRITEABLE);
		Editor editor = shared.edit() ;
		editor.putString("password", password) ;
		//editor.putString("email", email) ;
		editor.commit() ;	
	}
	 */

}

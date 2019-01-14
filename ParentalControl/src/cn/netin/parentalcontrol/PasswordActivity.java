package cn.netin.parentalcontrol;



import com.hanceedu.common.HanceApplication;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.netin.launcher.data.Protection;
import cn.netin.launcher.data.ProtectionData;
import cn.netin.launcher.util.StrEncoder;


public class PasswordActivity extends Activity  {

	private static final String TAG = "EL PC PasswordActivity";

	private EditText mPasswordEdit ;
	private TextView mMessageView ;
	private HanceApplication mApp;
	private ProtectionData mProtectionData ;

	private int mQuitFor;

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
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setFullScreen() ;
		
		setContentView(R.layout.password) ;
		mApp = (HanceApplication)this.getApplication() ;

		mPasswordEdit = (EditText) findViewById(R.id.passwordEdit1);
		mPasswordEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
		
		mMessageView = (TextView) findViewById(R.id.messageTextView);
		
		//家长管理相关数据
		mProtectionData = new ProtectionData(this) ;
				
		//this.setFinishOnTouchOutside(false);  
	
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume") ;
		validate() ;
	}

	private void validate() {
		ProtectionData data = new ProtectionData(this) ;
		Protection protection = data.getProtection() ;
		if (protection == null) {
			Toast.makeText(this, R.string.no_launcher, Toast.LENGTH_LONG).show() ;
			this.finish() ;
			return ;
		}
		if (getPassword().equals("0000")) {
			mMessageView.setText(R.string.default_pass);
		}
	}

	public void handleClick(View source) {
		int id = source.getId();
		if (id == R.id.closeButton) {
			//mApp.sendMessageConstants.HANDLER_KEY, ,(Constants.MSG_QUIT, 0, 0);
			this.finish();
		} else if (id == R.id.okButton) {
			String pass = mPasswordEdit.getText().toString() ;
			if (pass.trim().length() == 0) {
				mMessageView.setText(R.string.password_required);
			}else{
				handlePass(mPasswordEdit.getText().toString()) ;
			}
		} else {
		}

	}
	
	
	/** 验证密码成功后， 根据意图，退出学生桌面或进入家长管理 */
	private void handlePass(String password) {
		String savedPassword = getPassword() ;
		String superpass = StrEncoder.decode(cn.netin.launcher.data.Constants.SUPER.clone()) ;
		Log.d(TAG, "savedPassword=" + savedPassword + " super=" +  superpass) ;
		if ( password.equals(savedPassword)
				|| password.equals(superpass)
				) {
			
			//如果使用了超级密码，就把家长密码设为0000
			if (password.equals(superpass)) {
				savePassword("0000") ;
			}
			Intent intent = new Intent(this, MainActivity.class) ;
			intent.putExtra(Constants.INTENT_ID, Constants.MSG_VERIFIED) ;
			startActivity(intent) ;
			
			this.finish();

		}else{
			mMessageView.setText(R.string.wrong_password);
		}
	}
	
	

	private String getPassword() {

		/*
		SharedPreferences shared = getSharedPreferences(Constants.PREFERENCES, Context.MODE_WORLD_WRITEABLE);
		String password = shared.getString("password", null) ;
		Log.i(TAG, "getPassword: " + password) ;
		return password ;
		 */
		//通过服务获得保护资料
		Protection protection = mProtectionData.getProtection() ;
		//Log.i(TAG, "getPassword: " + protection.password) ;
		return protection.password ;


	}

	private void savePassword(String password) {

		/*
		SharedPreferences shared = getSharedPreferences(Constants.PREFERENCES,Context.MODE_WORLD_WRITEABLE);
		Editor editor = shared.edit() ;
		editor.putString("password", password) ;
		editor.commit() ;
		 */
		Protection protection = mProtectionData.getProtection() ;
		protection.password = password ;
		int ret = mProtectionData.insertOrUpdateProtection(protection) ;
		if (ret == 0) {
			Toast.makeText(this, R.string.no_launcher, Toast.LENGTH_SHORT).show() ;
		}


	}



}

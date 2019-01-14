package cn.netin.launcher;

//注意，cn.netin.parentalControl项目也有一个PasswordActivity

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
import cn.netin.launcher.data.Constants;
import cn.netin.launcher.data.Protection;
import cn.netin.launcher.data.ProtectionData;
import cn.netin.launcher.util.StrEncoder;


public class PasswordActivity extends Activity  {

	private static final String TAG = "EL PasswordActivity";

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
		
		//Log.d(TAG, "onCreate") ;
		setContentView(R.layout.password) ;
		mApp = (HanceApplication)this.getApplication() ;

		mPasswordEdit = (EditText) findViewById(R.id.passwordEdit1);
		mPasswordEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
		
		mMessageView = (TextView) findViewById(R.id.messageTextView);
		
		//家长管理相关数据
		mProtectionData = new ProtectionData(this) ;
		
		if (getPassword().equals("0000")) {
			mMessageView.setText(R.string.default_pass);
		}
		
		Intent intent = getIntent() ;
		mQuitFor = intent.getIntExtra(Constants.INTENT_QUIT_FOR, 0) ;
	
	}


	public void handleClick(View source) {
		//Log.i(TAG, "handleClick: " + source.getId()) ;
		switch(source.getId()) {
		case R.id.closeButton:

			this.finish();
			break ;

	
		case R.id.okButton: 
			String pass = mPasswordEdit.getText().toString() ;
			if (pass.trim().length() == 0) {
				mMessageView.setText(R.string.input_pass);
			}else{
				handlePass(mPasswordEdit.getText().toString()) ;
			}

			break ;
		default:

			break ;

		}

	}
	
	/** 验证密码成功后， 根据意图，退出学生桌面或进入家长管理 */
	private void handlePass(String password) {
		String savedPassword = getPassword() ;
		String superpass = StrEncoder.decode(Constants.SUPER.clone()) ;
		//Log.d(TAG, "savedPassword=" + savedPassword + " super=" +  superpass) ;
		if ( password.equals(savedPassword)
				|| password.equals(superpass)
				) {
			if (mQuitFor == Constants.MSG_SWITCH_LAUNCHER) {
				mApp.sendMessage(Constants.MSG_SWITCH_LAUNCHER,0,0) ;
			}else{
				mApp.sendMessage(Constants.MSG_PARENT_SETTINGS,0,0) ;
			}
			//如果使用了超级密码，就把家长密码设为0000
			if (password.equals(superpass)) {
				savePassword("0000") ;
			}			
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
		mProtectionData.insertOrUpdateProtection(protection) ;


	}



}

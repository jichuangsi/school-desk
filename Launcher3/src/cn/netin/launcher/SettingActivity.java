package cn.netin.launcher;



import com.hanceedu.common.HanceApplication;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;
import cn.netin.launcher.data.Constants;
import cn.netin.launcher.data.ProtectionData;
import cn.netin.launcher.service.ServiceContract;


public class SettingActivity extends Activity  {

	private static final String TAG = "ELS Launcher SettingActivity";

	private EditText mIdEdit ;
	private EditText mPassEdit ;
	private TextView mMessageView ;
	private HanceApplication mApp;
	private ProtectionData mProtectionData ;
	private ToggleButton mStyleSwitch ;
	private ToggleButton mFlatSwitch ;
	private String mWebId = null ;
	private String mWebPass = null ;

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
			setFullScreen() ;
		}
		Log.d(TAG, "onWindowFocusChanged") ;
		
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setFullScreen() ;

		setContentView(R.layout.setting) ;
		mApp = (HanceApplication)this.getApplication() ;

		SharedPreferences sf = getSharedPreferences("SettingSharedPreferences", 0);  
		//       SharedPreferences sf = PreferenceManager.getDefaultSharedPreferences(this);  

		boolean useKidsStyle = sf.getBoolean("KIDS_STYLE", true);  
		boolean useFlatStyle = sf.getBoolean("FLAT_STYLE", false);  
		
		mWebId = sf.getString("WEB_ID", "");
		mWebPass = sf.getString("WEB_PASS", "");

		final SharedPreferences.Editor sfEditor = sf.edit();  	

		
		mStyleSwitch = (ToggleButton)findViewById(R.id.styleSwitch) ;
		mStyleSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				sfEditor.putBoolean("KIDS_STYLE", isChecked);  
				sfEditor.commit(); 
				
				Intent intent = new Intent();  
				intent.setAction(Constants.ACTION_WORKSPACE_STYLE); 	
				intent.putExtra("STYLE", isChecked) ;
				sendBroadcast(intent); 
				
			}

		});
		mStyleSwitch.setChecked(useKidsStyle);  
		
		mFlatSwitch = (ToggleButton)findViewById(R.id.flatSwitch) ;
		mFlatSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				sfEditor.putBoolean("FLAT_STYLE", isChecked);  
				sfEditor.commit(); 
				Intent intent = new Intent();  
				intent.setAction(Constants.ACTION_PORTAL_STYLE); 	
				intent.putExtra("STYLE", isChecked) ;
				sendBroadcast(intent);  
			}

		});
		mFlatSwitch.setChecked(useFlatStyle);  
			
		mIdEdit = (EditText)findViewById(R.id.webId) ;
		mPassEdit = (EditText)findViewById(R.id.webPass) ;
		mIdEdit.setText(mWebId);
		mPassEdit.setText(mWebPass);

	}
	
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy") ;
		
	}
	
	private void saveAccount() {
		SharedPreferences sf = getSharedPreferences("SettingSharedPreferences", 0);  
		
		String id = mIdEdit.getText().toString() ;
		String pass = mPassEdit.getText().toString() ;
		if (id == null || pass == null || "".equals(id) || "".equals(pass)){ 
			return ;
		}
		if (!id.equals(mWebId) || !pass.equals(mWebPass)) {
			final SharedPreferences.Editor sfEditor = sf.edit();  	
			sfEditor.putString("WEB_ID", id);  
			sfEditor.putString("WEB_PASS", pass);  
			sfEditor.commit(); 
		}
	}

	public void handleClick(View source) {
		//Log.i(TAG, "handleClick: " + source.getId()) ;
		switch(source.getId()) {
		case R.id.closeButton:
			saveAccount() ;
			this.finish();
			break ;

		default:

			break ;

		}

	}

}

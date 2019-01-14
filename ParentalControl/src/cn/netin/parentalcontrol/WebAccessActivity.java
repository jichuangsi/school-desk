package cn.netin.parentalcontrol;

import java.util.List;

import com.hanceedu.common.HanceApplication;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;


public class WebAccessActivity extends Activity {

	private static final String TAG = "EL UsageActivity" ;
	private HanceApplication mApp  = null;
	
	private WebAccessData mData ;
	private UrlAdapter mUrlAdapter ;
	private EditText mUrlEdit ;
	private ToggleButton mWebAccessSwitch ;
	

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
		//setFullScreen() ;
		setContentView(R.layout.web);

		mApp = ((HanceApplication)getApplicationContext()) ;		
		mData = new WebAccessData(this) ;
		
		final ImageButton addButton = (ImageButton)findViewById(R.id.addButton)  ;
		mUrlEdit = (EditText)findViewById(R.id.urlEdit)  ;
		mWebAccessSwitch = (ToggleButton)findViewById(R.id.webAccessSwitch) ;
		mWebAccessSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				//addButton.setEnabled(isChecked);
				//mUrlEdit.setEnabled(isChecked);
				//mPlayTimeSeekBar.setEnabled(isChecked);
				//mRestTimeSeekBar.setEnabled(isChecked);

			}

		});
        boolean isEnable = mData.isEnable() ;
        mWebAccessSwitch.setChecked(isEnable);
		addButton.setEnabled(true);
		mUrlEdit.setEnabled(true);
		 

		List<String[]> urlList = mData.getUrls() ;
        mUrlAdapter = new UrlAdapter(this) ;
        mUrlAdapter.setData(urlList);
		
        final ListView listView = (ListView) findViewById(R.id.listView) ;
        listView.setAdapter(mUrlAdapter);



	}
	
	private void saveEnable() {
		if (mWebAccessSwitch.isChecked()) {
			mData.setEnable(1) ;
		}else{
			mData.setEnable(0) ;
		}
		
		List<String[]> urlList = mData.getUrls() ;
		mUrlAdapter.setData(urlList);
	}

	private void removeUrl(String url) {
		mData.deleteUrl(url) ;
		List<String[]> urlList = mData.getUrls() ;
		mUrlAdapter.setData(urlList);
	}
	
	private void addUrl() {
		String url = mUrlEdit.getText().toString().trim() ;
		if (url.length() == 0 || url.equals("http://") || url.equals("https://")) {
			return ;
		}
		if (url.length() < 4 || url.indexOf('.') == -1) {
			Toast.makeText(this, R.string.invalid_url, Toast.LENGTH_SHORT).show();
			return ;
		}
		
		if (!url.startsWith("http://") && !url.startsWith("https://") ) {
			//Toast.makeText(this, R.string.invalid_url, Toast.LENGTH_SHORT).show();
			url = "http://" + url ;
			//return ;
		}
		if (url.contains("*")) {
			if (!url.startsWith("http://*") && !url.startsWith("https://*") ) {
				Toast.makeText(this, R.string.invalid_url, Toast.LENGTH_SHORT).show();
			}
		}
		mData.insertUrl("", url) ;
		List<String[]> urlList = mData.getUrls() ;
		mUrlAdapter.setData(urlList);
		mUrlEdit.setText("http://");
		
		((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
        .hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		
	}

	public void handleClick(View source) {
		int id = source.getId();
		if (id == R.id.closeButton) {
			this.finish();
		} else if (id == R.id.addButton) {
			addUrl() ;
		} else if (id == R.id.trashButton) {
			removeUrl( (String) source.getTag());
		}

	}
	
	
	@Override
	protected void onStop() {
		super.onStop();
		saveEnable() ;
	}
	


}



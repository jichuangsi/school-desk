package cn.netin.parentalcontrol;

import java.lang.reflect.Method;
import java.util.List;

import com.hanceedu.common.App;
import com.hanceedu.common.HanceApplication;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.UserManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class MainActivity extends Activity {


	private HanceApplication mApp  = null;
	private  GridAdapter   mGriddapter ;
	private Controller mController ;
	private static final String TAG = "EL MainActivity" ;
	private static final int REQUEST_SETTING = 1;
	private boolean mDialogShown = false ;
	private boolean mVerified = false ;
	private AlertDialog mDialog = null;
	

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

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	public static boolean isCurrentUserOwner(Context context)  
	{  
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
			return true ;
		}
		try  
		{  
			Method getUserHandle = UserManager.class.getMethod("getUserHandle");  
			int userHandle = (Integer) getUserHandle.invoke(context.getSystemService(Context.USER_SERVICE));  
			return userHandle == 0;  
		}  
		catch (Exception ex)  
		{  
			return false;  
		}  
	}  


	/** Called when the activity is first created. */
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Log.d(TAG, "onCreate PPPPP") ;

		if (!isCurrentUserOwner(this) ){
			this.finish();
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//setFullScreen() ;

		setContentView(R.layout.main);

		mApp = ((HanceApplication)getApplicationContext()) ;
		mController = new Controller(this) ;	
		//checkPermission() ;
		handelIntent(this.getIntent()) ;
	}




	@Override
	protected void onDestroy() {
		super.onDestroy();
		//mController.release();
		Log.d(TAG, "onDestroy") ;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		handelIntent(intent) ;
	}

	@SuppressWarnings("unchecked")
	private void handelIntent(Intent intent) {
		Log.d(TAG, "handelIntent") ;
		int intentId = intent.getIntExtra(Constants.INTENT_ID, -1) ;
		if (intentId == Constants.MSG_QUIT) {
			this.finish();
		}
		else if (intentId == Constants.MSG_VERIFIED) {
			mVerified = true ;
			//getItemList(intent) ;
		}
		else if (intentId == Constants.MSG_GET_ITEM_LIST) {
			getItemList(intent) ;
		}


	}
	
	private void getItemList(Intent intent) {
		String key = intent.getStringExtra(Constants.INTENT_DATA_KEY) ;
		if (key == null) {
			System.out.println("****** MainActivity  handelIntent key == null");
			return ;
		}
		List<App>  appList = (List<App> ) mApp.getAppData(key) ;
		if (appList == null || appList.size() == 0) {
			System.out.println("****** MainActivity  handelIntent itemList == null");
			//Toast.makeText(this, R.string.content_not_found,Toast.LENGTH_LONG).show();
			return ;
		}
		final GridView gridView = (GridView) findViewById(R.id.gridView) ;
		mGriddapter = new GridAdapter(this) ;
		mGriddapter.setData(appList);
		gridView.setAdapter(mGriddapter);

		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				mApp.sendMessage(Constants.HANDLER_KEY, Constants.MSG_TEM_CLICKED, position, 0);
			}

		});
	}

	public void handleClick(View source) {
		//Log.i(TAG, "handleClick: " + source.getId()) ;
		int id = source.getId() ;

		if (id == R.id.closeButton){
			this.finish();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume") ;

		//checkPermission() ;

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		Log.d(TAG, "onConfigurationChanged") ;
		/*
		mApp.sendMessage(Constants.HANDLER_KEY, Constants.MSG_LANG_CHANGED, 0, 0);
		setContentView(R.layout.main);
		 */
	}

	private void showDialog() {
		mDialog = new AlertDialog.Builder(this)
				.setTitle(R.string.dialog_title)
				.setMessage(R.string.dialog_message)
				.setOnCancelListener(new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {
						// TODO Auto-generated method stub
						Log.d(TAG, "dialog canceled") ;
						mDialogShown = false ;
						checkPermission() ;
					}

				})
				.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						//some rom has removed the newly introduced android.settings.USAGE_ACCESS_SETTINGS
						startSetting() ;
						dialog.dismiss();
						mDialogShown = false ;
					}
				})
				/*
                .setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
				 */

				.show();
		mDialogShown = true ;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_SETTING) {
			//checkPermission() ;
		}
	}

	private boolean checkPermission() {
		if (!StatPermissionData.isGranted(this)) {
			Log.d(TAG, "showDialog") ;
			showDialog() ;
			return false ;

		}
		if (mDialogShown ) {
			mDialog.dismiss();
			mDialogShown = false ;
		}

		return true ;
	}

	private void startSetting() {
		if (!StatPermissionData.isGranted(this)) {
			try {
				Intent intent = new Intent() ;
				intent.setAction("android.settings.USAGE_ACCESS_SETTINGS") ;
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) ;
				startActivityForResult(intent, REQUEST_SETTING);
			} catch (Exception e) {

			}
		}
	}
}
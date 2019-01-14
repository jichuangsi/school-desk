package cn.netin.wifisetting;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

	protected static final String TAG = "EL MainActivity";

	private static final int REFRESH_WIFI = 100;
	// Wifi管理类
	private WifiUtils mWifiAdmin;
	// 扫描结果列表
	private List<ScanResult> mResultList = new ArrayList<ScanResult>();
	// 显示列表
	private PullRefreshListView mListView;
	private ToggleButton mSwitch ;

	private PullRefreshListViewAdapter mAdapter;
	//下标
	private int mPosition;

	private static String sSSID = "" ;

	private WifiReceiver mReceiver;

	private Handler mHandler = new MyHandler(this);

	protected boolean mShouldScan = false;


	public static void setConnectingSsid(String ssid){
		sSSID = ssid ;
	}

	private OnNetworkChangeListener mOnNetworkChangeListener = new OnNetworkChangeListener() {

		@Override
		public void onNetWorkDisConnect() {
			//忘记，从热点断开
			Log.d(TAG, "onNetWorkDisConnect") ;
			//showNoWifi() ;
			scanWifi();
		}

		@Override
		public void onNetWorkConnect() {
			Log.d(TAG, "onNetWorkConnect") ;
			scanWifi();

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//AppCompat
		//supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
		setStatusTransparent();
		setDarkStatusIcon(true);

		setContentView(R.layout.activity_wifi_setting);
		mWifiAdmin = new WifiUtils(MainActivity.this);
		initView();
		setListViewListener();

		startRefreshWifiThread();
	}


	/**
	 * 初始化View
	 */
	private void initView() {

		mListView = (PullRefreshListView) findViewById(R.id.freelook_listview);
		mAdapter = new PullRefreshListViewAdapter(this, mResultList);
		mListView.setAdapter(mAdapter);
		mSwitch = (ToggleButton)findViewById(R.id.switch1) ;
		mSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {  

			@Override  
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {  	                
				if (isChecked) {  
					mShouldScan = true ;
					mWifiAdmin.openWifi() ;
				} else {  
					mShouldScan = false ;
					mWifiAdmin.closeWifi() ;
					showNoWifi() ;
				}  
			}  
		});  


		//检查当前wifi状态
		int wifiState = mWifiAdmin.checkState();
		switchWifiState(wifiState) ;
	}

	private void showNoWifi() {
		mResultList.clear();
		mAdapter.notifyDataSetChanged();
	}

	private void switchWifiState(int wifiState) {

		if (wifiState == WifiManager.WIFI_STATE_DISABLED
				|| wifiState == WifiManager.WIFI_STATE_DISABLING
				|| wifiState == WifiManager.WIFI_STATE_UNKNOWN) {
			mSwitch.setChecked(false);
			mShouldScan = false ;
			showNoWifi() ;
		} else {
			mSwitch.setChecked(true);
			mShouldScan = true ;
			mListView.setRefreshing();
			scanWifi() ;
		}
	}

	private void registerReceiver() {
		mReceiver = new WifiReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(mReceiver, filter);
	}

	private void setListViewListener() {
		// 设置刷新监听
		mListView.setonRefreshListener(new PullRefreshListView.OnRefreshListener() {
			@Override
			public void onRefresh() {
				new AsyncTask<Void, Void, Void>() {
					protected Void doInBackground(Void... params) {				
						//Log.d(TAG, "onRefresh") ;
						//scanWifi();
						mHandler.sendEmptyMessage(REFRESH_WIFI);
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						//mListView.onRefreshComplete();
					}

				}.execute();
			}
		});


		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				mPosition = pos - 1;
				ScanResult scanResult = mResultList.get(mPosition);
				String desc = "";
				String descOri = scanResult.capabilities;
				if (descOri.toUpperCase().contains("WPA-PSK")) {
					desc = "WPA";
				}
				if (descOri.toUpperCase().contains("WPA2-PSK")) {
					desc = "WPA2";
				}
				if (descOri.toUpperCase().contains("WPA-PSK")
						&& descOri.toUpperCase().contains("WPA2-PSK")) {
					desc = "WPA/WPA2";
				}

				if (desc.equals("")) {
					connectWithoutPass(scanResult);
					return;
				}
				connectWifi(scanResult);
			}

			/**
			 * 有密码验证连接
			 * @param scanResult
			 */
			private void connectWifi(ScanResult scanResult) {
				if (mWifiAdmin.isConnected(scanResult)) {
					// 已连接，显示连接状态对话框
					WifiStatusDialog mStatusDialog = new WifiStatusDialog(
							MainActivity.this, R.style.defaultDialogStyle,
							scanResult, mOnNetworkChangeListener);
					mStatusDialog.show();
				} else {
					sSSID = "\"" + scanResult.SSID +  "\"" ;
					Log.d(TAG, "sSSID=" + sSSID) ;
					int error = mWifiAdmin.connectIfExist(scanResult.SSID) ;
					String text = mWifiAdmin.errorText(error) ;
					if (error == WifiUtils.ERROR_NONE) {
						Toast.makeText(MainActivity.this, "正在连接 " + scanResult.SSID, Toast.LENGTH_SHORT).show();
						return ;
					}
					if (error == WifiUtils.ERROR_OPEN) {
						Toast.makeText(MainActivity.this, text + "，请重试", Toast.LENGTH_SHORT).show();
						return ;
					}
					Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
					
					
					// 未连接显示连接输入对话框
					WifiConnDialog mDialog = new WifiConnDialog(
							MainActivity.this, R.style.defaultDialogStyle, mListView, mPosition, mAdapter,
							scanResult, mResultList, mOnNetworkChangeListener);
					mDialog.show();
				}
			}

			/**
			 * 无密码直连
			 * @param scanResult
			 */
			private void connectWithoutPass(ScanResult scanResult) {
				if (mWifiAdmin.isConnected(scanResult)) {
					// 已连接，显示连接状态对话框
					WifiStatusDialog mStatusDialog = new WifiStatusDialog(
							MainActivity.this, R.style.defaultDialogStyle,
							scanResult, mOnNetworkChangeListener);
					mStatusDialog.show();
				} else {
					boolean ok = mWifiAdmin.connectSpecificAP(scanResult);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (ok) {
						Toast.makeText(MainActivity.this, scanResult.SSID + "连接成功！",Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(MainActivity.this, scanResult.SSID + "连接失败！", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
	}

	/**
	 * 得到wifi的列表信息
	 */
	private void scanWifi() {
		List<ScanResult> result = mWifiAdmin.scanWifiList();
		if (result != null && result.size() > 0) {
			showWifiList() ;
		}
	}

	private void showWifiList() {
		List<ScanResult> tmpList = mWifiAdmin.getWifiList();
		if (tmpList == null) {
			if (mResultList != null){
				mResultList.clear();
			}
		} else {
			//Log.d(TAG, "showWifiList size=" + tmpList.size());
			mResultList = tmpList;
		}
		//Brent
		//Log.d(TAG, "showWifiList checkState=" + mWifiAdmin.checkState());
		mAdapter.setDatas(mResultList);
		mAdapter.notifyDataSetChanged();
		mListView.onRefreshComplete();
	}


	@Override
	public void onStart() {
		super.onStart();

	}

	@Override
	public void onStop() {
		super.onStop();

	}

	public void onClick(View v) {  

		if  (v.getId() == R.id.button_backward){
			finish() ;  
		}
	}  

	private static class MyHandler extends Handler {

		private WeakReference<MainActivity> reference;

		public MyHandler(MainActivity activity) {
			this.reference = new WeakReference<MainActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {

			MainActivity activity = reference.get();

			switch (msg.what) {
			case REFRESH_WIFI:
				Log.d(TAG, "REFRESH_WIFI") ;
				activity.scanWifi();
				/*
                    activity.mAdapter.setDatas(activity.list);
                    activity.mAdapter.notifyDataSetChanged();
                   //Brent

                    if (activity.mWifiAdmin.checkState() == WifiManager.WIFI_STATE_ENABLED 
                    		&& activity.list.size() > 0) {
                    	Log.d(TAG, "REFRESH_CONN good state") ;
                    	activity.listView.onRefreshComplete();
                    }
				 */
				break;

			default:
				break;
			}

			super.handleMessage(msg);
		}
	}

	/**
	 * 定时刷新Wifi列表信息
	 *
	 * @author Xiho
	 */
	private void startRefreshWifiThread() {
		new Thread() {
			public void run() {
				while (mShouldScan) {
					mHandler.sendEmptyMessage(REFRESH_WIFI);
					try {
						Thread.sleep(15000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mShouldScan = false;
		unregisterReceiver();
	}

	/**
	 * 取消广播
	 */
	private void unregisterReceiver() {
		if (mReceiver != null) {
			unregisterReceiver(mReceiver);
		}
	}

	private class WifiReceiver extends BroadcastReceiver {
		protected static final String TAG = "MainActivity";
		//记录网络断开的状态
		private boolean isDisConnected = false;
		//记录正在连接的状态
		private boolean isConnecting = false;

		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();

			if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {// wifi连接上与否
				//Log.d(TAG, "网络已经改变");
				NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				NetworkInfo.State wifiState = info.getState() ;
				if (wifiState.equals(NetworkInfo.State.DISCONNECTED)) {
					if (!isDisConnected) {
						Log.d(TAG, "wifi已经断开");						
						isDisConnected = true;

					}
				} else if (wifiState.equals(NetworkInfo.State.CONNECTING)) {
					if (!isConnecting) {
						Log.d(TAG, "正在连接...");
						isConnecting = true;
					}
				} else if (wifiState.equals(NetworkInfo.State.CONNECTED)) {	
					String ssid = mWifiAdmin.getWifiInfo().getSSID() ;
					Log.d(TAG, "CONNECTED：" + ssid);
					if (ssid.equals(sSSID) || (sSSID == null && !ssid.equals("<unknown ssid>")) ) {
						Toast.makeText(MainActivity.this, "连接成功：" + mWifiAdmin.getWifiInfo().getSSID(), Toast.LENGTH_SHORT).show();
						scanWifi();
					}
				}

			} else if (action.equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {
				int error = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR,
						0);
				switch (error) {

				case WifiManager.ERROR_AUTHENTICATING:
					WifiInfo info = mWifiAdmin.getWifiInfo() ;
					String ssid = "" ;
					if (info != null) {
						ssid =  info.getSSID() ;
						mWifiAdmin.forgetWifiBySsid(ssid);
					}
					Toast.makeText(getApplicationContext(), "wifi密码认证错误：" + ssid, Toast.LENGTH_LONG).show();					
					break;

				default:
					break;
				}

			}
			else if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
				int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
				Log.e(TAG, "wifiState=" + wifiState);
				switch (wifiState) {
				case WifiManager.WIFI_STATE_ENABLING:
					Log.d(TAG, "wifi正在启用");
					break;
				case WifiManager.WIFI_STATE_ENABLED:
					Log.d(TAG, "Wi-Fi已启用。");									
					mSwitch.setChecked(true);
					mListView.setRefreshing();
					scanWifi() ;
					break;

				case WifiManager.WIFI_STATE_DISABLED:
					Log.d(TAG, "Wi-Fi已关闭");
					showNoWifi() ;
					mSwitch.setChecked(false);
					break;

				}
			}
			//Brent
			else if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
				Log.d(TAG, "Wi-Fi扫描有结果");
				showWifiList() ;
			} 
		}

	}

	/**
	 * 说明：Android 4.4+ 设置状态栏透明
	 */
	@SuppressLint("NewApi")
	protected void setStatusTransparent() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			// 5.0+ 实现
			Window window = getWindow();
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(Color.TRANSPARENT);
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			// 4.4 实现
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}
	}

	/**
	 * 说明：Android 6.0+ 状态栏图标原生反色操作
	 */
	protected void setDarkStatusIcon(boolean dark) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			View decorView = getWindow().getDecorView();
			if (decorView == null) return;

			int vis = decorView.getSystemUiVisibility();
			if (dark) {
				vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
			} else {
				vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
			}
			decorView.setSystemUiVisibility(vis);
		}
	}

}

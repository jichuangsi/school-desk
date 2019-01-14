package cn.netin.launcher.service;

import java.util.ArrayList;
import java.util.List;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class DownloadService extends Service {
	public static final String URL_EXTRA = "URL" ;
	public static final String ACTION_EXTRA = "ACTION" ;
	public static final int ACTION_NONE = 0 ;
	public static final int ACTION_INSTALL = 1 ;
	public static final int ACTION_VIEW = 2 ;

	public static final String TAG = "EL DownloadAndInstallService" ;
	/** 安卓系统下载类 **/
	private DownloadManager mDownloadManager;

	/** 接收下载完的广播 **/
	private DownloadCompleteReceiver mReceiver;

	private List<Download> mDownloadList ;

	private class Download{
		long id ;
		String url ;
		int action ;

		public Download(long id, String url, int action){
			this.id = id ;
			this.url = url ;
			this.action = action ;
		}
	}

	public DownloadService() {

	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate") ;
		mDownloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

		mReceiver = new DownloadCompleteReceiver();
		//注册下载广播
		registerReceiver(mReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

		mDownloadList = new ArrayList<Download>() ;

	}

	private boolean isInList(String url){
		for (Download d : mDownloadList) {
			if (d.url.equals(url)) {
				return true ;
			}
		}
		return false ;
	}

	private void removeFromList(long id){
		for (Download d : mDownloadList) {
			if (d.id == id) {
				mDownloadList.remove(d) ;
				return  ;
			}
		}
	}

	private Download getDownload(long id){
		for (Download d : mDownloadList) {
			if (d.id == id) {
				return d ;
			}
		}
		return null ;
	}

	/** 初始化下载器 **/
	private void startDownload(String url, int action) {
		if (isInList(url)) {
			Toast.makeText(this, "此文件已经在下载队列中，不必重复下载", Toast.LENGTH_SHORT).show();
			return ;
		}
		Uri uri = Uri.parse(url);
		DownloadManager.Request downloadRequest = new DownloadManager.Request(uri);
		// 设置允许使用的网络类型，这里是移动网络和wifi都可以
		//downloadRequest.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);

		// 下载时，通知栏显示途中

		downloadRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
		//downloadRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

		//downloadRequest.setTitle("下载APK");
		//downloadRequest.setDescription("Apk Downloading");
		downloadRequest.setVisibleInDownloadsUi(true);

		//downloadRequest.setAllowedOverRoaming(false);


		// 设置下载后文件存放的位置
		String apkName = uri.getLastPathSegment();
		downloadRequest.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, apkName);
		//downloadRequest.setDestinationInExternalPublicDir(dirType, subPath)

		// 将下载请求放入队列
		long id =  mDownloadManager.enqueue(downloadRequest);
		Log.d(TAG, "id=" + id + " apk=" + apkName) ;
		Download d = new Download(id, url, action) ;
		mDownloadList.add(d) ;

		Toast.makeText(this, "已经将" + apkName + "加入下载队列", Toast.LENGTH_SHORT).show();


	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		String url = intent.getStringExtra(URL_EXTRA) ;
		int action = intent.getIntExtra(ACTION_EXTRA, 0) ;
		if (url != null) {
			if (isNetworkAvailable()) {
				startDownload(url, action) ;
			}else{
				Toast.makeText(this, "当前网络不可用，无法下载", Toast.LENGTH_SHORT).show();
			}

		}

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy") ;
		// 注销下载广播
		if (mReceiver != null)
			unregisterReceiver(mReceiver);

		super.onDestroy();
	}


	private boolean isNetworkAvailable() {   
		ConnectivityManager conManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo network = conManager.getActiveNetworkInfo();
		if (network != null) {
			return (network.isAvailable() && network.isConnected()) ;

		}
		return false;   
	}  

	// 接受下载完成后的intent
	class DownloadCompleteReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			//获取下载的文件id
			long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1); 
			String mimeType = mDownloadManager.getMimeTypeForDownloadedFile(id) ;
			Uri uri = mDownloadManager.getUriForDownloadedFile(id);
			Log.d(TAG, "completed: " + id + " : " + uri);
			if (mimeType != null && uri != null) {
				//String path = uri.getPath() ;
				//ActivityBridge.installApk(path) ;
				int action = ACTION_NONE ; ;
				Download d = getDownload(id) ;
				if (d != null) {
					action = d.action ;
				}
				if (action == ACTION_NONE) {
					Toast.makeText(DownloadService.this, "下载成功", Toast.LENGTH_SHORT).show() ;
				}
				else if (action == ACTION_INSTALL) {
					Intent installIntent = new Intent(Intent.ACTION_VIEW);
					installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
					installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(installIntent);
				}else{
					Intent viewIntent = new Intent(Intent.ACTION_VIEW);
					viewIntent.addCategory("android.intent.category.DEFAULT");
					viewIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					viewIntent.setDataAndType(uri, mimeType);
					context.startActivity(viewIntent);
				}


			}else{
				Toast.makeText(context, "下载失败", Toast.LENGTH_SHORT).show();
			}

			removeFromList(id) ;
		}

	}
}
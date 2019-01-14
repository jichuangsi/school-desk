package cn.netin.launcher.installer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;
import cn.netin.launcher.R;
import cn.netin.launcher.service.AppStat;


public class InstallActivity extends Activity {

	private static final String SCHEME_FILE = "file";
	private static final String SCHEME_CONTENT = "content";
	private static final String SCHEME_PACKAGE = "package";
	private static final String TAG = "EL InstallerActivity" ;
	private File mContentUriApkStagingFile = null;
	private AsyncTask<Uri, Void, File> mStagingAsynTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_installer);
		handelIntent(this.getIntent()) ;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		handelIntent(intent) ;
	}

	@Override
	protected void onDestroy() {
		if (mStagingAsynTask != null) {
			mStagingAsynTask.cancel(true);
			mStagingAsynTask = null;
		}
		super.onDestroy();
	}

	private void handelIntent(Intent intent) {

		Uri uri = intent.getData() ;
		processPackageUri(uri) ;

	}

	private boolean processPackageUri(final Uri packageUri) {

		if (packageUri == null) {
			Toast.makeText(this, "URI无效", Toast.LENGTH_LONG).show(); 
			this.finish();
			return false;
		}
		Log.d(TAG, "path=" + packageUri.getPath()) ;
		Log.d(TAG, "uri=" +  packageUri.toString()) ;

		//Toast.makeText(this, "URI:" + packageUri.toString(), Toast.LENGTH_LONG).show(); ;

		final String scheme = packageUri.getScheme();	

		if (scheme == null) {
			Toast.makeText(this, "scheme数据无效", Toast.LENGTH_LONG).show(); 
			this.finish();
			return false;
		}

		String uriStr = packageUri.toString() ;
		if (uriStr.endsWith("INSTALLER_TEST.apk")) {
			this.finish();
			return false;
		}
		/*
		if (!uriStr.endsWith(".apk")) {
			Toast.makeText(this, "只支持apk文件安装", Toast.LENGTH_LONG).show(); ;
			finish();
			return false;
		}
		 */

		switch (scheme) {

		case SCHEME_PACKAGE: 
			Toast.makeText(this, "不支持package多安装包", Toast.LENGTH_LONG).show(); ;
			finish();
			return false;


		case SCHEME_FILE: 
			checkIfAllowedAndInitiateInstall(packageUri) ;
			break;

		case SCHEME_CONTENT: 
			Toast.makeText(this, "请稍候", Toast.LENGTH_SHORT).show(); 
			mStagingAsynTask = new StagingAsyncTask();
			mStagingAsynTask.execute(packageUri);
			return false;


		default: 
			Toast.makeText(this, "scheme数据无效", Toast.LENGTH_LONG).show(); 
			finish() ;

			return false;

		}//switch


		return true;
	}

	public static void setPermission(String filePath)  {

		String command = "chmod " + "777" + " " + filePath;
		Log.d(TAG, command) ;
		Runtime runtime = Runtime.getRuntime();
		try {
			runtime.exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void checkIfAllowedAndInitiateInstall(Uri fileUri) {
		String apkPath =  fileUri.getPath() ;
		File file = new File(apkPath);		
		String pkg = PackageParser.getPackageName(file, apkPath) ;
		if (pkg == null) {
			Toast.makeText(this, "解析APK包错误", Toast.LENGTH_LONG).show(); 
			clearCachedApkIfNeededAndFinish();
			return ;
		}
		Log.d(TAG, "pkg=" + pkg) ;
		boolean allowed = AppStat.isAllowed(pkg, "") ;
		if (!allowed) {
			Toast.makeText(this, "禁止安装此应用", Toast.LENGTH_LONG).show(); 
			AppStat.setInstallerAllowed(false) ;
			clearCachedApkIfNeededAndFinish();
			return ;
		}
		AppStat.setInstallerAllowed(true) ;

		startSystemInstaller(fileUri) ;

	}

	private void startSystemInstaller(Uri fileUri){
		//Toast.makeText(this, "pkg=" + pkg, Toast.LENGTH_SHORT).show(); 
		Intent intent = new Intent() ;
		intent.setData(fileUri) ;
		intent.setClassName("com.android.packageinstaller", "com.android.packageinstaller.PackageInstallerActivity") ;
		try {
			startActivityForResult(intent, 101) ;
		}catch(android.content.ActivityNotFoundException e){
			Log.e(TAG, "no com.android.packageinstaller") ;	
			startOtherInstaller(fileUri) ;
		}
	}
	
	private void startOtherInstaller(Uri fileUri) {
		Intent intent = new Intent() ;
		intent.setData(fileUri) ;

		PackageManager pm = getPackageManager();
		List<ResolveInfo> resolveInfoList = pm.queryIntentActivities(intent, 0);
		if(resolveInfoList == null || resolveInfoList.isEmpty()){
			Toast.makeText(this, "找不到安装器", Toast.LENGTH_SHORT).show();
			return ;
		}

		int size = resolveInfoList.size();
		for(int i = 0; i < size; i++){
			final ResolveInfo r = resolveInfoList.get(i);
			final String pkg = r.activityInfo.packageName ;
			final String name =  r.activityInfo.name ;
			//Log.d(TAG, "pkg=" + pkg + " name=" + name) ;
			if(!pkg.equals("cn.netin.launcher")){
				intent.setClassName(pkg, name) ;
				try {
					startActivityForResult(intent, 101) ;
				}catch(android.content.ActivityNotFoundException e){
					Toast.makeText(this, "启动安装程序错误：" + pkg, Toast.LENGTH_LONG).show();	
				}
				break ;
			}
		}
	}



	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d(TAG, "onActivityResult resultCode=" + resultCode) ;
		clearCachedApkIfNeededAndFinish() ;
		AppStat.setInstallerAllowed(false) ;
	}



	private final class StagingAsyncTask extends AsyncTask<Uri, Void, File> {
		/*
    	private static final long SHOW_EMPTY_STATE_DELAY_MILLIS = 300;
        private final Runnable mEmptyStateRunnable = new Runnable() {
            @Override
            public void run() {

                ((TextView) findViewById(R.id.app_name)).setText(R.string.app_name_unknown);
                ((TextView) findViewById(R.id.install_confirm_question))
                        .setText(R.string.message_staging);
                mInstallConfirm.setVisibility(View.VISIBLE);
                findViewById(android.R.id.tabhost).setVisibility(View.INVISIBLE);
                findViewById(R.id.spacer).setVisibility(View.VISIBLE);
                findViewById(R.id.ok_button).setEnabled(false);
                Drawable icon = getDrawable(R.drawable.ic_file_download);
                Utils.applyTint(PackageInstallerActivity.this,
                        icon, android.R.attr.colorControlNormal);
                ((ImageView) findViewById(R.id.app_icon)).setImageDrawable(icon);
            }
        };
		 */

		@Override
		protected void onPreExecute() {
			//在UI线程中执行runable
			//getWindow().getDecorView().postDelayed(mEmptyStateRunnable,SHOW_EMPTY_STATE_DELAY_MILLIS);
		}

		@Override
		protected File doInBackground(Uri... params) {
			if (params == null || params.length <= 0) {
				Log.e(TAG, "doInBackground invalid params") ;
				return null;
			}
			Uri packageUri = params[0];
			File sourceFile = null;
			try {
				sourceFile = File.createTempFile("package", ".apk", getCacheDir());
				//sourceFile = new File(getCacheDir(), "temp.apk") ;

			} catch (IOException e) {
				Log.e(TAG, "createTempFile fail") ;
				return null ;
			}
			Log.d(TAG, "temp file=" + sourceFile.getPath()) ;

			InputStream in = null;
			try {
				in = getContentResolver().openInputStream(packageUri);
			} catch (FileNotFoundException e) {
				Log.e(TAG, "openInputStream fail") ;				
			}
			if (in == null) {
				sourceFile.delete();
				return null;			
			}
			OutputStream out = null;
			try {
				out = new FileOutputStream(sourceFile);
			} catch (FileNotFoundException e) {
				Log.e(TAG, "FileOutputStream fail") ;
			}
			if (out == null) {
				sourceFile.delete();
				try {
					in.close();
				} catch (IOException e) {
				}
				return null;			
			}

			byte[] buffer = new byte[4096];
			int bytesRead;
			try {
				while ((bytesRead = in.read(buffer)) >= 0) {
					// Be nice and respond to a cancellation
					if (isCancelled()) {
						sourceFile.delete();
						in.close();
						out.close();
						return null;
					}
					out.write(buffer, 0, bytesRead);
				}
			} catch (IOException e) {
				Log.e(TAG, "read in to out error") ;
			}finally{
				try {
					in.close();
				} catch (IOException e) {

				}
				try {
					out.close();
				} catch (IOException e) {

				}
			}

			return sourceFile;
		}

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(File file) {
			Log.e(TAG, "onPostExecute") ;
			//getWindow().getDecorView().removeCallbacks(mEmptyStateRunnable);
			if (isFinishing()) {
				return;
			}

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
				if ( isDestroyed()){
					return;
				}				
			}

			if (file == null) {
				//showDialogInner(DLG_PACKAGE_ERROR);
				//setPmResult(PackageManager.INSTALL_FAILED_INVALID_APK);
				Toast.makeText(InstallActivity.this, "Content URI无效", Toast.LENGTH_LONG).show(); ;
				InstallActivity.this.finish();
				return;
			}
			setPermission(file.getPath()) ;
			mContentUriApkStagingFile = file;
			Uri fileUri = Uri.fromFile(file);

			processPackageUri(fileUri);

		}

		@Override
		protected void onCancelled(File file) {
			// getWindow().getDecorView().removeCallbacks(mEmptyStateRunnable);
		}
	};	

	private void clearCachedApkIfNeededAndFinish() {
		if (mContentUriApkStagingFile != null) {
			mContentUriApkStagingFile.delete();
			mContentUriApkStagingFile = null;
		}
		finish();
	}


	public static void saveFile(InputStream ins, String path) {
		try {
			File file = new File(path) ;
			boolean ret = file.createNewFile() ;
			Log.d(TAG, "ret=" + ret) ;
			OutputStream os = new FileOutputStream(file);
			int bytesRead = 0;
			byte[] buffer = new byte[8192];
			while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
			os.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}




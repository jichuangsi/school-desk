package cn.netin.launcher.installer;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import cn.netin.launcher.R;

public class InstallerCheckActivity extends Activity {
	private static final String TAG = "EL Installer" ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate") ;
		setContentView(R.layout.activity_installer_check);
	}	

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy") ;
	}



	private void testInstaller() {
		File parentFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) ;
		String dir = parentFile.getAbsolutePath() ;
		Log.d(TAG, "dir=" + dir) ;
		File file = new File(parentFile, "webcast.apk");
		Intent intent = new Intent(Intent.ACTION_VIEW);
		// 由于没有在Activity环境下启动Activity,设置下面的标签
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if(Build.VERSION.SDK_INT>=24) { //判读版本是否在7.0以上
			//参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
			Uri providerUri = FileProvider.getUriForFile(this, "cn.netin.apkinstaller.fileprovider", file);
			//添加这一句表示对目标应用临时授权该Uri所代表的文件
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			intent.setDataAndType(providerUri, "application/vnd.android.package-archive");
		}else{
			intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
		}
		this.startActivity(intent);
	}

	public void onClick(View v) {  

		switch (v.getId()) {  
		case R.id.test_button:  
			testInstaller() ;
			finish() ;  
			break;  


		default:  
			break;  
		}  
	}
}

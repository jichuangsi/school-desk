package cn.netin.kidsbrowser;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.view.Gravity;
import android.webkit.DownloadListener;
import android.widget.Toast;

//内部类
public class DownloadServiceDownloadListener implements DownloadListener {
	private Context mContext ;
	public static final String URL_EXTRA = "URL" ;
	public static final String ACTION_EXTRA = "ACTION" ;
	public static final int ACTION_NONE = 0 ;
	public static final int ACTION_INSTALL = 1 ;
	public static final int ACTION_VIEW = 2 ;

	public DownloadServiceDownloadListener(Context context){
		mContext = context ;
	}

	@Override
	public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
			long contentLength) {
		if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			Toast t=Toast.makeText(mContext, "需要SD卡。", Toast.LENGTH_LONG);
			t.setGravity(Gravity.CENTER, 0, 0);
			t.show();
			return;
		}
		Intent intent = new Intent("cn.netin.launcher.service.DOWNLOADSERVICE") ;
		intent.setPackage(mContext.getPackageName());  
		intent.putExtra(URL_EXTRA, url) ;
		intent.putExtra(ACTION_EXTRA, ACTION_VIEW) ;
		mContext.startService(intent) ;
	}



}



package cn.netin.launcher.service;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class RemoteService extends Service {

	private static final String TAG = "EL RemoteService" ;
	private static final int PORT = 10002;
	private static final int POOL_SIZE = 10;
	private static SocketServer mServer = null ;


	@Override
	public void onCreate() {
		super.onCreate();

		Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@ RemoteService onCreate") ;

		for ( int i = 0 ; i < 100 ; i++) {
			int port = PORT  ;
			try {
				mServer = new SocketServer(this, port, POOL_SIZE) ;
				new Thread(mServer).start();
				Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@ NetworkService started at " + port) ;
				break ;
			} catch (IOException e) {
				Log.e(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@ NetworkService start fail") ;
				e.printStackTrace();
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "RemoteService onDestroy") ;
		if (mServer != null) {
			mServer.close();
		}

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}



}

package cn.netin.launcher;

import android.app.Application;
import android.widget.ImageView;
import cn.netin.launcher.receiver.ConnectivityReceiver;
import cn.netin.launcher.receiver.ConnectivityReceiver.ConnectivityListener;

public class ConnectivityHandler {
	private ConnectivityReceiver mConnectivityReceiver ;
	private ImageView mWifiView = null;
	
	public ConnectivityHandler(Application application){
		mConnectivityReceiver = new ConnectivityReceiver(application, new ConnectivityListener() {
			@Override
			public void onChange(int type, boolean isConnected) {
				//Log.d(TAG, "ConnectivityReceiver onChange type=" + type + " isConnected=" + isConnected);
				handleConnectivity(type, isConnected);
			}
		});
	}
	
	public void setView(ImageView view){
		mWifiView = view ;
		boolean isWifiConnected = this.mConnectivityReceiver.isConnected(ConnectivityReceiver.TYPE_WIFI);
		handleConnectivity(ConnectivityReceiver.TYPE_WIFI, isWifiConnected);

	}
	

	private void handleConnectivity(int type, boolean isConnected) {
		if (mWifiView == null) {
			return ;
		}
		if (type == ConnectivityReceiver.TYPE_WIFI) {
			if (isConnected) {
				mWifiView.setImageResource(R.drawable.wifi4);
			} else {
				mWifiView.setImageResource(R.drawable.wifi0);
			}
		}
	}
	
	public void release() {
		mConnectivityReceiver.release();
		mConnectivityReceiver = null;
		mWifiView = null;
	}
}

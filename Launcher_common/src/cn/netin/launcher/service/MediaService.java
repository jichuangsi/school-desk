package cn.netin.launcher.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

/**
 * 播放 服务类
 * 
 */
public class MediaService extends Service {

	private static final String TAG = "EL MediaService" ;
	private MediaHelper mMediaHelper = null ;
	private Messenger mClientMessenger = null ;

	private MediaHelper.MediaHelperListener mMediaHelperListener = new MediaHelper.MediaHelperListener() {
		@Override
		public void onCompletion() {
			notifyStop() ;
		}
	};

	//负责接收并处理进来的消息
	class IncomingHandler extends Handler {        
		@Override        
		public void handleMessage(Message msg) { 

			String path ;
			mClientMessenger = null ;
			mClientMessenger = msg.replyTo;  

			switch (msg.what) {   
			
			case ServiceContract.MSG_MEDIA_PLAY:                   
				path = msg.getData().getString("str0") ;
				if (path == null) {
					Log.d(TAG, "MSG_MEDIA_PLAY path == null.");
					return ;
				}
				play(path) ;
				break;

			case ServiceContract.MSG_MEDIA_RECORD:                   
				path = msg.getData().getString("str0") ;
				if (path == null) {
					Log.d(TAG, "MSG_MEDIA_RECORD path == null.");
					return ;
				}
				record(path, msg.arg1) ;
				break; 

			case ServiceContract.MSG_MEDIA_STOP:                   
				stop() ;
				break;  
				
			default:                  
				super.handleMessage(msg);         
			}

			msg = null ;
		} //handleMessage
	}
	
	//创建一个信使，处理者为IncomingHandler
	final Messenger mMessenger = new Messenger(new IncomingHandler());    

	//当本服务被绑定时，返回信使给调用者，调用者可以差遣信使发送消息给服务。
	@Override    
	public IBinder onBind(Intent intent) {      
		return mMessenger.getBinder();  
	}

	private void notifyStop() {
		if(mClientMessenger != null){
			//System.out.println("%%%%%%%%% onCompletion: playEnd") ;
			Message message = Message.obtain(null, ServiceContract.MSG_MEDIA_END) ;
			try {
				mClientMessenger.send(message) ;
				
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mClientMessenger = null ;
		}
		
	}


	@Override
	public void onCreate() {
		super.onCreate();	
		//System.out.println("Media Service on Create");
		mMediaHelper = new MediaHelper(this) ;
		mMediaHelper.setListener(mMediaHelperListener) ;

	}



	@Override
	public boolean onUnbind(Intent intent) {
		mClientMessenger = null ;
		return super.onUnbind(intent);
		
	}

	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		mClientMessenger = null ;
		if (mMediaHelper != null) {
			mMediaHelper.release() ;
		}
		mMediaHelper = null ;
	}



	/**
	 * 播放声音
	 * @param path
	 */
	public void play(String path) {
		mMediaHelper.play(path) ;
	}

	/**
	 * 播放声音
	 * @param path
	 */
	public void record(String path, int sec) {
		mMediaHelper.record(path, sec) ;
	}


	/**
	 * 停止
	 */
	public void stop() {	
		if (mMediaHelper == null) {
			return ;
		}
		mMediaHelper.stop() ;
	}


}

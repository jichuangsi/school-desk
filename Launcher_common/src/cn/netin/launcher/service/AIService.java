package cn.netin.launcher.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import cn.netin.launcher.service.aispeech.AIScorer;
import cn.netin.launcher.service.aispeech.AIScorer.AIScorerListener;


/**
 * 播放 服务类
 * 
 */
public class AIService extends Service {
	private static final String TAG = "EL AIService" ;	
	private AIScorer mScorer = null ;
	private MediaHelper mMediaHelper = null ;
	private Messenger mClientMessenger = null ;

	private AIScorerListener mAIScorerListener = new AIScorerListener() {

		@Override
		public void onCompletion(int result, String json) {
			if (result != 0) {
				notifyError(result) ;
			}else{
				notifyResult(result, json) ;
			}
			
			
		}

	};

	//负责接收并处理进来的消息
	class IncomingHandler extends Handler {        
		@Override        
		public void handleMessage(Message msg) { 

			String refText ;
			mClientMessenger = msg.replyTo;  

			switch (msg.what) {               
			case ServiceContract.MSG_AISCORE_START:                   
				refText = msg.getData().getString("str0") ;
				score(refText) ;


				break;
				
			case ServiceContract.MSG_AISCORE_STOP:             

				stop() ;

				break;  

			case ServiceContract.MSG_AISCORE_PLAY:                   


				break; 

			case ServiceContract.MSG_AISCORE_PLAY_STOP:                   


				break; 
 
			default:                  
				super.handleMessage(msg);         
			}     
		} 
	}
	//创建一个信使，处理者为IncomingHandler
	final Messenger mMessenger = new Messenger(new IncomingHandler());    

	//当本服务被绑定时，返回信使给调用者，调用者可以差遣信使发送消息给服务。
	@Override    
	public IBinder onBind(Intent intent) {      
		return mMessenger.getBinder();  
	}

	private void notifyError(int errorCode) {
		if(mClientMessenger != null){
			//System.out.println("%%%%%%%%% onCompletion: playEnd") ;
			Message message = Message.obtain(null, ServiceContract.MSG_AISCORE_ERROR) ;
			message.arg1 = errorCode ;
			try {
				mClientMessenger.send(message) ;			
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		mClientMessenger = null ;
	}


	@Override
	public void onCreate() {
		super.onCreate();	

		mScorer = new AIScorer(this) ;
		mScorer.setListener(mAIScorerListener);

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

	private void score(String refText) {
		int error = mScorer.score(refText) ;
		if (error != 0) {
			notifyError(error) ;
		}
	}

	private void notifyResult(int result, String json) {
		//System.out.println("mClicentMessenger = " + mClientMessenger) ;
		if(mClientMessenger != null){
			//System.out.println("%%%%%%%%% onCompletion: playEnd") ;
			Message message = Message.obtain(null, ServiceContract.MSG_AISCORE_RESULT) ;
			message.arg1 = result ;
			Bundle bundle = new Bundle() ;
			bundle.putString("str0", json) ;
			message.setData(bundle) ;
			//System.out.println("what = " + message.what);
			try {
				
				mClientMessenger.send(message) ;
				
			} catch (RemoteException e) {
				Log.e(TAG, "notifyResult fail") ;
				e.printStackTrace();
			}
		}else{
			Log.e(TAG, "notifyResult mClientMessenger == null") ;			
		}
		
		mClientMessenger = null ;
	}

	private void stop() {
		mScorer.stopScore();
	}
}

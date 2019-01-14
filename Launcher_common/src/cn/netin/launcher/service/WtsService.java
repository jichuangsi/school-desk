package cn.netin.launcher.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.hanceedu.common.util.FileSearcher;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.Toast;
import cn.netin.launcher.R;


/**
 * 播放 服务类
 * 
 */
public class WtsService extends Service {

	private MediaPlayer mMediaPlayer ;
	private Messenger mClientMessenger = null ;
	private static final int PLAY = 0xF0 ;
	private static final int STOP = 0xF1 ;	
	private static final int STOPPED = 0xF2 ;	
	private static final int CHECK = 0xF3 ;		
	
	private static String mPath ;
	private static String mDataPath ;
	private boolean mReady ;

	//创建一个信使，处理者为IncomingHandler
	private Messenger mMessenger = new Messenger(new IncomingHandler());    
	
	//负责接收并处理进来的消息
	class IncomingHandler extends Handler {        
		@Override        
		public void handleMessage(Message msg) { 
			String word ;
			mClientMessenger = msg.replyTo;  

			switch (msg.what) {               
			case PLAY:                   
				word = msg.getData().getString("str0") ;
				if (word == null) {
					System.err.println("WtsService word == null.");
					return ;
				}
				play(word) ;

				break; 
				
			case STOP:                   

				stop() ;

				break;  
			case CHECK:   
				//System.out.println("WtsService CHECK...");
				word = msg.getData().getString("str0") ;
				if (word == null) {
					System.err.println("WtsService word == null.");
					return ;
				}
				check(word) ;

				break;  
			default:                  
				super.handleMessage(msg);         
			}     
		} 
	}

	
	//当本服务被绑定时，返回信使给调用者，调用者可以差遣信使发送消息给服务。
	@Override    
	public IBinder onBind(Intent intent) {  
		IBinder binder = mMessenger.getBinder();  
		if (binder == null) {
			System.out.println("%%%%%%%%%  binder == null") ;
		}
		return binder ;  
	}

	private void notifyStop() {
		if(mClientMessenger != null){
			//System.out.println("%%%%%%%%% onCompletion: playEnd") ;
			Message message = Message.obtain(null, STOPPED) ;
			try {
				mClientMessenger.send(message) ;
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	

	private void check(String word) {
		if (!initVoice() ) {
			return ;
		}
		if(mClientMessenger != null){
			int flag = 0 ;
			boolean ret =  voice( word, null) ;
			//System.out.println("WtsService CHECK ret= " + ret);
			if (ret) {
				flag = 1 ;
			}
			Message message = Message.obtain(null, CHECK) ;
			message.arg1 =  flag ;
			try {
				mClientMessenger.send(message) ;
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	
	/**
	 * 播放结束监听类
	 */
	MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {

		@Override
		public void onCompletion(MediaPlayer mp) {

			 notifyStop() ;
		}

	};




	@Override
	public void onCreate() {
		super.onCreate();

		mMediaPlayer = new MediaPlayer();//实例化一个多媒体播放器
		mMediaPlayer.setOnCompletionListener(mOnCompletionListener);//设置它监听完毕器
		
		//mPath = this.getCacheDir().getAbsolutePath() + File.separator + "word.wav" ; 
		//System.out.println("wtsService mPath=" + mPath) ;
		mPath = "/mnt/sdcard/word.wav" ; 
		//System.out.println("wtsService mPath=" + mPath) ;
		//mDataPath = this.getString(R.string.data_path) ;
		mDataPath = "/mnt/sdcard/学习内容/voice.wts" ;
		loadLibrary() ;
		initVoice() ;
		
	}
	
	private String findDataPath() {
		String[] filter = new String[1] ;
		filter[0] = "英语发音库.wts" ;

		//搜索某个目录下 某个后缀名的文件 
		//int storage_paths = R.array.storage_paths;
		//int data_paths = R.array.data_paths;
		//final String[] data_paths_array = getResources().getStringArray(data_paths);
		
		String[] data_paths_array = {
				"/mnt/flash/学习内容/" ,
				"/mnt/sdcard/学习内容/"
		};
		
		FileSearcher fileSearcher = new FileSearcher() ;
		
		List<String> paths = fileSearcher.search(data_paths_array, filter, true);
		if (paths == null || paths.size() == 0) {
			//final String[] storage_paths_array =  getResources().getStringArray(storage_paths);
			final String[] storage_paths_array ={
					"/mnt/flash" ,
					"/mnt/sdcard"	
			} ;
			
			paths = fileSearcher.search(storage_paths_array, filter, false);
			if (paths == null || paths.size() == 0) {
				Toast.makeText(this, "找不到发音库", Toast.LENGTH_LONG).show();
				return null;
			}			
		}
		return paths.get(0) ;
	}

	private boolean loadLibrary() {
		String lib = "hcic" ; 
        try   
        {  
            System.loadLibrary(lib); 
       
        } catch (UnsatisfiedLinkError ule)   
        {  
            System.out.println("ERROR: Could not loadlibrary: " + lib);  
            return false ;
        }    
        return true ;
	}

	private boolean initVoice() {
		if (mReady) {
			return true ;
		}
		mDataPath = findDataPath() ;
		if (mDataPath == null) {
			return false ;
		}
		mReady = init( mDataPath) ;
		if (!mReady) {
			Toast.makeText(this, "找不到发音库：" + mDataPath , Toast.LENGTH_LONG).show();
		}
		return mReady ;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mMediaPlayer.release();
		mMediaPlayer = null ;		
		mMessenger = null ;
		mClientMessenger = null ;
		System.out.println("%%%%%%%%% WtsService onDestroy") ;
	}

	/**
	 * 停止
	 */
	public void stop() {
		if (mMediaPlayer.isPlaying()) {
			System.out.println("%%%%%%%%% stop: playEnd") ;
			mMediaPlayer.stop();
		}
	

	}


	/**
	 * 播放声音
	 * @param path
	 */
	public void play(String word) {
		
		if (!initVoice() ) {
			return ;
		}
		boolean ret = voice( word, mPath) ;
		if (!ret) {
			System.out.println("word not in voicebank: " + word);
			notifyStop() ;
			return;
		}
		File mp3File = new File(mPath);
		if (!mp3File.exists()) {
			System.out.println("wav not exists: " + mPath);
			notifyStop() ;
			return;
		}

		try {
			//Brent
			//mMediaPlayer.reset();
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.stop();
			}
			mMediaPlayer.reset();
			mMediaPlayer.setDataSource(mPath);
			mMediaPlayer.prepare();
			mMediaPlayer.start();

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			//Log.i(TAG, e.getMessage());
		} catch (IllegalStateException e) {
			e.printStackTrace();
			//Log.i(TAG, e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			//Log.i(TAG, e.getMessage());
		}
	}



	public native static boolean init(String path ) ;
	//如果path == NULL，仅查询，不保存
	public native static boolean voice(String word, String path) ;
	public native static void close() ;


}

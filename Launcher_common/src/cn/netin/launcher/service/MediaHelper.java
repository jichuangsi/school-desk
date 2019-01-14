package cn.netin.launcher.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.PowerManager;

public class MediaHelper {

	private static MediaPlayer mMediaPlayer ;
	private static MediaRecorder mMediaRecorder ;
	private static AudioRecord mAudioRecord ;
	private static AudioTrack mAudioTrack ;


	private Context mContext ;
	private MediaHelperListener mListener ;
	private static boolean mMediaRecorder_recording ;
	private static volatile boolean mAudioRecord_recording ;
	private static volatile boolean mAudioTrack_playing ;


	/**录制的文件*/
	private File mPcmFile ;
	/**录音输入源*/	
	private int mAudioSource = MediaRecorder.AudioSource.MIC ;
	/**频率*/
	private int mAudioFrequence = 8000;  
	/**通道*/
	private int mAudioInChannel = AudioFormat.CHANNEL_IN_MONO ;  
	private int mAudioOutChannel = AudioFormat.CHANNEL_OUT_MONO ;  
	/**录制编码*/
	private int mAudioEncoding = AudioFormat.ENCODING_PCM_16BIT ;  

	/**PCM录制线程*/
	private AudioRecordThread  mAudioRecordThread = null ;
	/**PCM播放线程*/	
	private AudioTrackThread mAudioTrackThread = null ;

	private int mBufferSize = 16384 ;

	private Timer mTimer = null ;
	//延时执行的任务，录音时间到，执行停止
	private TimerTask mTask = null ;

	/**
	 * 播放结束监听类
	 */
	private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {

		@Override
		public void onCompletion(MediaPlayer mp) {
			notifyCompletion() ;
		}

	};

	/**
	 * 监听接口
	 */
	public interface MediaHelperListener{
		public void onCompletion();
	}

	public void setListener(MediaHelperListener listener) {
		mListener = listener ;
	}

	public MediaHelper(Context context) {
		mContext = context ;

		//实例化MediaPlayer
		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setOnCompletionListener(mOnCompletionListener);//设置它监听完毕器
		mMediaPlayer.setWakeMode(mContext, PowerManager.PARTIAL_WAKE_LOCK);

		//实例化MediaRecorder
		mMediaRecorder = new MediaRecorder(); 
		//mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); 
		//mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP); 
		//mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);


		//实例化AudioTrack  
		mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, mAudioFrequence, mAudioOutChannel, mAudioEncoding, mBufferSize, AudioTrack.MODE_STREAM);  
		//实例化AudioRecord
		mAudioRecord = new AudioRecord(mAudioSource, mAudioFrequence, mAudioInChannel, mAudioEncoding, mBufferSize);  

	}

	private void notifyCompletion() {
		if (mListener != null) {
			mListener.onCompletion() ;
		}
	}


	/**
	 * 播放
	 * @param path
	 */
	public void play(String path) {

		if (path.indexOf(".pcm") != -1) {
			playPcm(path) ;
		}else{
			playMedia(path) ; 
		}

	}

	/**
	 * 播放声音
	 * @param path
	 */
	public void playMedia(String path) {

		stop() ;
		mMediaPlayer.reset();
		try {
			mMediaPlayer.setDataSource("file://" + path);
			System.out.println("mMediaPlayer.setDataSource(path)==" + path);
			mMediaPlayer.prepare();
		} catch (Exception e) {
			e.printStackTrace();
			mListener.onCompletion() ;
			return ;
		} 		
		mMediaPlayer.start();

	}

	/**
	 * PCM录音
	 * @param path
	 */
	public void playPcm(String path) {

		stop() ;

		mPcmFile = new File(path);
		if(!mPcmFile.exists()){
			System.err.println("MediaHelper playPcm path not exists: " + path);
			notifyCompletion() ;	
			return ;
		}

		//实例化PcmPlayThread
		mAudioTrackThread = null ;
		mAudioTrackThread = new AudioTrackThread() ;
		mAudioTrackThread.start();
	}


	/**
	 * 录音
	 * @param path
	 */
	public void record(String path, int sec) {

		if (path.indexOf(".pcm") != -1) {
			recordPcm(path, sec) ;
		}else{
			recordMedia(path, sec) ; 
		}
	}

	/**
	 * 录音
	 * @param path
	 */
	private void recordMedia(String path, int sec) {

		stop() ;
		//TEMP
		mMediaRecorder.reset() ;
		mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); 
		mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP); 
		mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		mMediaRecorder.setOutputFile(path); 
		try {
			mMediaRecorder.prepare();
		} catch (Exception e1) {
			e1.printStackTrace();
			notifyCompletion() ;	
			return ;
		} 
		mMediaRecorder.start();   // Recording is now started ...	

		schedule(sec) ;
	}



	/**
	 * PCM录音
	 * @param path
	 */
	private void recordPcm(String path, int sec) {

		stop() ;

		mPcmFile = new File(path);
		if(mPcmFile.exists()){
			mPcmFile.delete();
		}
		try {
			mPcmFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			notifyCompletion() ;	
			return ;
		}

		mAudioRecordThread = null ;
		//实例化AudioRecordThread
		mAudioRecordThread = new AudioRecordThread() ;
		mAudioRecordThread.start();

		schedule(sec) ;

	}
	
	private void resetTimer() {
		if (mTimer != null) {
			mTimer.cancel() ;
			mTimer = null ;
		}
		if (mTask != null) {
			mTask.cancel() ;
			mTask = null ;
		}
	}

	private void schedule(int sec) {
		resetTimer() ;
		mTask = new TimerTask() {
			@Override
			public void run() {
				stop() ;
				notifyCompletion() ;
			}
		};
		mTimer = new Timer() ;
		mTimer.schedule(mTask, sec * 1000);
	}
	
	/**
	 * 停止
	 */
	public void stop() {

		resetTimer() ;
		
		if (mAudioRecord_recording) {
			//改变这个变量后，mAudioRecordThread会自己停止
			mAudioRecord_recording = false ;
			wait_a_moment() ;
		}
		if (mAudioTrack_playing) {
			//改变这个变量后，mAudioTrackThread会自己停止
			mAudioTrack_playing = false ; 
			//下句不必要
			//mAudioTrack.stop() ;
			wait_a_moment() ;
		}

		if (mMediaRecorder_recording) {
			mMediaRecorder_recording = false ;
			mMediaRecorder.stop() ;
			wait_a_moment() ;
		}

		if (mMediaPlayer.isPlaying()) {
			mMediaPlayer.stop();
		}

	}

	private void wait_a_moment() {
		try {
			Thread.sleep(500) ;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 释放资源
	 */
	public void release() {

		stop() ;
		resetTimer() ;
		
		mMediaPlayer.release();
		mMediaPlayer = null ;	

		mMediaRecorder.release() ;
		mMediaRecorder = null ;

		mAudioRecordThread = null ;
		mAudioTrackThread = null ;

		mAudioTrack.release() ;
		mAudioTrack = null ;

	}

	private boolean isAudioRecordRecording() {
		return mAudioRecord_recording ;
	}

	private boolean isAudioTrackPlaying() {
		return mAudioTrack_playing ;
	}

	/**
	 * PCM录音线程类
	 *
	 */
	private class AudioRecordThread extends Thread{


		@Override
		public void run() {


			try {  
				//开通输出流到指定的文件  
				DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(mPcmFile), mBufferSize));  
				//定义缓冲  
				short[] buffer = new short[mBufferSize];  
				//开始录制  
				mAudioRecord.startRecording();  
				mAudioRecord_recording = true ;
				//定义循环，根据mAudioRecord_recording的值来判断是否继续录制  
				while(isAudioRecordRecording()){  
					//从bufferSize中读取字节，返回读取的short个数  
					int bufferReadResult = mAudioRecord.read(buffer, 0, mBufferSize);  
					waveScalse(buffer,bufferReadResult);
					//循环将buffer中的音频数据写入到OutputStream中  
					for(int i=0; i<bufferReadResult; i++){  
						dos.writeShort(buffer[i]);  
					}  
				}  
				//录制结束  
				mAudioRecord.stop();  
				dos.flush() ;
				dos.close();  


			} catch (Exception e) {  
				e.printStackTrace();
			}  

			mAudioRecord_recording = false ;
		}

		/**
		 * 增益PCM声音
		 * @param tmpBuf 数据
		 * @param len 长度
		 */
		private void waveScalse(short[] tmpBuf, int len) {
			for (int i = 0; i < len;i++) {
				int value = (int)( tmpBuf[i] * 1.5f);
				if (value < -32767) { //pcm16数据 大小范围
					value = -32767;
				} else if (value > 32767) {
					value = 32767;
				}
				tmpBuf[i]= (short)value;
			}
		}
	}



	/**
	 * PCM播放线程类
	 *
	 */
	private class AudioTrackThread extends Thread{

		@Override
		public void run() {

			short[] buffer = new short[mBufferSize / 4];  
			try {  
				//定义输入流，将音频写入到AudioTrack类中，实现播放  
				DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(mPcmFile)));  

				//开始播放  
				mAudioTrack.play();  
				mAudioTrack_playing = true ;
				short tmp ;
				int i ;
				while(isAudioTrackPlaying() && dis.available() > 0){  
					i = 0;  
					while( dis.available() > 0 && i < buffer.length){ 
						tmp = dis.readShort();
						buffer[i] = tmp;
						i++;  
					}  
					//然后将数据写入到AudioTrack中  
					mAudioTrack.write(buffer, 0, buffer.length);  

				}  

				//播放结束  
				mAudioTrack.stop(); 
				dis.close(); 



			} catch (Exception e) {  
				e.printStackTrace();
			}  
			mAudioTrack_playing = false ;
			notifyCompletion() ;	
		}   
	}


}

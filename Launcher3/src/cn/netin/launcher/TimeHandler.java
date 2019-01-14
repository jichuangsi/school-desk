package cn.netin.launcher;

import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
import android.widget.TextView;

public class TimeHandler {
	
	private Thread mTimeThread = null;
	private TextView mTextView = null;
	private String mTimeStr = "" ;
	public TimeHandler() {
		// 时钟显示更新线程
		mTimeThread = new TimeThread();
		mTimeThread.start();
	}
	
	public void setView(TextView view){
		mTextView = view ;
		mTextView.setText(getCurrentTime());
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {

			String currentTime = getCurrentTime();
			if (!mTimeStr.equals(currentTime) && mTextView != null) {
				// Log.d(TAG, "currentTime=" + currentTime);
				mTextView.setText(currentTime);
				mTimeStr = currentTime;
			}
		}

	};
	
	private static String getCurrentTime() {
		Time time = new Time();
		time.setToNow();
		// String currentTime = time.format("%Y-%m-%d %H:%M:%S");
		return time.format("%H:%M");
	}

	

	public class TimeThread extends Thread {

		@Override
		public void run() {
			while (!this.isInterrupted()) {
				try {
					Thread.sleep(1000);// 线程暂停1秒，单位毫秒
					Message message = new Message();
					message.what = 1;
					mHandler.sendMessage(message);// 发送消息
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			//Log.d(TAG, "TimeThread stopped");
		}

	}

	public void release() {
		if (mTimeThread != null) {
			mTimeThread.interrupt();
		}
	}
}

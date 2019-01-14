package cn.netin.parentalcontrol;

import com.hanceedu.common.HanceApplication;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;


public class RestTimeActivity extends Activity {

	private static final String TAG = " RestTimeActivity" ;
	private HanceApplication mApp  = null;

	private RestTimeData mData = null ;
	ToggleButton mRestSwitch ;
	ToggleButton mPeriodSwitch ;
	private SeekBar mPlayTimeSeekBar ;
	private SeekBar mRestTimeSeekBar ;
	private TimePicker mStartTimePicker  ;
	private TimePicker mEndTimePicker ;
	

	private OnSeekBarChangeListener mSeekBarListener = new OnSeekBarChangeListener() {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {

		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			int progress = seekBar.getProgress() ;
			//Log.i(TAG, "progress=" + progress) ;
			//进度条分成了6格，每格为17%，按格对齐
			int mutiply = (progress + 7) / 16;
			int newProgress = (int) (mutiply * 100.0f / 6.0f + 0.5f );
			//最少要有一格
			if (newProgress < 17) {
				newProgress = 17 ;
			}
			seekBar.setProgress(newProgress);
			Log.i(TAG, "newProgess=" + newProgress) ;
		}

	};


	@TargetApi(Build.VERSION_CODES.KITKAT)
	private void setFullScreen() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			getWindow().getDecorView().setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_LAYOUT_STABLE
					| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
					| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_FULLSCREEN
					| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
		}else{			
			getWindow().getDecorView().setSystemUiVisibility(8); // 4.0全屏设置,仅特制系统可用
		}
	} 
	

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			//setFullScreen() ;
		}
	}

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setFullScreen() ;
		setContentView(R.layout.act_rest_time);

		mApp = ((HanceApplication)getApplicationContext()) ;
		mData = new RestTimeData(this) ;
		RestTime restTime = mData.getRestTime() ;
		if (restTime.play == 0) {
			restTime.play = 15 ;
		}
		if (restTime.rest == 0) {
			restTime.rest = 5 ;
		}
		
		mPlayTimeSeekBar = (SeekBar)findViewById(R.id.sb_play_time) ;
		mPlayTimeSeekBar.setOnSeekBarChangeListener(mSeekBarListener);
		int progress = (int) ((float)restTime.play / Constants.MAX_PLAY_TIME  * 100) ;
		//为了界面对齐做了一点小手脚
		int mutiply = (progress + 7) / 16;
		progress = (int) (mutiply * 100.0f / 6.0f + 0.5f );
		mPlayTimeSeekBar.setProgress(progress);

		mRestTimeSeekBar = (SeekBar)findViewById(R.id.restTimeSeekBar) ;
		mRestTimeSeekBar.setOnSeekBarChangeListener(mSeekBarListener) ;
		progress = (int) ((float)restTime.rest / Constants.MAX_REST_TIME * 100 ) ;
		mutiply = (progress + 7) / 16;
		progress = (int) (mutiply * 100.0f / 6.0f + 0.5f );
		mRestTimeSeekBar.setProgress(progress);

		mRestSwitch = (ToggleButton)findViewById(R.id.playTimeSwitch) ;
		mRestSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				mPlayTimeSeekBar.setEnabled(isChecked);
				mRestTimeSeekBar.setEnabled(isChecked);

			}

		});
		mRestSwitch.setChecked(restTime.enableRest);
		boolean isPlayTimeOn = mRestSwitch.isChecked() ;
		mPlayTimeSeekBar.setEnabled(isPlayTimeOn);
		mRestTimeSeekBar.setEnabled(isPlayTimeOn);

		mStartTimePicker = (TimePicker)findViewById(R.id.startTime) ;
		mEndTimePicker = (TimePicker)findViewById(R.id.endTime) ;
		mPeriodSwitch = (ToggleButton)findViewById(R.id.playPeriodSwitch) ;
		mPeriodSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				mStartTimePicker.setEnabled(isChecked);
				mEndTimePicker.setEnabled(isChecked);
			}

		});

		boolean enablePeriod = restTime.enablePeriod ;
		mPeriodSwitch.setChecked(enablePeriod);



		mStartTimePicker.setIs24HourView(true) ;
		mEndTimePicker.setIs24HourView(true) ;
		mStartTimePicker.setEnabled(enablePeriod);
		mEndTimePicker.setEnabled(enablePeriod);
		//Log.d(TAG, "get startTime=" + restTime.startTime + " endTime=" + restTime.endTime ) ;
		mStartTimePicker.setCurrentHour(restTime.startTime / 60);
		mStartTimePicker.setCurrentMinute(restTime.startTime % 60);
		mEndTimePicker.setCurrentHour(restTime.endTime / 60);
		mEndTimePicker.setCurrentMinute(restTime.endTime % 60);		
	}

	private boolean save() {
		RestTime restTime = new RestTime() ;
		restTime.enableRest = mRestSwitch.isChecked() ;
		restTime.play = (int)(mPlayTimeSeekBar.getProgress() / 100.0f * Constants.MAX_PLAY_TIME ) ;
		restTime.rest = (int)(mRestTimeSeekBar.getProgress() / 100.0f * Constants.MAX_REST_TIME ) ;
		restTime.enablePeriod = mPeriodSwitch.isChecked() ;
		int startTime = mStartTimePicker.getCurrentHour() * 60 + mStartTimePicker.getCurrentMinute() ;
		int endTime = mEndTimePicker.getCurrentHour() * 60 + mEndTimePicker.getCurrentMinute() ;	
		//以5分钟对齐
		startTime = (startTime + 2) / 5 * 5 ;
		endTime = (endTime + 2) / 5 * 5 ;		
		if (restTime.startTime > 22 * 60
			|| restTime.endTime < 8 * 60
			|| restTime.endTime - restTime.startTime <= 1 * 60
				) {
			Toast.makeText(this, R.string.invalid_period, Toast.LENGTH_LONG).show()  ;
			return false ;
		}
		restTime.startTime = startTime ;
		restTime.endTime = endTime ;		
		//Log.d(TAG, "play=" + restTime.play + " rest=" + restTime.rest) ;
		//Log.d(TAG, "startTime=" + restTime.startTime + " endTime=" + restTime.endTime) ;
		mData.insertOrUpdateRestTime(restTime) ;
		return true ;
	}

	public void handleClick(View source) {
		int id = source.getId();
		if (id == R.id.closeButton) {
			this.finish();
		}

	}

	@Override
	protected void onStop() {
		super.onStop();
		save() ;
	}
	
}



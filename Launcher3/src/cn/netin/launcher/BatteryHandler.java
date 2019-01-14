package cn.netin.launcher;

import android.app.Application;
import android.os.BatteryManager;
import android.view.View;
import android.widget.ImageView;
import cn.netin.launcher.receiver.BatteryStatusReceiver;
import cn.netin.launcher.receiver.BatteryStatusReceiver.BatteryStatusListener;

public class BatteryHandler {
	
	private ImageView mBatteryView = null;
	private BatteryStatusReceiver mBatteryStatusReceiver = null ;
	
	public BatteryHandler(Application application){
		
		mBatteryStatusReceiver = new BatteryStatusReceiver(application, new BatteryStatusListener() {
			@Override
			public void onChange(int status, int level, boolean overHeat) {
				//Log.d(TAG, "BatteryStatusReceiver status=" + status + " level=" + level + " overHeat=" + overHeat);
				handleBatteryStatus(status, level, overHeat);
			}
		});		
	
	}
	
	public void setView(ImageView batteryView) {
		mBatteryView = batteryView ;
		int[] status = this.mBatteryStatusReceiver.getBatteryStatus();
		this.handleBatteryStatus(status[0], status[1], false);
	}

	private void handleBatteryStatus(int status, int level, boolean overHeat) {

		if (mBatteryView == null || mBatteryView.getVisibility() != View.VISIBLE) {
			return;
		}
		if (overHeat) {
			mBatteryView.setImageResource(R.drawable.battery_error);
		} else {
			switch (status) {
			case BatteryManager.BATTERY_STATUS_UNKNOWN:
				// mBatteryView.setBackgroundDrawable(null);
				mBatteryView.setVisibility(View.INVISIBLE);
				break;
			case BatteryManager.BATTERY_STATUS_CHARGING:
				if (level > 90)
					mBatteryView.setImageResource(R.drawable.battery5c);
				else if (level > 80)
					mBatteryView.setImageResource(R.drawable.battery4c);
				else if (level > 60)
					mBatteryView.setImageResource(R.drawable.battery3c);
				else if (level > 40)
					mBatteryView.setImageResource(R.drawable.battery2c);
				else
					mBatteryView.setImageResource(R.drawable.battery1c);
				break;
			case BatteryManager.BATTERY_STATUS_DISCHARGING:
			case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
				if (level > 90)
					mBatteryView.setImageResource(R.drawable.battery5);
				else if (level > 80)
					mBatteryView.setImageResource(R.drawable.battery4);
				else if (level > 60)
					mBatteryView.setImageResource(R.drawable.battery3);
				else if (level > 40)
					mBatteryView.setImageResource(R.drawable.battery2);
				else if (level > 20)
					mBatteryView.setImageResource(R.drawable.battery1);
				else
					mBatteryView.setImageResource(R.drawable.battery0);
				break;
			case BatteryManager.BATTERY_STATUS_FULL:
				//Log.d(TAG, "BATTERY_STATUS_FULL");
				break;
			default:
				mBatteryView.setImageResource(R.drawable.battery_unknown);
				break;
			}
		}
	}
	
	public void release() {
		mBatteryStatusReceiver.release();
		mBatteryStatusReceiver = null;
		mBatteryView = null ;
	}
}

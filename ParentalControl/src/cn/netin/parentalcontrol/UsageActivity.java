package cn.netin.parentalcontrol;

import java.util.List;

import com.hanceedu.common.App;
import com.hanceedu.common.HanceApplication;
import com.hanceedu.common.util.DateInfo;
import com.hanceedu.common.util.DateUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RadioButton;


public class UsageActivity extends Activity {
	private static final int DAY_STATS = 1 ;
	private static final int WEEK_STATS = 2 ;
	private static final int MONTH_STATS = 3 ;
	private static final String TAG = "EL UsageActivity" ;
	private HanceApplication mApp  = null;

	private AppStatData mAppStatData = null ;
	private int mStatType = 1 ;
	private int mDateIndex = 6 ;
	private DateInfo[] mDateInfos ;
	private ViewGroup mDateButtonsLayout ;
	private ListView mListView ;
	private UsageAdapter mUsageAdapter ;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.usage);

		mApp = ((HanceApplication)getApplicationContext()) ;

		mAppStatData = new AppStatData(this) ;		
		mListView = (ListView) findViewById(R.id.listView) ;
		mUsageAdapter = new UsageAdapter(this) ;
		mListView.setAdapter(mUsageAdapter);

		mDateButtonsLayout = (ViewGroup) findViewById(R.id.dateButtonsLayout) ;
		setupDateButtons() ;
		updateList() ;
	}

	private void updateList() {
		long startDate = mDateInfos[mDateIndex].days ;
		long endDate = 0 ;
		if (mDateIndex < 6) {
			endDate = mDateInfos[mDateIndex + 1].days ;
		}  	
		//Log.d(TAG, "startDate=" + startDate + " endDate=" + endDate) ;
		List<App> appList = mAppStatData.getStats(startDate, endDate, mStatType);
		mUsageAdapter.setData(appList);

	}

	private void setupDateButtons() {
		RadioButton button = null;
		switch (mStatType){
		case DAY_STATS :
			mDateInfos = DateUtil.getRecent7Days(0);
			for (int i = 0 ; i < 7; i++) {
				button = (RadioButton) mDateButtonsLayout.getChildAt(i) ;
				button.setText(mDateInfos[i].mmmd);
			}
			break ;

		case WEEK_STATS :
			mDateInfos = DateUtil.getRecent7Weeks(0);
			for (int i = 0 ; i < 7; i++) {
				button = (RadioButton) mDateButtonsLayout.getChildAt(i) ;
				button.setText(mDateInfos[i].mmmd);
			}
			break ;

		case MONTH_STATS :
			mDateInfos = DateUtil.getRecent7Months(0);
			for (int i = 0 ; i < 7; i++) {
				button = (RadioButton) mDateButtonsLayout.getChildAt(i) ;
				String month = mDateInfos[i].mmmd ;
				month = month.substring(0, month.indexOf(' ')) ;
				button.setText(month);
			}
			break ;   	
		}
		if (button != null) {
			button.setChecked(true);
		}
		//int count = mDateButtonsLayout.getChildCount() ;

	}

	public void handleClick(View source) {
		int id = source.getId();
		if (id == R.id.closeButton) {
			this.finish();
		} else if (id == R.id.dayButton) {
			mStatType = DAY_STATS ;
			mDateIndex = 6 ;
			setupDateButtons() ;
			updateList() ;
		} else if (id == R.id.weekButton) {
			mStatType = WEEK_STATS ;
			mDateIndex = 6 ;
			setupDateButtons() ;
			updateList() ;
		} else if (id == R.id.monthButton) {
			mStatType = MONTH_STATS ;
			mDateIndex = 6 ;
			setupDateButtons() ;
			updateList() ;
		} else if (id == R.id.navi1) {
			mDateIndex = 0 ;
			updateList() ;
		} else if (id == R.id.navi2) {
			mDateIndex = 1 ;
			updateList() ;
		} else if (id == R.id.navi3) {
			mDateIndex = 2 ;
			updateList() ;
		} else if (id == R.id.navi4) {
			mDateIndex = 3 ;
			updateList() ;
		} else if (id == R.id.navi5) {
			mDateIndex = 4 ;
			updateList() ;
		} else if (id == R.id.navi6) {
			mDateIndex = 5 ;
			updateList() ;
		} else if (id == R.id.navi7) {
			mDateIndex = 6 ;
			updateList() ;
		}

	}

}



package cn.netin.parentalcontrol;

import java.util.List;

import com.hanceedu.common.App;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class GridAdapter extends BaseAdapter {

	private static final int[] bgs = {
		R.drawable.bg_icon0 ,
		R.drawable.bg_icon1 ,	
		R.drawable.bg_icon2 ,
		R.drawable.bg_icon3 ,
		R.drawable.bg_icon4 ,
		R.drawable.bg_icon5 ,
		R.drawable.bg_icon6 ,
		R.drawable.bg_icon7 ,
		R.drawable.bg_icon8 ,
		R.drawable.bg_icon9 ,
		R.drawable.bg_icon10
	} ;

	private Context mContext;

	private List<App> mAppList = null ;

	public GridAdapter(Context context) {
		mContext = context;
	}
	
	public void setData(List<App> appList) {
		mAppList = appList ;
		this.notifyDataSetChanged();
	}

	
	@Override
	public int getCount() {
		if (mAppList == null) {
			return 0;
		}
		return mAppList.size() ;
	}

	@Override
	public Object getItem(int position) {
		if (mAppList == null) {
			return null;
		}
		return mAppList.get(position) ;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		App app = (App) getItem(position) ;
		if (app == null) {
			return null ;
		}
		if (convertView == null) {
			convertView = new TextView(mContext);
		}
		TextView v = (TextView)convertView ;
		//v.setWidth(Constants.ICON_BG_SIZE);
		//v.setHeight(Constants.ICON_BG_SIZE);
		v.setTextColor(Color.WHITE);
		v.setTextSize(16);
		v.setPadding(10, 16, 12, 0);
		v.setGravity(Gravity.CENTER_HORIZONTAL);
		v.setCompoundDrawablePadding(20);
		v.setBackgroundResource(bgs[position % 11]);
		v.setText(app.getName());

		v.setCompoundDrawablesWithIntrinsicBounds( null, app.getDrawable(), null, null);

		
		return convertView;
	}


}

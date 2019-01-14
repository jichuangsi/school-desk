package cn.netin.parentalcontrol;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hanceedu.common.App;

public class AppAdapter extends BaseAdapter {

	Context mContext;

	private List<App> mAppList = null ;
	private int mSelected = -1 ;


	public AppAdapter(Context context) {
		mContext = context;
	}
	
	public void setData(List<App> appList) {
		mAppList = appList ;
		mSelected = -1 ;
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
		//v.setTextColor(Color.BLACK);
		v.setText(app.getName());
		v.setCompoundDrawablePadding(10);
		v.setPadding(10, 5, 3, 3);
		v.setTextSize(20);
		v.setCompoundDrawablesWithIntrinsicBounds(app.getSmallDrawable(), null, null, null);
        if (position == mSelected) {
        	v.setBackgroundColor(0xffff9702);
    	} else {
    		v.setBackgroundColor(Color.TRANSPARENT);
    	}
      
		
		return convertView;
	}

	
    public void select(int position) {

    	if (position == mSelected) {
    		mSelected = -1 ;
    	}else{
        	mSelected = position ;
    	}

    	notifyDataSetInvalidated() ;
    }
    
    public int getSelected() {
    	return mSelected ;
    }
    public void deselect() {
    	mSelected = -1 ;
    	this.notifyDataSetChanged() ;
    }
}

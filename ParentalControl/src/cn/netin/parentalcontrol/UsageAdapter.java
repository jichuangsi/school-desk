package cn.netin.parentalcontrol;

import java.util.List;

import com.hanceedu.common.App;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


public class UsageAdapter extends BaseAdapter {


	Context mContext;

	private List<App> mAppList = null ;

	private int mSelected = -1 ;


	public UsageAdapter(Context context) {
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
		LayoutInflater inflater = LayoutInflater.from(mContext);
		if (convertView == null) {
			convertView = (LinearLayout) inflater.inflate(R.layout.usage_item, null, false);
		}
		TextView v = (TextView)convertView.findViewById(R.id.appView) ;
		v.setText(app.getName());
		v.setCompoundDrawablePadding(10);
		v.setPadding(3, 3, 3, 3);
		v.setCompoundDrawablesWithIntrinsicBounds(app.getDrawable(), null, null, null);
		//v.setBackgroundColor(Color.TRANSPARENT);
		
		TextView barView = (TextView)convertView.findViewById(R.id.barView) ;
		int sum = app.getSum() ;
		int width = (int)(sum * 0.005) ;
		int parentWidth = parent.getWidth() ;
		//System.out.println( "parentWidth====================================" + parentWidth) ; 
		if (width > parentWidth - 250 ) {
			width = parentWidth - 300 ;
		}
		barView.setWidth(width);
		int minutes = sum / 60 ;
		String s = "" ;
		if (minutes < 60) {
			s = minutes + "m" ;
		}else{
			int hour = minutes / 60 ;
			minutes = minutes % 60 ;
			s = hour + "h" + minutes + "m" ;
		}
		TextView valueView = (TextView)convertView.findViewById(R.id.valueView) ;
		valueView.setText(s);
		
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

package cn.netin.parentalcontrol;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class UrlAdapter extends BaseAdapter {


	Context mContext;

	private List<String[]> mUrlList = null ;

	private int mSelected = -1 ;


	public UrlAdapter(Context context) {
		mContext = context;
	}
	
	public void setData(List<String[]> urlList) {
		mUrlList = urlList ;
		mSelected = -1 ;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (mUrlList == null) {
			return 0;
		}
		return mUrlList.size() ;
		
	}

	@Override
	public Object getItem(int position) {
		if (mUrlList == null) {
			return null;
		}
		return mUrlList.get(position) ;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		String[] nameUrl = (String[]) getItem(position) ;
		if (nameUrl == null) {
			return null ;
		}
		String url = nameUrl[1] ;
		LayoutInflater inflater = LayoutInflater.from(mContext);
		if (convertView == null) {
			convertView = (ViewGroup) inflater.inflate(R.layout.url_item, null, false);
		}
		TextView v = (TextView)convertView.findViewById(R.id.textView) ;
		v.setText(url);
	
		ImageButton button = (ImageButton)convertView.findViewById(R.id.trashButton) ;
		button.setTag(url);
		
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

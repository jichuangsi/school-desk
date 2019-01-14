package cn.netin.kidsbrowser;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


/***
 * 
 * 给书签UI提供数据源
 *
 */
public class UrlAdapter extends BaseAdapter {
	
	private static final String TAG = "EL UrlAdapter" ;
	private int[] resIds = {R.drawable.st_0 , R.drawable.st_1 ,R.drawable.st_2 ,R.drawable.st_3} ;


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
	

	private static String getSiteName(String url) {
		String name = url.replace("https://", "") ;
		name = name.replace("http://", "") ;
		int len = name.length() ;
		int pos = name.indexOf('.') ;
		if (pos == -1 || pos == 0 || pos == len -1) {
			return name ;
		}
		int pos2 = name.lastIndexOf('.') ;
		if (pos2 == pos) {
			return name.substring(0, pos) ;
		}
		if (pos2 == pos + 1) {
			return name ;
		}
		if (pos2 == len -1) {
			return name ;
		}
			
		name = name.substring(pos + 1, pos2 ) ;
		
		//System.out.println("name=" + name + " pos=" + pos + " pos2=" + pos2) ;
		return name;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		String[] nameUrl = (String[]) getItem(position) ;
		if (nameUrl == null) {
			return null ;
		}
		
		String url = nameUrl[1] ;
		String name = nameUrl[0];
		if (name == null || name.isEmpty()) {
			name = getSiteName(url);
		}
		
		LayoutInflater inflater = LayoutInflater.from(mContext);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.bookmark_item, null, false);
		}
		TextView v = (TextView)convertView ;
		v.setText(name);
		int n = position % 4 ;
		v.setBackgroundResource(resIds[n]);
		
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

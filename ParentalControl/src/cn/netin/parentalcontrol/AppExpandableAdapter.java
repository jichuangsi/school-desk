package cn.netin.parentalcontrol;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.hanceedu.common.App;
//import com.hanceedu.common.R;

public class AppExpandableAdapter extends BaseExpandableListAdapter {
	private static final String TAG = "EL AppExpandableAdapter" ; 
	private Context mContext;
	private List<App> mAppList = null ;
	private int mSelectedGroup = 0 ;
	private int mSelectedChild = -1 ;

	private static String[] mGroups = null ; 


	public AppExpandableAdapter(Context context) {
		mContext = context ;
		mGroups = context.getResources().getStringArray(Constants.GROUPS_ARRAY);
	}

	public void setData(List<App> appList) {
		if (appList == null) {
			Log.d(TAG, "AppExpandableAdapter setData appList == null" );
			return ;
		}
		//Log.d(TAG, "AppExpandableAdapter setData size=" + appList.size()  );
		mAppList = appList ;
		//mSelectedGroup = 0 ;
		mSelectedChild = -1 ;
		this.notifyDataSetChanged();
	}


	@Override
	public Object getChild(int groupPosition, int childPosition) {
		if (mAppList == null || mAppList.size() == 0) {
			Log.d(TAG, "getChild mAppList == null || mAppList.size() == 0 groupPosition=" + groupPosition) ;
			return null ;
		}
		//Log.d(TAG, "getChild: groupPosition=" + groupPosition + " childPosition="  + childPosition) ; 

		/** app是按group排序的 */
		int i = 0 ;
		for (App app : mAppList) {
			if (app.getParentId() == groupPosition) {
				if (childPosition == i) {
					return app ;
				}
				i++ ;
			}
		}

		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		//Log.d(TAG, "getChildId: groupPosition=" + groupPosition + " childPosition="  + childPosition) ; 
		return 0 ;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		//Log.i(TAG, "getChildView groupPosition:" + groupPosition + " childPosition=" + childPosition ) ;
		App app = (App)getChild( groupPosition, childPosition) ;
		if (app == null) {
			Log.i(TAG, "getChildView getChild app == null" ) ;
			return null ;
		}

		if (convertView == null) {
			convertView = new TextView(mContext);
		}
		TextView v = (TextView)convertView ;
		//v.setTextColor(Color.BLACK);
		v.setText(app.getName());
		v.setTextSize(18) ;
		v.setCompoundDrawablePadding(10);
		v.setPadding(50, 3, 3, 3);
		v.setCompoundDrawablesWithIntrinsicBounds(app.getSmallDrawable(), null, null, null);
		if (groupPosition == mSelectedGroup && childPosition == mSelectedChild) {
        	v.setBackgroundColor(0xffff9702);

		} else {
			v.setBackgroundColor(Color.TRANSPARENT);
		}

		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		//Log.d(TAG, "getChildrenCount groupPosition=" + groupPosition  );
		if (mAppList == null || mAppList.size() == 0) {
			Log.d(TAG, "getChildrenCount mAppList == null || mAppList.size() == 0 groupPosition=" + groupPosition) ;
			return 0 ;
		}
		int count = 0 ;
		for (App app : mAppList) {
			if (app.getParentId() == groupPosition) {
				count++ ;
			}
		}
		//Log.d(TAG, "getChildrenCount groupPosition=" + groupPosition + " count=" + count );
		return count;
	}

	@Override
	public Object getGroup(int groupPosition) {

		return mGroups[groupPosition] ;
	}

	@Override
	public int getGroupCount() {
		return mGroups.length;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {


		//Log.d(TAG, "getGroupView: " + groupPosition) ;
		String title = (String)getGroup( groupPosition) ;
		if (title == null) {
			return null ;
		}

		if (convertView == null) {
			convertView = new TextView(mContext);
		}
		TextView v = (TextView)convertView ;
		//v.setTextColor(Color.BLACK);
		v.setText(title);
		v.setTextSize(20) ;
		v.setPadding(60, 5, 5, 5);
		//Log.d(TAG, "groupPosition:" + groupPosition) ;
		if (groupPosition == mSelectedGroup) {
        	v.setBackgroundColor(0xffff9702);
		} else {
			v.setBackgroundColor(Color.TRANSPARENT);
		}

		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public void selectGroup(int position) {
		//Log.d(TAG, "selectGroup:" + position) ;
		mSelectedGroup = position ;
		this.notifyDataSetChanged() ;
	}
	public void selectChild(int position) {
		mSelectedChild = position ;
		this.notifyDataSetChanged() ;
	}
	public int getSelectedGroup() {
		return mSelectedGroup ;
	}
	public int getSelectedChild() {
		return mSelectedChild ;
	}


}

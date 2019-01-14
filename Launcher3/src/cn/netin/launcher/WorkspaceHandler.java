package cn.netin.launcher;

import java.util.ArrayList;
import java.util.List;

import com.hanceedu.common.App;
import com.hanceedu.common.util.AppUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import cn.netin.launcher.data.Constants;

public class WorkspaceHandler {
	private static final String TAG = "EL WorkspaceHandler" ;
	private static final int[] bgs = { R.drawable.bg_icon0, R.drawable.bg_icon1, R.drawable.bg_icon2,
			R.drawable.bg_icon3, R.drawable.bg_icon4, R.drawable.bg_icon5, R.drawable.bg_icon6, R.drawable.bg_icon7,
			R.drawable.bg_icon8, R.drawable.bg_icon9, R.drawable.bg_icon10, R.drawable.bg_icon11 };

	private Context mContext ;
	//最上层的的workspace第几屏，当打开子workspace时记录下来，便于返回
	private int mTopWorkspaceScreen = 0;
	//在workspace的第一层时，mGroupId=0, 在子workspace时，此值大于0
	private int mGroupId = 0;
	private boolean mKidsStyle = true ;
	private String mTitle = "" ;
	private List<View> mAppViewList = new ArrayList<View>();
	List<App> mAppList = null;
	private AppUtil mAppUtil = null;

	/** 桌面 */
	private Workspace mWorkspace = null;
	/** 桌面指示器 */
	private WorkspaceIndicator mIndicator = null;
	private TextView mCategoryView = null;
	/** 生成程序view的inflater */
	private LayoutInflater mInflater = null;
	private OnClickListener mOnClickListener = new OnClickListener();
	private WorkspaceListener mListener = null;

	public WorkspaceHandler(Context context){
		mContext = context ;
		SharedPreferences sf = context.getSharedPreferences(Constants.PREFERENCES, 0);  
		mKidsStyle = sf.getBoolean("KIDS_STYLE", true); 
		mInflater = LayoutInflater.from(context);// 实例化inflater
		registerReceiver() ;
		mAppUtil = new AppUtil() ;
	}

	public void setLisener(WorkspaceListener listener){
		mListener = listener ;
	}
	
	public void setAppList(List<App> list) {
		Log.d(TAG, "setAppList size=" + list.size()) ;
		mAppUtil.setAppList(list);
		showAppList(0) ;
	}
	
	public void setView(Workspace workspace, WorkspaceIndicator indicator, TextView categoryView ){
		mWorkspace = workspace ;
		mIndicator = indicator ;
		mCategoryView = categoryView ;
		if (Constants.SHOW_LABEL) {
			mIndicator.setShowLabels(true);
		}

		mWorkspace.setIndicator(mIndicator);// 设置桌面指示器
		// mIndicator.setOnClickListener(this);
		mIndicator.setGotoScreenListener(new WorkspaceIndicator.GotoScreenListener() {
			@Override
			public void gotoScreen(int screen) {
				// System.out.println("gotoScreen screen=" + screen);
				mWorkspace.snapToScreen(screen);
			}
		});
	}
	
	public void clear() {
		if (mWorkspace != null) {
			mWorkspace.clearWorksapce();
		}
	}
	
	public void reload(){
		addViewListToWorkspace(mAppViewList);
	}

	public void show(int screenId) {
		mWorkspace.setVisibility(View.VISIBLE);
		mIndicator.setVisibility(View.VISIBLE);
		if (screenId == 0) {
			mWorkspace.setToScreen(1);
		} else {
			mWorkspace.setToScreen(screenId - 1);
		}
		// Log.d(TAG, "snapToScreen " + screenId) ;
		mWorkspace.snapToScreen(screenId);
	}
	
	public void showTopScreen() {
		showAppList(0);
		mWorkspace.setToScreen(mTopWorkspaceScreen);
	}

	public void hide() {
		// 一定要用GONE。如果用INVISIBLE,动画依然显示
		mIndicator.setVisibility(View.GONE);
		mWorkspace.setVisibility(View.INVISIBLE);
	}
	
	//是否在子屏
	public boolean isInSubscreen(){
		return mGroupId > 0 ;
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Constants.ACTION_WORKSPACE_STYLE)) {
				mKidsStyle = intent.getBooleanExtra("STYLE", false) ;
				//Log.d(TAG, "style=" + style + " flag=" + flag) ;
				showAppList(0) ;

			}
		}        
	};


	private void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.ACTION_WORKSPACE_STYLE);
		mContext.registerReceiver(mReceiver, filter); 
	}

	// 按parentId显示
	private void showAppList(int groupId) {
		mGroupId = groupId ;
		if (groupId == 0) {
			// mBackButton.setVisibility(View.INVISIBLE);
			if (Constants.SHOW_LABEL) {
				mIndicator.setShowLabels(true);
			}
			mTitle = "" ;
		} else {
			// mBackButton.setVisibility(View.VISIBLE);
			mIndicator.setShowLabels(false);
		}
		List<App> appList = mAppUtil.getAppList(groupId);
		mAppViewList.clear();
		mWorkspace.clearWorksapce();
		if (appList == null || appList.isEmpty()) {		
			if (groupId == 0) {
				Log.e(TAG, "showAppList top appList is empty!") ;
				return ;
			}else{
				Toast.makeText(mContext, R.string.empty_category, Toast.LENGTH_SHORT).show(); 
				showAppList(0) ;
			}
			return;
		}
		
		//程序组的ParentId=0 GroupId=自编号，子程序的ParentId=GroupId, groupId=0
		//为什么不用id 作为groupId ? 为了轻易区分程序组， 程序组的groupId > 0

		//Log.d(TAG, "showAppList appList parentId=" + parentId + " count=" + appList.size()) ;
		for (int i = 0; i < appList.size(); i++) {
			App app = appList.get(i);
			//空的程序组不显示
			if (app.getGroupId() > 0) {
				//Log.d(TAG, "is group:" + app.getId()) ;
				if (!mAppUtil.hasChild(app.getGroupId())) {
					//Log.d(TAG, "no child:" + app.getId()) ;
					continue ;
				}else{
					//Log.d(TAG, "has child:" + app.getId()) ;
				}
			}
			if (app.getDrawable() != null) {
				View view = createItemView(app, i);
				mAppViewList.add(view);
			}else{
				//Log.e(TAG, "showAppList no drawable for " + app.getPkg() + " " + app.getCls()) ;
			}

		}
		addViewListToWorkspace(mAppViewList);

	}
	

	/**
	 * 把图标加入桌面
	 */
	private void addViewListToWorkspace(List<View> viewList) {
		mCategoryView.setText(mTitle);
		if (viewList == null) {
			Log.e(TAG, "addViewListToWorkspace viewList is null") ;
			return;
		}
		//Log.e(TAG, "addViewListToWorkspace count=" +  viewList.size()) ;
		for (int i = 0; i < viewList.size(); i++) {
			View v = viewList.get(i);
			if (Constants.GROUP_SCREEN) {
				App app = (App) v.getTag();
				mWorkspace.addInScreen(v, app.getScreen());
			} else {
				mWorkspace.addInScreen(v);
			}
		}
		mWorkspace.setToScreen(0);
	}


	/**
	 * 创建程序图标view
	 */
	private View createItemView(App app, int index) {

		// applications.xml 只有一个 com.hance.studyhome.BubbleTextView
		TextView itemView = (TextView) mInflater.inflate(R.layout.application, null, false);
		if (mKidsStyle){
			itemView.setBackgroundResource(bgs[index % 12]);
		}
		CharSequence title = app.getName();
		Drawable drawable = app.getDrawable();

		// todo: 是否需要缩放
		// img = Utilities.createIconThumbnail(img, this);//缩放图标
		// img = com.hanceedu.common.util.DrawableUtil.changeDrawableSize(this,
		// img, mIconSize, mIconSize) ;
		// System.out.println("$$$$$$$$$$$$$$$$ after width=" +
		// img.getIntrinsicWidth() + " height=" + img.getIntrinsicHeight());
		itemView.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
		itemView.setText(title);
		itemView.setTag(app);
		itemView.setOnClickListener(mOnClickListener);// 设置监听器

		return itemView;
	}


	class OnClickListener implements View.OnClickListener {
		public void onClick(View v) {
			App app = (App) v.getTag();
			int id = app.getId();
			if (app.getGroupId() > 0) {
				mTopWorkspaceScreen = mIndicator.getCurrentScreen();
				mTitle = app.getName() ;
				showAppList(app.getGroupId());
			} else {
				if (mListener != null){
					mListener.onItemClicked(id);
				}
			}
		}
	}
	
	public void release() {
		if (mWorkspace != null) {
			mWorkspace.release();
			mWorkspace = null;
		}
		mIndicator = null;
		mInflater = null;
		mOnClickListener = null;
		
		mContext.unregisterReceiver(mReceiver) ;
		mContext = null ;
	}

	public interface WorkspaceListener {
		void onItemClicked(int id) ;
	}
}

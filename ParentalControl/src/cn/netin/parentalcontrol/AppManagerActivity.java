package cn.netin.parentalcontrol;

import java.util.List;

import com.hanceedu.common.App;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class AppManagerActivity extends Activity {
	
	private static final String TAG = "EL AppManagerActivity" ;
	private AppAdapter mAppAdapter = null  ;
	private AppExpandableAdapter mExpandableAdapter = null  ;
	
	private AppData mAppData = null ;
	

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
        
        setContentView(R.layout.app_manager);
        
        mAppData = new AppData(this) ;
		List<App> appList = mAppData.getAvailableAppList() ;
		List<App> launcherAppList = mAppData.getRemovableLauncherAppList() ;
		
        final ListView listView = (ListView) findViewById(R.id.listView1) ;
        mAppAdapter = new AppAdapter(AppManagerActivity.this) ;
		mAppAdapter.setData(appList);
        listView.setAdapter(mAppAdapter);
        final TextView  infoView = (TextView) findViewById(R.id.infoView) ;

        listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Log.d(TAG, "onItemClick:" + position ) ;				
				mAppAdapter.select(position);  
				
				App app = (App)mAppAdapter.getItem(position) ;
				if (app != null) {
					infoView.setText(app.getPkg() + " / " +  app.getCls());
				}						
			}
        	
        });
        
        final ExpandableListView expandableListView = (ExpandableListView) findViewById(R.id.expandableListView1) ;
        mExpandableAdapter = new AppExpandableAdapter(AppManagerActivity.this) ;
        

        expandableListView.setAdapter(mExpandableAdapter);
        mExpandableAdapter.setData(launcherAppList);
        
        expandableListView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				mExpandableAdapter.selectGroup(groupPosition) ;
				return false;
			}
        	
        });

        expandableListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				mExpandableAdapter.selectGroup(groupPosition) ;
				mExpandableAdapter.selectChild(childPosition);
				return false;
			}
        	
        });
    }
    
    private void addApp() {
		int selected = mAppAdapter.getSelected() ;
		int selectedGroup = mExpandableAdapter.getSelectedGroup() ;
		if (selected == -1) {
			Toast.makeText(this, R.string.select_source,   Toast.LENGTH_LONG).show()  ;
			return ;
		}
		App app = (App)mAppAdapter.getItem(selected) ;
		if (app == null) {
			Toast.makeText(this, R.string.select_source,   Toast.LENGTH_LONG).show()  ;
			return ;
		}
		app.setParentId(selectedGroup);
		if (mAppData.addApp(app)) {
			Toast.makeText(this, R.string.add_ok,   Toast.LENGTH_SHORT).show()  ;
			List<App> launcherAppList = mAppData.getRemovableLauncherAppList() ;		
			Log.i(TAG, "addApp launcherAppList size:" + launcherAppList.size() ) ;
			mExpandableAdapter.setData(launcherAppList);
			
			List<App> appList = mAppData.getAvailableAppList() ;
			mAppAdapter.setData(appList);
			//mAppAdapter.deselect();
		}else{
			Toast.makeText(this, R.string.error,   Toast.LENGTH_LONG).show()  ;
		}
    }
    
    private void removeApp() {
		int selectedGroup = mExpandableAdapter.getSelectedGroup() ;
		int selectedChild = mExpandableAdapter.getSelectedChild() ;
		if (selectedChild == -1) {
			Toast.makeText(this, R.string.select_target,   Toast.LENGTH_LONG).show() ;
			return ;
		}
		App app = (App)mExpandableAdapter.getChild(selectedGroup, selectedChild) ;
		if (app == null) {
			Toast.makeText(this, R.string.select_target,   Toast.LENGTH_LONG).show() ;
			return ;
		}
		if (mAppData.removeApp(app)) {
			Toast.makeText(this, R.string.remove_ok,   Toast.LENGTH_SHORT).show()  ;
			List<App> launcherAppList = mAppData.getRemovableLauncherAppList() ;			
			mExpandableAdapter.setData(launcherAppList);
			
			List<App> appList = mAppData.getAvailableAppList() ;
			mAppAdapter.setData(appList);
			//mAppAdapter.deselect();
		}else{
			Toast.makeText(this, R.string.error,   Toast.LENGTH_LONG).show() ;
		}
    }

	public void handleClick(View source) {
		int id = source.getId();
		if (id == R.id.bt_add) {
			addApp() ;
		} else if (id == R.id.bt_remove) {
			removeApp() ;
		} else if (id == R.id.closeButton) {
			this.finish();
		}
	}
    
}
package cn.netin.launcher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import cn.netin.launcher.data.LauncherState ;

public class BlockActivity extends Activity {
	
	private static final String TAG = "EL BlockActivity" ;
		
    @Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy") ;
		LauncherState.setBlocking(false) ;
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume") ;
		LauncherState.setBlocking(true) ;
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG, "onPause") ;
		LauncherState.setBlocking(false) ;
	}

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_warning);
        
        if (LauncherState.isCurrentUserOwner(this)) {
        	findViewById(R.id.switch_owner).setVisibility(View.GONE);
        }
        Log.d(TAG, "onCreate") ;
        LauncherState.setBlocking(true) ;
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        finish();
        return;
    }
}
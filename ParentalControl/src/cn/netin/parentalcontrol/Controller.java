package cn.netin.parentalcontrol;

import java.util.List;
import java.util.Locale;

import com.hanceedu.common.App;
import com.hanceedu.common.HanceApplication;
import com.hanceedu.common.data.DataInterface.DataListener;
import com.hanceedu.common.data.DataInterface.Param;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import cn.netin.launcher.service.ServiceContract;


public class Controller {

	private static final String TAG = "EL Controller" ;

	private HanceApplication mApp ;   
	private MainUiData mData ;
	private List<App> mAppList ;
	private Context mContext ;


	public  Controller(Context context) {
		mContext = context ;
		mApp = ((HanceApplication)context.getApplicationContext()) ;

		MyHandler handler = new MyHandler() ;
		mApp.setHandler(Constants.HANDLER_KEY, handler) ;

		mData = new MainUiData(context) ;
		mData.setListener(new DataListener() {
			@Override
			public void onData(Param param) {
				handleData(param); 

			}
		});
		getItemList() ;


		//启动应用统计服务
		Intent serviceIntent = new Intent() ;
		serviceIntent.setAction(ServiceContract.SERVICE_APPSTATSERVICE ) ;
		serviceIntent.setPackage(ServiceContract.SERVICE_PKG);
		context.startService(serviceIntent) ;	

	}


	private void getItemList() {
		Param param = new Param(Constants.MSG_GET_ITEM_LIST) ;
		param.key = Constants.KEY_ALL_ITEM_LIST ;
		Locale locale = mContext.getResources().getConfiguration().locale ;
		if (locale.equals(Locale.CHINA) || locale.equals(Locale.CHINESE)) {
			param.s0 = "launcher-zh.xml" ;	
		}else{
			param.s0 = "launcher.xml" ;			
		}
		Log.d(TAG, "getItemList param.s0" + param.s0 ) ;
		param.s1 = "UTF-8" ;
		mData.prepare(param) ;
	}



	@SuppressWarnings("unchecked")
	private void handleData(Param param) {	

		if (param.i0 == 0) {
			Log.d(TAG, "handleData param.i0 == 0");
			return ;
		}
		mAppList =  (List<App>) mApp.getAppData(param.key);
		Log.d(TAG, "mAppList size=" + mAppList.size()) ;
		Intent intent = new Intent(mContext, MainActivity.class) ;
		intent.putExtra(Constants.INTENT_ID, Constants.MSG_GET_ITEM_LIST) ;
		intent.putExtra(Constants.INTENT_DATA_KEY, param.key) ;
		mContext.startActivity(intent) ;		
	}

	/**
	 * 安全启动程序
	 * @param intent
	 */
	private void startActivitySafely(Intent intent) {

		try {
			mContext.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			//Toast.makeText(this, R.string.activity_not_found ,Toast.LENGTH_SHORT).show();
			Log.e(TAG, "miss: " + intent.getComponent().getPackageName() + " / " + intent.getComponent().getClassName());
			Toast.makeText(mContext, R.string.error,Toast.LENGTH_SHORT).show();
		}

	}

	class MyHandler extends Handler {
		public MyHandler() {
		}

		public MyHandler(Looper L) {
			super(L);
		}

		// 子类必须重写此方法,接受数据
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			Intent intent ;
			
			switch (msg.what) {

			case Constants.MSG_LANG_CHANGED :
				getItemList() ;
				break;	
			case Constants.MSG_TEM_CLICKED :
				handleItemClick( msg.arg1) ;
				break;	
			case Constants.MSG_QUIT :
				intent = new Intent(mContext, MainActivity.class) ;
				intent.putExtra(Constants.INTENT_ID, Constants.MSG_QUIT) ;
				mContext.startActivity(intent) ;	
				break;	
			case Constants.MSG_VERIFIED :
				intent = new Intent(mContext, MainActivity.class) ;
				intent.putExtra(Constants.INTENT_ID, Constants.MSG_VERIFIED) ;
				mContext.startActivity(intent) ;
				break;	
			}
		}
	}

	private void handleItemClick(int index) {
		App app = mAppList.get(index) ;
		String pkg = app.getPkg() ;
		String cls = app.getCls() ;
		Intent intent = new Intent();
		//intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);	
		intent.putExtra("title", app.getName());
		intent.putExtra("param", app.getData());
		//intent.putExtra("id", item.getId());	
		intent.setComponent(new ComponentName(pkg, cls)) ;
		startActivitySafely(intent);//启动程序			

	}


	public void release(){
		mData.release() ;
		mData =  null ;
		mApp.release() ;
		mApp = null ;
		mAppList = null ;
	}

}


package cn.netin.parentalcontrol;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.hanceedu.common.App;
import com.hanceedu.common.HanceApplication;
import com.hanceedu.common.data.DataInterface;
import com.hanceedu.common.util.AppParser;
import com.hanceedu.common.util.DrawableUtil;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;

public class MainUiData implements DataInterface {
	private static final String TAG = "EL Main Data" ;
	//private static final String FOLDER = "72/" ;
	private static final String FOLDER = "" ;
	private Context mContext ;
	private HanceApplication mApp ;
	private DataListener mListener ;
	private static int sIconSize = 36 ;

	public MainUiData(Context context) {
		mContext = context ;
		mApp = (HanceApplication) context.getApplicationContext() ;
		
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		float density = dm.density ;
		sIconSize = (int) (Constants.MAIN_ICON_SIZE * density) ;
	}


	private class DataTask extends AsyncTask<Param, Integer,  Param> {

		@SuppressWarnings("deprecation")
		@Override
		protected Param doInBackground(Param... params) {

			Param param = params[0] ;
			//Log.i("LauncherData", "doInBackground...") ;

			if (param.msgId == Constants.MSG_GET_ITEM_LIST) {
				int total ;
				List<App> appList = null ;

				
				InputStream in = null;
				try {
					in = mContext.getAssets().open(param.s0);
				} catch (IOException e) {
					Log.e(TAG, "getAssets open fail:" + param.s0) ;
					in = null ;
				}
				if (in == null) {
					param.i0 = 0 ;
					return param ;
				}
				appList = AppParser.parse(in, param.s1);
				total = appList.size() ;
				Resources res = mContext.getResources() ;

				Drawable drawable ;
				for (App app : appList) {
					String icon = app.getIcon() ;

					if(icon != null && !icon.equals("")){
						icon = FOLDER + icon ;
						//从Assets中找Icon图标。
						try {
							InputStream is = res.getAssets().open(icon);
							drawable = Drawable.createFromStream(is, null);
						} catch (IOException e) {
							//e.printStackTrace();
							Log.e(TAG, "createFromStream fail ");
							//如果找不到，就用默认图标代替
							drawable = res.getDrawable(R.drawable.ic_launcher);
						}
						drawable = DrawableUtil.resizeDrawable( mContext, drawable, sIconSize, sIconSize) ;						
						//Log.d(TAG, "icon=" + icon) ;
						app.setDrawable(drawable);
					}

				}//for

				param.i0 = total ;
				if (total > 0) {
					mApp.putAppData(param.key, appList) ;
				}	
			}

			return param ;

		}

		@Override
		protected void onPostExecute( Param param) {	
			mListener.onData(param) ;
		}

	}

	@Override
	public Param execute(Param param) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean prepare(Param param) {
		new DataTask().execute(param) ;
		return true;
	}


	@Override
	public void setListener(DataListener dataListener) {
		mListener = dataListener ;
	}

	public void release() {
		mContext = null ;
		mApp = null ;
		mListener = null ;

	}

}

package cn.netin.launcher.receiver;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;



public  class WallpaperIntentReceiver extends BroadcastReceiver {
	private static final String TAG = "EL WallpaperIntentReceiver" ;
	private final Application mApplication;
	//private WeakReference<WallpaperIntentReceiverListener> mListener = null ;
	WallpaperIntentListener mListener = null ;
	public interface WallpaperIntentListener {
		void onWallpaper(Bitmap bitmap) ;
	}

	public WallpaperIntentReceiver(Application application, WallpaperIntentListener listener) {
		mApplication = application;
		//mListener = new WeakReference<WallpaperIntentReceiverListener>(listener);
		mListener = listener ;
		IntentFilter filter = new IntentFilter(Intent.ACTION_WALLPAPER_CHANGED);
		application.registerReceiver(this, filter);			
		Log.e(TAG, "WallpaperIntentReceiver : registerReceiver ") ;
		
		@SuppressWarnings("deprecation")
		String s = Intent.ACTION_WALLPAPER_CHANGED ;

	}


	@Override
	public void onReceive(Context context, Intent intent) {
		// Load the wallpaper from the ApplicationContext and store it locally
		// until the Launcher Activity is ready to use it
		final Drawable drawable = mApplication.getWallpaper();

		Log.e(TAG, "WallpaperIntentReceiver : onReceive ") ;
		if (drawable instanceof BitmapDrawable) {
			Bitmap wallpaperBitmap = ((BitmapDrawable) drawable).getBitmap();
			// If Launcher is alive, notify we have a new wallpaper
			if (mListener != null) {
				mListener.onWallpaper(wallpaperBitmap);
			}else{
				Log.e(TAG, "WallpaperIntentReceiver : mListener is null ") ;
			}
			/*
			if (mListener != null) {
				final WallpaperIntentReceiverListener listener = mListener.get();
				if (listener != null) {
					listener.onWallpaper(wallpaperBitmap);
				}else{
					Log.e(TAG, "WallpaperIntentReceiver : listener is null ") ;
				}
			}else{
				Log.e(TAG, "WallpaperIntentReceiver : mListener is null ") ;
			}
			 */
		} else {
			//throw new IllegalStateException("The wallpaper must be a BitmapDrawable.");
			Log.e(TAG, "WallpaperIntentReceiver : The wallpaper must be a BitmapDrawable.") ;
		}

	}
	
	public void release() {
		mApplication.unregisterReceiver(this);  
	}
}


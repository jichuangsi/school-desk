/**
 * 
 */
package cn.netin.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.hanceedu.common.util.DrawableUtil;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import cn.netin.launcher.data.Constants;

/**
 * @author mac
 *
 */
public class Avatar {

	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;

	private static final String TAG = "EL Avatar" ;
	private static final String AVATAR_FILE = "avatar" ;
	//public static final int AVATAR_SIZE = 96 ;
	private static int sAvatarSize = 0 ;

	private static int getAvatarSize(Context context) {
		if (sAvatarSize > 0) {
			return sAvatarSize ;
		}
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getResources().getDisplayMetrics();  
		sAvatarSize = (int)(dm.density * 64);  
		return sAvatarSize ;
	}

	public static Bitmap getDefault(Context context, boolean canBeNull) {
		BitmapDrawable drawable = null ;
		int asize = getAvatarSize( context) ;
		try {
			FileInputStream fis = context.openFileInput(AVATAR_FILE) ;
			drawable = (BitmapDrawable) BitmapDrawable.createFromStream(fis, AVATAR_FILE) ;
			if (drawable != null) {
				//return drawable.getBitmap() ;
				Bitmap bitmap = DrawableUtil.toRoundBitmap(drawable.getBitmap()) ; 
				bitmap =  Bitmap.createScaledBitmap(bitmap, asize, asize,true);
				return bitmap ;
			}else{
				Log.e(TAG, "avatar drawable is null") ;
				if (canBeNull) {
					return null ;
				}
			}

		} catch (FileNotFoundException e) {
			//Log.d(TAG, "getDefault FileNotFoundException") ;
			//e.printStackTrace();
			if (canBeNull) {
				return null ;
			}
			Log.e(TAG, "avatar file not exists") ;
		}

		drawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.avatar_0) ;
		Bitmap bitmap = DrawableUtil.toRoundBitmap(drawable.getBitmap()) ; 
		bitmap =  Bitmap.createScaledBitmap(bitmap, asize, asize,true);

		return bitmap ;
	}
	

	public static boolean saveDefault(Context context, BitmapDrawable drawable) {
			
		Bitmap bitmap = getAvatar(drawable, context) ;		
		try {
			FileOutputStream fos = context.openFileOutput(AVATAR_FILE, Context.MODE_PRIVATE) ;
			bitmap.compress(CompressFormat.PNG, 100, fos) ;
			try {
				fos.flush();
				fos.close();
			} catch (IOException e1) {
				Log.d(TAG, "saveDefault IOException") ;
				return false ;
			}
		} catch (FileNotFoundException e) {
			Log.d(TAG, "saveDefault FileNotFoundException") ;
			return false ;
		}

		return true ;
	}

	public static Bitmap getAvatar(Context context, int resId) {
		BitmapDrawable drawable = null ;
		drawable = (BitmapDrawable) context.getResources().getDrawable(resId) ;
		Bitmap bitmap = DrawableUtil.toRoundBitmap(drawable.getBitmap()) ; 
		int size = getAvatarSize( context) ;
		bitmap =  Bitmap.createScaledBitmap(bitmap, size, size,true);

		return bitmap ;
	}

	public static Bitmap getAvatar(BitmapDrawable drawable, Context context) {
		Bitmap bitmap = DrawableUtil.toRoundBitmap(drawable.getBitmap()) ; 
		int size = getAvatarSize( context) ;
		bitmap =  Bitmap.createScaledBitmap(bitmap, size, size,true);
		Log.d(TAG, "getAvatar scaled size=" + size + " w=" + bitmap.getWidth() + " h=" + + bitmap.getHeight()) ;	
		
		return bitmap ;
	}

	public static Bitmap getAvatar(String path, Context context) {
		Bitmap bitmap = DrawableUtil.scaleDownFile(path, 256, 256) ;
		if (bitmap == null) {
			Log.e(TAG, "getAvatar path bitmap == null" ) ;
			return null ;
		}
		Log.d(TAG, "scaleDown w=" + bitmap.getWidth() + " h=" + bitmap.getHeight()) ;
		bitmap = DrawableUtil.toRoundBitmap(bitmap) ; 
		int size = getAvatarSize( context) ;
		bitmap =  Bitmap.createScaledBitmap(bitmap, size, size,true);

		return bitmap ;
	}

	public static String getName(Context context) {
		SharedPreferences shared = context.getSharedPreferences(Constants.PREFERENCES,Context.MODE_PRIVATE);
		String name = shared.getString("AVATAR_NAME", "Hello") ;
		//Log.d(TAG, "getName: " + name) ;
		if (name.isEmpty()) {
			context.getResources().getString(R.string.set_avatar); 
		}

		return name ;
	}

	public static void saveName(Context context, String name, boolean logoAsAvatar) {
		SharedPreferences shared = context.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
		Editor editor = shared.edit() ;
		//Log.d(TAG, "saveName: " + name) ;
		editor.putString("AVATAR_NAME", name) ;
		editor.putBoolean("LOGO_AVATAR", logoAsAvatar) ;	
		editor.commit() ;
		//String text = shared.getString("name", "Hello") ;
		//Log.d(TAG, "getName after save: " + text) ;
	}

	/** Create a file Uri for saving an image or video */
	public static Uri getOutputMediaFileUri(int type){
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type){
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES), "Avatar");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (! mediaStorageDir.exists()){
			if (! mediaStorageDir.mkdirs()){
				Log.d(TAG, "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE){
			mediaFile = new File(mediaStorageDir.getPath() + File.separator +
					"IMG_"+ timeStamp + ".jpg");
		} else if(type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator +
					"VID_"+ timeStamp + ".mp4");
		} else {
			return null;
		}

		return mediaFile;
	}





}




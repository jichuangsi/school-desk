package com.hanceedu.common.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

import com.hanceedu.common.R;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
/**
 * Drawable工具类
 * @author Iron
 * @date 2011-9-23
 */
public class DrawableUtil
{
	private static final String TAG = "EL DrawableUtil" ;

	/**
	 * 
	 * @param context
	 * @param px
	 * @return
	 */
	public static float px2dip(Context context, float px)
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (px - 0.5f) / scale;
	}

	/**
	 * 
	 * @param context
	 * @param dip
	 * @return
	 */
	public static float dip2px(Context context, float dip)
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (dip * scale + 0.5f);
	}

	/**
	 * 通过图片名找得到该图片在android系统中对应的id
	 * 
	 * @param pic
	 *            需要查找的图片名称(不需要后缀名)
	 * @return R中存储的改图片的id
	 */
	public static int getImageId(String pic) {
		if (pic == null || pic.trim().equals("")) {
			return -1;
		}
		Class<?> draw = R.drawable.class;
		try {
			Field field = draw.getDeclaredField(pic);
			return field.getInt(pic);
		} catch (SecurityException e) {
			return -1;
		} catch (NoSuchFieldException e) {
			return -1;
		} catch (IllegalArgumentException e) {
			return -1;
		} catch (IllegalAccessException e) {
			return -1;
		}
	}



	/**
	 * 放大缩小图片
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidht = ((float) w / width);
		float scaleHeight = ((float) h / height);
		matrix.postScale(scaleWidht, scaleHeight);//设置缩放后大小
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h,
				matrix, true);
		return newbmp;
	}


	/**
	 * 裁剪指定区域的Drawable对象并返回
	 * 
	 * @return 裁剪后的Drawable对象
	 */
	public static Drawable clipDrawable(Drawable drawable, int startX,
			int startY, int width, int height) {
		Bitmap bitmap = drawableToBitmap(drawable);

		Bitmap bitmap2 = Bitmap.createBitmap(bitmap, startX, startY, width, height);
		if(!bitmap.isRecycled()) {
			bitmap.recycle();

		}
		return new BitmapDrawable(bitmap2);
	}

	/**
	 * 改变Drawable的尺寸大小
	 */
	public static Drawable resizeDrawable(Context context,
			Drawable drawable, int destWidth, int destHeight) {

		return resizeDrawable( context,
				drawable,  destWidth,  destHeight, 0) ;
	}


	/**
	 * 改变Drawable的尺寸大小
	 */
	public static Drawable resizeDrawable(Context context,
			Drawable drawable, int destWidth, int destHeight,int top) {
		
		if (context == null) {
			//Log.e(TAG, "resizeDrawable context is null") ;
			return  null ;
		}
		
		if (drawable == null)
			return null;

		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		final float ratio = (float) width / height;
		if (width > height) {
			destHeight = (int) (destWidth / ratio);
		} else if (height > width) {
			destWidth = (int) (destHeight * ratio);
		}
		Bitmap bitmap = Bitmap.createBitmap(destWidth, destHeight, drawable
				.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
						: Bitmap.Config.RGB_565);

		Canvas canvas = new Canvas(bitmap);
		canvas.setDrawFilter(new PaintFlagsDrawFilter(Paint.DITHER_FLAG, 0));
		drawable.setBounds(0, top, destWidth, destHeight);
		drawable.draw(canvas);
		drawable =null;
		return new BitmapDrawable(context.getResources(), bitmap);

	}

	/*********************图形缩放**************************/
	public static Bitmap imageScale(Bitmap bitmap, int dst_w, int dst_h){

		int  src_w = bitmap.getWidth();
		int  src_h = bitmap.getHeight();
		float scale_w = ((float)dst_w)/src_w;
		float  scale_h = ((float)dst_h)/src_h;
		Matrix  matrix = new Matrix();
		matrix.postScale(scale_w, scale_h);
		Bitmap dstbmp = Bitmap.createBitmap(bitmap, 0, 0, src_w, src_h, matrix, true);

		return dstbmp;
	}

	/*********************Drawable转Bitmap************************/
	public static Bitmap drawabletoBitmap(Drawable drawable){

		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicWidth();

		Bitmap bitmap = Bitmap.createBitmap(width, height, drawable.getOpacity() != PixelFormat.OPAQUE ?
				Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);

		drawable.draw(canvas);

		return bitmap;
	}

	public static Drawable zoomDrawable(Drawable d,int w,int h){
		Bitmap b = drawabletoBitmap(d);

		b = imageScale(b, w, h);

		return new BitmapDrawable(b);
	}

	public static void   saveBitmapAsJpg(Bitmap bitmap, String path) {

		try {
			FileOutputStream fos = new FileOutputStream(path);

			if(bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos)){
				fos.flush();
				fos.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


	public static void saveBitmap(Bitmap bitmap, String path)  {
		File file = new File(path);
		FileOutputStream out;
		try {
			out = new FileOutputStream(file);
			if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
				out.flush();
				out.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public static Bitmap drawableCoverColor(Context mContext, Bitmap bitmap,
			int color) {
		final int Color_RED = 0x55FF0000;
		if (color == 0)
			color = Color_RED;
		BitmapDrawable bitmapDrawable1 = new BitmapDrawable(bitmap);
		Bitmap bitmap1 = bitmapDrawable1.getBitmap();
		BitmapDrawable bitmapDrawable2 = new BitmapDrawable(bitmap);
		Bitmap bitmap2 = bitmapDrawable2.getBitmap();
		Canvas canvas = new Canvas(bitmap);
		// 画个白色底部，避免影响
		ColorFilter cf = new PorterDuffColorFilter(0xFFFFFFFF,
				PorterDuff.Mode.MULTIPLY);
		Paint p = new Paint();
		p.setColorFilter(cf);
		canvas.drawBitmap(bitmap2, 0, 0, p);
		// 将黑色邊緣阴影覆盖掉
		ColorFilter cf1 = new PorterDuffColorFilter(color,
				PorterDuff.Mode.SRC_IN);
		Paint p1 = new Paint();
		// p1.setStyle(Paint.Style.STROKE);
		// p1.setStrokeWidth(10);
		p1.setAntiAlias(false);
		p1.setColorFilter(cf1);
		canvas.drawBitmap(bitmap1, 0, 0, p1);
		// drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
		// drawable.getIntrinsicHeight());
		// drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
		// drawable.draw(canvas);
		// 为图片画上颜色
		ColorFilter cf2 = new PorterDuffColorFilter(color,
				PorterDuff.Mode.MULTIPLY);
		Paint p2 = new Paint();
		// p1.setStyle(Paint.Style.STROKE);
		// p1.setStrokeWidth(10);
		p2.setAntiAlias(false);
		p2.setColorFilter(cf2);
		canvas.drawBitmap(bitmap2, 0, 0, p2);
		return bitmap;
	}



	/**
	 * 字节数组转成Bitmap
	 * @param temp
	 * @return
	 */
	public static Bitmap bytesToBitmap(byte[] temp) {
		Bitmap bitmap=null;
		if (temp != null) {
			bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);

		}
		return bitmap;
	}

	/**
	 * bitmap转成字节数组
	 * @param bitmap
	 * @return
	 */
	public static  byte[] bitmapToBytes(Bitmap bitmap) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
		try {
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toByteArray();
	}


	/**
	 * Drawable转成Bitmap
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {
		BitmapDrawable b= (BitmapDrawable)drawable;
		return b.getBitmap();  
	}


	/* TODO
	 * 将Drawable转化为Bitmap

	public static Bitmap drawableToBitmap(Drawable drawable) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, drawable
				.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;
	}

	 */


	/**
	 * Bitmap转成drawable
	 * @param bitmap
	 * @return
	 */
	public static Drawable bitmapToDrawable(Bitmap bitmap){
		Drawable d = new BitmapDrawable(bitmap);
		d.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
		return d;
	}

	/**
	 * Drawable转成 字节数组
	 * @param drawable
	 * @return
	 */
	public static byte[] drawableToBytes(Drawable drawable){
		Bitmap bitmap = drawableToBitmap(drawable);
		return bitmapToBytes(bitmap);
	}


	/**
	 * 字节数组转成Drawable
	 * @param bytes
	 * @return
	 */
	public static Drawable bytesToDrawable(byte[] bytes){
		Bitmap bitmap = bytesToBitmap(bytes);
		return bitmapToDrawable(bitmap);
	}


	/**
	 * 改变bitmap的大小
	 * @param bitmap
	 * @return
	 */
	public static Bitmap scaledBitmap(Bitmap bitmap,int width,int height){
		if(bitmap!=null){
			// 缩放裁剪图片
			bitmap = Bitmap.createScaledBitmap(bitmap, width,height,true);
		}
		return bitmap;
	}



	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left,top,right,bottom,dst_left,dst_top,dst_right,dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			top = 0;
			bottom = width;
			left = 0;
			right = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}

		Bitmap output = Bitmap.createBitmap(width,
				height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int)left, (int)top, (int)right, (int)bottom);
		final Rect dst = new Rect((int)dst_left, (int)dst_top, (int)dst_right, (int)dst_bottom);
		final RectF rectF = new RectF(dst);

		paint.setAntiAlias(true);

		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);
		return output;
	}


	public static Bitmap scaleDownFile(String path, int targetW, int targetH){

		// Get the dimensions of the bitmap
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		// Determine how much to scale down the image
		int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

		// Decode the image file into a Bitmap sized to fill the View
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);

		return bitmap ;
	}


	//获取默认密度的应用图标
	public static Drawable  getIconDrawable(Context context, String pkg, String cls, int defaultIcon){
		Drawable drawable = null ;
		PackageManager pm = context.getPackageManager() ;
		
		try {
			if (cls != null && !cls.isEmpty()) {
				drawable = pm.getActivityIcon(new ComponentName(pkg, cls)) ;
				if (drawable == null) {
					drawable = pm.getApplicationIcon(pkg) ;
				}
			}else{
				drawable = pm.getApplicationIcon(pkg) ;
			}			
		} catch (NameNotFoundException e) {
			Log.e(TAG, "getIconDrawable fail: " + pkg + " / " + cls);
		}		
		if (drawable == null && defaultIcon != 0) {
			drawable = context.getResources().getDrawable(defaultIcon);
		}		
		return drawable ;

	}

	public static Drawable getLargeIconDrawable(Context context, ActivityInfo info) {	
		int iconId = info.getIconResource();
		if (iconId == 0) {
			return null ;
		}
		return getLargeIconDrawable(context, info.applicationInfo, iconId) ;
	}
	

	private static int getLauncherLargeIconDensity(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		int dpi = activityManager.getLauncherLargeIconDensity();
		//Log.d(TAG, "getLauncherLargeIconDensity=" + dpi);
		return dpi;
	}

	@SuppressWarnings("deprecation")
	private static Drawable getLargeIconDrawable(Context context, ApplicationInfo appInfo, int iconId){
		PackageManager pm = context.getPackageManager() ;
		Resources resources = null;
		// 获得应用资源
		try {
			resources = pm.getResourcesForApplication(appInfo);
		} catch (NameNotFoundException e) {
			Log.e(TAG, "getLargeIconDrawable getResourcesForApplication fail");
			return null;
		}
		int density = getLauncherLargeIconDensity(context) ;
		Drawable drawable = null ;
		try{
			drawable = resources.getDrawableForDensity(iconId, density) ;
		}catch (NotFoundException e){		
		}
		return drawable ;
	}

	public static Drawable getLargeIconDrawable(Context context, String pkg,  String cls) {
		if (context == null) {
			//Log.e(TAG, "getLargeIconDrawable context is null") ;
			return  null ;
		}
		PackageManager pm = context.getPackageManager() ;
		ActivityInfo actInfo = null;
		ApplicationInfo appInfo = null;
		int iconId = 0;
		// 取ActivityInfo
		if (cls != null && !cls.isEmpty()) {
			try {
				actInfo = pm.getActivityInfo(new ComponentName(pkg, cls), 0);	
				appInfo = actInfo.applicationInfo;
			} catch (NameNotFoundException e1) {
				//Log.e(TAG, "getLargeIconDrawable invalid Activity : " + pkg + " / " + cls) ;
				try {
					appInfo = pm.getApplicationInfo(pkg, 0) ;
				} catch (NameNotFoundException e) {
					//Log.e(TAG, "getLargeIconDrawable invalid package : " + pkg ) ;
					return null ;
				}
			}		
		}else{
			try {
				appInfo = pm.getApplicationInfo(pkg, 0) ;
			} catch (NameNotFoundException e) {
				//Log.e(TAG, "getLargeIconDrawable invalid package : " + pkg ) ;
				return null ;
			}
		}

		// 取Activity图标
		if (actInfo != null) {
			//会返回activity或者package的图标
			iconId = actInfo.getIconResource();
		}
		// 如果没有actInfo
		if (iconId == 0) {
			iconId = appInfo.icon;
			//不大可能
			if (iconId == 0) {
				Log.e(TAG, "getLargeIconDrawable appInfo iconId == 0");
				return null;
			}
		}
		return getLargeIconDrawable(context, appInfo, iconId) ;
	}



}

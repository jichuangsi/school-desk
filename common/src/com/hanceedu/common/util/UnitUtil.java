package com.hanceedu.common.util;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * 单位转换类
 * @author QiJintang
 *
 */
public class UnitUtil {

	/**
	 * px单位转成dip
	 * @param context
	 * @param px
	 * @return
	 */
	public static float px2dip(Context context, int px) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (px - 0.5f) / scale;
	}
	
	/**
	 * dip单位值转成px单位值
	 * @param dip dip单位的值
	 * @return px单位的值
	 */
	public static float dip2px(Context context, int dip) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (dip * scale + 0.5f);
	}
	

	public static String getDisplayMetrics(Context context) {

		StringBuffer buf = new StringBuffer();
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		int screenWidth = dm.widthPixels;
		int screenHeight = dm.heightPixels;
		float density = dm.density;
		float densityDpi = dm.densityDpi ;
		buf.append("screenWidth: " + String.valueOf(screenWidth) + "\n" );
		buf.append("screenHeight: " + String.valueOf(screenHeight) + "\n") ;
		buf.append("density: " + String.valueOf(density)+ "\n");
		buf.append("densityDpi :" + String.valueOf(densityDpi) + "\n" );

		return buf.toString();

	}
	
}

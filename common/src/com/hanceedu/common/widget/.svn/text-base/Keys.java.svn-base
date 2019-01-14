package com.hanceedu.common.widget;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.hanceedu.common.util.DrawableUtil;
import com.hanceedu.common.util.KeyInfo;
import com.hanceedu.common.util.PullParesXml;

/**
 * 虚拟键盘的按键对象
 * 
 * 该对象主要负责实现按键对象的点击效果实现以及提供外部接口实现该按键点击的功能实现
 * 
 * @author ben.upsilon@gmail.com
 * @date 2011-9-20
 * @~ hance.page//Keyboard
 */
public class Keys extends View {
	/**
	 * key 的点击事件回调接口
	 */
	public interface OnClickListener {
		void onClick(String ch);
	}

	/** 画布对象用于绘制按键的高亮效果 */
	private Canvas mCanvas;

	private Bitmap bitmap;
	
	/** xml解析器 */
	private PullParesXml xml;

	/** 存储按钮高亮图片信息 */
	private Map<String, Bitmap> maps = new HashMap<String, Bitmap>();

	private OnClickListener clickListener;

	private static final String LOG_TAG = Keys.class.getName();

	// 类构造器
	public Keys(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public Keys(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	// 类初始化方法
	void init(Context context) {
		setPressed(true);
		setClickable(true);
		// 在初始化时解析xml获取该键盘的显示信息
		xml = new PullParesXml(context);
		List<KeyInfo> list = xml.parse();
		for (KeyInfo keyInfo : list) {
			maps.put(keyInfo.getSourceId(), BitmapFactory.decodeResource(
					getResources(), DrawableUtil.getImageId(keyInfo.getSourceId())));
		}
		// 在该层准备一张空的图片用于显示键盘
		bitmap = Bitmap.createBitmap(506, 102, Config.ARGB_8888);
		mCanvas = new Canvas(bitmap);
	}

	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(bitmap, 0, 0, null);
	}

	public void setOnClickListener(OnClickListener listener) {
		this.clickListener = listener;
	}

	// 为该键盘绑定点击事件
	public boolean onTouchEvent(MotionEvent event) {
		Log.d(LOG_TAG, event.toString());
		switch (event.getAction()) {

		case MotionEvent.ACTION_UP:
			bitmap = Bitmap.createBitmap(506, 102, Config.ARGB_8888);
			mCanvas = new Canvas(bitmap);
			invalidate();
			break;
		case MotionEvent.ACTION_DOWN:
			// 通过按下时的坐标查找按钮对象信息
			KeyInfo info = getKeyInfoByXY(event.getX(), event.getY());
			if (null != info) {
				Log.d(LOG_TAG, maps.get(info.getSourceId()).toString());
				mCanvas.drawBitmap(
						maps.get(info.getSourceId()),
						null,
						new RectF(info.getLeft(), info.getTop(), info
								.getRight(), info.getBottom()), null);
				clickListener.onClick(info.getKeyId());
			}
			invalidate();
			break;
		}

		return true;
	}

	/**
	 * 通过点击时的坐标判断该坐标范围点击到的是否是按钮,是什么按钮
	 * @param x 点击时的x坐标值
	 * @param y 点击时的y坐标值
	 * @return 对应坐标范围的按钮数据或空如果该范围内没有按钮
	 */
	private KeyInfo getKeyInfoByXY(float x, float y) {
		Log.d(LOG_TAG, x + ":" + y);
		return xml.byXY(x, y);
	}
}

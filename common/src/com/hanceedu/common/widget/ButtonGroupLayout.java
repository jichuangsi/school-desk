package com.hanceedu.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hanceedu.common.R;

public class ButtonGroupLayout extends ViewGroup {
	private OnItemClickListener onItemClickListener = new OnItemClickListener() {
		public void onItemClick(int part) {
		}
	};

	private static final int DEFAULT_CHILD_WIDTH = 40;
	private static final int DEFAULT_CHILD_HEIGHT = 40;

	private int mItemLayotId;
	// 这个变量加在这主要是为了以后做扩展用的
	/** 布局方式 */
	private int mLayoutModel = 0;

	/** 起始角度 */
	private float mStartAngle = 180.0f;

	/** 终止角度 */
	private float mEndAngle = -180.0f;

	/** 总共有多少个Button默认为1个 */
	private int mItemCount = 1;

	/** 圆心的中点x坐标 */
	private int circleCenter_x;

	private int mSelectStatusRes;

	/** 菜单中每个选项所占的角度 */
	private double mItemDegree = 0;

	private float mItemRadio;

	/** 当终止角度不是360度时需要一个offset值处理起始和终止位置 */
	private float mOffset = 0;

	private int[] mButtonStrRes;

	public interface OnItemClickListener {
		public void onItemClick(int part);
	}

	private int mCurrentPart = 1;

	private boolean isNeedFoucus;

	private int mLayoutRadio;

	private int[] mButtonDrawableRes;

	/** 圆心的中点y坐标 */
	private int circleCenter_y;

	private Context mContext;

	public void setOnItemClickListener(OnItemClickListener itemClickListener) {
		this.onItemClickListener = itemClickListener;
	}

	public ButtonGroupLayout(Context context) {
		this(context, null, 0);
	}

	public ButtonGroupLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ButtonGroupLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.ButtonGroupLayout, defStyle, 0);
		mItemCount = a.getInt(R.styleable.ButtonGroupLayout_buttonCount, 1);
		mStartAngle = a
				.getFloat(R.styleable.ButtonGroupLayout_startAngel, -180);

		mEndAngle = a.getFloat(R.styleable.ButtonGroupLayout_endAngel, 180);

		mContext = context;
		mLayoutRadio = a.getInt(R.styleable.ButtonGroupLayout_layoutRadio, 50);
		mItemRadio = a.getFloat(R.styleable.ButtonGroupLayout_itemRadio, 10);
		isNeedFoucus = a.getBoolean(R.styleable.ButtonGroupLayout_isNeedFocus,
				false);
		a.recycle();
		inital();
	}

	public boolean onInterceptTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			if (isInTouchArae(event.getX(), event.getY())) {
				int degree = calculateAngle(event.getX(), event.getY());
				System.out.println("degree is " + degree);
				final int startAngle = (int) (mStartAngle - mItemDegree / 2);
				if (mOffset > 0) {
					if (degree >= -180 && degree < mOffset - 180)
						degree += 360;
				}
				if (mOffset < 0) {
					if (degree >= 180 + mOffset && degree <= 180)
						degree -= 360;
				}
				int part = (int) ((degree - startAngle) / mItemDegree);
				if (part < 0 || part > mItemCount - 1) {
				} else {
					onItemClickListener.onItemClick(part);
					mCurrentPart = part;
					if (isNeedFoucus) {
						setFocus();
					}
				}
				break;
			}
		}
		return false;
	}

	private void setFocus() {
		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).setFocusableInTouchMode(false);
		}
		getChildAt(mCurrentPart).setFocusableInTouchMode(true);
		getChildAt(mCurrentPart).requestFocus();
	}

	/**
	 * 设置当前的焦点button
	 */
	public void setCurrentPart(int part) {
		mCurrentPart = part;
		setFocus();
	}

	public void inital() {
		mItemDegree = (int) ((mEndAngle - mStartAngle) / (mItemCount - 1));
		mButtonDrawableRes = new int[mItemCount];
		mButtonStrRes = new int[mItemCount];
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for (int i = 0; i < mItemCount; i++) {
			TextView child;
			if (mItemLayotId != 0) {
				child = (TextView) inflater.inflate(mItemLayotId, null, false);
			} else {
				child = (TextView) inflater.inflate(R.layout.icons_item, null,
						false);
			}
			addView(child, i);
		}
		if (mStartAngle < -180) {
			mOffset = (float) (mStartAngle - mItemDegree / 2 + 180);
		}
		if (mEndAngle > 180) {
			mOffset = mEndAngle - 180;
		}
		setButtonRes(mButtonDrawableRes, mButtonStrRes);
		setchildrenClickable();
		setFocus();
	}

	private void setchildrenClickable() {
		for (int i = 0; i < getChildCount(); i++) {
			getChildAt(i).setClickable(true);
			getChildAt(i).setFocusable(true);
		}
	}
	

	private void setButtonRes(int[] buttonDrawableRes, int[] buttonStrRes) {
		// FIXME 暂时先这么设置如果还需要setText那也再说了
		// int[] colors = { Color.RED, Color.YELLOW, Color.BLUE, Color.GREEN,
		// Color.MAGENTA };
		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			if (buttonDrawableRes[i] > 0)
				child.setBackgroundResource(buttonDrawableRes[i]);
			else
				child.setBackgroundColor(Color.TRANSPARENT);
		}
	}

	public void setDrawableResIds(int[] drawableResIds) {
		mButtonDrawableRes = drawableResIds;
		setButtonRes(mButtonDrawableRes, null);
		postInvalidate();
	}

	/**
	 * 设置当前选中的位置,如果id值大于最大或小于0则回到默认0状态
	 * 
	 * @param id
	 *            : 选中的位置
	 */
	public void setClickPart(int id) {
		if (id > mItemCount - 1 || id < 0) {
			// mClicktPart = 0;
		} else {
			// mClicktPart = id;
		}
	}

	// @Override
	// protected void onDraw(Canvas canvas) {
	// // 扇形
	// canvas.save();
	// canvas.clipPath(
	// getSectorClip(circleCenter_x, circleCenter_y, mMaxRadio,
	// mItemAreas[mClicktPart].start,
	// mItemAreas[mClicktPart].end), Region.Op.REPLACE);
	// drawScene(canvas);
	// canvas.restore();
	//
	// }

	/**
	 * set the specific index of child's drawable res and text res
	 * 
	 * @param index
	 * @param resId
	 */
	public void setChildRes(int index, int drawableResId, int strResId) {

	}

	/** 绘制图片 */
	private void drawScene(Canvas canvas) {
		Matrix matrix = new Matrix();
		Bitmap bMap = getBitmap(mSelectStatusRes);
		// 如果实际发生的缩放则相关图肯定也是需要缩放的
		float scale = getMeasuredWidth() / (float) bMap.getWidth();
		matrix.setScale(scale, scale, 0, 0);
		canvas.drawBitmap(bMap, matrix, null);
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int width = getMeasuredWidth();
		int height = getMeasuredHeight();

		// 如果缩放的 也要保证 是按照正方形输出
		if (width != height) {
			int dimen = width > height ? height : width;
			setMeasuredDimension(getBackground().getMinimumHeight(),
					getBackground().getMinimumHeight());
		}
		// 设置child的大小如果那个背景不存在,那么好吧就用默认大小
		// set children's measure width/height it's from the res set to the
		// children
		// if there is no res found for the child the measure will be use the
		// default values
		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			int cHeight = DEFAULT_CHILD_HEIGHT;
			int cWidth = DEFAULT_CHILD_WIDTH;
			if (mButtonDrawableRes[i] > 0) {
				Drawable cDrawable = getResources().getDrawable(
						mButtonDrawableRes[i]);
				cHeight = cDrawable.getMinimumHeight();
				cWidth = cDrawable.getMinimumWidth();
			}
			child.measure(cWidth, cHeight);
		}
		// 当measure的时候计算出该group的最大半径,以及中心的坐标
		circleCenter_x = getMeasuredWidth() / 2;
		circleCenter_y = getMeasuredHeight() / 2;
	}

	/*
	 * 得到图像的bitmap 少了一个缩放
	 */
	private Bitmap getBitmap(int id) {
		return BitmapFactory.decodeResource(getResources(), id);
	}

	private int calculateAngle(float clickX, float clickY) {
		int SideX = (int) clickX - this.getWidth() / 2;
		int SideY = (int) clickY - this.getHeight() / 2;
		// 计算角度
		// mDegrees =
		return (int) (Math.atan2(SideY, SideX) * (180 / Math.PI));
	}

	/**
	 * 判断点击位置与圆环中心的距离
	 * 
	 * @param Touch_x
	 *            点击位置的x坐标
	 * @param Touch_y
	 *            点击位置的y坐标
	 * @return
	 */
	private boolean isInTouchArae(float Touch_x, float Touch_y) {
		float distance = (float) Math.sqrt((Touch_x - circleCenter_x)
				* (Touch_x - circleCenter_x) + (Touch_y - circleCenter_y)
				* (Touch_y - circleCenter_y));
		// 判断距离是否大于圆环半径或者小于最小半径
		if (distance < mLayoutRadio - mItemRadio
				|| distance > mLayoutRadio + mItemRadio)
			return false;
		return true;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		int count = getChildCount();
		// System.out.println("child count = " + count);
		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			int childXcood = (int) (circleCenter_x + mLayoutRadio
					* Math.cos((mStartAngle + mItemDegree * i) * Math.PI / 180));
			// System.out.println("child childXcood = " + childXcood);
			int childYcood = (int) (circleCenter_y + mLayoutRadio
					* Math.sin((mStartAngle + mItemDegree * i) * Math.PI / 180));
			// System.out.println("child childYcood = " + childYcood);
			int childX = child.getMeasuredWidth() / 2;
			int childY = child.getMeasuredHeight() / 2;
			// System.out.println("child childX = " + childX);
			// System.out.println("child childY = " + childY);
			child.layout(childXcood - childY, childYcood - childX, childXcood
					+ childX, childYcood + childY);
		}
	}
}

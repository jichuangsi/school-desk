package com.hanceedu.common.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

/**
 * 工作空间指示器 
 * @author QiJintang
 *
 */
public class WorkspaceIndicator extends View {

	private static final int DOTWIDTH = 25;//一个点的大小
	private static final int SMALL_P = 3;// 小小圆	
	private static final int NORMAL_P = 5;// 小圆
	private static final int FOCUS_P = 10;// 大圆
	private static final int MAX = 10; // 最大显示的圆点数
	private static int mMoreWidth = 0 ; // 表示更多的三个小点的宽度
	

	private int mCount = 1;//默认一屏
	private int mShowCount ; // 当前应该显示的圆点数.
	private int mCurrentScreen = 0;//当前第几屏

	private Paint mPaint;//画笔
	private AlphaAnimation mAnimation;//渐变动画
	private Paint mTextPaint;
	private GotoScreenListener mGotoScreenListener ; // 页面跳转监听

	/**
	 * 初始化
	 * @param context
	 * @param count 总页数
	 */
	public WorkspaceIndicator(Context context,int count) {
		super(context);
		mCount = count;
		init();
	}

	public WorkspaceIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	/**
	 * 初始化
	 */
	public void init(){
		this.setClickable(true);

		//this.setVisibility(View.VISIBLE);//不显示
		//this.setVisibility(View.GONE);//不显示
		//圆点的画笔
		mPaint = new Paint();
		mPaint.setColor(Color.WHITE);//设置画笔为白色
		//画笔设置阴影，2pix
		mPaint.setShadowLayer(2, 0, 0, Color.GRAY);
		mPaint.setAntiAlias(true);//设置抗锯齿
		mPaint.setFilterBitmap(true);

		//文字的画笔
		mTextPaint = new Paint();
		mTextPaint.setColor(Color.BLACK);
		mTextPaint.setAntiAlias(true);

		mMoreWidth = SMALL_P * 10 ;
		
		mAnimation = new AlphaAnimation(1,(float) 0.5);//实例化渐变动画 从显示渐变到隐藏
		mAnimation.setDuration(2000);//设置渐变时间
		mAnimation.setFillAfter(true);

		//设置动画监听器
		mAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation arg0) {
				//System.out.println("onAnimationStart");
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				//System.out.println("onAnimationRepeat");
			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				//System.out.println("WorkspaceIndicator onAnimationEnd");

				//setVisibility(View.GONE);//动画结束后 隐藏控件
			}
		});
	}


	/***
	 * 画图的主要方法
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		int section = mCurrentScreen / MAX ;;
		int start = section * MAX ;                                                                                                                                                                                                                                                                                        
		mShowCount = MAX ;
		if ( isLastSection() ) {
			mShowCount = mCount % MAX ;
			//如果是MAX的整数倍
			if (mShowCount == 0) {
				mShowCount = MAX ;
			}
		}
		//计算宽度和高度
		setMeasuredDimension() ;
		     
		//画三个小点
		if (mCount > MAX && mCurrentScreen > MAX - 1) {
			canvas.drawCircle(SMALL_P * 2, DOTWIDTH / 2, SMALL_P, mPaint);
			canvas.drawCircle(SMALL_P * 5, DOTWIDTH / 2, SMALL_P, mPaint);
			canvas.drawCircle(SMALL_P * 8 , DOTWIDTH / 2, SMALL_P, mPaint);	

		}



		//循环画页数点
		for (int i = start, j = 0 ; i < start + mShowCount; i++, j++) {
			if (i == mCurrentScreen) {
				//如果是当前屏 画大点
				canvas.drawCircle(mMoreWidth + DOTWIDTH / 2 + j * DOTWIDTH, DOTWIDTH / 2, FOCUS_P, mPaint);

				//画页码文字
				String pageNum = String.valueOf(i + 1);
				float textSize = 12.0f ;
				if (pageNum.length() >= 2) {
					mTextPaint.setTextSize(textSize - 1);
				} else {
					mTextPaint.setTextSize(textSize);
				}
				Rect textRect = new Rect();
				//获得文字所占的大小：textRect
				mTextPaint.getTextBounds(pageNum, 0, pageNum.length(),textRect);

				//调整大小
				int r = textRect.right ;
				if (i == 0){
					r += 2;
				}

				if (i > 9){
					r += 3;
				}
				//使得文字在圆的中间： 总高度 - 文字高度/2
				canvas.drawText(pageNum,
						(float) ((DOTWIDTH - (float) r) / 2.0)
						+ j * DOTWIDTH + mMoreWidth ,
						(float) ((DOTWIDTH - (float) textRect.top) / 2.0 ),
						mTextPaint);
			} else {
				//则画小点
				canvas.drawCircle(mMoreWidth + DOTWIDTH / 2 + j * DOTWIDTH, DOTWIDTH / 2, NORMAL_P, mPaint);
			}
		}

		//System.out.println("mCount=" + mCount + " Max=" + MAX + " section=" + section + " lastSection=" + lastSection) ;
		if (mCount > MAX &&  !isLastSection() ) {
			int leftSpace = mMoreWidth + mShowCount * DOTWIDTH;
			//System.out.println("leftSpace=" + leftSpace);
			canvas.drawCircle(leftSpace + SMALL_P * 2, DOTWIDTH / 2, SMALL_P, mPaint);
			canvas.drawCircle(leftSpace + SMALL_P * 5, DOTWIDTH / 2, SMALL_P, mPaint);
			canvas.drawCircle(leftSpace + SMALL_P * 8 , DOTWIDTH / 2, SMALL_P, mPaint);	

		}
	}

	//提供尺寸给容器
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		//System.out.println("overide onMeasure setMeasuredDimension");
		setMeasuredDimension();//设置高宽度
	}




	/**
	 * 显示指示器
	 * @param currentScreen
	 */
	public void show(int currentScreen){
		//System.out.println("show " + currentScreen);
		mCurrentScreen = currentScreen;
		//System.out.println("show CurrentScreen======" + mCurrentScreen) ;
		
		//setMeasuredDimension();//重新设置高宽度

		invalidate();
		//this.setVisibility(View.VISIBLE);
		this.startAnimation(mAnimation);
	}

	/**
	 * 隐藏指示器
	 * @param start
	 * @param currentScreen
	 */
	public void hide(int start, int currentScreen){
		//System.out.println("hide start " + start);
		//System.out.println("hide currentScreen " + currentScreen);		
		show(currentScreen);
		mAnimation.setStartOffset(start+500);//延迟 启动动画时间
		this.startAnimation(mAnimation);
	}

	/**
	 * 设置总屏数
	 * @param childCount
	 */
	public void setScreenCount(int childCount) {
		mCount = childCount;
		setMeasuredDimension();//重新设置高宽度
	}

	/***
	 * 计算控件的宽度和高度
	 */
	private void setMeasuredDimension() {
		
		int width ;
		if (mCount < MAX) {
			width = DOTWIDTH  * mCount + mMoreWidth * 2 ;
		}else{
			width = DOTWIDTH  * MAX + mMoreWidth * 2 ;
		}
		//调用系统函数， 提供尺寸给容器
		setMeasuredDimension(width, DOTWIDTH);//重新设置高宽度	
	}
/*
	
	@Override
	protected void drawableStateChanged() {
		// TODO Auto-generated method stub
		super.drawableStateChanged();
		int[] state = this.getDrawableState();

		if(state.equals(PRESSED_ENABLED_WINDOW_FOCUSED_STATE_SET)){
			Log.e("test", "click") ;
			mPaint.setColor(Color.argb(100, 254, 199, 199));//设置画笔偏红色
			mPaint.setShadowLayer(2, 0, 0, Color.RED);
			invalidate();
		}else if(state == ENABLED_WINDOW_FOCUSED_STATE_SET){
			mPaint.setColor(Color.WHITE);//设置画笔为白色
			mPaint.setShadowLayer(2, 0, 0, Color.GRAY);
			invalidate();
		}else if(state == ENABLED_STATE_SET){
			mPaint.setColor(Color.WHITE);//设置画笔为白色
			mPaint.setShadowLayer(2, 0, 0, Color.GRAY);
			invalidate();
		}else{
			mPaint.setColor(Color.WHITE);//设置画笔为白色
			mPaint.setShadowLayer(2, 0, 0, Color.GRAY);
			invalidate();
		}
	}
	
	*/

	/**
	 * 点击时系统传过来的事件。根据坐标判断是点了哪个页码
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		if (event.getAction() == MotionEvent.ACTION_UP ) {
			int scr = -1 ;
			float x = event.getX();
			if (x < mMoreWidth && mCurrentScreen > MAX - 1) {
				scr = mCurrentScreen - MAX ;
			}
			else if (x > (mMoreWidth + mShowCount * DOTWIDTH) && !isLastSection() ) {
				scr = mCurrentScreen + MAX ;
			}
			else {
				scr = (int) (x - mMoreWidth) / DOTWIDTH  +  (mCurrentScreen / MAX) * MAX;		
			}
			if (scr > mCount - 1) {
				scr = mCount ;
			}
			System.out.println("scr=" + scr);
			//回调翻页函数
			if (scr != -1) {
				mGotoScreenListener.gotoScreen(scr) ;
			}
			return false ;
		}
		return true ;

	}
	
	private boolean isLastSection() {
		int section = mCurrentScreen / MAX ;;
		int lastSection = (mCount -1 ) / MAX ;
		return section == lastSection ;
	}

	public interface GotoScreenListener {
		void gotoScreen(int screen);
	}
	public void setGotoScreenListener(GotoScreenListener listener){
		this.mGotoScreenListener = listener;
	}

}

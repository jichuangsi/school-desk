package cn.netin.launcher;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import cn.netin.launcher.data.Constants;

/**
 * 工作空间指示器 
 * @author QiJintang
 *
 */
public class WorkspaceIndicator extends View {
	private static final String TAG = "WorkspaceIndicator" ; //一个点的大小
	private static final int DOTWIDTH = 60;//一个点的大小
	private static final int DOTHEIGHT = 60;//一个点的大小
	private static final int SMALL_P = 3;// 小小圆	
	//private static final int NORMAL_P = 10;// 小圆
	//private static final int FOCUS_P = 14;// 大圆
	private static final int NORMAL_P = 6;// 小圆
	private static final int FOCUS_P = 10;// 大圆	

	private static final int MAX = 10; // 最大显示的圆点数
	//private float textSize = 14.0f ;
	private float textSize = 10.0f ;
	private float mLabelTextSize = 16.0f ;

	private static int mMoreWidth = 0 ; // 表示更多的三个小点的宽度


	private int mCount = 1;//默认一屏
	private int mShowCount ; // 当前应该显示的圆点数.
	private int mCurrentScreen = 0;//当前第几屏

	private Paint mPaint;//画笔
	private AlphaAnimation mAnimation;//渐变动画
	private Paint mTextPaint;
	private Paint mLabelPaint;
	private GotoScreenListener mGotoScreenListener ; // 页面跳转监听

	private String[] mLabels ;
	private boolean mShowLabels = false ;
	
	public void setShowLabels(boolean flag) {
		mShowLabels = flag ; 
	}
	
	public int getCurrentScreen() {
		return mCurrentScreen ;
	}

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

		mLabels = this.getResources().getStringArray(R.array.labels) ;

		//this.setVisibility(View.VISIBLE);//不显示
		//this.setVisibility(View.GONE);//不显示
		mPaint = new Paint();
		mPaint.setColor(Color.WHITE);//设置画笔为白色
		mPaint.setShadowLayer(2, 0, 0, Color.GRAY);
		mPaint.setAntiAlias(true);//设置抗锯齿
		mPaint.setFilterBitmap(true);

		mTextPaint = new Paint();
		mTextPaint.setColor(Color.BLACK);
		mTextPaint.setAntiAlias(true);


		mLabelPaint = new Paint();
		mLabelPaint.setColor(Color.WHITE);
		mLabelPaint.setAntiAlias(true);
		mLabelPaint.setTextSize(mLabelTextSize);
		mLabelPaint.setTextAlign(Align.CENTER);

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
				//WorkspaceIndicator.this.setAlpha(0.5f);
			}
		});
	}


	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (this.getVisibility() == View.GONE) {
			return ;
		}

		Rect textRect = new Rect();
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
		if (mCount == 0 ) {
			mShowCount = 0 ;
		}
		//Log.d(TAG, "onDraw mCount=" + mCount + " mShowCount=" + mShowCount ) ;
		//System.out.println("1111111111111111 ondraw");
		setMeasuredDimension() ;

		if (mCount > MAX && mCurrentScreen > MAX - 1) {
			canvas.drawCircle(SMALL_P * 2, DOTWIDTH / 2, SMALL_P, mPaint);
			canvas.drawCircle(SMALL_P * 5, DOTWIDTH / 2, SMALL_P, mPaint);
			canvas.drawCircle(SMALL_P * 8 , DOTWIDTH / 2, SMALL_P, mPaint);	

		}



		//循环画点
		for (int i = start, j = 0 ; i < start + mShowCount; i++, j++) {
			if (i == mCurrentScreen) {
				//如果是当前屏 画大点
				canvas.drawCircle(mMoreWidth + DOTWIDTH / 2 + j * DOTWIDTH, DOTHEIGHT / 3, FOCUS_P, mPaint);

				String pageNum = String.valueOf(i + 1);


				if (pageNum.length() >= 2) {
					mTextPaint.setTextSize(textSize - 1);
				} else {
					mTextPaint.setTextSize(textSize);
				}
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
						(float) (DOTWIDTH / 3.0f -  textRect.top / 2.0 ),
						mTextPaint);
			} else {
				//则画小点
				canvas.drawCircle(mMoreWidth + DOTWIDTH / 2 + j * DOTWIDTH, DOTHEIGHT / 3, NORMAL_P, mPaint);
			}
			if (Constants.GROUP_SCREEN && mShowLabels) {
				String lable = mLabels[i] ;
				//获得文字所占的大小：textRect
				mLabelPaint.getTextBounds(lable, 0, lable.length(),textRect);
				//使得文字在圆的中间： 总高度 - 文字高度/2
				canvas.drawText(lable,
						(float) (DOTWIDTH / 2.0
								+ j * DOTWIDTH + mMoreWidth) ,
								(float) ( DOTWIDTH + textRect.top  ),
								mLabelPaint);
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
		if (this.getVisibility() == View.GONE) {
			return ;
		}
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

	private void setMeasuredDimension() {

		int width ;
		if (mCount < MAX) {
			width = DOTWIDTH  * mCount + mMoreWidth * 2 ;
		}else{
			width = DOTWIDTH  * MAX + mMoreWidth * 2 ;
		}
		setMeasuredDimension(width, DOTHEIGHT);//重新设置高宽度	
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
			//System.out.println("scr=" + scr);
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

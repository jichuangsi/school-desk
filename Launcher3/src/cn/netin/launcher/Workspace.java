package cn.netin.launcher;


import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * 允许不指定加入的item在哪一屏
 */
public class Workspace extends ViewGroup {
	private static final String TAG = "EL Workspace" ;
	private Scroller mScroller;
	private Paint mPaint;
	private Bitmap mWallpaperBitmap = null;	
	private WorkspaceIndicator mIndicator;
	/**生成程序view的inflater*/
	private LayoutInflater mInflater;
	private WallpaperManager mWallpaperManager;

	private final static int SNAP_VELOCITY = 500;
	private final static int TOUCH_STATE_REST = 0;
	private final static int TOUCH_STATE_SCROLLING = 1;
	private final static int TOUCH_STATE_AUTOSCROLL = 2 ;
	private final static int GAP = 40 ;
	private final static int INVALID_SCREEN = -1;

	private int mScrollerX = 0;
	private int mCurrentScreen = 0;
	private int mNextScreen = INVALID_SCREEN;
	private int mCurrentDuration ;
	private float mDownX ;
	private float mLastX ;
	private float mMoveDistance ;
	private long mLastTime ;
	private int mSpeed = 0 ;

	private int mTouchState = TOUCH_STATE_REST;

	private int mScreenCount = 0 ;
	private int mCapacityPerScreen = 0 ;
	private int mItemTotal = 0 ;

	private boolean mScrollLeft = false ;
	private boolean mWallpaperLoaded  = false ;
	private boolean childrenCacheEnabled = false ;
	private boolean mIsScrolling = false ;


	public Workspace(Context context) {
		super(context);

		mWallpaperManager = WallpaperManager.getInstance(context);
		mScroller = new Scroller(context);


		this.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
		mPaint = new Paint();
		mPaint.setDither(false);
		mInflater = LayoutInflater.from(context);//实例化inflater
	}

	public Workspace(Context context, AttributeSet attrs) {
		super(context, attrs);
		mWallpaperManager = WallpaperManager.getInstance(context);
		mScroller = new Scroller(context);

		this.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
		//Brent
		//TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.Workspace);
		//mCurrentScreen = a.getInteger(R.styleable.Workspace_defaultScreen, 0);
		mPaint = new Paint();
		//No dithering is generally faster, but higher precision colors are just truncated down (e.g. 8888 -> 565). 
		mPaint.setDither(false);
		mInflater = LayoutInflater.from(context);//实例化inflater
	}

	/**
	 * Set the background's wallpaper.
	 */
	void loadWallpaper(Bitmap bitmap) {
		mWallpaperBitmap = bitmap;
		mWallpaperLoaded = true;
		//Call this when something has changed which has invalidated the layout of this view. This will schedule a layout pass of the view tree. 
		requestLayout();
		//Invalidate the whole view. If the view is visible, onDraw(Canvas) will be called at some point in the future. 
		//This must be called from a UI thread. To call from a non-UI thread, call postInvalidate(). 
		invalidate();
	}

	/**
	 * 判断用户是否在拖动，如果是就让这里的OnTouch处理，否则，让子View处理

onInterceptTouchEvent默认返回值是false, 
Layout里 onTouchEvent默认返回值是false，所以只消费了ACTION_DOWN事件，
View里onTouch默认返回值是true, 消费了:ACTION_DOW,ACTION_UP。

1.当onInterceptTouchEvent返回false时，表示没有消费完此事件，会继续传递个子组件的onTouch继续处理。
注意这种情况不会就不会传递给这个ViewGroup自身的onTouch事件处理了。这和onTouch如果返回false，后续的move、up等事件都不会继续处理了可以做同样理解。
2.当onInterceptTouchEvent返回true时，表示消费完此事件，或者说将在此组件上消费该事件。这种情况该事件会传递给ViewGroup自身的onTouch事件去处理，
而不会传递给子组件的onTouch方法了。

onInterceptTouchEvent()说的是是否允许Touch事件继续向下（子控件）传递，一但返回True，则向下传递之路被截断（所有子控件将没有机会参与Touch事件）； 
onTouchEvent()说的是当前控件在处理完Touch事件后，是否还允许Touch事件继续向上（父控件）传递，一但返回True，则父控件不用操心,由自己来处理Touch事件。

Parameters
	 * (non-Javadoc)
	 * @see android.view.ViewGroup#onInterceptTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		if (getChildCount() == 0) {
			return true;
		}

		final int action = ev.getAction();

		//如果是ACTION_MOVE，且mTouchState是滚动中
		if ((action == MotionEvent.ACTION_MOVE) && (mTouchState != TOUCH_STATE_REST)) {
			//交到自己的onTouch处理
			return true;
		}

		final float currentX = ev.getX();
		switch (action) {

		case MotionEvent.ACTION_MOVE:

			final int xDiff = (int) Math.abs(currentX - mDownX);
			//如果拖动足够长的距离，就进入滚动状态
			boolean xMoved = xDiff > GAP;
			if (xMoved) {
				//System.out.println("xxxxxxxxxxxxxxxxx moved " + xDiff + " mTouchSlop=" + mTouchSlop );
				mTouchState = TOUCH_STATE_SCROLLING;
			}else{
				//System.out.println("xxxxxxxxxxxxxxxxx NOT moved " + xDiff  + " mTouchSlop=" + mTouchSlop  );
			}
			break;

		case MotionEvent.ACTION_DOWN:
			handleActionDown(currentX) ;
			break;

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			// 如果抬起或取消，状态为REST
			mTouchState = TOUCH_STATE_REST;
			break;
		}

		//如果SCROLLING, 就return true, 在这里处理OnTouch
		//如果REST , 就return false, 让子view获得事件
		return mTouchState != TOUCH_STATE_REST;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (getChildCount() == 0) {
			return true;
		}

		final float currentX = event.getX();
		mScrollerX = this.getScrollX();

		final int action = event.getAction();
		switch (action) {

		case MotionEvent.ACTION_DOWN:
			handleActionDown(currentX) ;
			break;

		case MotionEvent.ACTION_MOVE:		
			//移动得慢，TP上报会迟钝，可能移动5px,才上报1px

			//deltaX 为本次移动的相对值
			float deltaX =  currentX - mLastX ;
			
			//累计移动距离
			mMoveDistance = deltaX ;
			//要够一定的距离，才处理
			if (deltaX < - GAP) { //如果向左滑动
				mScrollLeft = true ;		
				//scrollBy((int) -deltaX, 0);
				handleMove(currentX) ;
			} else if (deltaX > GAP) { ////如果向右滑动
				mScrollLeft = false ;
				//scrollBy((int) deltaX, 0);
				handleMove(currentX) ;	
				
			} // end if (deltaX < - GAP) {
			
			break;

		case MotionEvent.ACTION_UP:

			float movedX =  currentX - mDownX;	
			//判断滑动距离
			if (movedX < - GAP) { //如果向左滑动
				mScrollLeft = true ;
			} else if (movedX > GAP) { ////如果向右滑动
				mScrollLeft = false ;	
			} // end if (deltaX < - GAP) {


			//如果速度够大
			boolean fastEnougth =  mSpeed > SNAP_VELOCITY ;
			//如果移动的总距离超过GAP
			boolean farEnougth = Math.abs(movedX) > GAP ;	
			//System.out.println("up speed=" + mSpeed + " mCurrentScreen=" + mCurrentScreen + " mScrollLeft=" + mScrollLeft + " movedX=" + movedX + "  event.getX()=" +  event.getX());
			if (fastEnougth || farEnougth) {
				if (mScrollLeft &&  mCurrentScreen < mScreenCount -1) {
					mNextScreen = mCurrentScreen + 1 ;
					//System.out.println("page - 1 =" + mNextScreen );
					snapToScreen() ;
					
				}
				else if (!mScrollLeft && mCurrentScreen > 0  ) {
					mNextScreen = mCurrentScreen - 1 ;
					//System.out.println("page - 1 =" + mNextScreen);
					snapToScreen() ;
					
				}else{
					//System.out.println("up speed3=" + mSpeed + " mCurrentScreen=" + mCurrentScreen + " mScrollLeft=" + mScrollLeft);

					mNextScreen = mCurrentScreen ;
					snapToScreen() ;
					
				}
			}else{
				//System.out.println("up slow or near Speed=" + mSpeed + " mCurrentScreen=" + mCurrentScreen + " mScrollLeft=" + mScrollLeft);

				mNextScreen = mCurrentScreen ;
				snapToScreen() ;
				
			}
			//将状态设为静止
			mTouchState = TOUCH_STATE_REST;

			break;

		case MotionEvent.ACTION_CANCEL:
			//将状态设为静止
			mTouchState = TOUCH_STATE_REST;
			break;

		}

		return true;
	}
	



	private void handleActionDown(float currentX) {
		if (!mScroller.isFinished()) {
			mScroller.abortAnimation();
			mNextScreen = INVALID_SCREEN ;
		}
		// 记下刚按下的位置
		mDownX = currentX ;
		mLastX = currentX ;
		mLastTime = System.currentTimeMillis() ;
		//System.out.println("@@@@@ ACTION_DOWN mDownX=" + mDownX) ;
		mMoveDistance = 0 ;
		mTouchState = TOUCH_STATE_REST ;
	}

	/**
	 * mScrollLeft=true时，手势从右到左， mMoveDistance为负数，且值越来越小，mScrollX为正数，且值越来越大
	 * mScrollLeft=true时，手势从左到右， mMoveDistance为正数，且值越来越大，mScrollX为负数，且值越来越小
	 * */
	private void handleMove(float currentX) {
		
		long currentTime = System.currentTimeMillis() ;	
		long timePassed = currentTime - mLastTime ;
		if (timePassed < 100) {
			timePassed = 100 ;
		}
		mSpeed =  (int ) ( Math.abs(mMoveDistance) * 1000 / timePassed );
		
		//System.out.println("handleMove mMoveDistance="  + mMoveDistance + "mSpeed=" + mSpeed+ " timePassed=" + timePassed) ;

		float distance = 0 ;
		//如果是按下去之后的第一个Move事件
		if (mTouchState != TOUCH_STATE_AUTOSCROLL ) {
			mTouchState = TOUCH_STATE_AUTOSCROLL;
			int plus = 0 ;
			if (mSpeed > 1000) {
				plus = 400 ;
				mCurrentDuration = 900 ;
			}
			else if (mSpeed > 500 &&  mSpeed < 1001) {
				plus = 400 ;
				mCurrentDuration = 1200 ;
			}
			else if (mSpeed > 200 &&  mSpeed < 501) {
				plus = 300 ;
				mCurrentDuration = 2500 ;
			}
			else if (mSpeed < 201) {
				plus = 200 ;
				mCurrentDuration = 3000 ;
			}
							
			if (mScrollLeft) {
				distance = mMoveDistance - plus ;
			}else{
				distance = mMoveDistance + plus ;
			}

			int scrollX = this.getScrollX() ;
			//System.out.println("######### FIRST mMoveDistance=" + mMoveDistance  + " distance=" + distance  +  " duration=" + mCurrentDuration
				//	+ " scrollX=" + scrollX + " timePassed=" + timePassed);
			enableChildrenCache();
			mScroller.startScroll( (int)scrollX, 0,  - (int)distance, 0, mCurrentDuration);
			mIndicator.show(mCurrentScreen);
			
			mLastTime = currentTime ;
			mLastX = currentX ;
			mMoveDistance = 0 ;

		}else{ //是后继的Move事件
			if (timePassed < 800) {
				return ;
			}
			if (Math.abs(mMoveDistance) < 150) {
				return ;
			}					

			//是否可以继续滑动
			boolean moreToGo ;
			if (mScrollLeft) {
				moreToGo = mScrollerX < getWidth() * ( mScreenCount - 1) ;
			}else{
				moreToGo =  mScrollerX > 0 ;
			}
			if (!moreToGo) {
				//System.out.println("!!!!!!!!!!!!!!!! no more to go") ;
				return ;
			}
			
			int plus = 0 ;
			if (mSpeed > 500) {
				plus = 200 ;
				mCurrentDuration = 1800 ;
			}
			else if (mSpeed > 200 &&  mSpeed < 501) {
				plus = 150 ;
				mCurrentDuration = 3700 ;
			}
			else if (mSpeed < 201) {
				plus = 100 ;
				mCurrentDuration = 4500 ;
			}
							
			if (mScrollLeft) {
				distance = mMoveDistance - plus ;
			}else{
				distance = mMoveDistance + plus ;
			}

			int scrollX = this.getScrollX() ;
			//System.out.println("######### NEXT distance=" + distance  +  " duration=" + mCurrentDuration
				//	+ " scrollX=" + scrollX + " FinalX()=" + mScroller.getFinalX());
			//mScroller.forceFinished(true) ;
			enableChildrenCache();
			mScroller.startScroll(  scrollX, 0,   - (int)distance, 0, mCurrentDuration);
			final int whichScreen = (mScrollerX + (getWidth() / 2)) / getWidth();
			if (whichScreen != mCurrentScreen) {
				//mCurrentScreen = whichScreen ;
				mIndicator.show(whichScreen);
			}
			
			mLastTime = currentTime ;
			mLastX = currentX ;
			mMoveDistance = 0 ;

		}

	}



	/**
	 * 让Scroller产生滚动数据，并根据这些数据重绘，产生动画效果
	 * */
	private void snapToScreen() {
		//System.out.println("snapToScreen " + mNextScreen);

		final int newX = mNextScreen * getWidth();
		final int delta = newX - mScrollerX;
		enableChildrenCache();
		mScroller.startScroll(mScrollerX, 0, delta, 0, 1000);	
		mCurrentScreen = mNextScreen ;
		mIndicator.show(mNextScreen);
		mNextScreen = INVALID_SCREEN ;
	}

	/**
	 * 让Scroller产生滚动数据，并根据这些数据重绘，产生动画效果
	 * */
	public void snapToScreen(int screen) {
		mNextScreen = screen ;
		snapToScreen() ;
	}

	/**
	 * 直接跳到某一屏
	 */
	public void setToScreen(int whichScreen) {
		mCurrentScreen = whichScreen;
		final int newX = whichScreen * getWidth();
		//mScroller.startScroll(newX, 0, 0, 0, 0);
		//Log.d(TAG, "newX=" + newX) ;
		scrollTo(newX, 0);
		enableChildrenCache();
		mIndicator.show(whichScreen);
		invalidate();
	}

	/**
	 * 指示这个ViewGroup的每个子View的大小和位置
	 * */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

		int childX = 0;
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() != View.GONE) {
				final int childWidth = child.getMeasuredWidth();
				child.layout(childX, 0, childX + childWidth, child.getMeasuredHeight());
				childX += childWidth;
			}
		}
	}

	/**
	 * 指示这个ViewGroup的大小
	 * widthMeasure和heightMeasureSpec虽然是整型，实际上它是根据SpecView.MeasureSpec编码的，包含了int mode和int size数据。
	 * 用getMode()来获得mode (三种模式： AT_MOST, EXACTLY, UNSPECIFIED), 用getSize()来获得size

	 * */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		//final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		if (widthMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException("error mode.");
		}

		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		if (heightMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException("error mode.");
		}
		//给每个子View和本ViewGroup一样大小
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
		Log.d(TAG, "onMeasure getSize=" + MeasureSpec.getSize(widthMeasureSpec) ) ;
	}

	/** 无论在任何情况下， 这个会一直调用 */
	@Override
	public void computeScroll() {

		mIsScrolling = mScroller.computeScrollOffset() ;
		if (mIsScrolling) {
			mScrollerX = mScroller.getCurrX();
			//todo: Set the scrolled position of your view. This will cause a call to onScrollChanged(int, int, int, int) and the view will be invalidated.
			//System.out.println( "mScroller scrollTo " + mScrollerX) ;
			scrollTo(mScrollerX, 0);
			// 如果没有墙纸，就通知系统墙纸滚动
			if (!mWallpaperLoaded) {
				updateWallpaperOffset();
			}
		} else { //如果滚动已经停止
			//System.out.println( "mScroller not mIsScrolling") ;
			//滚动停止，就清除缓存
			clearChildrenCache();
		}
		//非常重要，否则界面会不更新
		invalidate() ;
	}


	/**
	 * 添加view 到桌面
	 */
	public void addInCurrentScreen(View child, int cellX, int cellY, int spanX, int spanY) {
		addInScreen(child, mCurrentScreen, cellX, cellY, spanX, spanY, false);
	}

	/**
	 * 添加view 到桌面
	 */
	public void addInCurrentScreen(View child, int cellX, int cellY, int spanX, int spanY, boolean insert) {
		addInScreen(child, mCurrentScreen, cellX, cellY, spanX, spanY, insert);
	}
	
	/**
	 * 添加view 到桌面，指定哪一屏，哪个位置自动计算
	 */
	public void addInScreen(View child, int screen) {
		addInScreen(child, screen, -1, -1, 1, 1, false);
	}


	/**
	 * 添加view 到桌面，哪一屏，哪个位置自动计算
	 */
	public void addInScreen(View child) {
		addInScreen(child, -1, -1, -1, 1, 1, false);
	}


	/**
	 *  添加view 到桌面
	 */
	public void addInScreen(View child, int screen, int cellX, int cellY, int spanX, int spanY) {
		addInScreen(child, screen, cellX, cellY, spanX, spanY, false);
	}


	/**
	 * 添加view 到桌面
	 * @param child 要添加的View
	 * @param screen 哪一屏, 如果 -1 , 就让CellLayout自己计算
	 * @param cellX 放在格子的第几列，如果 -1 , 就让CellLayout自己计算摆放的位置
	 * @param cellY 放在格子的第几行
	 * @param spanX 横向占多少个格，正常1
	 * @param spanY 竖向占多少个格，正常1
	 * @param insert 插入否
	 */
	public void addInScreen(View child, int screen, int cellX, int cellY, int spanX, int spanY, boolean insert) {

		if (child == null) {
			Log.e(TAG, "addInScreen child == null !!!");
			return ;
		}
		//不指定哪一屏，就要计算放在哪一屏
		if (screen == -1) {
			//mCapacityPerScreen还没获得时，数值为0，为防止出错，先给一个99的值
			if (mCapacityPerScreen == 0) {
				mCapacityPerScreen = 99;
			}
			screen = mItemTotal / mCapacityPerScreen ;
			//System.out.println("addInScreen screen=" + screen ) ;
		}
		mItemTotal++ ;

		//每个Screen 都是一个cell layout
		CellLayout workspaceScreen  ;
		
		//Log.d(TAG, "screen=" + screen + " mScreenCount=" + mScreenCount + " ") ;
		//如果要放入的屏现在还没有，就增加一个屏
		if ( screen > mScreenCount - 1) {
			workspaceScreen = (CellLayout) mInflater.inflate(R.layout.workspace_screen, null, false);
			this.addView(workspaceScreen) ;
			mScreenCount = screen + 1 ; 
			mIndicator.setScreenCount(mScreenCount);
		}else{
			workspaceScreen = (CellLayout) getChildAt(screen);
		}	
		if (workspaceScreen == null) {
			Log.e(TAG, "workspaceScreen == null") ;
			return ;
		}

		//计算每屏的格子个数
		if (mCapacityPerScreen == 99) {
			mCapacityPerScreen = workspaceScreen.getCountX() * workspaceScreen.getCountY() ;
			Log.d(TAG, "mCapacityPerScreen=" + mCapacityPerScreen 
					+ " count x=" + workspaceScreen.getCountX() + " count y=" + workspaceScreen.getCountY()
				+  " spanX=" + spanX + " spanY=" + spanY) ;
		}	
		/**把子View的layoutParams强制转成CellLayout.LayoutParams，无论如何，只为带上x,y, spanX, spanY这几个值
		 * 为什么不直接创建CellLayoutParams，而是先取child的LayoutParams呢？为的是取child的宽度和高度。如果没有指定，
		 * 就创建CellLayoutParams，宽度和高度默认都是wrap_content
		 */
		CellLayout.CellLayoutParams cellLayoutParams = (CellLayout.CellLayoutParams) child.getLayoutParams();
		if (cellLayoutParams == null) {
			cellLayoutParams = new CellLayout.CellLayoutParams(cellX, cellY, spanX, spanY);
		} else {
			cellLayoutParams.cellX = cellX;
			cellLayoutParams.cellY = cellY;
			cellLayoutParams.cellHSpan = spanX;
			cellLayoutParams.cellVSpan = spanY;
		}


		workspaceScreen.addView(child, insert ? 0 : -1, cellLayoutParams);
	}

	/**
	 * 设置页码指示器
	 * @param indicator
	 */
	public void setIndicator(WorkspaceIndicator indicator) {
		mIndicator = indicator;
	}

	/**
	 * 获取当前第几屏
	 * @return
	 */
	public int getCurrentScreen() {
		return mCurrentScreen;
	}

	/**
	 * 绘制VIew本身的内容，通过调用View.onDraw(canvas)函数实现,绘制自己的孩子通过dispatchDraw（canvas）实现
	 * Called by draw to draw the child views. This may be overridden by derived classes to gain control
	 * just before its children are drawn (but after its own view has been drawn). 
	 * **/
	@Override
	protected void dispatchDraw(Canvas canvas) {
		boolean restore = false;
		View child ;

		if (mWallpaperLoaded) {
			canvas.drawBitmap(mWallpaperBitmap, mScrollerX, 0, mPaint);
		}
		//取得视图树开始重绘的时间，给drawChild用，大概是用于重绘状态改变、同步之类
		final long drawingTime = getDrawingTime();

		/*
		for (int i = 0 ; i < this.getChildCount(); i++) {
			child =  getChildAt(i) ;
			if (child != null) {
				drawChild(canvas, child, getDrawingTime());
			}
		}

		*/


		//如果不是在自动滚动中，就只画当前屏
		//boolean fastDraw = mTouchState != TOUCH_STATE_AUTOSCROLL && mNextScreen == INVALID_SCREEN;
		boolean fastDraw = !mIsScrolling ;
		if (fastDraw) {
			child =  getChildAt(mCurrentScreen) ;
			if (child != null) {
				//Log.d(TAG, "mCurrentScreen is not null: " + mCurrentScreen) ;
				drawChild(canvas, child, drawingTime);
			}
		} else {//滚动中
			//Log.d(TAG, "not is fastDraw") ;
			//画左屏
			if (mCurrentScreen > 0) {
				child =  getChildAt(mCurrentScreen - 1) ;
				if (child != null) {
					drawChild(canvas, child, drawingTime);
				}
			}
			//画当前屏
			child =  getChildAt(mCurrentScreen) ;
			if (child != null) {
				drawChild(canvas, child, drawingTime);
			}
			//画右屏
			if (mCurrentScreen < mScreenCount -1) {
				child =  getChildAt(mCurrentScreen + 1) ;
				if (child != null) {
					drawChild(canvas, child, drawingTime);
				}
			}		
		}

/*
		if (restore) {
			canvas.restore();
		}
		*/
	}

	/**
	 * 页面重绘时使用图像缓存。可能是基于性能的考虑，会多用内存。画完后，用clearChildrenCache释放内存。
	 * 实际使用中，不如不用，或者只开一次，不释放。
	 *  */
	void enableChildrenCache() {
/*
		if (childrenCacheEnabled) {
			//System.out.println("childrenCacheEnabled before");
			return ;
		}
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final CellLayout layout = (CellLayout) getChildAt(i);
			layout.setChildrenDrawnWithCacheEnabled(true);
			layout.setChildrenDrawingCacheEnabled(true);
		}
		childrenCacheEnabled = true ;

*/
	}


	void clearChildrenCache() {
		
		/*
		if (!childrenCacheEnabled) {
			//System.out.println("clearChildrenCache before");
			return ;
		}
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final CellLayout layout = (CellLayout) getChildAt(i);
			layout.setChildrenDrawnWithCacheEnabled(false);
			layout.setChildrenDrawingCacheEnabled(false);
		}
		childrenCacheEnabled = false ;
		*/

	}



	/**
	 * 更新壁纸位置
	 */
	private void updateWallpaperOffset() {
		View child = getChildAt(getChildCount() - 1) ;
		if (child == null) {
			return ;
		}
		int scrollRange = child.getRight() - (getRight() - getLeft()) ;
		mWallpaperManager.setWallpaperOffsetSteps(1.0f / (getChildCount() - 1),0);
		//System.out.println("mScrollX=" + mScrollX + " scrollRange=" + scrollRange);
		int scrollX = mScrollerX ;
		if (scrollX > scrollRange) {
			scrollX = scrollRange ;
		}
		mWallpaperManager.setWallpaperOffsets(getWindowToken(), scrollX / (float) scrollRange, 0);
	}

	/**
	 * 将每一屏的child清除，然后将每一屏（CellLayout）清除
	 */
	public void clearWorksapce() {

		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final ViewGroup child = (ViewGroup) getChildAt(i);
			child.removeAllViews() ;
		}
		this.removeAllViews() ;
		//mWallpaperLoaded = false;
		mScreenCount = 0 ;
		mCapacityPerScreen = 0 ;
		mItemTotal = 0 ;
		mIndicator.setScreenCount(0);
	}

	public void release() {
		mScroller = null ;
		mPaint = null ;
		mWallpaperBitmap = null;	
		mIndicator = null ;
		mInflater = null ;
		mWallpaperManager = null ;
		
	}

}


package cn.netin.launcher;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;



public class CellLayout extends ViewGroup {
    private boolean mPortrait;
    private int mCellWidth;
    private int mCellHeight;    
    private int mLongAxisStartPadding;
    private int mLongAxisEndPadding;
    private int mShortAxisStartPadding;
    private int mShortAxisEndPadding;
    private int mShortAxisCells;
    private int mLongAxisCells;
    private int mWidthGap;
    private int mHeightGap;
    //private final CellInfo mCellInfo = new CellInfo();  
    int[] mCellXY = new int[2];
    int mChildCount = 0 ;
   // private final WallpaperManager mWallpaperManager;

    public CellLayout(Context context) {
        this(context, null);
    }

    public CellLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CellLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // R.styleable.CellLayout is defined in attrs.xml
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CellLayout, defStyle, 0);
        mCellWidth = a.getDimensionPixelSize(R.styleable.CellLayout_cellWidth, 10);
        mCellHeight = a.getDimensionPixelSize(R.styleable.CellLayout_cellHeight, 10);     
        mLongAxisStartPadding = a.getDimensionPixelSize(R.styleable.CellLayout_longAxisStartPadding, 10);
        mLongAxisEndPadding =  a.getDimensionPixelSize(R.styleable.CellLayout_longAxisEndPadding, 10);
        mShortAxisStartPadding = a.getDimensionPixelSize(R.styleable.CellLayout_shortAxisStartPadding, 10);
        mShortAxisEndPadding = a.getDimensionPixelSize(R.styleable.CellLayout_shortAxisEndPadding, 10);  
        mShortAxisCells = a.getInt(R.styleable.CellLayout_shortAxisCells, 2);
        mLongAxisCells = a.getInt(R.styleable.CellLayout_longAxisCells, 2);
        mPortrait =  a.getBoolean(R.styleable.CellLayout_isPort, false) ;
        a.recycle();

        /*
         * Indicates whether this ViewGroup will always try to draw its children using their drawing cache. 
         * This property can be set to true when the cache rendering is slightly different from the children's 
         * normal rendering. Renderings can be different, for instance, when the cache's quality is set to low. 
         * When this property is disabled, the ViewGroup will use the drawing cache of its children only 
         * when asked to. It's usually the task of subclasses to tell ViewGroup when to start using the drawing cache 
         * and when to stop using it.
         */
        //Todo.
        setAlwaysDrawnWithCacheEnabled(false);
        
//        mWallpaperManager = WallpaperManager.getInstance(getContext());        
    }

    /*
     * 
     * Cancels a pending long press. Your subclass can use this if you want the context menu to come up 
     * if the user presses and holds at the same place, but you don't want it to come up if they press 
     * and then move around enough to cause scrolling. 
     * @see android.view.View#cancelLongPress()
     */
    @Override
    public void cancelLongPress() {
        super.cancelLongPress();
        // Cancel long press for all children
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            child.cancelLongPress();
        }
    }
    
/**
 * 取水平格子数
 */
    int getCountX() {
        return mPortrait ? mShortAxisCells : mLongAxisCells;
    }

    /**
     * 取垂直格子数
     */
    int getCountY() {
        return mPortrait ? mLongAxisCells : mShortAxisCells;
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        // Generate an id for each view, this assumes we have at most 256x256 cells
        // per workspace screen
        final CellLayoutParams cellParams = (CellLayoutParams) params;
        cellParams.regenerateId = true;

        // 计算放在哪个格子   
        if (cellParams.cellX == -1) {
        	if (mPortrait) {
            	cellParams.cellX = mChildCount % mShortAxisCells ;
            	cellParams.cellY = mChildCount / mShortAxisCells  ;        		
        	}else{
        		cellParams.cellX = mChildCount % mLongAxisCells ;
        		cellParams.cellY = mChildCount / mLongAxisCells  ;
        	}
        }
        mChildCount++ ;
        super.addView(child, index, params);
    }

    /**
     * 指示一个子VIEW获得焦点时显示的Highlight区域
     * Request that a rectangle of this view be visible on the screen, scrolling if necessary just enough. 
A View should call this if it maintains some notion of which part of its content is interesting. 
For example, a text editing view should call this when its cursor moves.
     * @see android.view.ViewGroup#requestChildFocus(android.view.View, android.view.View)
     */
    @Override
    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
        if (child != null) {
            Rect r = new Rect();
            child.getDrawingRect(r);
            requestRectangleOnScreen(r);
        }
    }

 
    int getLeftPadding() {
        return mPortrait ? mShortAxisStartPadding : mLongAxisStartPadding;
    }

    int getTopPadding() {
        return mPortrait ? mLongAxisStartPadding : mShortAxisStartPadding;        
    }

    int getRightPadding() {
        return mPortrait ? mShortAxisEndPadding : mLongAxisEndPadding;
    }

    int getBottomPadding() {
        return mPortrait ? mLongAxisEndPadding : mShortAxisEndPadding;        
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO: currently ignoring padding
    	
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize =  MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize =  MeasureSpec.getSize(heightMeasureSpec);
        
        if (widthSpecMode == MeasureSpec.UNSPECIFIED || heightSpecMode == MeasureSpec.UNSPECIFIED) {
            throw new RuntimeException("CellLayout cannot have UNSPECIFIED dimensions");
        }

        final int shortAxisCells = mShortAxisCells;
        final int longAxisCells = mLongAxisCells;
        final int longAxisStartPadding = mLongAxisStartPadding;
        final int longAxisEndPadding = mLongAxisEndPadding;
        final int shortAxisStartPadding = mShortAxisStartPadding;
        final int shortAxisEndPadding = mShortAxisEndPadding;
        final int cellWidth = mCellWidth;
        final int cellHeight = mCellHeight;

        //mPortrait = heightSpecSize > widthSpecSize;
        // 长边一行有多少个间隔带，比如有四个格子，那么它们之间就有3个间隔带
        int numShortGaps = shortAxisCells - 1;
        // 短边一行有多少个间隔带
        int numLongGaps = longAxisCells - 1;

        //格子之间的间距根据剩余的空间均匀分配
        if (mPortrait) {
        	//垂直剩余的尺寸
            int vSpaceLeft = heightSpecSize - longAxisStartPadding - longAxisEndPadding - (cellHeight * longAxisCells);
            mHeightGap = vSpaceLeft / numLongGaps;

            //水平剩余的尺寸
            int hSpaceLeft = widthSpecSize - shortAxisStartPadding - shortAxisEndPadding  - (cellWidth * shortAxisCells);
            if (numShortGaps > 0) {
                mWidthGap = hSpaceLeft / numShortGaps;
            } else {
                mWidthGap = 0;
            }
        } else {
            int hSpaceLeft = widthSpecSize - longAxisStartPadding - longAxisEndPadding - (cellWidth * longAxisCells);
            mWidthGap = hSpaceLeft / numLongGaps;
            int vSpaceLeft = heightSpecSize - shortAxisStartPadding - shortAxisEndPadding - (cellHeight * shortAxisCells);
            if (numShortGaps > 0) {
                mHeightGap = vSpaceLeft / numShortGaps;
            } else {
                mHeightGap = 0;
            }
        }
        
        int count = getChildCount();

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            CellLayoutParams cellLayoutParams = (CellLayoutParams) child.getLayoutParams();

            //setup() :计算子VIEW的x, y, width, height
            if (mPortrait) {
                cellLayoutParams.setup(cellWidth, cellHeight, mWidthGap, mHeightGap, shortAxisStartPadding, longAxisStartPadding);
            } else {
                cellLayoutParams.setup(cellWidth, cellHeight, mWidthGap, mHeightGap, longAxisStartPadding, shortAxisStartPadding);
            }
            
            if (cellLayoutParams.regenerateId) {
                child.setId(((getId() & 0xFF) << 16) | (cellLayoutParams.cellX & 0xFF) << 8 | (cellLayoutParams.cellY & 0xFF));
                cellLayoutParams.regenerateId = false;
            }

            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(cellLayoutParams.width, MeasureSpec.EXACTLY);
            int childheightMeasureSpec = MeasureSpec.makeMeasureSpec(cellLayoutParams.height, MeasureSpec.EXACTLY);
            child.measure(childWidthMeasureSpec, childheightMeasureSpec);
        }

        setMeasuredDimension(widthSpecSize, heightSpecSize);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
     
     	int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                CellLayout.CellLayoutParams lp = (CellLayout.CellLayoutParams) child.getLayoutParams();
                int childLeft = lp.x;
                int childTop = lp.y;
                child.layout(childLeft, childTop, childLeft + lp.width, childTop + lp.height);
   /*             
                if (lp.dropped) {
                    lp.dropped = false;
                    final int[] cellXY = mCellXY;
                    getLocationOnScreen(cellXY);                    
                    mWallpaperManager.sendWallpaperCommand(getWindowToken(), "android.home.drop",
                           cellXY[0] + childLeft + lp.width / 2, cellXY[1] + childTop + lp.height / 2, 0, null);        
                }
                */
                
            }
        }
    }

    @Override
    protected void setChildrenDrawingCacheEnabled(boolean enabled) {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View view = getChildAt(i);
            view.setDrawingCacheEnabled(enabled);
            // Update the drawing caches
            view.buildDrawingCache(true);
        }
    }

    @Override
    protected void setChildrenDrawnWithCacheEnabled(boolean enabled) {
        super.setChildrenDrawnWithCacheEnabled(enabled);
    }


    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new CellLayout.CellLayoutParams(getContext(), attrs);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof CellLayout.CellLayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new CellLayout.CellLayoutParams(p);
    }

    /**
     * LayoutParams只有两个属性：height width 
     * MarginLayoutParams 多了：bottomMargin leftMargin rightMargin topMargin 
     * */
    public static class CellLayoutParams extends ViewGroup.MarginLayoutParams {
        /**
         * Horizontal location of the item in the grid.
         */
        //@ViewDebug.ExportedProperty
        public int cellX;

        /**
         * Vertical location of the item in the grid.
         */
        //@ViewDebug.ExportedProperty
        public int cellY;

        /**
         * Number of cells spanned horizontally by the item.
         */
        //@ViewDebug.ExportedProperty
        public int cellHSpan;

        /**
         * Number of cells spanned vertically by the item.
         */
        //@ViewDebug.ExportedProperty
        public int cellVSpan;
        
        /**
         * Is this item currently being dragged
         */
        public boolean isDragging;

        // X coordinate of the view in the layout.
        //@ViewDebug.ExportedProperty
        int x;
        // Y coordinate of the view in the layout.
       // @ViewDebug.ExportedProperty
        int y;
        
        boolean regenerateId;
        
        boolean dropped;        

        public CellLayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            cellHSpan = 1;
            cellVSpan = 1;
        }

        public CellLayoutParams(ViewGroup.LayoutParams source) {
            super(source);
            cellHSpan = 1;
            cellVSpan = 1;
        }
        
        public CellLayoutParams(int cellX, int cellY, int cellHSpan, int cellVSpan) {
            super(CellLayoutParams.MATCH_PARENT, CellLayoutParams.MATCH_PARENT);
            this.cellX = cellX;
            this.cellY = cellY;
            this.cellHSpan = cellHSpan;
            this.cellVSpan = cellVSpan;
        }

        public void setup(int cellWidth, int cellHeight, int widthGap, int heightGap,int hStartPadding, int vStartPadding) {
            
            final int myCellHSpan = cellHSpan;
            final int myCellVSpan = cellVSpan;
            final int myCellX = cellX;
            final int myCellY = cellY;
            
            width = myCellHSpan * cellWidth + ((myCellHSpan - 1) * widthGap) - leftMargin - rightMargin;
            height = myCellVSpan * cellHeight + ((myCellVSpan - 1) * heightGap) - topMargin - bottomMargin;
            x = hStartPadding + myCellX * (cellWidth + widthGap) + leftMargin;
            y = vStartPadding + myCellY * (cellHeight + heightGap) + topMargin;
        }
         
    }

}

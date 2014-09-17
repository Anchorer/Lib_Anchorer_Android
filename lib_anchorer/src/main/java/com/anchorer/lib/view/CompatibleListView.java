package com.anchorer.lib.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * View: CompatibleListView
 * Description: 自定义ListView，有如下特性：
 * 				1. 能够兼容内部嵌套的ViewPager（横向滑动控件），能够解决由于嵌套带来的滑动冲突问题
 * 				2. 能够监测滑动到顶部和底部的事件。
 * 				3. 能够检测滑动到指定位置的事件。
 * 			    4. 能够检测滑动状态改变的事件。
 *
 * 				使用时需要注意：
 * 				1. （可选）如果要实现滑动到顶部或者底部的监听，则需要在目标组件中实现OnListViewBorderListener接口，并且调用CompatibleListView的initOnBorderListener()方法进行初始化。
 * 				2. （可选）如果要实现滑动到指定位置的监听，则需要在目标组件中实现onListViewSpecifiedListener接口，并且调用CompatibleListView的initOnSpecifiedHeightListener()方法进行初始化。
 * 			    3. （可选）如果要实现滑动状态改变的监听，则需要在目标组件中实现OnListViewScrollStateChangedListener接口，并且调用CompatibleListView的initOnScrollStateChangeListener()方法进行初始化。
 *
 * Created by Anchorer/duruixue on 2014/8/16.
 * @author Anchorer
 */
public class CompatibleListView extends ListView {
	
	//滑动距离及坐标
	private float xDistance, yDistance, xLast, yLast;  
	
	//功能开关
	private boolean supportBorderDetectFlag = false;			//设定ListView是否支持顶部底部边缘检测
	private boolean supportSpecifiedHeightDetectFlag = false;	//设定ListView是否支持指定位置的滚动检测

	//标记ListView的滚动状态：顶部或者底部
	private final int SCROLL_STATE_TOP = 1;		//滚动到了顶部
	private final int SCROLL_STATE_BOTTOM = 2;	//滚动到了底部
	private final int SCROLL_STATE_MIDDLE = 3;	//滚动到了中间
	private int scrollState = SCROLL_STATE_BOTTOM;
	
	//标记ListView的滚动状态：指定位置
	private int specifiedFirstVisibleItemPosition;	//指定位置：ListView的子项位置
	private int specifiedFirstItemTopDistance;		//指定位置：ListView第一个子项滚动到的位置
	private final int SCROLL_STATE_SPECIFIED_TOP = 1;		//滚动到了指定位置的上方
	private final int SCROLL_STATE_SPECIFIED_BOTTOM = 2;	//滚动到了指定位置的下方
	private int scrollSpecifiedState = SCROLL_STATE_SPECIFIED_TOP;
	
	private boolean isHorizontalMove = true;

    /**
     * 公共接口：监听ListView滚动到上下边界
     */
	public static interface OnListViewBorderListener {
		public void onBottom();
		public void onTop();
	}
	private OnListViewBorderListener onBorderListener;

    /**
     * 公共接口：监听ListView滚动到指定位置的上下方
     */
	public static interface OnListViewSpecifiedHeightListener {
		public void onSpecifiedTop();
		public void onSpecifiedBottom();
	}
	private OnListViewSpecifiedHeightListener onSpecifiedListener;

    /**
     * 公共接口：监听ListView滚动状态改变
     */
    public static interface OnListViewScrollStateChangedListener {
        public void onStateChanged(AbsListView view, int state);
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount);
    }
    private OnListViewScrollStateChangedListener onStateChangedListener;
	
	public CompatibleListView(final Context context) {
		super(context);
		setOnScrollListener(new CompatibleListViewScrollListener());
	}
	
    public CompatibleListView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        setOnScrollListener(new CompatibleListViewScrollListener());
    }
    
    public CompatibleListView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        setOnScrollListener(new CompatibleListViewScrollListener());
    }
    
    /**
	 * 初始化ListView的滚动监听器
	 */
	public void initOnBorderListener(OnListViewBorderListener listener) {
		supportBorderDetectFlag = true;
		onBorderListener = listener;
	}
	public void initOnSpecifiedHeightListener(OnListViewSpecifiedHeightListener listener, int specifiedFirstVisibleItemPosition, int specifiedFirstItemTopDistance) {
		supportSpecifiedHeightDetectFlag = true;
		onSpecifiedListener = listener;
		this.specifiedFirstVisibleItemPosition = specifiedFirstVisibleItemPosition;
		this.specifiedFirstItemTopDistance = specifiedFirstItemTopDistance;
	}
    public void initOnScrollStateChangeListener(OnListViewScrollStateChangedListener listener) {
        onStateChangedListener = listener;
    }
    
	/**
	 * 监听器：滑动监听，监听ListView滑动到了顶部或底部
	 */
	public class CompatibleListViewScrollListener implements OnScrollListener {

		public CompatibleListViewScrollListener() {}
		
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
            if(onStateChangedListener != null)
                onStateChangedListener.onStateChanged(view, scrollState);
        }
		
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if(onStateChangedListener != null)
                onStateChangedListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);

			/**
			 * 判断滑到顶部或者底部
			 */
			if(supportBorderDetectFlag) {
				if(visibleItemCount + firstVisibleItem >= totalItemCount) {
					//滑动到了底部
					if(scrollState != SCROLL_STATE_BOTTOM) {
						if(onBorderListener != null)
							onBorderListener.onBottom();
						scrollState = SCROLL_STATE_BOTTOM;
					}
				}
				else if(firstVisibleItem == 0) {
					//滑动到了顶部
					if(scrollState != SCROLL_STATE_TOP) {
						if(onBorderListener != null)
							onBorderListener.onTop();
						scrollState = SCROLL_STATE_TOP;
					}
				}
				else {
					//滑动到了中间
					scrollState = SCROLL_STATE_MIDDLE;
				}
			}
			
			/**
			 * 判断滑到指定位置的上方或者下方
			 */
			if(supportSpecifiedHeightDetectFlag) {
				if(firstVisibleItem < specifiedFirstVisibleItemPosition) {
					//滑动到了指定位置的上方
					scrolledToSpecifiedTop();
				} else if(firstVisibleItem == specifiedFirstVisibleItemPosition) {
					View v = view.getChildAt(0);
					int height = Math.abs(v == null ? 0 : v.getTop());
					if(height > specifiedFirstItemTopDistance) {
						//滑动到指定位置的下方
						scrolledToSpecifiedBottom();
					} else {
						//滑动到了指定位置的上方
						scrolledToSpecifiedTop();
					}
				} else {
					//滑动到指定位置的下方
					scrolledToSpecifiedBottom();
				}
			}
		}
	}
	
	/**
	 *  滚动到指定位置的上方
	 */
	private void scrolledToSpecifiedTop() {
		if(scrollSpecifiedState != SCROLL_STATE_SPECIFIED_TOP) {
			if(onSpecifiedListener != null)
				onSpecifiedListener.onSpecifiedTop();
			scrollSpecifiedState = SCROLL_STATE_SPECIFIED_TOP;
		}
	}
	
	/**
	 * 滚动到指定位置的下方
	 */
	private void scrolledToSpecifiedBottom() {
		if(scrollSpecifiedState != SCROLL_STATE_SPECIFIED_BOTTOM) {
			if(onSpecifiedListener != null)
				onSpecifiedListener.onSpecifiedBottom();
			scrollSpecifiedState = SCROLL_STATE_SPECIFIED_BOTTOM;
		}
	}
	
    /**
     * 拦截横向滑动事件
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
    	switch (ev.getAction()) {  
        case MotionEvent.ACTION_DOWN: 
        	isHorizontalMove = false;
            xDistance = yDistance = 0f;  
            xLast = ev.getX();  
            yLast = ev.getY();  
            break;  
        case MotionEvent.ACTION_MOVE:  
            final float curX = ev.getX();  
            final float curY = ev.getY();  
              
            xDistance += Math.abs(curX - xLast);  
            yDistance += Math.abs(curY - yLast);  
            xLast = curX;  
            yLast = curY;  
              
            if(xDistance > yDistance){  
            	isHorizontalMove = true;
                return false;  
            } else {
            	isHorizontalMove = false;
            }
	    }  
	    return super.onInterceptTouchEvent(ev);
    }
	
    public boolean isHorizontalMove() {
		return isHorizontalMove;
	}
    
}

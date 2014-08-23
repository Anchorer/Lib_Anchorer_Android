package com.anchorer.lib.view;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

/**
 * View: CompatibleScrollView
 * Description: 自定义ScrollView，有如下特性：
 * 				1. 解决ScrollView嵌套ViewPage带来的滑动冲突问题
 * 				2. 带有滑动到顶部以及底部的监听，只需要在目标组件中实现OnBorderListener接口并进行初始化
 * 				3. 带有滑动到指定位置的监听器，只需要在目标组件中实现OnScrolledToSpecificHeightListener接口并进行初始化
 *
 * Created by Anchorer/duruixue on 2014/8/16.
 * @author Anchorer
 */
public class CompatibleScrollView extends ScrollView {
	//滑动距离及坐标
	private float xDistance, yDistance, xLast, yLast;  
	
	//标记ScrollView的滚动状态
	private final int SCROLL_STATE_TOP = 1;		//滚动到了顶部
	private final int SCROLL_STATE_BOTTOM = 2;	//滚动到了底部
	private final int SCROLL_STATE_MIDDLE = 3;	//在中间滚动
	private int scrollState = SCROLL_STATE_TOP;
	
	//公共接口：监听ScrollView滚动到上下边界
	public static interface OnBorderListener {
		public void onBottom();
		public void onTop();
	}
	private OnBorderListener onBorderListener;
	
	//公共接口：监听ScrollView滚动到某个位置
	private int scrollHeight;
	private final int SCROLL_STATE_HEIGHT_TOP = 11;		//滚动到了指定位置的上方
	private final int SCROLL_STATE_HEIGHT = 12;			//滚动到了指定位置
	private final int SCROLL_STATE_HEIGHT_BOTTOM = 13;	//滚动到了指定位置的下方
	private int scrollStateForSpecificPosition = SCROLL_STATE_HEIGHT_TOP;
	public static interface OnScrolledToSpecificHeightListener {
		public void scrolledToTop();
		public void scrolledToBottom();
	}
	private OnScrolledToSpecificHeightListener onScrolledToSpecificHeightListener;
	
	//功能开关
	private boolean supportBorderDetectFlag = false;			//是否支持滚动到边缘检测
	private boolean supportSpecifiedHeightDetectFlag = false;	//是否支持滚动到指定位置检测
	
	private boolean supportScrolling = true;
	
	
	public CompatibleScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	/**
	 * 初始化ScrollView的滚动监听器
	 */
	public void initOnBorderListener(Activity activity) {
		supportBorderDetectFlag = true;
		onBorderListener = (OnBorderListener) activity;
	}
	public void initOnBorderListener(Fragment fragment) {
		supportBorderDetectFlag = true;
		onBorderListener = (OnBorderListener) fragment;
	}
	public void initOnScrolledToSpecificHeightListener(Activity activity, int scrollHeight) {
		supportSpecifiedHeightDetectFlag = true;
		onScrolledToSpecificHeightListener = (OnScrolledToSpecificHeightListener) activity;
		this.scrollHeight = scrollHeight;
	}
	public void initOnScrolledToSpecificHeightListener(Fragment fragment, int scrollHeight) {
		supportSpecifiedHeightDetectFlag = true;
		onScrolledToSpecificHeightListener = (OnScrolledToSpecificHeightListener) fragment;
		this.scrollHeight = scrollHeight;
	}
	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		doOnBorderListener();
		doOnScrolledToSpecificHeightListener();
	}
	
	/**
	 * 检测ScrollView的滑动状态，触发OnBorderListener接口
	 */
	private void doOnBorderListener() {
		if(supportBorderDetectFlag) {
			View contentView = getChildAt(0);
			if(contentView != null && contentView.getMeasuredHeight() <= getScrollY() + getHeight()) {
				//滑动到了底部
				if(scrollState != SCROLL_STATE_BOTTOM) {
					if(onBorderListener != null)
						onBorderListener.onBottom();
					scrollState = SCROLL_STATE_BOTTOM;
				}
			} else if(getScrollY() == 0) {
				//滑动到了顶部
				if(scrollState != SCROLL_STATE_TOP) {
					if(onBorderListener != null)
						onBorderListener.onTop();
					scrollState = SCROLL_STATE_TOP;
				}
			} else {
				//滑动到了中间
				scrollState = SCROLL_STATE_MIDDLE;
			}
		}
	}
	
	/**
	 * 检测ScrollView的滚动状态，触发OnScrolledToSpecificHeightListener接口
	 */
	private void doOnScrolledToSpecificHeightListener() {
		if(supportSpecifiedHeightDetectFlag) {
			if(getScrollY() < scrollHeight) {
				if(scrollStateForSpecificPosition != SCROLL_STATE_HEIGHT_TOP) {
					//滑到了指定位置的上方
					if(onScrolledToSpecificHeightListener != null)
						onScrolledToSpecificHeightListener.scrolledToTop();
					scrollStateForSpecificPosition = SCROLL_STATE_HEIGHT_TOP;
				}
			} else if(getScrollY() > scrollHeight) {
				if(scrollStateForSpecificPosition != SCROLL_STATE_HEIGHT_BOTTOM) {
					//滑到了指定位置的下方
					if(onScrolledToSpecificHeightListener != null)
						onScrolledToSpecificHeightListener.scrolledToBottom();
					scrollStateForSpecificPosition = SCROLL_STATE_HEIGHT_BOTTOM;
				}
			} else {
				//滑到了指定位置
				scrollStateForSpecificPosition = SCROLL_STATE_HEIGHT;
			}
		}
	}
	
	/**
	 * 拦截横向滑动事件，解决与ViewPager嵌套所带来的滑动冲突问题
	 */
	@Override  
    public boolean onInterceptTouchEvent(MotionEvent ev) {  
		if(!supportScrolling) {
			return false;
		}
		
        switch (ev.getAction()) {  
            case MotionEvent.ACTION_DOWN:  
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
                    return false;  
                }    
        }  
        return super.onInterceptTouchEvent(ev);  
    }  
	
	public void setSupportScrolling(boolean supportScrolling) {
		this.supportScrolling = supportScrolling;
	}
	
}

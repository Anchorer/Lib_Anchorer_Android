package com.anchorer.lib.view;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ListView;


/**
 * View: OnBorderListView
 * Description: 自定义ListView，有如下特性：
 * 				1. 能够监测滑动到顶部和底部的事件。
 * 				使用时需要注意：
 * 				1. （可选）如果要实现滑动到顶部或者底部的监听，则需要在目标组件中实现OnListViewBorderListener接口，并且调用initInterface()方法进行初始化。
 * 				2. （可选）如果要实现对列表滑动事件的监听，则需要在目标组件中实现OnListViewScrolledListener接口，并且调用initInterface()方法进行初始化。
 *
 * Created by Anchorer/duruixue on 2013/12/13.
 * @author Anchorer
 */
public class OnBorderListView extends ListView {
	
	//标记ListView的滚动状态：顶部或者底部
	private final int SCROLL_STATE_TOP = 1;		//滚动到了顶部
	private final int SCROLL_STATE_BOTTOM = 2;	//滚动到了底部
	private final int SCROLL_STATE_MIDDLE = 3;	//在中间滚动
	private int scrollState = SCROLL_STATE_BOTTOM;

	//公共接口：监听ListView滚动到上下边界
	public static interface OnListViewBorderListener {
		public void onBottom();
		public void onTop();
	}
	private OnListViewBorderListener onBorderListener;
	
	//公共接口：监听ListView的滚动事件
	public static interface OnListViewScrolledListener {
		public void onListViewScrolled();
		public void onListViewScrollStateChanged();
	}
	private OnListViewScrolledListener mScrolledListener;
	
	public OnBorderListView(Context context) {
		super(context);
		setOnScrollListener(new ListViewScrollListener());
	}
	
	public OnBorderListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnScrollListener(new ListViewScrollListener());
	}
	
	public OnBorderListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setOnScrollListener(new ListViewScrollListener());
	}
	
	public void initInterface(Fragment fragment) {
		if(fragment instanceof OnListViewBorderListener)
			onBorderListener = (OnListViewBorderListener) fragment;
		if(fragment instanceof OnListViewScrolledListener)
			mScrolledListener = (OnListViewScrolledListener) fragment;
	}
	public void initInterface(Activity activity) {
		if(activity instanceof OnListViewBorderListener)
			onBorderListener = (OnListViewBorderListener) activity;
		if(activity instanceof OnListViewScrolledListener)
			mScrolledListener = (OnListViewScrolledListener) activity;
	}
	
	
	/**
	 * 监听器：滑动监听，监听ListView滑动到了顶部或底部
	 */
	public class ListViewScrollListener implements OnScrollListener {
		public ListViewScrollListener() {}
		
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if(mScrolledListener != null) {
				mScrolledListener.onListViewScrollStateChanged();
			}
		}
		
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			if(mScrolledListener != null) {
				mScrolledListener.onListViewScrolled();
			}
			
			/**
			 * 判断滑到顶部或者底部
			 */
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
	}
	
}

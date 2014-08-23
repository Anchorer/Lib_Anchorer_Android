package com.anchorer.lib.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * View: BlocksClickRelativeLayout
 * 自定义RelativeLayout，可以拦截点击事件，使事件不再向其子控件传递。
 * Custom RelativeLayout. This RelativeLayout can intercept click event, preventing any touch event
 * from passing to its children.
 *
 * Created by Anchorer/duruixue on 2014/8/16.
 * @author Anchorer
 */
public class BlocksClickRelativeLayout extends RelativeLayout {

	public BlocksClickRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public BlocksClickRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public BlocksClickRelativeLayout(Context context) {
		super(context);
	}

    /**
     * Intercept any touch event.
     * @param ev    event
     */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return true;
	}

}

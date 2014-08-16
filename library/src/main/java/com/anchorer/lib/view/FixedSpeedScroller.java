package com.anchorer.lib.view;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * Utility: FixedSpeedScroller
 * Description: 自定义Scroller，在原来Scroller的基础上改变滚动的时间
 *
 * Created by Anchorer/duruixue on 2013/7/31.
 * @author Anchorer
 */
public class FixedSpeedScroller extends Scroller {
	private int DURATION_FIXED = 300;
	
	public FixedSpeedScroller(Context context) {
		super(context);
	}
	
	public FixedSpeedScroller(Context context, Interpolator interpolator) {
		super(context, interpolator);
	}

	@Override
	public void startScroll(int startX, int startY, int dx, int dy, int duration) {
		super.startScroll(startX, startY, dx, dy, DURATION_FIXED);
	}
	
	@Override
	public void startScroll(int startX, int startY, int dx, int dy) {
		super.startScroll(startX, startY, dx, dy, DURATION_FIXED);
	}
	
	public void setmDuration(int mDuration) {
		this.DURATION_FIXED = mDuration;
	}
	
	public int getmDuration() {
		return DURATION_FIXED;
	}
}

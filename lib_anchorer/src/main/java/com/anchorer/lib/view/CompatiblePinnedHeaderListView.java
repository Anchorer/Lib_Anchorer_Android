package com.anchorer.lib.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListAdapter;

/**
 *  * View: CompatiblePinnedHeaderListView
 * Description: 自定义CompatibleListView，除原有兼容特性外，增加了如下特性：
 * 				带有PinnedHeader特性，即标签项可以固定在列表头部
 *
 * 				使用时需要注意：
 * 				1. （必选）列表的自定义Adapter需要实现PinnedHeaderAdapter接口
 * 				2. （可选）如果要实现PinnedHeader的功能，则需要在目标组件中对ListView执行setPinnedHeaderView方法进行初始化
 * 				3. （可选）如果要实现滑动到顶部或者底部的监听，则需要在目标组件中实现OnListViewBorderListener接口，并且通过initOnBorderListener()方法进行初始化
 *
 * Created by Anchorer/duruixue on 2014/8/16.
 * @author Anchorer
 */
public class CompatiblePinnedHeaderListView extends CompatibleListView {
	
	/**
     * Adapter interface.  The list adapter must implement this interface.
     */
    public interface PinnedHeaderAdapter {

        /**
         * Pinned header state: don't show the header.
         */
        public static final int PINNED_HEADER_GONE = 0;

        /**
         * Pinned header state: show the header at the top of the list.
         */
        public static final int PINNED_HEADER_VISIBLE = 1;

        /**
         * Pinned header state: show the header. If the header extends beyond
         * the bottom of the first shown element, push it up and clip.
         */
        public static final int PINNED_HEADER_PUSHED_UP = 2;
        
        public static final int PINNED_HEADER_PUSHED_NULL = -1;
        
        /**
         * Computes the desired state of the pinned header for the given
         * position of the first visible list item. Allowed return values are
         * {@link #PINNED_HEADER_GONE}, {@link #PINNED_HEADER_VISIBLE} or
         * {@link #PINNED_HEADER_PUSHED_UP}.
         */
        int getPinnedHeaderState(int position);

        /**
         * Configures the pinned header view to match the first visible list item.
         *
         * @param header pinned header view.
         * @param position position of the first visible list item.
         * @param alpha fading of the header view, between 0 and 255.
         */
        void configurePinnedHeader(View header, int position, int alpha);
    }
    
    //实现PinnedHeader的各种参数
    private static final int MAX_ALPHA = 255;

    private PinnedHeaderAdapter mAdapter;
    private View mHeaderView;
    private boolean mHeaderViewVisible;

    private int mHeaderViewWidth;
    private int mHeaderViewHeight;
	
    public CompatiblePinnedHeaderListView(Context context) {
		super(context);
		setOnScrollListener(new CompatiblePinnedHeaderListViewScrollListener());
	}
    
	public CompatiblePinnedHeaderListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnScrollListener(new CompatiblePinnedHeaderListViewScrollListener());
	}
	
	public CompatiblePinnedHeaderListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setOnScrollListener(new CompatiblePinnedHeaderListViewScrollListener());
	}
	
	/**
	 * 初始化ListView的滚动监听器
	 */
	public void initOnBorderListener(OnListViewBorderListener listener) {
		super.initOnBorderListener(listener);
	}
	
	/**
	 * 监听器：滑动监听，监听ListView滑动到了顶部或底部
	 */
	public class CompatiblePinnedHeaderListViewScrollListener extends CompatibleListViewScrollListener {
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			configureHeaderView(firstVisibleItem);
			super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
		}
	}
	
	public void setPinnedHeaderView(View view) {
        mHeaderView = view;

        // Disable vertical fading when the pinned header is present
        // change ListView to allow separate measures for top and bottom fading edge;
        // in this particular case we would like to disable the top, but not the bottom edge.
        if (mHeaderView != null) {
            setFadingEdgeLength(0);
        }
        requestLayout();
    }
	
	@Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        mAdapter = (PinnedHeaderAdapter)adapter;
    }
	
	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mHeaderView != null) {
            measureChild(mHeaderView, widthMeasureSpec, heightMeasureSpec);
            mHeaderViewWidth = mHeaderView.getMeasuredWidth();
            mHeaderViewHeight = mHeaderView.getMeasuredHeight();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mHeaderView != null) {
            mHeaderView.layout(0, 0, mHeaderViewWidth, mHeaderViewHeight);
            configureHeaderView(getFirstVisiblePosition());
        }
    }
    
    public void configureHeaderView(int position) {
        if (mHeaderView == null) {
            return;
        }
        
        int state = PinnedHeaderAdapter.PINNED_HEADER_PUSHED_NULL;
        if(mAdapter != null) {
        	state = mAdapter.getPinnedHeaderState(position);
        }
        switch (state) {
            case PinnedHeaderAdapter.PINNED_HEADER_GONE: {
                mHeaderViewVisible = false;
                break;
            }

            case PinnedHeaderAdapter.PINNED_HEADER_VISIBLE: {
                mAdapter.configurePinnedHeader(mHeaderView, position, MAX_ALPHA);
                if (mHeaderView.getTop() != 0) {
                    mHeaderView.layout(0, 0, mHeaderViewWidth, mHeaderViewHeight);
                }
                mHeaderViewVisible = true;
                break;
            }

            case PinnedHeaderAdapter.PINNED_HEADER_PUSHED_UP: {
                View firstView = getChildAt(0);
                int bottom = (firstView == null) ? 0 : firstView.getBottom();
                int headerHeight = mHeaderView.getHeight();
                int y;
                int alpha;
                if (bottom < headerHeight) {
                    y = (bottom - headerHeight);
                    alpha = MAX_ALPHA * (headerHeight + y) / headerHeight;
                } else {
                    y = 0;
                    alpha = MAX_ALPHA;
                }
                mAdapter.configurePinnedHeader(mHeaderView, position, alpha);
                if (mHeaderView.getTop() != y) {
                    mHeaderView.layout(0, y, mHeaderViewWidth, mHeaderViewHeight + y);
                }
                mHeaderViewVisible = true;
                break;
            }
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mHeaderViewVisible) {
            drawChild(canvas, mHeaderView, getDrawingTime());
        }
    }
    
}

package com.anchorer.lib.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.anchorer.lib.R;
import com.anchorer.lib.utils.SystemUtils;

/**
 * View: CompatiblePinnedHeaderRefreshableListView
 * Description: 自定义CompatiblePinnedHeaderListView，改写自开源项目，在CompatiblePinnedHeaderListView的基础上增加了如下特性：
 * 				下拉刷新
 *
 * 				使用时需要注意（当然也包含CompatiblePinnedHeaderListView使用时需要注意的一些地方）：
 * 				1. （必选）列表的自定义Adapter需要实现PinnedHeaderAdapter接口
 * 				2. （必选）使用列表必须先调用initializePullHeader()方法对列表头部进行初始化
 * 				3. （可选）如果要实现PinnedHeader的功能，则需要在目标组件中对ListView执行setPinnedHeaderView方法进行初始化
 * 				4. （可选）如果要实现滑动到顶部或者底部的监听，则需要在目标组件中实现OnPinnedHeaderListViewBorderListener接口，并且需要通过initOnBorderListener方法进行初始化
 * 				5. （可选）如果要实现下拉刷新的功能，则需要在目标组件中为ListView设置OnPinnedHeaderRefreshListener
 *
 * 				关于自定义适配器的写法。
 * 				由于该ListView具有下拉刷新和PinnedHeader双重特性，因此在自定义适配器时需要注意如下问题：
 * 				1. ListView下拉刷新时，其头部的下拉刷新提示布局作为Header View添加到ListView的头部，使得ListView的真实长度增加了1，而下拉的头部则为ListView位置1处的子项。
 * 				2. PinnedHeaderAdapter是以列表数据源以及标签数据源两个List的数据源来标定PinnedHeader的显示的，因此在自定义适配器时需要注意由于下拉头部增加所带来的影响。
 * 				具体来说，需要区分数据在数据源中的位置和ListView的子项在ListView中的位置。有关的参考信息如下：
 * 					(1) 对于ListView显示的子项和数据源中的对应关系，ListView中position处的子项显示的数据为数据源List中position-1处的位置。
 * 					(2) getView()和bindSectionHeader()方法中的参数position是指数据源中数据的位置。
 * 					(3) configPinnedHeader(), getPositionForSection()以及getSectionForPosition()方法所对应的position均为ListView中子项的位置。
 *
 *  Created by  Anchorer/duruixue on 2014/8/16.
 *  @author Anchorers
 */
public class CompatiblePinnedHeaderRefreshableListView extends CompatiblePinnedHeaderListView {
	
	//列表Header部分的布局与控件
    private View mHeaderContainer = null;
    private View mHeaderView = null;
    private ImageView mArrow = null;
    private ProgressBar mProgress = null;
    private TextView mText = null;
    private TextView mTimeText = null;
    
    //用来执行下拉的机制
    private float mY = 0;
    private float mHistoricalY = 0;
    private int mHistoricalTop = 0;
    private int mInitialHeight = 0;
    private boolean mFlag = false;
    private boolean mArrowUp = false;
    private boolean mIsRefreshing = false;
    private int mHeaderHeight = 0;		//Height of header
    private int mHeaderTriggerHeight = 0;	//Height of trigger line for header
    private long lastUpdateTimeMillis = 0;
    
    private final int DISPLACE_BACK_DENSITY = 4;	//用来控制列表回滚时的速度
    
    //公共接口：执行刷新动作
    public interface OnPinnedHeaderRefreshListener {
    	/**
    	 * 执行刷新列表的动作
    	 * @param listView	目标ListView
    	 * @param forceFlush	是否不判断时间间隔而强制进行刷新
    	 */
        public void onRefresh(CompatiblePinnedHeaderRefreshableListView listView, boolean forceFlush);
    }
    private OnPinnedHeaderRefreshListener mListener = null;

    //标记列表的下拉状态
    private static final int REFRESH = 0;
    private static final int NORMAL = 1;
    
    //自定义的显示文字
    private String pullingHintText;
    private String toGoHintText;
    private String refreshingHintText;

    public CompatiblePinnedHeaderRefreshableListView(final Context context) {
        super(context);
    }

    public CompatiblePinnedHeaderRefreshableListView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public CompatiblePinnedHeaderRefreshableListView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 为列表设置OnRefreshListener
     */
    public void setOnRefreshListener(final OnPinnedHeaderRefreshListener listener) {
        mListener = listener;
    }
    
    /**
     * 初始化头部的布局
     * @param layoutResId		头部布局文件的ID
     * @param listHeaderResId	头部父控件的资源ID，类型为RelativeLayout/LinearLayout
     * @param arrowResId		头部箭头的资源ID，类型为ImageView
     * @param progressResId		头部圆形加载框的资源ID，类型为ProgressBar
     * @param textResId			头部提示文字控件的资源ID，类型为TextView
     * @param timeTextResId		头部提示时间控件的资源ID，类型为TextView
     * @param headerHeightDp		头部总的高度，单位为DP
     * @param headerTriggerHeightDp	头部触发刷新线的高度，单位为DP，这两个参数均根据自定义的布局进行设置
     * @param pullingHintText	正在下拉的文字提示
     * @param toGoHintText		松开刷新的文字提示
     * @param refreshingHintText	正在更新的文字提示
     */
    public void initializePullHeader(int layoutResId, int listHeaderResId, int arrowResId, int progressResId, int textResId, int timeTextResId, float headerHeightDp, float headerTriggerHeightDp, String pullingHintText, String toGoHintText, String refreshingHintText) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mHeaderContainer = inflater.inflate(layoutResId, null);
        mHeaderView = mHeaderContainer.findViewById(listHeaderResId);
        mArrow = (ImageView) mHeaderContainer.findViewById(arrowResId);
        mProgress = (ProgressBar) mHeaderContainer.findViewById(progressResId);
        mText = (TextView) mHeaderContainer.findViewById(textResId);
        mTimeText = (TextView) mHeaderContainer.findViewById(timeTextResId);
        addHeaderView(mHeaderContainer);

        mHeaderHeight = (int) (headerHeightDp * getContext().getResources().getDisplayMetrics().density);
        mHeaderTriggerHeight = (int) (headerTriggerHeightDp * getContext().getResources().getDisplayMetrics().density);
        setHeaderHeight(0);
        
        this.pullingHintText = pullingHintText;
        this.toGoHintText = toGoHintText;
        this.refreshingHintText = refreshingHintText;
    }

    /**
     * 刷新成功，隐藏头部布局
     * @param updateLastTime	是否更新上次刷新时间
     */
    public void completeRefreshing(boolean updateLastTime) {
    	if(updateLastTime)
    		this.lastUpdateTimeMillis = System.currentTimeMillis() / 1000;
    	
        mProgress.setVisibility(View.GONE);
        mArrow.setVisibility(View.VISIBLE);
        mHandler.sendMessage(mHandler.obtainMessage(NORMAL, mHeaderTriggerHeight, 0));
        mIsRefreshing = false;
        invalidateViews();
    }

    @Override
    public boolean onInterceptTouchEvent(final MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mHandler.removeMessages(REFRESH);
                mHandler.removeMessages(NORMAL);
                mY = mHistoricalY = ev.getY();
                if (mHeaderContainer.getLayoutParams() != null) {
                    mInitialHeight = mHeaderContainer.getLayoutParams().height;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(final MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
            	if(getChildAt(0) != null)
            		mHistoricalTop = getChildAt(0).getTop();
                break;
            case MotionEvent.ACTION_UP:
                if (!mIsRefreshing) {
                    if (mArrowUp) {
                        startRefreshing(false);
                        mHandler.sendMessage(mHandler.obtainMessage(REFRESH, (int) (ev.getY() - mY) / 2 + mInitialHeight, 0));
                    } else {
                        if (getChildAt(0) != null && getChildAt(0).getTop() == 0) {
                            mHandler.sendMessage(mHandler.obtainMessage(NORMAL, (int) (ev.getY() - mY) / 2 + mInitialHeight, 0));
                        }
                    }
                } else {
                    mHandler.sendMessage(mHandler.obtainMessage(REFRESH, (int) (ev.getY() - mY) / 2 + mInitialHeight, 0));
                }
                mFlag = false;
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean dispatchTouchEvent(final MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            float direction = ev.getY() - mHistoricalY;
            int height = (int) (ev.getY() - mY) / 2 + mInitialHeight;
            if (height < 0) {
                height = 0;
            }

            float deltaY = Math.abs(mY - ev.getY());
            ViewConfiguration config = ViewConfiguration.get(getContext());
            if (deltaY > config.getScaledTouchSlop()) {

                // Scrolling downward
                if (direction > 0) {
                    // Refresh bar is extended if top pixel of the first item is visible
                    if (getChildAt(0) != null && getChildAt(0).getTop() == 0) {
                        if (mHistoricalTop > 0) {
                             mY = ev.getY(); 
                        }

                        // Extends refresh bar
                        setHeaderHeight(height);

                        // Stop list scroll to prevent the list from overscrolling
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                        mFlag = false;
                    } else {
                    	mY = ev.getY();
                    }
                } else if (direction < 0) {
                    // Scrolling upward
                    // Refresh bar is shortened if top pixel of the first item is visible
                    if (getChildAt(0) != null && getChildAt(0).getTop() == 0) {
                        setHeaderHeight(height);

                        // If scroll reaches top of the list, list scroll is enabled
                        if (getChildAt(1) != null && getChildAt(1).getTop() <= 1 && !mFlag) {
                            ev.setAction(MotionEvent.ACTION_DOWN);
                            mFlag = true;
                        }
                    }
                }
            }
            mHistoricalY = ev.getY();
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean performItemClick(final View view, final int position, final long id) {
        // When position == 0, this is the refresh header element
        return position == 0 || super.performItemClick(view, position - 1, id);
    }

    private void setHeaderHeight(final int height) {
        if (height <= 1) {
            mHeaderView.setVisibility(View.GONE);
        } else {
            mHeaderView.setVisibility(View.VISIBLE);
            mTimeText.setText("上次更新：" + SystemUtils.getLastUpdateTimeHintStr(getLastUpdateInterval()));
        }

        // Extends refresh bar
        LayoutParams lp = (LayoutParams) mHeaderContainer.getLayoutParams();
        if (lp == null) {
            lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        }
        lp.height = height;
        mHeaderContainer.setLayoutParams(lp);

        // Refresh bar shows up from bottom to top
        LinearLayout.LayoutParams headerLp = (LinearLayout.LayoutParams) mHeaderView.getLayoutParams();
        if (headerLp == null) {
            headerLp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        }
        headerLp.topMargin = -mHeaderHeight + height;
        mHeaderView.setLayoutParams(headerLp);

        if (!mIsRefreshing) {
            // If scroll reaches the trigger line, start refreshing
            if (height > mHeaderTriggerHeight && !mArrowUp) {
                mArrow.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.rotate));
                mText.setText(toGoHintText);
                rotateArrow();
                mArrowUp = true;
            } else if (height < mHeaderTriggerHeight && mArrowUp) {
                mArrow.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.rotate));
                mText.setText(pullingHintText);
                rotateArrow();
                mArrowUp = false;
            }
        }
    }

    private void rotateArrow() {
        Drawable drawable = mArrow.getDrawable();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.save();
        canvas.rotate(180.0f, canvas.getWidth() / 2.0f, canvas.getHeight() / 2.0f);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        canvas.restore();
        mArrow.setImageBitmap(bitmap);
    }

    /**
     * 开始执行刷新的操作
     */
    private void startRefreshing(boolean forceFlush) {
        mArrow.setVisibility(View.GONE);
        mProgress.setVisibility(View.VISIBLE);
        mText.setText(refreshingHintText);
        mIsRefreshing = true;

        if (mListener != null) {
            mListener.onRefresh(this, forceFlush);
        }
    }
    
    /**
     * 目标组件调用：启动刷新动作。
     */
    public void startRefreshingFromOutside(boolean forceFlush) {
    	startRefreshing(forceFlush);
    	mHandler.sendMessage(mHandler.obtainMessage(REFRESH, mHeaderTriggerHeight, 0));
    }

    @SuppressLint("HandlerLeak")
	private final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);

            int limit = 0;
            switch (msg.what) {
                case REFRESH:
                    limit = mHeaderTriggerHeight;
                    break;
                case NORMAL:
                    limit = 0;
                    break;
            }

            // Elastic scrolling
            if(!(msg.what == REFRESH && !mIsRefreshing)) {
	            if (msg.arg1 >= limit) {
	                setHeaderHeight(msg.arg1);
	                int displacement = (msg.arg1 - limit) / DISPLACE_BACK_DENSITY;
	                if (displacement == 0) {
	                    mHandler.sendMessage(mHandler.obtainMessage(msg.what, msg.arg1 - 1, 0));
	                } else {
	                    mHandler.sendMessage(mHandler.obtainMessage(msg.what, msg.arg1 - displacement, 0));
	                }
	            }
            }
        }
    };
    
    /**
     * 获取到距离上次更新的时间间隔（单位为s）
     */
    public long getLastUpdateInterval() {
    	return System.currentTimeMillis() / 1000 - this.lastUpdateTimeMillis;
    }

}

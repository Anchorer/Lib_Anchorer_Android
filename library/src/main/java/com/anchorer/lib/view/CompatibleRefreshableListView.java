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
 * View: CompatibleRefreshableListView
 * Description: 自定义CompatibleListView，改写自开源项目，在CompatibleListView的基础上增加了如下特性：
 * 				下拉刷新
 *
 * 				使用时需要注意：
 * 				1. （必选）使用列表必须先调用initializePullHeader()方法对列表头部进行初始化
 * 				2. （可选）如果要实现滑动到顶部或者底部的监听，则需要在目标组件中实现OnListViewBorderListener接口，并且通过initOnBorderListener()方法进行初始化
 * 				3. （可选）如果要实现下拉刷新的功能，则需要在目标组件中为ListView设置OnRefreshListener
 *
 * Created by Anchorer/duruixue on 2014/8/16.
 * @author Anchorer
 */
public class CompatibleRefreshableListView extends CompatibleListView {
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
    public interface OnRefreshListener {
    	/**
    	 * 执行刷新列表的动作
    	 * @param listView	目标ListView
    	 * @param forceFlush	是否不判断时间间隔而强制进行刷新
    	 */
        public void onRefresh(CompatibleRefreshableListView listView, boolean forceFlush);
    }
    private OnRefreshListener mListener = null;
    
    public interface OnRefreshableListViewInterface {
    	public void onTopOverDrag();
    }
    private OnRefreshableListViewInterface mInterface;

    //标记列表的下拉状态
    private static final int REFRESH = 0;
    private static final int NORMAL = 1;
    
    //自定义的显示文字
    private String pullingHintText;
    private String toGoHintText;
    private String refreshingHintText;

    public CompatibleRefreshableListView(final Context context) {
        super(context);
    }

    public CompatibleRefreshableListView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public CompatibleRefreshableListView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }
    
    /**
     * 为列表设置OnRefreshListener
     */
    public void setOnRefreshListener(final OnRefreshListener listener) {
        mListener = listener;
    }
    
    public void setmInterface(OnRefreshableListViewInterface mInterface) {
		this.mInterface = mInterface;
	}
    
    /**
     * 初始化头部的布局
     * @param layoutResId		头部布局文件的ID
     * @param listHeaderResId	头部父控件的资源ID，类型为RelativeLayout/LinearLayout
     * @param arrowResId		头部箭头的资源ID，类型为ImageView
     * @param progressResId		头部圆形加载框的资源ID，类型为ProgressBar
     * @param textResId			头部提示文字控件的资源ID，类型为TextView
     * @param lastUpdateTimeTextResId	头部提示上次更新时间的资源ID，类型为TextView
     * @param headerHeightDp		头部总的高度，单位为DP
     * @param headerTriggerHeightDp	头部触发刷新线的高度，单位为DP，这两个参数均根据自定义的布局进行设置
     * @param pullingHintText	正在下拉的文字提示
     * @param toGoHintText		松开刷新的文字提示
     * @param refreshingHintText	正在更新的文字提示
     */
    public void initializePullHeader(int layoutResId, int listHeaderResId, int arrowResId, int progressResId, int textResId, int lastUpdateTimeTextResId, float headerHeightDp, float headerTriggerHeightDp, String pullingHintText, String toGoHintText, String refreshingHintText) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mHeaderContainer = inflater.inflate(layoutResId, null);
        mHeaderView = mHeaderContainer.findViewById(listHeaderResId);
        mArrow = (ImageView) mHeaderContainer.findViewById(arrowResId);
        mProgress = (ProgressBar) mHeaderContainer.findViewById(progressResId);
        mText = (TextView) mHeaderContainer.findViewById(textResId);
        mTimeText = (TextView) mHeaderContainer.findViewById(lastUpdateTimeTextResId);
        addHeaderView(mHeaderContainer);

        mHeaderHeight = (int) (headerHeightDp * getContext().getResources().getDisplayMetrics().density);
        mHeaderTriggerHeight = (int) (headerTriggerHeightDp * getContext().getResources().getDisplayMetrics().density);
        setHeaderHeight(0);
        
        this.pullingHintText = pullingHintText;
        this.toGoHintText = toGoHintText;
        this.refreshingHintText = refreshingHintText;
    }

    /**
     * 完成刷新，隐藏头部布局
     * @param updateTimeMillis	是否更新刷新时间戳
     */
    public void completeRefreshing(boolean updateTimeMillis) {
    	if(updateTimeMillis)
    		this.lastUpdateTimeMillis = System.currentTimeMillis() / 1000;
        mProgress.setVisibility(View.GONE);
        mArrow.setVisibility(View.VISIBLE);
        mHandler.sendMessage(mHandler.obtainMessage(NORMAL, mHeaderTriggerHeight, 0));
        mIsRefreshing = false;
        invalidateViews();
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
            if(!isHorizontalMove()) {
	            if (deltaY > config.getScaledTouchSlop()) {
	
	                // Scrolling downward
	                if (direction > 0) {
	                    // Refresh bar is extended if top pixel of the first item is visible
	                    if (getChildAt(0) != null && getChildAt(0).getTop() == 0) {
	                    	if(mInterface != null) {
	                    		mInterface.onTopOverDrag();
	                    	}
	                    	
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
            }
            mHistoricalY = ev.getY();
        }
        return super.dispatchTouchEvent(ev);
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
    public boolean performItemClick(final View view, final int position, final long id) {
        // When position == 0, this is the refresh header element
        return position == 0 || super.performItemClick(view, position - 1, id);
    }

    private void setHeaderHeight(final int height) {
        if (height <= 1) {
            mHeaderView.setVisibility(View.GONE);
        } else {
            mHeaderView.setVisibility(View.VISIBLE);
            // TODO Custom text displays
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

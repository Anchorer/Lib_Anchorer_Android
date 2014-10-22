package com.anchorer.lib.view;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.anchorer.lib.model.Advert;
import com.anchorer.lib.utils.image.ImageUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * View: DefaultAdvertSlider
 * 自定义ViewPager，提供广告位轮播的ViewPager控件，内置自动轮播和点击监听机制
 *
 * Created by Anchorer/duruixue on 2013/8/21.
 * @author Anchorer
 */
public class DefaultAdvertSlider extends ViewPager {
	//数据源
	private List<Advert> mViewPagerListData;

	//标题显示控件
	private TextView titleView;
	
	//小圆点显示
    private List<View> dotViews;
	private int defaultDotResOfNormal;
	private int defaultDotResOfSelected;
	
	//图片填充方式
	private ScaleType scaleType;
    private DisplayImageOptions mImageOptions;

	//自动轮播机制
	private int currentItemPosition = 0;
	private ScheduledExecutorService scheduledExecutorService;
	private int viewPagerScrollState = ViewPager.SCROLL_STATE_IDLE;
	private FixedSpeedScroller mScroller;
	
	private boolean resumeFromOtherPageFlag = false;
	
	//公共接口：轮播项的点击监听事件
	public interface OnClickDefaultAdvertSliderItemListener {
		public void clickDefaultAdvertSliderItem(int id, String title, String link);
	}
	private OnClickDefaultAdvertSliderItemListener clickDefaultAdvertSliderItemListener;
	
	public DefaultAdvertSlider(Context context) {
		super(context);
	}
	
	public DefaultAdvertSlider(Context context, AttributeSet attr) {
		super(context, attr);
	}

    public void initSlider(OnClickDefaultAdvertSliderItemListener listener, List<Advert> viewPagerListData, DisplayImageOptions imageOptions) {
        initSlider(listener, viewPagerListData, imageOptions, null, null, -1, -1, ScaleType.CENTER_CROP);
    }

	public void initSlider(OnClickDefaultAdvertSliderItemListener listener, List<Advert> viewPagerListData, DisplayImageOptions imageOptions,
                           TextView titleView, List<View> dotViews, int defaultDotResOfNormal, int defaultDotResOfSelected, ScaleType scaleType) {
		this.clickDefaultAdvertSliderItemListener = listener;
		this.mViewPagerListData = viewPagerListData;
        this.mImageOptions = imageOptions;
		this.titleView = titleView;
        this.dotViews = dotViews;
		this.defaultDotResOfNormal = defaultDotResOfNormal;
		this.defaultDotResOfSelected = defaultDotResOfSelected;
		this.scaleType = scaleType;

		setAdapter(new DefaultSliderAdapter());
		setOnPageChangeListener(new DefaultPageChangeListener());

		try {
			Field mField = ViewPager.class.getDeclaredField("mScroller");
			mField.setAccessible(true);
			mScroller = new FixedSpeedScroller(getContext(), new LinearInterpolator());
			mField.set(this, mScroller);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private class DefaultPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageSelected(int position) {
			currentItemPosition = position;
			
			Advert ad = mViewPagerListData.get(position);
			if(titleView != null)
                titleView.setText(ad.getTitle());

            if(dotViews != null) {
                for(int i = 0; i < dotViews.size(); i++) {
                    if(i == position)
                        dotViews.get(i).setBackgroundResource(defaultDotResOfSelected);
                    else
                        dotViews.get(i).setBackgroundResource(defaultDotResOfNormal);
                }
            }
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}
		@Override
		public void onPageScrollStateChanged(int newState) {
			viewPagerScrollState = newState;
		}
	}
	
	private class DefaultSliderAdapter extends PagerAdapter {
		@Override
		public int getCount() {
			return mViewPagerListData.size();
		}
		
		@Override
		public Object instantiateItem(View container, final int position) {
            final Advert ad = mViewPagerListData.get(position);
            ImageView itemImageView = new ImageView(getContext());
			itemImageView.setScaleType(scaleType);
			
			//为每一个轮播图片设置点击监听
			itemImageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(clickDefaultAdvertSliderItemListener != null) {
                        clickDefaultAdvertSliderItemListener.clickDefaultAdvertSliderItem(ad.getId(), ad.getTitle(), ad.getLink());
					}
				}
			});
            ImageLoader.getInstance().displayImage(ad.getImageUrl(), new ImageViewAware(itemImageView, false), mImageOptions, new ImageUtils.AnimateFirstDisplayListener());

            ViewParent mParent = itemImageView.getParent();
            if(mParent instanceof ViewPager) {
                ((ViewPager) mParent).removeAllViews();
            }

			((ViewPager)container).addView(itemImageView);
			return itemImageView;
		}
		
		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView((View)object);
		}
		
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
		
		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}
		
		@Override
		public Parcelable saveState() {
			return null;
		}
		
		@Override
		public void startUpdate(View container) {
		}
		
		@Override
		public void finishUpdate(View container) {
		}
	}
	
	/**
	 * 启动startScheduledExecutorService，实现自动轮播。参数均为自动轮播线程的参数
	 * @param task			自动轮播线程
	 * @param initialDelay	初始延迟时间
	 * @param period		轮播间隔时间
	 * @param unit			时间单位
	 */
	public void startScheduledExecutorService(Runnable task, long initialDelay, long period, TimeUnit unit) {
		if(scheduledExecutorService == null || scheduledExecutorService.isShutdown()) {
			scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
			scheduledExecutorService.scheduleAtFixedRate(task, initialDelay, period, unit);
		}
	}
	
	/**
	 * 停止scheduledExecutorService
	 */
	public void shutDownScheduledExecutorService() {
		if(scheduledExecutorService != null)
			scheduledExecutorService.shutdown();
	}
	
	/**
	 * 判断ViewPager是否正处于手动拖拽状态
	 */
	public boolean isDragging() {
		return viewPagerScrollState == ViewPager.SCROLL_STATE_DRAGGING;
	}
	
	/**
	 * 判断ViewPager是否正处于Settling状态
	 */
	public boolean isSettling() {
		return viewPagerScrollState == ViewPager.SCROLL_STATE_SETTLING;
	}
	
	public List<Advert> getViewPagerListData() {
		return mViewPagerListData;
	}

	public void setViewPagerListData(List<Advert> viewPagerListData) {
		this.mViewPagerListData = viewPagerListData;
	}

	public TextView getTitleView() {
		return titleView;
	}

	public void setTitleView(TextView titleView) {
		this.titleView = titleView;
	}

	public int getDefaultDotResOfNormal() {
		return defaultDotResOfNormal;
	}

	public void setDefaultDotResOfNormal(int defaultDotResOfNormal) {
		this.defaultDotResOfNormal = defaultDotResOfNormal;
	}

	public int getDefaultDotResOfSelected() {
		return defaultDotResOfSelected;
	}

	public void setDefaultDotResOfSelected(int defaultDotResOfSelected) {
		this.defaultDotResOfSelected = defaultDotResOfSelected;
	}

	public int getViewPagerScrollState() {
		return viewPagerScrollState;
	}

	public void setViewPagerScrollState(int viewPagerScrollState) {
		this.viewPagerScrollState = viewPagerScrollState;
	}
	
	public int getCurrentItemPosition() {
		return currentItemPosition;
	}
	
	/**
	 * 递增当前页面位置的标记
	 */
	public void increaseCurrentItemPosition() {
		currentItemPosition = (currentItemPosition + 1) % mViewPagerListData.size();
	}
	
	/**
	 * 跳转到当前页面，其中当前页面的位置由currentItemPosition标记
	 * @param mDuration	跳转时间间隔，控制页面滑动速度
	 */
	public void slideToCurrentItem(int mDuration) {
		setCurrentItem(currentItemPosition);
		mScroller.setmDuration(mDuration);
	}
	
	public void setResumeFromOtherPageFlag(boolean resumeFromOtherPageFlag) {
		this.resumeFromOtherPageFlag = resumeFromOtherPageFlag;
	}
	public boolean isResumeFromOtherPageFlag() {
		return resumeFromOtherPageFlag;
	}
}

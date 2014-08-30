package com.anchorer.lib.view;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * FragmentViewPagerTabsContainer
 * 自定义控件集合，该集合中包含有一系列的自定义Tab，这些Tab与ViewPager共同实现同一个Activity/Fragment中不同页面的切换。ViewPager所包含的页面均为Fragment实现。
 * 				1. （必选）按照构造方法初始化实例。构造方法中包含ViewPager及其适配器。
 * 				2. （必选）目标组件需要调用addTabView方法给该控件添加Tabs，Tabs可自定义布局，但是数量需要大于0.
 * 				3. （可选）如果目标组件（Activity或者Fragment）需要监听Tab之间的切换，则需要实现FragmentViewPagerTabsChangeListener接口。
 *
 * Created by Anchorer/duruixue on 2013/12/9.
 * @author Anchorer
 */
public class FragmentViewPagerTabsContainer {
	// 该Tabs框架所包含的Tabs
	private List<List<FragmentViewPagerTab>> mTabViews;
	
	// 该Tabs框架内控制页面切换的ViewPager
	private ViewPager mViewPager;
	
	// 控制Tabs的切换
	private int currentTabIndex = -1;
	private int previousTabIndex = currentTabIndex;
	private int preViewPagerState = 0;
	
	//公共接口：监听Tab页面之间的切换
	public interface FragmentViewPagerTabsChangeListener {
		public void onTabChanged(int tabIndex, boolean refreshPage);
		public void onTabScrolledFixed(int fixedPosition);
	}
	public FragmentViewPagerTabsChangeListener tabChangeListener;
	
	/**
	 * 构造方法：初始化FragmentViewPagerTabsContainer
	 * @param viewPager	包含切换页面的ViewPager
	 * @param viewPagerOffScreenLimit	ViewPager缓存页面的最大个数
	 * @param adapter	ViewPager的自定义适配器
     * @param listener  FragmentViewPagerTabsChangeListener，监听Tab的切换事件
	 */
	public FragmentViewPagerTabsContainer(ViewPager viewPager, int viewPagerOffScreenLimit, FragmentPagerAdapter adapter, FragmentViewPagerTabsChangeListener listener) {
		mTabViews = new ArrayList<List<FragmentViewPagerTab>>();
		this.mViewPager = viewPager;
		this.mViewPager.setOffscreenPageLimit(viewPagerOffScreenLimit);
		if(adapter != null)
			this.mViewPager.setAdapter(adapter);
		this.mViewPager.setOnPageChangeListener(new ViewPagerChangeListener());
        this.tabChangeListener = listener;
	}
	
	/**
	 * （必选项）
	 * 添加Tab，该Tab的布局由应用程序自定义
	 */
	public void addTabViews(int position, FragmentViewPagerTab tabView) {
		if(mTabViews == null)
			mTabViews = new ArrayList<List<FragmentViewPagerTab>>();
		tabView.setOnClickListener(new SelectTabListener(position));
		
		while(mTabViews.size() <= position) {
			mTabViews.add(new ArrayList<FragmentViewPagerTab>());
		}
		mTabViews.get(position).add(tabView);
	}
	
	/**
	 * 监听器：点击标签，跳转到相应的ViewPager页面
	 */
	class SelectTabListener implements OnClickListener {
		private int pos;
		
		public SelectTabListener(int pos) {
			this.pos = pos;
		}
		
		@Override
		public void onClick(View v) {
			if(currentTabIndex != pos) {
				setCurrentItemAtPosition(pos);
			}
		}
	}
	
	/**
	 * 选中对应position位置处的ViewPager页面
	 * @param position position of ViewPager
	 */
	private void setCurrentItemAtPosition(int position) {
		if(position >= 0 && position <= mTabViews.size()) {
			mViewPager.setCurrentItem(position);
			if(currentTabIndex != position)
				setTabAtPosition(position, true);
		}
	}
	
	/**
	 * 选中指定位置处的标签
	 * @param position	指定位置（0,1,2,3,4）
	 */
	public void setTabAtPosition(int position, boolean refreshPage) {
		if(currentTabIndex != position) {
			previousTabIndex = currentTabIndex;
			currentTabIndex = position;
			
			for(int i = 0; i < mTabViews.size(); i++) {
				if(i == previousTabIndex)
					setUI(i, false);
				if(i == currentTabIndex)
					setUI(i, true);
			}
			
			/**
			 * 对Tab页面切换实现监听
			 */
			if(tabChangeListener != null)
				tabChangeListener.onTabChanged(position, refreshPage);
		}
	}
	
	private void setUI(int position, boolean selected) {
		if(position >= 0 && position < mTabViews.size()) {
			List<FragmentViewPagerTab> tabs = mTabViews.get(position);
            for(FragmentViewPagerTab tab : tabs) {
                if(selected)
                    tab.setUIForSelected();
                else
                    tab.setUIForUnSelected();
            }
		}
	}
	
	/**
	 * 监听器：监听ViewPager页面切换事件
	 */
	class ViewPagerChangeListener implements ViewPager.OnPageChangeListener {
		@Override
		public void onPageScrollStateChanged(int state) {
			if(preViewPagerState == ViewPager.SCROLL_STATE_SETTLING && state == ViewPager.SCROLL_STATE_IDLE) {
				if(tabChangeListener != null) {
					tabChangeListener.onTabScrolledFixed(mViewPager.getCurrentItem());
				}
			}
			preViewPagerState = state;
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {}

		@Override
		public void onPageSelected(int position) {
			setCurrentItemAtPosition(position);
		}
	}
	
	public int getCurrentTabIndex() {
		return currentTabIndex;
	}
	
}

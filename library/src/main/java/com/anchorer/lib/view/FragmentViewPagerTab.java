package com.anchorer.lib.view;

import android.content.Context;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * FragmentViewPagerTab
 * Description: 自定义TabView，适用于包含多个Fragment页面切换的ViewPager，其布局可自定义
 *
 * Created by Anchorer/duruixue on 2013/12/9.
 * @author Anchorer
 */
public class FragmentViewPagerTab {
	private Context mContext;
	RelativeLayout mTabLayout;
	TextView mTabText;
	ImageView mTabImage;
	
	private int resIdOfBg;
	private int resIdOfBgSelected;
	private int resIdOfTextColor;
	private int resIdOfTextColorSelected;
	private int resIdOfImage;
	private int resIdOfImageSelected;
	
	/**
	 * 构造方法
	 * @param mContext		上下文环境
	 * @param mTabLayout	Tab的父控件，RelativeLayout
	 * @param mTabText		Tab的文字，TextView
	 * @param mTabImage		Tab的图片，ImageView
	 * @param resIdOfBg				默认背景
	 * @param resIdOfBgSelected		选中背景
	 * @param resIdOfTextColor			默认文字颜色
	 * @param resIdOfTextColorSelected	选中文字颜色
	 * @param resIdOfImage			默认图片
	 * @param resIdOfImageSelected	选中图片
	 */
	public FragmentViewPagerTab(Context mContext, RelativeLayout mTabLayout, TextView mTabText, ImageView mTabImage, 
			int resIdOfBg, int resIdOfBgSelected, 
			int resIdOfTextColor, int resIdOfTextColorSelected, 
			int resIdOfImage, int resIdOfImageSelected) {
		this.mContext = mContext;
		this.mTabLayout = mTabLayout;
		this.mTabText = mTabText;
		this.mTabImage = mTabImage;
		this.resIdOfBg = resIdOfBg;
		this.resIdOfBgSelected = resIdOfBgSelected;
		this.resIdOfTextColor = resIdOfTextColor;
		this.resIdOfTextColorSelected = resIdOfTextColorSelected;
		this.resIdOfImage = resIdOfImage;
		this.resIdOfImageSelected = resIdOfImageSelected;
	}
	
	public void setUIForSelected() {
		setUI(resIdOfBgSelected, resIdOfTextColorSelected, resIdOfImageSelected);
	}
	
	public void setUIForUnSelected() {
		setUI(resIdOfBg, resIdOfTextColor, resIdOfImage);
	}

	public void setUI(int bgRes, int textColorRes, int imageRes) {
		if(mTabLayout != null && bgRes != -1)
			mTabLayout.setBackgroundResource(bgRes);
		if(mTabText != null && textColorRes != -1)
			mTabText.setTextColor(mContext.getResources().getColor(textColorRes));
		if(mTabImage != null && imageRes != -1)
			mTabImage.setImageResource(imageRes);
	}
	
	public void setOnClickListener(OnClickListener listener) {
		if(mTabLayout != null)
			mTabLayout.setOnClickListener(listener);
	}
}

package com.anchorer.lib.model;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.anchorer.lib.utils.image.ImageUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

/**
 * AdvertSliderItem
 * 表示一个轮播项，用于{@link com.anchorer.lib.view.DefaultAdvertSlider}轮播控件。
 *
 * Created by Anchorer/duruixue on 2013/8/21.
 * @author Anchorer
 */
public class AdvertSliderItem {
	//标题
	private boolean mDisplayTitleFlag;	//是否显示标题
	
	//包含的广告对象
	private Advert ad;
	
	public AdvertSliderItem() {}

	public String getDisplayedTitle() {
		if(mDisplayTitleFlag)
			return ad.getTitle();
		else
			return "";
	}

	public void setDisplayTitleFlag(boolean displayTitleFlag) {
		this.mDisplayTitleFlag = displayTitleFlag;
	}

    public void setAdvert(Advert ad) {
        this.ad = ad;
    }

    public int getAdvertId() {
        return ad.getId();
    }

    public String getAdvertTitle() {
        return ad.getTitle();
    }

    public String getAdvertLink() {
        return ad.getLink();
    }

}

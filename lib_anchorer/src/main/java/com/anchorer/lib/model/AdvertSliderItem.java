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
	//广告ID
	private int advertId;
	
	//广告位置
	private int location;

	//广告的类型
	private int type;
	
	//标题
	private String title;				//标题
	private boolean displayTitleFlag;	//是否显示标题
	
	//图片和小圆点
	private View dotView;				//小圆点
	private ImageView imageView;		//广告图片
	private String imageUrl;			//广告图片URL
	
	//内容
	private String url;					//点击打开网页的URL
	private int linkid;					//点击打开内容的ID，这里的内容ID根据type的不同有不同的含义，依应用程序自定义
	
	//包含的广告对象
	private Advert advert;
	
	public AdvertSliderItem() {}
	
	public void setImageView(Context context, ImageLoader imageLoader, DisplayImageOptions options) {
		this.imageView = new ImageView(context);
		imageLoader.displayImage(imageUrl, new ImageViewAware(imageView, false), options, new ImageUtils.AnimateFirstDisplayListener());
		imageView.setScaleType(ScaleType.CENTER_CROP);
	}
	
	public int getLocation() {
		return location;
	}
	
	public void setLocation(int location) {
		this.location = location;
	}
	
	public void setAdvert(Advert advert) {
		this.advert = advert;
	}
	
	public Advert getAdvert() {
		return advert;
	}

	public String getTitle() {
		return title;
	}
	
	public String getDisplayedTitle() {
		if(displayTitleFlag)
			return title;
		else
			return "";
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isDisplayTitleFlag() {
		return displayTitleFlag;
	}

	public void setDisplayTitleFlag(boolean displayTitleFlag) {
		this.displayTitleFlag = displayTitleFlag;
	}

	public View getDotView() {
		return dotView;
	}

	public void setDotView(View dotView) {
		this.dotView = dotView;
	}

	public ImageView getImageView() {
		return imageView;
	}

	public void setImageView(ImageView imageView) {
		this.imageView = imageView;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	public void setDotBackgroundRes(int resid) {
		if(dotView != null)
			dotView.setBackgroundResource(resid);
	}

	public int getAdvertId() {
		return advertId;
	}

	public void setAdvertId(int advertId) {
		this.advertId = advertId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getLinkid() {
		return linkid;
	}

	public void setLinkid(int linkid) {
		this.linkid = linkid;
	}
	
}

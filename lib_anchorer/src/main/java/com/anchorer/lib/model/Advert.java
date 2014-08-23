package com.anchorer.lib.model;

import android.content.Context;
import android.text.TextUtils;

import com.anchorer.lib.utils.SystemUtils;

/**
 * Model: Advert
 * 广告
 *
 * Created by Anchorer/duruixue on 2013/11/19.
 * @author Anchorer
 */
public class Advert {
	private Context mContext;
	private String advTitle;
	private String advId;
	private int advLocation;
	private String advDownUrl;
	//第三方接口信息
	private String statUrl;
	private String sendMacKey;
	private String sendOsKey;
	private String sendPlatformKey;
	
	public Advert(Context context) {
		this.mContext = context;
	}
	
	/**
	 * 获取到调用的第三方接口的完整URL
	 */
	public String getCompleteStatUrl() {
		String completeUrl = statUrl;
		if(!TextUtils.isEmpty(sendMacKey)) {
			completeUrl += "&" + sendMacKey + "=" + SystemUtils.getMacAddress(mContext);
		}
		if(!TextUtils.isEmpty(sendOsKey)) {
			completeUrl += "&" + sendOsKey + "=" + SystemUtils.getSystemVersion();
		}
		if(!TextUtils.isEmpty(sendPlatformKey)) {
			completeUrl += "&" + sendPlatformKey + "=" + SystemUtils.getDevicePlatform();
		}
		return completeUrl;
	}

	public Context getmContext() {
		return mContext;
	}

	public void setmContext(Context mContext) {
		this.mContext = mContext;
	}

	public String getAdvTitle() {
		return advTitle;
	}

	public void setAdvTitle(String advTitle) {
		this.advTitle = advTitle;
	}

	public String getAdvId() {
		return advId;
	}

	public void setAdvId(String advId) {
		this.advId = advId;
	}

	public int getAdvLocation() {
		return advLocation;
	}

	public void setAdvLocation(int advLocation) {
		this.advLocation = advLocation;
	}

	public String getAdvDownUrl() {
		return advDownUrl;
	}

	public void setAdvDownUrl(String advDownUrl) {
		this.advDownUrl = advDownUrl;
	}

	public String getStatUrl() {
		return statUrl;
	}

	public void setStatUrl(String statUrl) {
		this.statUrl = statUrl;
	}

	public String getSendMacKey() {
		return sendMacKey;
	}

	public void setSendMacKey(String sendMacKey) {
		this.sendMacKey = sendMacKey;
	}

	public String getSendOsKey() {
		return sendOsKey;
	}

	public void setSendOsKey(String sendOsKey) {
		this.sendOsKey = sendOsKey;
	}

	public String getSendPlatformKey() {
		return sendPlatformKey;
	}

	public void setSendPlatformKey(String sendPlatformKey) {
		this.sendPlatformKey = sendPlatformKey;
	}
	
}

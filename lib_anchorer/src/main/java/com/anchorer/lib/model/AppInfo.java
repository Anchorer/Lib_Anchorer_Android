package com.anchorer.lib.model;

import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * Model: AppInfo
 * 应用程序信息
 * Application Infomation.
 *
 * Created by Anchorer/duruixue on 2013/12/13..
 * @author Anchorer
 */
public class AppInfo {
    private String appName;
    private String packageName;
    private String versionName;
    private int versionCode;
    private Drawable appIcon;

    public void print() {
        Log.d("TEST", "Name: " + appName +
                ", Package: " + packageName +
                ", VersionName: " + versionName +
                ", VersionCode: " + versionCode);
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

}

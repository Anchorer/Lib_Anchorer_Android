package com.anchorer.lib.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.KeyguardManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.anchorer.lib.consts.LibConst;
import com.anchorer.lib.model.AppInfo;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

/**
 * Utility: SystemUtils
 * 系统工具，提供各种和系统操作相关的方法，提供各种调用系统API的入口
 * This class provides a lot of operations on System level.
 *
 * Created by Anchorer/duruixue on 2013/8/6.
 * @author Anchorer
 */
@SuppressLint("SimpleDateFormat")
public class SystemUtils {
	/**
	 * 打开输入法键盘
     * Open input keyboard.
     *
     * @param v View focused with input keyboard.
	 */
	public static void openInputKeyboard(View v) {
		InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    	imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY);
	}
	
	/**
	 * 关闭输入法键盘
     * Close input keyboard
     *
     * @param v View focused with input keyboard.
	 */
	public static void closeInputKeyboard(View v) {
		InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		if(imm.isActive()) {
			imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
	
	/**
	 * 打开系统默认的分享列表进行内容的分享
     * Call system share API
     *
	 * @param type	分享的类型（"text/plain"）
     *              share type
	 * @param title     share title
	 * @param content   share content
	 * @param shareDialogTitle  title of share dialog
	 */
	public static void sendContentToShare(Context context, String type, String title, String content, String shareDialogTitle) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType(type);
		intent.putExtra(Intent.EXTRA_SUBJECT, title);
		intent.putExtra(Intent.EXTRA_TEXT, content);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(Intent.createChooser(intent, shareDialogTitle));
	}
	
	/**
	 * 获取系统版本
     * Get system api level
	 */
	public static int getSystemApiLevel() {
		return VERSION.SDK_INT;
	}
	
	/**
	 * 获取系统版本号
     * Get system version
	 */
	public static String getSystemVersion() {
		return VERSION.RELEASE;
	}
	
	/**
	 * 获取设备型号
     * Get device platform
	 */
	public static String getDevicePlatform() {
		return Build.MODEL;
	}
	
	/**
	 * 调用本地视频播放器
     * Call system video player
     *
	 * @param srcPath	视频路径
     *                  video path
	 */
	public static void startVideoPlayer(Context context, String srcPath) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse(srcPath), "video/*");
		context.startActivity(intent);
	}
	
	/**
	 * 得到当天的日期
     * Get date at present.
     *
     * @param format Date string format, support two format at present:
     *               1. MM-dd
     *               2. yyyy-MM-dd
	 */
	public static String getDateOfToday(String format) {
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DAY_OF_MONTH);
		String monthStr = (month < 10) ? "0" : "";
		monthStr += month;
		String dayStr = (day < 10) ? "0" : "";
		dayStr += day;
		if(format.equals("MM-dd"))
			return monthStr + "-" + dayStr;
		else if(format.equals("yyyy-MM-dd"))
			return year + "-" + monthStr + "-" + dayStr;
		return "";
	}
	
	/**
	 * 根据日期获取时间戳(以ms为单位)
     * Get time stamp from date string of specific format.
     *
     * @param dateStr Date string
     * @param format Date string format
	 */
	@SuppressLint("SimpleDateFormat")
	public static long getTimeStampFromDateTime(String dateStr, String format) {
		if(!TextUtils.isEmpty(dateStr)) {
			try {
				Date date = new SimpleDateFormat(format).parse(dateStr);
				return date.getTime();
			} catch (ParseException e) {
                L.e(LibConst.LOG, "SystemUtils getTimeStampFromDateTime ParseException. [dateStr: " + dateStr + ", format:" + format + "]", e);
				return 0;
			}
		}
		return 0;
	}
	
	/**
	 * 根据时间戳转换成指定格式的日期事件字符串
     * Translate time stamp to date string of specific format
     *
	 * @param stamp		时间戳
     *                  time stamp
	 * @param format	定义转换格式
     *                  date string format
	 */
	public static String getDateTimeFromTimeStamp(long stamp, String format) {
		SimpleDateFormat df = new SimpleDateFormat(format);
		return df.format(new Timestamp(stamp));
	}
	
	/**
	 * 将制定内容复制到剪贴板
     * Copy content to clipboard.
     *
	 * @param context   Context
	 * @param str	需要复制的内容
     *              content to copy
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void copyStrToClipboard(Context context, String str) {
		ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData textData = ClipData.newPlainText("Youxiduo", str);
		manager.setPrimaryClip(textData);
	}
	
	/**
	 * 获取设备唯一识别码
     * Get unique device ID.
     *
	 * @return Unique device ID
	 */
	public static String getUniqueUuid(Context context) {
		final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		final String tmDevice, tmSerial, androidId;
		// Get the device ID
		tmDevice = "" + tm.getDeviceId();
		// Get the serial number of SIM
		tmSerial = "" + tm.getSimSerialNumber();
		// A 64-bit number on the first boot of the device and keep constant unless a factory reset occurs
		androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
		// Generate the unique uuid
		UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
		return deviceUuid.toString();
	}
	
	/**
	 * 获取应用程序配置项
     * Get meta value of application
     *
	 * @param context	Context
	 * @param metaKey	配置项的键
     *                  meta key
	 */
    public static String getMetaValue(Context context, String metaKey) {
        Bundle metaData = null;
        String apiKey = null;
        if (context == null || metaKey == null) {
        	return null;
        }
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
            	apiKey = metaData.getString(metaKey);
            }
        } catch (NameNotFoundException e) {
            L.e(LibConst.LOG, "SystemUtils -- getMetaValue NameNotFoundException", e);
        }
        return apiKey;
    }
    
    /*
     * 调用系统的下载器进行下载
     * 1. 首先检测该路径下文件是否存在；如果已经存在，则删除该文件
     * 2. 调用系统下载器进行下载
     * 
     * @param context	上下文对象
     * @param url		下载地址
     * @param cachePath	本地缓存路径
     * @param fileName	本地存储的文件名
     * @param notificationTitle	通知栏通知的标题
     * @param notificationDesc	通知栏通知的内容
     */
	/*public static void startDownloadService(Context context, String url, String cachePath, String fileName, String notificationTitle, String notificationDesc) {
    	if(TextUtils.isEmpty(url)) {
    		Toast.makeText(context, "下载地址为空", Toast.LENGTH_SHORT).show();
    	} else {
    		*//*
    		 * 检测系统下载器是否有效
    		 * 1. 如果无效，则调用浏览器进行下载
    		 * 2. 如果有效，则调用系统下载器进行后台下载
    		 *//*
    		int state = context.getPackageManager().getApplicationEnabledSetting("com.android.providers.downloads");
    		if(state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED || state == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT) {
    			//系统下载器有效，则执行下载
    			String absPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + cachePath + "/" + fileName;
            	//1. 检测文件是否已经存在，若存在则删除文件
            	File file = new File(absPath);
            	if(file != null && file.exists()) {
            		file.delete();
            	}
            	
            	//2. 调用系统下载器进行下载
            	DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            	DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        		request.setDestinationInExternalPublicDir(cachePath, fileName);
        		request.setTitle(notificationTitle);
        		request.setDescription(notificationDesc);
        		if(VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        			request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        		}
        		downloadManager.enqueue(request);
    		} else {
    			//系统下载器无效，则调用浏览器进行下载
    			WebUtils.openWebbrowser(context, url);
    		}
    	}
    }*/
    
    /**
     * 开启拍照/裁剪/相册选择照片的页面
     * Open Camerar/Crop/Album
     *
     * @param context	Context
     * @param intentAction	Intent Action，指定了要做的操作
     * @param data		Intent data
     * @param type		图片类型
     * @param crop		是否裁剪，是的话为"true"
     * @param aspectX,aspectY	宽高比
     * @param outputX,outputY	裁剪大小
     * @param scale		是否维持原比例
     * @param returnData	是否返回data
     * @param extraOutput	Extra Output destination
     * @param outputFormat	输出格式
     * @param noFaceDetection	是否无人脸识别
     * @param requestCode	Activity result code
     */
    public static void startPicAndCropService(Activity context, String intentAction, Uri data, String type, String crop,
    				int aspectX, int aspectY, int outputX, int outputY,
    				boolean scale, boolean returnData, Uri extraOutput, String outputFormat, boolean noFaceDetection, int requestCode) {
    	Intent intent = new Intent(intentAction);
    	intent.setDataAndType(data, type);
    	intent.putExtra("crop", crop);
    	intent.putExtra("aspectX", aspectX);
    	intent.putExtra("aspectY", aspectY);
    	intent.putExtra("outputX", outputX);
    	intent.putExtra("outputY", outputY);
    	intent.putExtra("scale", scale);
    	intent.putExtra("return-data", returnData);
    	if(extraOutput != null)
    		intent.putExtra(MediaStore.EXTRA_OUTPUT, extraOutput);
    	intent.putExtra("outputFormat", outputFormat);
    	intent.putExtra("noFaceDetection", noFaceDetection);
    	context.startActivityForResult(intent, requestCode);
    }

    /**
     * 获取上次更新的时间间隔所对应的时间提示信息
     * @param interval	时间间隔，单位为s
     */
    public static String getLastUpdateTimeHintStr(long interval) {
        if(interval <= LibConst.TIMESTAMP_MINUTE)
            return "刚刚";
        else if(interval < LibConst.TIMESTAMP_HOUR)
            return interval / LibConst.TIMESTAMP_MINUTE + "分钟前";
        else if(interval < LibConst.TIMESTAMP_DAY)
            return interval / LibConst.TIMESTAMP_HOUR + "小时前";
        else
            return "很久很久以前";
    }

    /**
     * 获取设备的Mac地址
     * Get device Mac address
     *
     * @param context Context
     */
    public static String getMacAddress(Context context) {
    	WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    	return manager.getConnectionInfo().getMacAddress();
    }
    
    /**
     * 获取设备上已安装的应用信息
     * Get installed applications info
     *
     * @param includeSystemApps	是否获取系统应用
     *                          include system application or not
     */
    public static List<AppInfo> getAppInfoList(Context context, boolean includeSystemApps) {
    	List<AppInfo> appList = new ArrayList<AppInfo>();
		List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);

        for(PackageInfo packageInfo : packages) {
			AppInfo appInfo = new AppInfo();
			appInfo.setAppName(packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString());
			appInfo.setPackageName(packageInfo.packageName);
			appInfo.setVersionName(packageInfo.versionName);
			appInfo.setVersionCode(packageInfo.versionCode);
			appInfo.setAppIcon(packageInfo.applicationInfo.loadIcon(context.getPackageManager()));
			
			if(includeSystemApps) {
				//获取系统应用和用户应用
				appList.add(appInfo);
			} else {
				//只获取用户应用
				if((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0)
					appList.add(appInfo);
			}
		}
		
		return appList;
    }
    
    /**
     * 获取设备上已安装应用的包名，不包含系统应用
     * Get package names of installed applications
     *
     * @param context Context
     * @return list of package names
     */
    public static List<String> getInstalledPackageList(Context context) {
    	List<String> packList = new ArrayList<String>();
    	List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(0);
    	for(PackageInfo packInfo : packs) {
    		if((packInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0)
    			packList.add(packInfo.packageName);
    	}
    	return packList;
    }
    
    /**
     * 卸载指定包名的应用程序
     * Uninstall application of specific package name
     *
     * @param context Context
     * @param packageName package name of the application
     */
    public static void uninstallApp(Context context, String packageName) {
    	Uri packageUri = Uri.parse("package:" + packageName);
		Intent intent = new Intent(Intent.ACTION_DELETE);
		intent.setData(packageUri);
		context.startActivity(intent);
    }
    
    /**
     * 获取IP地址
     * Get IP address
     *
     * @return  IP address of device
     * @throws java.net.SocketException
     */
    public static String getLocalIPAddress() throws SocketException {
    	for(Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
    		NetworkInterface intf = en.nextElement();
    		for(Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
    			InetAddress address = enumIpAddr.nextElement();
    			if(!address.isLoopbackAddress()) {
                    return address.getHostAddress();
    			}
    		}
    	}
    	return null;
    }
    
    /**
     * 查询当前正在运行的进程
     * Get running processes
     *
     * @param context Context
     * @return List of running processes
     */
    public static List<RunningAppProcessInfo> detectRunningTasks(Context context) {
    	ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        return manager.getRunningAppProcesses();
    }
    
    /**
     * 判断屏幕是否开启
     * Check whether the screen is on or not
     *
     * @param context   Context
     * @return True if the screen is on, otherwise False
     */
    public static boolean isScreenOn(Context context) {
    	PowerManager manager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
    	return manager.isScreenOn();
    }
    
    /**
     * 判断是否处在锁屏状态
     * Check whether the screen is locked
     *
     * @param context   Context
     * @return True if the screen is locked, otherwise False
     */
    public static boolean isScreenLocked(Context context) {
    	KeyguardManager manager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
    	return manager.inKeyguardRestrictedInputMode();
    }
    
    /**
     * 启动指定包名的应用程序
     * Run application of specific package name
     *
     * @param context   Context
     * @param packageName	package name of the application
     */
    public static void startApp(Context context, String packageName) {
    	Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
    	context.startActivity(intent);
    }
}

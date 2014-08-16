package com.anchorer.lib.view;

import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * View: VideoEnabledWebView
 * Description: 自定义WebView，支持视频的全屏播放，配合VideoEnabledWebChromeClient使用。
 *
 * Created by Anchorer/duruixue on 2013/10/29.
 * @author Anchorer
 */
public class VideoEnabledWebView extends WebView {
	private VideoEnabledWebChromeClient videoEnabledWebChromeClient;
	private boolean addedJavascriptInterface;
	
	public class JavascriptInterface {
		public void notifyVideoEnd() {
			new Handler(Looper.getMainLooper()).post(new Runnable() {
				@Override
				public void run() {
					if(videoEnabledWebChromeClient != null) {
						videoEnabledWebChromeClient.onHideCustomView();
					}
				}
			});
		}
	}

	public VideoEnabledWebView(Context context) {
		super(context);
		addedJavascriptInterface = false;
		setWebViewClientTest();
	}
	
	public VideoEnabledWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		addedJavascriptInterface = false;
		setWebViewClientTest();
	}
	
	public VideoEnabledWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		addedJavascriptInterface = false;
		setWebViewClientTest();
	}
	
	private void setWebViewClientTest() {
		setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
//				Log.d(LibConst.LOG_TEST, "shouldOverrideUrlLoading: " + url);
				String lowerUrl = url.toLowerCase();
				if(lowerUrl.endsWith(".mp4") || lowerUrl.endsWith(".flv")) {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					String trend = "";
					if(lowerUrl.endsWith(".mp4")) {
						trend = "mp4";
					} else if(lowerUrl.endsWith(".flv")) {
						trend = "flv";
					}
					intent.setDataAndType(Uri.parse(url), "video/" + trend);
					getContext().startActivity(intent);
				}
				return super.shouldOverrideUrlLoading(view, url);
			}
		});
	}
	
	public boolean isVideoFullscreen() {
		return videoEnabledWebChromeClient != null && videoEnabledWebChromeClient.isVideoFullscreen();
	}
	
	/** 
     * Pass only a VideoEnabledWebChromeClient instance. 
     */ 
	@SuppressLint("SetJavaScriptEnabled")
    public void setWebChromeClient(WebChromeClient client) {
		getSettings().setJavaScriptEnabled(true);
		if(client instanceof VideoEnabledWebChromeClient) {
			this.videoEnabledWebChromeClient = (VideoEnabledWebChromeClient) client;
		}
		super.setWebChromeClient(client);
	}
	
    @Override 
    public void loadData(String data, String mimeType, String encoding) { 
        addJavascriptInterface(); 
        super.loadData(data, mimeType, encoding); 
    }     
    
    @Override 
    public void loadDataWithBaseURL(String baseUrl, String data, String mimeType, String encoding, String historyUrl) { 
        addJavascriptInterface(); 
        super.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl); 
    }     
    
    @Override 
    public void loadUrl(String url) { 
        addJavascriptInterface(); 
        super.loadUrl(url); 
    }
    
    @SuppressLint("NewApi")
	@Override 
    public void loadUrl(String url, Map<String, String> additionalHttpHeaders) { 
        addJavascriptInterface(); 
        super.loadUrl(url, additionalHttpHeaders); 
    }

    private void addJavascriptInterface() {
        if (!addedJavascriptInterface) {
            // Add javascript interface to be called when the video ends (must be done before page load) 
            addJavascriptInterface(new JavascriptInterface(), "_VideoEnabledWebView"); // Must match Javascript interface name of VideoEnabledWebChromeClient             
            addedJavascriptInterface = true; 
        }
    }
}

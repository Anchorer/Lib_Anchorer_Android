package com.anchorer.lib.utils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.anchorer.lib.consts.LibConst;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility: WebUtils
 * 网络工具，提供各种和网络操作相关的方法
 * Web Utils, provide a lot of internet related operations
 *
 * Created by Anchorer/duruixue on 2013/8/5.
 * @author Anchorer
 */
public class WebUtils {
	/**
	 * 请求网络获取数据，所返回的数据格式为JSON
     * Connect API for JSON data
     *
	 * @param url   url to request
	 * @param requestEncoding   request encoding type
     * @param responseEncoding  response encoding type
     * @param params    request parameter list
     * @return  response content as JSONObject
	 */
	public static JSONObject getJsonResultFromRequest(String url, String requestEncoding, String responseEncoding,List<NameValuePair> params) throws IOException, JSONException {
		//生成HttpClient对象 ，设置connect_timeout和so_timeout
		DefaultHttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, LibConst.WEB_CONNECTION_TIMEOUT);
		httpClient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, LibConst.WEB_SO_TIMEOUT);
		
		//设置自定义的重试机制，连接失败重试3次
		httpClient.setHttpRequestRetryHandler(new HttpRequestRetryHandler() {
			@Override
			public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                return executionCount <= 3;
			}
		});
		
		//根据URL、编码方式以及参数生成HttpPost请求对象
		HttpPost httpPost = new HttpPost(url);
		UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(params, requestEncoding);
		httpPost.setEntity(uefEntity);
		
		//执行request，获得response
		HttpResponse response = httpClient.execute(httpPost);
		HttpEntity entity = response.getEntity();
		try {
			if(entity != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String content = EntityUtils.toString(entity, responseEncoding);
				return new JSONObject(new JSONTokener(content));
			}
			return new JSONObject();
		} finally {
			//response中的数据获取完毕，将httpClient释放
			httpClient.getConnectionManager().shutdown();
		}
	}
	
	public static JSONObject getJsonResultFromRequest(String url, String requestEncoding, String responseEncoding) throws IOException, JSONException {
		return getJsonResultFromRequest(url, requestEncoding, responseEncoding, new ArrayList<NameValuePair>());
	}
	
	public static JSONObject getJsonResultFromRequest(String url) throws IOException, JSONException {
		return getJsonResultFromRequest(url, LibConst.ENCODING_UTF_8, LibConst.ENCODING_UTF_8, new ArrayList<NameValuePair>());
	}
	
	public static JSONObject getJsonResultFromRequest(String url, List<NameValuePair> params) throws IOException, JSONException {
		return getJsonResultFromRequest(url, LibConst.ENCODING_UTF_8, LibConst.ENCODING_UTF_8, params);
	}
	
	/**
	 * 判断当前网络是否可用
     * Check if network is enabled
     *
     * @param context Context
     * @return True if enabled, otherwise False
	 */
	public static boolean isNetworkEnabled(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return info != null;
	}
	
	/**
	 * 打开浏览器浏览网页
     * Open system web browser for specific webpage
     *
     * @param context   Context
     * @param url Web URL to access
	 */
	public static void openWebbrowser(Context context, String url) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		context.startActivity(intent);
	}
	
}

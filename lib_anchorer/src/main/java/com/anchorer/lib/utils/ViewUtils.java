package com.anchorer.lib.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

import java.io.FileNotFoundException;

/**
 * Utility: ViewUtils
 * 视图工具，提供各种和视图相关的方法
 * View Utils, provides a lot of view-related operations.
 *
 * Created by Anchorer/duruixue on 2013/7/31.
 * @author Anchorer
 */
public class ViewUtils {
	
	/**
	 * 从资源文件得到Bitmap对象
     * Get bitmap object from resource
     *
	 * @param resId 资源文件的ID，如果该ID小于0（一般为-1），则说明不设置默认资源文件
     *              resource id. if resId < 0, then no default resource used.
     * @return bitmap object
	 */
	public static Bitmap getBitmapFromDrawableRes(Context context, int resId) {
		if(resId < 0)
			return null;
		Resources res = context.getResources();
		BitmapDrawable drawable = (BitmapDrawable)res.getDrawable(resId);
		return drawable.getBitmap();
	}
	
	/**
	 * 从Drawable对象得到Bitmap对象
     * Drawable object ==> Bitmap object
     *
     * @param drawable Drawable object
     * @return Bitmap object
	 */
	public static Bitmap getBitmapFromDrawable(Drawable drawable) {
		int w = drawable.getIntrinsicWidth();
		int h = drawable.getIntrinsicHeight();
		
		Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888 : Config.RGB_565;
		Bitmap bitmap = Bitmap.createBitmap(w, h, config);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, w, h);
		drawable.draw(canvas);
		
		return bitmap;
	}
	
	/**
	 * 获取圆角图片
     * Get round corner bitmap
     *
	 * @param bitmap	原始图片bitmap
     *                  original bitmap
	 * @param roundPx	圆角弧度
     *                  round coner pixel
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		
		return output;
	}
	
	/**
	 * 从图片的Uri对象获取Bitmap图片
     * Uri of image ==> Bitmap object
     *
     * @param context Context
	 * @param uri	图片的Uri对象，由SD卡上的临时文件而来
     *              Uri of image
	 */
	public static Bitmap decodeUriAsBitmap(Context context, Uri uri) {
		Bitmap bitmap;
		try {
			bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
		} catch(FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}
	
	/**
	 * 根据ListView子视图动态改变ListView的高度
     * Set height of ListView based on its children
     *
	 * @param listView	要调整高度的ListView
	 * @param length	ListView列表项的数目（即列表的长度）
     *                  length of ListView
	 * @param childHeight	每个列表项的高度，单位为px，可以用dp2px方法转换
     *                      height of each child
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView, int length, int childHeight) {
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = length * childHeight + (listView.getDividerHeight() * (length - 1));
		listView.setLayoutParams(params);
	}
	
	/**
	 * 设置ListView的高度
     * Set height for ListView
     *
	 * @param listView	要设置高度的ListView
	 * @param listHeight	ListView的高度
     *                      height of ListView
	 */
	public static void setListViewHeight(ListView listView, int listHeight) {
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = listHeight;
		listView.setLayoutParams(params);
	}
	
	/**
	 * 获取到ListView的实际高度
     * Get the measured height of ListView
     *
	 * @param listView  the ListView to check
     * @return height of ListView
	 */
	public static int getListViewMeasuredHeight(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if(listAdapter == null) {
			return 0;
		}
		
		int totalHeight = 0;
		for(int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		
		return totalHeight;
	}
	
	/**
	 * 根据GridView子视图动态改变GridView的高度
     * Set height for GridView based on its children
     *
     * @param gridView  GridView
     * @param rowHeight height of each row
     * @param rowsNum   number of rows
	 */
	public static void setGridViewHeightBasedOnChildren(GridView gridView, int rowsNum, int rowHeight) {
		ViewGroup.LayoutParams params = gridView.getLayoutParams();
		params.height = rowsNum * rowHeight;
		gridView.setLayoutParams(params);
	}
	
	/**
	 * 将dp转换成px
     * dp ==> px
	 */
	public static int dp2px(Context context, float dpValue) {
        if(context != null) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dpValue * scale + 0.5f);
        }
        return (int) dpValue;
	}
	
	/**
	 * 将px转换成dp
     * dp ==> px
	 */
	public static int px2dp(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	
	/**
	 * 获取屏幕宽度（分辨率）
     * Get screen width of Pixel
     *
     * @param activity Context
	 */
	public static int getScreenWidthPixels(Activity activity) {
		DisplayMetrics metric = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
		return metric.widthPixels;
	}
	
	/**
	 * 获取屏幕宽度（dp）
     * Get screen width of dp
     *
     * @param activity Context
	 */
	public static int getScreenWidthDps(Activity activity) {
		return px2dp(activity, getScreenWidthPixels(activity));
	}
	
	/**
	 * 获取屏幕高度（分辨率）
     * Get screen height of Pixel
     *
     * @param activity Context
	 */
	public static int getScreenHeightPixels(Activity activity) {
		DisplayMetrics metric = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
		return metric.heightPixels;
	}
	
	/**
	 * 获取屏幕高度（dp）
     * Get screen height of dp
     *
     * @param activity Context
	 */
	public static int getScreenHeightDps(Activity activity) {
		return px2dp(activity, getScreenHeightPixels(activity));
	}
	
	/**
	 * 消除掉ProgressDialog
     * Dismiss ProgressDialog
     *
     * @param dialog dialog to dismiss
	 */
	public static void dismissProgressDialog(ProgressDialog dialog) {
		if(dialog != null && dialog.isShowing())
			dialog.dismiss();
	}
	
	/**
	 * 消除掉PopupWindow
     * Dismiss PopupWindow
     *
     * @param window PopupWindow to dismiss
	 */
	public static void dismissPopupWindow(PopupWindow window) {
		if(window != null && window.isShowing())
			window.dismiss();
	}
	
}

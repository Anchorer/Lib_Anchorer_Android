package com.anchorer.lib.utils.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * Utility: ImageUtils
 * 提供图片操作的相关方法
 * Provide operations about images.
 *
 * Created by Anchorer/duruixue on 2014/3/21.
 * @author Anchorer
 */
public class ImageUtils {
	/**
	 * 创建一个DisplayImageOptions对象
     * Create a {@link com.nostra13.universalimageloader.core.DisplayImageOptions} object
     * {@link com.nostra13.universalimageloader.core.DisplayImageOptions} provides options for loading an image.
     *
	 * @param defaultResId			图片默认的资源
     *                              default image resource id, this default image will be displayed:
     *                              1. before completion of loading image
     *                              2. after failure of loading image
	 * @param supportMemoryCache	是否支持内存缓存
     *                              True to support memory cache
	 * @param supportSdcardCache	是否支持SD卡缓存
     *                              True to support file cache
	 * @param roundPixel			圆角大小
     *                              >0 to support round coner
	 */
	public static DisplayImageOptions getDisplayImageOptions(int defaultResId, boolean supportMemoryCache, boolean supportSdcardCache, int roundPixel) {
		if(roundPixel > 0) {
			return new DisplayImageOptions.Builder()
						.showImageForEmptyUri(defaultResId)
                        .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
						.showImageOnFail(defaultResId)
						.showImageOnLoading(defaultResId)
						.cacheInMemory(supportMemoryCache)
						.cacheOnDisc(supportSdcardCache)
						.displayer(new RoundedBitmapDisplayer(roundPixel))
						.considerExifParams(true).build();
		} else {
			return new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(defaultResId)
                    .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                    .showImageOnFail(defaultResId)
                    .showImageOnLoading(defaultResId)
                    .cacheInMemory(supportMemoryCache)
                    .cacheOnDisc(supportSdcardCache)
                    .displayer(new FadeInBitmapDisplayer(300))
                    .considerExifParams(true).build();
		}
	}
	public static DisplayImageOptions getDisplayImageOptions(int defaultResId, int roundPixel) {
		return getDisplayImageOptions(defaultResId, true, true, roundPixel);
	}
	public static DisplayImageOptions getDisplayImageOptions(int defaultResId) {
		return getDisplayImageOptions(defaultResId, true, true, 0);
	}
	
	/**
	 * ImageLoader的监听器，加载图片成功时淡入图片显示
     * Animation Of first display of iamge.
	 */
	public static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {
		ImageLoader imageLoader = ImageLoader.getInstance();
		public static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());
		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					animate(imageView, 0.2f, 800);
					displayedImages.add(imageUri);
				}
			}
		}
	}
	
	private static void animate(ImageView imageView, float fromAnimation, long durationMillis) {
		if (imageView != null) {
			AlphaAnimation fadeImage = new AlphaAnimation(fromAnimation, 1);
			fadeImage.setDuration(durationMillis);
			fadeImage.setInterpolator(new DecelerateInterpolator());
			imageView.startAnimation(fadeImage);
		}
	}
	
	/**
     * 从URL下载图片，保存到指定路径，并且获取到Bitmap字符串
     * Get specific image from URL.
     *
     * @param context   Context
     * @param url	    URL of image
     * @param cachePath Cache path for image
     * @param requiredSize	requried image size
     * @return  Bitmap object of the image
     */
    public static Bitmap downloadPicFromUrl(Context context, String url, String cachePath, int requiredSize) throws Exception {
        FileCache fileCache = new FileCache(context, cachePath);
    	File file = fileCache.getFile(url);
        URL imageUrl = new URL(url);
        HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(30000);
        conn.setInstanceFollowRedirects(true);
        InputStream is = conn.getInputStream();
        OutputStream os = new FileOutputStream(file);
        copyStream(is, os);
        os.close();
        conn.disconnect();
        return decodeFile(file, requiredSize);
    }

    /**
     * 对图片进行缩放处理，减小内存的占用
     * Scale image to reduce memory useage
     *
     * @param f		图片文件
     *              image file
     * @param requiredSize	图片需要的大小，越大表示图片质量越高，一般为2的幂数
     *                      requried image size
     */
    private static Bitmap decodeFile(File f, int requiredSize){
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream stream1 = new FileInputStream(f);
            BitmapFactory.decodeStream(stream1,null,o);
            stream1.close();
            
            //Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while(true){
                if(width_tmp / 2 < requiredSize || height_tmp / 2 < requiredSize)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }
            
            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            FileInputStream stream2 = new FileInputStream(f);
            Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
            stream2.close();
            return bitmap;
        } catch (FileNotFoundException e) {
        	e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static void copyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}

package com.anchorer.lib.utils.image;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.graphics.Bitmap;

/**
 * Utility: MemoryCache
 * 内存缓存，用来在内存中缓存图片，同时动态释放内存，防止由图片带来的OOM问题。
 * Memory cache for images.
 * MemoryCache manages memory dynamically to avoid the OOM problem caused by bitmaps.
 *
 * Created by Anchorer/duruixue on 2013/7/31.
 * @author Anchorer
 */
public class MemoryCache {
	//加载入内存中的图片，根据LRU原则进行排列，便于释放内存操作
	private Map<String, Bitmap> cache = Collections.synchronizedMap(new LinkedHashMap<String, Bitmap>(10, 1.5f, true));
	//缓存中图片所占用的字节数，通过此变量严格控制缓存所占用的内存
	private long size = 0;
	//缓存只能占用的最大堆内存
	private long limit = 1000000;
	
	/**
	 * 初始化MemoryCache，设置最大允许使用的堆内存
	 */
	public MemoryCache() {
		setLimit(Runtime.getRuntime().maxMemory() / 4);
	} 
	
	private void setLimit(long newLimit) {
		this.limit = newLimit;
	}
	
	/**
	 * 获取MemoryCache中指定ID的图片
	 */
	public Bitmap get(String id) {
		try {
			if(!cache.containsKey(id))
				return null;
			return cache.get(id);
		} catch (NullPointerException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 从MemoryCache中删除指定项
	 * @param id	内存缓存指定项的ID
	 */
	public void remove(String id) {
		if(cache.containsKey(id))
			cache.remove(id);
	}
	
	/**
	 * 向内存缓存中存入图片
	 */
	public Bitmap put(String id, Bitmap bitmap) {
		try {
			if(cache.containsKey(id)) {
				size -= getSizeInBytes(cache.get(id));
			}
			cache.put(id, bitmap);
			size += getSizeInBytes(bitmap);
			//每次插入新的图片都需要检查一下内存占用，以免引起内存溢出
			checkSize();
		} catch (Throwable th) {
			th.printStackTrace();
		}
		return bitmap;
	}
	
	/**
	 * 严格控制堆内存，根据LRU动态释放内存
	 */
	private void checkSize() {
		if(size > limit) {
			Iterator<Entry<String, Bitmap>> iter = cache.entrySet().iterator();
			while(iter.hasNext()) {
				Entry<String, Bitmap> entry = iter.next();
				size -= getSizeInBytes(entry.getValue());
				iter.remove();
				if(size <= limit)
					break;
			}
		}
	}
	
	/**
	 * 清空缓存
	 */
	public void clear() {
		try {
			cache.clear();
			size = 0;
		} catch(NullPointerException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * 获取图片占用的内存
	 */
	long getSizeInBytes(Bitmap bitmap) {
		if(bitmap == null)
			return 0;
		return bitmap.getRowBytes() * bitmap.getHeight();
	}
	
	public long getSize() {
		return size;
	}
	
}

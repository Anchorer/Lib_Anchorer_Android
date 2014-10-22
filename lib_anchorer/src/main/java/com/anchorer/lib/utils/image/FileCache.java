package com.anchorer.lib.utils.image;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import com.anchorer.lib.consts.LibConst;

import org.apache.http.util.EncodingUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Utility: FileCache
 * 文件缓存，提供SD卡上的各种文件操作。
 * FileCache provides file operations on SD card, which can be used to load images or file downloading stuff, etc.
 *
 * Created by Anchorer/duruixue on 2013/7/31.
 * @author Anchorer
 */
public class FileCache {
	private File cacheDir;

	/**
	 * 构造方法
     * Constructor
     *
	 * @param context   Context
	 * @param cachePath 缓存存放路径
     *                  Cache path
	 */
	public FileCache(Context context, String cachePath) {
		//如果有SD卡则在SD卡中创建一个目录存放缓存的图片
		//如果没有SD卡则放在系统的缓存目录中
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			cacheDir = new File(Environment.getExternalStorageDirectory(), cachePath);
		} else {
			cacheDir = context.getCacheDir();
		}
		if(!cacheDir.exists())
			cacheDir.mkdirs();
	}
	
	/**
	 * 根据图片的URL来获取缓存的图片
     * Get cached file by URL.
     *
     * @param url URL of image or file
	 */
	public File getFile(String url) {
		//将url的hashCode作为缓存的文件名
		String fileName = String.valueOf(url.hashCode());
		return new File(cacheDir, fileName);
	}
	
	/**
	 * 清空缓存
     * clear cache
	 */
	public void clear() {
		File[] files = cacheDir.listFiles();
		if(files == null)
			return ;
		for(File f : files)
			f.delete();
	}
	
	/**
	 * 将指定内容写入SD卡。如果没有SD卡，则写入系统目录
     * Write content to specific file.
     *
	 * @param content	要写入的内容
     *                  content to write
	 * @param fileName	写入文件名称
     *                  name of file to write
	 */
	public void writeStrToFile(String content, String fileName) {
		File file = new File(cacheDir, fileName);
		try {
			if(!file.exists())
				file.createNewFile();
			FileOutputStream outStream = new FileOutputStream(file);
			byte[] buffer = content.getBytes();
			outStream.write(buffer);
			outStream.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
     * 读取文件中的内容
     * Read file.
     *
	 * @param fileName	文件名称
     * @return content of file, as String
	 */
	public String readStrFromFile(String fileName) {
		String content = "";
		if(fileName == null)
			return content;
		
		File file = new File(cacheDir, fileName);
		try {
			if(!file.exists())
				return "";
			
			FileInputStream inStream = new FileInputStream(file);
			byte[] buffer = new byte[inStream.available()];
			inStream.read(buffer);
			content = EncodingUtils.getString(buffer, LibConst.ENCODING_UTF_8);
			inStream.close();
			
			return content;
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return content;
	}

    /**
     * 获取存储空间剩余大小，单位为B
     */
    public static long getAvailableSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return blockSize * availableBlocks;
    }


}

/**---------------------------------------------------------------------
 * Utility: EncodeUtils
 * Description: 字符串工具，提供各种字符串操作相关的方法
 * 
 * Author: DuRuixue
 * Date: 2013-8-19
------------------------------------------------------------------------ */
package com.anchorer.lib.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Base64;

import com.anchorer.lib.consts.LibConst;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utils Class about encoding and decoding.
 *
 * Created by Anchorer/duruixue on 2014/8/15.
 */
public class EncodeUtils {
	/**
	 * MD5加密
     * MD5 Encoding
	 * @param string    String to encode
     * @return  Encoded String
	 */
	public static String md5(String string) {
		byte[] hash;
		try {
			hash = MessageDigest.getInstance("MD5").digest(string.getBytes(LibConst.ENCODING_UTF_8));
		} catch(NoSuchAlgorithmException e) {
			throw new RuntimeException("MD5 should be supported?", e);
		} catch(UnsupportedEncodingException e) {
			throw new RuntimeException("UTF-8 should be supported?", e);
		}
		
		StringBuilder hex = new StringBuilder(hash.length * 2);
		for(byte b : hash) {
			if((b & 0xFF) < 0x10)
				hex.append("0");
			hex.append(Integer.toHexString(b & 0xFF));
		}
		return hex.toString();
	}
	
	/**
	 * 使用Base64算法对字符串加密
     * Encode String by Base64
     *
	 * @param 	originalStr		初始字符串
     *                          Original String
	 * @param	encodingType	编码方式
     *                          Encoding type
	 */
	@SuppressLint("NewApi")
	public static String encodeWithBase64(String originalStr, String encodingType) throws Exception {
		if(originalStr == null)
			return null;
		return new String(Base64.encode(originalStr.getBytes(encodingType), Base64.DEFAULT));
	}
	@SuppressLint("NewApi")
	public static String encodeWithBase64(byte[] byteArray) throws Exception {
		if(byteArray == null)
			return null;
		return new String(Base64.encode(byteArray, Base64.DEFAULT));
	}
	
	/**
	 * 使用Base64算法对字符串进行解密
	 * @param 	encodedStr		加密后的字符串
     *                          Encoded String
	 * @param	encodingType	编码方式
     *                          Encoding type
	 */
	@SuppressLint({ "NewApi", "InlinedApi" })
	public static String decodeWithBase64(String encodedStr, String encodingType) throws Exception {
		if(encodedStr == null)
			return null;
		byte[] result = Base64.decode(encodedStr, Base64.DEFAULT);
		return new String(result, encodingType);
	}
	
	/**
	 * 对Bitmap对象使用Base64进行加密
     * Encode Bitmap object by Base64
     *
	 * @param bitmap	待加密的Bitmap对象
     *                  Bitmap object to encode
	 * @throws Exception
	 */
	public static String encodeBitmapWithBase64(Bitmap bitmap) throws Exception {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 100, stream);
		byte[] bytes = stream.toByteArray();
		return encodeWithBase64(bytes);
	}
	
	/**
	 * 从Bitmap图片对象获取其码流
     * Get byte array stream from Bitmap object
	 * @param bitmap	图片对象
     *                  Bitmap object
	 */
	public static byte[] getByteArrayFromBitmap(Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 100, stream);
		return stream.toByteArray();
	}
	
	/**
	 * 将String字符串转换成Unicode
     * String ==> Unicode
     *
	 * @param str	原始字符串
     *              Original String
	 */
	public static String StringToUnicode(String str) {
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            strBuilder.append("\\u").append(Integer.toHexString(str.charAt(i)));
        }
        return strBuilder.toString();
	}
	
	/**
	 * 将Unicode转换成String字符串
     * Unicode ==> String
     *
	 * @param str	Unicode String
	 */
	public static String UnicodeToString(String str) {
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            str = str.replace(matcher.group(1), ch + "");
        }
        return str;
	}
	
}

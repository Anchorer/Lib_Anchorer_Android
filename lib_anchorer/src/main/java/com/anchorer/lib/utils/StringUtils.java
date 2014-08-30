package com.anchorer.lib.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility: StringUtils
 * 字符串工具，提供各种字符串操作相关的方法。
 * Support operations about Strings.
 *
 * Created by Anchorer/duruixue on 2013/8/6.
 * @author Anchorer
 */
public class StringUtils {
	/**
	 * 得到字符串的子字符串
     * Get sub string of original string. Don't consider ArrayIndexOutOfBoundException.
     *
	 * @param	str		初始字符串
     *                  original string
	 * @param 	length	从头部开始子字符串的长度
     *                  length of substring from the beginning
	 */
	public static String getSubString(String str, int length) {
		if(str == null)
			return null;
		int strLength = str.length();
		return str.substring(0, Math.min(length, strLength));
	}
	
	/**
	 * 使用正则表达式对指定字符串格式进行检查
     * Match string by regular expression.
     *
	 * @param source	指定字符串
     *                  string to match
	 * @param regularPattern	正则表达式
     *                          regular expression
	 */
	public static boolean validateStringPattern(String source, String regularPattern) {
		Pattern pattern = Pattern.compile(regularPattern);
		Matcher matcher = pattern.matcher(source);
		return matcher.matches();
	}
	
	/**
	 * 获取到"yyyy-MM-dd"格式的日期字符串
     * Get date string from date numbers by "yyyy-MM-dd" pattern
     *
	 * @param year  Year
	 * @param month Month
	 * @param day   Day
	 */
	public static String getDataStr(int year, int month, int day) {
		String date = year + "-";
		if(month >= 1 && month < 10) {
			date += ("0" + month);
		} else {
			date += month;
		}
		date += "-";
		if(day >= 1 && day < 10) {
			date += ("0" + day);
		} else {
			date += day;
		}
		return date;
	}
	
	/**
	 * 将下载速度转化成字符串显示
     * Get download string from download velocity
     *
	 * @param kbVelocity	以KB为单位的数字
     *                      download velocity of "KB/s"
	 * @return  Donwload velocity string
     *          1) KB/s if v < 1MB/s
     *          2) MB/s as default if v > 1MB/s
	 */
	public static String getDownloadVelocity(long kbVelocity) {
		if(kbVelocity < 1024)
			return kbVelocity + "KB/s";
		double mbVelocity = (double)((kbVelocity * 10) / 1024) / 10;
		if(mbVelocity - (int) mbVelocity == 0)
			return (int) mbVelocity + "MB/s";
		return mbVelocity + "MB/s";
	}

    /**
     * 将整形数组转换成字符串数组
     *
     * @param intArray  要转化的Integer数组
     * @return  转化后的字符串数组
     */
    public static String[] getStringArrayFromIntArray(int[] intArray) {
        String[] strArray = new String[intArray.length];
        for(int i = 0; i < intArray.length; i++) {
            strArray[i] = String.valueOf(intArray[i]);
        }
        return strArray;
    }

}

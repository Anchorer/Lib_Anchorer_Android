package com.anchorer.lib.utils;

import android.text.TextUtils;

import com.anchorer.lib.consts.LibConst;

/**
 * Math Utils Class.
 *
 * Created by Anchorer/duruixue on 2014/11/7.
 */
public class MathUtils {

    /**
     * double类型保留两位小数输出
     * @param value 原始double值
     */
    public static double getTwoDecimalDouble(double value) {
        String valueStr = String.format("%.2f", value);
        try {
            if(!TextUtils.isEmpty(valueStr)) {
                return Double.parseDouble(valueStr);
            }
        } catch(NumberFormatException e) {
            L.e(LibConst.LOG, "MathUtils -- getTwoDecimalDouble: NumberFormatException", e);
        }
        L.e(LibConst.LOG, "MathUtils -- getTwoDecimalDouble: failed.");
        return value;
    }

}

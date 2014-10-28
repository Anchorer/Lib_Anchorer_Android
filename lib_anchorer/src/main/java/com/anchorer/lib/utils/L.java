package com.anchorer.lib.utils;

import android.util.Log;

/**
 * Logger Util.
 *
 * Created by Anchorer/duruixue on 2014/10/22.
 */
public class L {

    public static volatile boolean DEBUG = true;

    /**
     * Enable Logger
     */
    public static void enableLogging() {
        DEBUG = true;
    }

    /**
     * Disable Logger
     */
    public static void disableLogging() {
        DEBUG = false;
    }

    public static void v(String tag, String msg) {
        L.v(tag, msg, null);
    }

    public static void v(String tag, String msg, Throwable t) {
        if(DEBUG) {
            Log.v(tag, msg, t);
        }
    }

    public static void d(String tag, String msg) {
        L.d(tag, msg, null);
    }

    public static void d(String tag, String msg, Throwable t) {
        if(DEBUG) {
            Log.d(tag, msg, t);
        }
    }

    public static void i(String tag, String msg) {
        L.i(tag, msg, null);
    }

    public static void i(String tag, String msg, Throwable t) {
        if(DEBUG) {
            Log.i(tag, msg, t);
        }
    }

    public static void w(String tag, String msg) {
        L.w(tag, msg, null);
    }

    public static void w(String tag, String msg, Throwable t) {
        if(DEBUG) {
            Log.w(tag, msg, t);
        }
    }

    public static void e(String tag, String msg) {
        L.e(tag, msg, null);
    }

    public static void e(String tag, String msg, Throwable t) {
        if(DEBUG) {
            Log.e(tag, msg, t);
        }
    }

}

package com.anchorer.lib.utils;

import android.util.Log;

/**
 * Logger Util.
 *
 * Created by Anchorer/duruixue on 2014/10/22.
 */
public class L {

    private static int level;

    public static final int LEVEL_VERBOSE = Log.VERBOSE;
    public static final int LEVEL_DEBUG = Log.DEBUG;
    public static final int LEVEL_INFO = Log.INFO;
    public static final int LEVEL_WARN = Log.WARN;
    public static final int LEVEL_ERROR = Log.ERROR;
    public static final int LEVEL_NONE = 100;

    /**
     * Enable Logger
     * @param level Log Level
     */
    public static void enableLogging(int level) {
        L.level = level;
    }

    /**
     * Enable Logger: default level verbose
     */
    public static void enableLogging() {
        enableLogging(LEVEL_VERBOSE);
    }

    /**
     * Set Logger Level
     * @param level Log Level
     */
    public static void setLoggingLevel(int level) {
        enableLogging(level);
    }

    /**
     * Disable Logger
     */
    public static void disableLogging() {
        L.level = LEVEL_NONE;
    }

    public static void v(String tag, String msg) {
        L.v(tag, msg, null);
    }

    public static void v(String tag, String msg, Throwable t) {
        if(level <= LEVEL_VERBOSE) {
            Log.v(tag, msg, t);
        }
    }

    public static void d(String tag, String msg) {
        L.d(tag, msg, null);
    }

    public static void d(String tag, String msg, Throwable t) {
        if(level <= LEVEL_DEBUG) {
            Log.d(tag, msg, t);
        }
    }

    public static void i(String tag, String msg) {
        L.i(tag, msg, null);
    }

    public static void i(String tag, String msg, Throwable t) {
        if(level <= LEVEL_INFO) {
            Log.i(tag, msg, t);
        }
    }

    public static void w(String tag, String msg) {
        L.w(tag, msg, null);
    }

    public static void w(String tag, String msg, Throwable t) {
        if(level <= LEVEL_WARN) {
            Log.w(tag, msg, t);
        }
    }

    public static void e(String tag, String msg) {
        L.e(tag, msg, null);
    }

    public static void e(String tag, String msg, Throwable t) {
        if(level <= LEVEL_ERROR) {
            Log.e(tag, msg, t);
        }
    }

}

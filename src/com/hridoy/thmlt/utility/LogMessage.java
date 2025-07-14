package com.hridoy.thmlt.utility;

import android.util.Log;

import static com.hridoy.thmlt.ThMLT.TAG;

public class LogMessage {
    private static boolean isLoggingEnabled = false;
    private static int logLevel = Log.INFO; // Only log >= this level

    public static void enable(boolean enable) {
        isLoggingEnabled = enable;
    }

    public static void setLogLevel(int level) {
        logLevel = level;
    }

    public static void v(String message) {
        log(Log.VERBOSE, message);
    }

    public static void d(String message) {
        log(Log.DEBUG, message);
    }

    public static void i(String message) {
        log(Log.INFO, message);
    }

    public static void w(String message) {
        log(Log.WARN, message);
    }

    public static void e(String message) {
        log(Log.ERROR, message);
    }

    private static void log(int level, String message) {
        if (!isLoggingEnabled || level < logLevel) return;
        Log.println(level, TAG, message);
    }
}


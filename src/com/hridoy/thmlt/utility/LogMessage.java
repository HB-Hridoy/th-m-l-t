package com.hridoy.thmlt.utility;

import android.util.Log;
import static com.hridoy.thmlt.ThMLT.TAG;

public class LogMessage {
    private static boolean isLoggingEnabled = false;

    // Public methods to enable/disable logging
    public static void enable(boolean enable) {
        isLoggingEnabled = enable;
    }

    public static boolean isLoggingEnabled() {
        return isLoggingEnabled;
    }

    // Standard logging methods
    public static void v(String message) {
        if (isLoggingEnabled) Log.v(TAG, message);
    }

    public static void d(String message) {
        if (isLoggingEnabled) Log.d(TAG, message);
    }

    public static void i(String message) {
        if (isLoggingEnabled) Log.i(TAG, message);
    }

    public static void w(String message) {
        if (isLoggingEnabled) Log.w(TAG, message);
    }

    public static void e(String message) {
        if (isLoggingEnabled) Log.e(TAG, message);
    }
}

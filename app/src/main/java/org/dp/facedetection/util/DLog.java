package org.dp.facedetection.util;

import android.util.Log;

/**
 * Created by caydencui on 2018/10/23.
 */

public class DLog {
    public static boolean mSwitch = true;
    public static boolean mWrite = false;
    static long time;
    public static final int V = 1;
    public static final int D = 2;
    public static final int I = 3;
    public static final int W = 4;
    public static final int E = 5;

    public DLog() {
    }

    public static void clearCache() {
        FileUtil.writeFile("/sdcard/readsense.txt", "log0:\n", false);
    }

    public static void time(String tag) {
        if(time == 0L) {
            time = System.currentTimeMillis();
        } else {
            d(tag + " cost: " + (System.currentTimeMillis() - time));
            time = 0L;
        }

    }

    public static void v() {
        v((Object)null);
    }

    public static void d() {
        d((Object)null);
    }

    public static void i() {
        i((Object)null);
    }

    public static void w() {
        w((Object)null);
    }

    public static void e() {
        e((Object)null);
    }

    public static void v(Object message) {
        v((String)null, message);
    }

    public static void d(Object message) {
        d((String)null, message);
    }

    public static void i(Object message) {
        i((String)null, message);
    }

    public static void w(Object message) {
        w((String)null, message);
    }

    public static void e(Object message) {
        e((String)null, message);
    }

    public static void v(String tag, Object message) {
        llog(1, tag, message);
    }

    public static void d(String tag, Object message) {
        llog(2, tag, message);
    }

    public static void i(String tag, Object message) {
        llog(3, tag, message);
    }

    public static void w(String tag, Object message) {
        llog(4, tag, message);
    }

    public static void e(String tag, Object message) {
        llog(5, tag, message);
    }

    public static void llog(int type, String tagStr, Object obj) {
        if(mSwitch) {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            int index = 5;
            String className = stackTrace[index].getFileName();
            String methodName = stackTrace[index].getMethodName();
            int lineNumber = stackTrace[index].getLineNumber();
            String tag = tagStr == null?"DLog":tagStr;
            methodName = methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("[ (").append(className).append(":").append(lineNumber).append(")#").append(methodName).append(" ] ");
            String msg;
            if(obj == null) {
                msg = "Log with null Object";
            } else {
                msg = obj.toString();
            }

            if(msg != null) {
                stringBuilder.append(msg);
            }

            String logStr = stringBuilder.toString();
            if(mWrite) {
                FileUtil.writeFile("/sdcard/readsense.txt", tag + " : " + logStr + "\n", true);
            }

            switch(type) {
                case 1:
                    Log.v(tag, logStr);
                    break;
                case 2:
                    Log.d(tag, logStr);
                    break;
                case 3:
                    Log.i(tag, logStr);
                    break;
                case 4:
                    Log.w(tag, logStr);
                    break;
                case 5:
                    Log.e(tag, logStr);
            }

        }
    }
}

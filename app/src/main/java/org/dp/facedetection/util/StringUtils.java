package org.dp.facedetection.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by caydencui on 2018/10/23.
 */

public class StringUtils {
    public StringUtils() {
    }

    public static boolean isEmpty(CharSequence string) {
        return string == null || string.length() == 0 || string.equals("null");
    }

    public static boolean isSpace(String string) {
        return string == null || string.trim().length() == 0;
    }

    public static String null2Length0(String string) {
        return string == null?"":string;
    }

    public static int length(CharSequence string) {
        return string == null?0:string.length();
    }

    public static String upperFirstLetter(String string) {
        return !isEmpty(string) && Character.isLowerCase(string.charAt(0))?(char)(string.charAt(0) - 32) + string.substring(1):string;
    }

    public static String lowerFirstLetter(String string) {
        return !isEmpty(string) && Character.isUpperCase(string.charAt(0))?(char)(string.charAt(0) + 32) + string.substring(1):string;
    }

    public static String toDBC(String string) {
        if(isEmpty(string)) {
            return string;
        } else {
            char[] chars = string.toCharArray();
            int i = 0;

            for(int len = chars.length; i < len; ++i) {
                if(chars[i] == 12288) {
                    chars[i] = 32;
                } else if('！' <= chars[i] && chars[i] <= '～') {
                    chars[i] -= 'ﻠ';
                } else {
                    chars[i] = chars[i];
                }
            }

            return new String(chars);
        }
    }

    public static String toSBC(String string) {
        if(isEmpty(string)) {
            return string;
        } else {
            char[] chars = string.toCharArray();
            int i = 0;

            for(int len = chars.length; i < len; ++i) {
                if(chars[i] == 32) {
                    chars[i] = 12288;
                } else if(33 <= chars[i] && chars[i] <= 126) {
                    chars[i] += 'ﻠ';
                } else {
                    chars[i] = chars[i];
                }
            }

            return new String(chars);
        }
    }

    public static String getNowDateTime() {
        String format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat s_format = new SimpleDateFormat(format);
        Date d_date = new Date();
        String s_date = "";
        s_date = s_format.format(d_date);
        return s_date;
    }

    public static String getNowDate() {
        String format = "yyyy-MM-dd";
        SimpleDateFormat s_format = new SimpleDateFormat(format);
        Date d_date = new Date();
        String s_date = "";
        s_date = s_format.format(d_date);
        return s_date;
    }
}

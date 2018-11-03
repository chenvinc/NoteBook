package com.example.chenm.notebook.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author chenhongyu
 * @Date 2018/9/8
 * @Time 16:10
 * @Version 1.0
 * @Description ${DESCRIPTION}
 */
public class CommonUtils {

    public static String getYear() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy",Locale.getDefault());
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    public static String getMouth() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("MM",Locale.getDefault());
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    public static String getDay() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd",Locale.getDefault());
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    public static String getDate(long date) {
        Date currentTime = new Date(date);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
        return formatter.format(currentTime);
    }
}

package com.example.chenm.notebook.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

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
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    public static String getMouth() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("MM");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    public static String getDay() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    public static String getDate(long date) {
        Date currentTime = new Date(date);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
        return formatter.format(currentTime);
    }

    public static String getDate2(long date) {
        Date currentTime = new Date(date);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return formatter.format(currentTime);
    }

    public static String getDate3(long date) {
        Date currentTime = new Date(date);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(currentTime);
    }
}

package com.example.chenm.notebook.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.widget.ScrollView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * @author chenhongyu
 * @Date 2018/9/8
 * @Time 16:10
 * @Version 1.0
 * @Description 公用工具类
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

    /**
     * 截取scrollview的屏幕
     * **/
    private static Bitmap getScrollViewBitmap(ScrollView scrollView) {
        int h = 0;
        Bitmap bitmap;
        // 获取listView实际高度
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            h += scrollView.getChildAt(i).getHeight();
        }
        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(scrollView.getWidth(), h,
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        scrollView.draw(canvas);
        return bitmap;
    }

    /**
     * 截图RecyclerView
     **/
    private static File getListViewBitmap(RecyclerView recyclerView,File path) {
        int h = 0;
        Bitmap bitmap;
        // 获取listView实际高度
        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            h += recyclerView.getChildAt(i).getHeight();
        }
        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(recyclerView.getWidth(), h,
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        recyclerView.draw(canvas);
        // 测试输出
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (null != out) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }

    // 程序入口 截取ScrollView
    public static Bitmap shootScrollView(ScrollView scrollView) {
        String state = Environment.getExternalStorageState();
        if (!state.equals(Environment.MEDIA_MOUNTED)){
            return null;
        }
        return getScrollViewBitmap(scrollView);
    }

//    private static File getFilePath(){
//        Calendar now = new GregorianCalendar();
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss",Locale.getDefault());
//        String fileName = "SettlementImg" + formatter.format(now.getTime());
//        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/bookKeeping/");
//        File file =  new File(dir + fileName + ".png");
//        if (!dir.exists()) {
//            dir.mkdirs();
//        }
//        if (!file.exists()) {
//            try {
//                file.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return file;
//    }

    public static String getTimeSign(){
        Calendar now = new GregorianCalendar();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss",Locale.getDefault());
        String fileName = "SettlementImg" + formatter.format(now.getTime());
        return fileName;
    }
}


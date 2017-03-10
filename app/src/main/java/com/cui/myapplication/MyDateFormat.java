package com.cui.myapplication;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Cui on 2016/8/12.
 */
public class MyDateFormat {
    /**
     * @return 返回yyyy-MM-dd HH:mm:ss
     */
    public static SimpleDateFormat getDateTimeFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
    /**
     * @return 返回yyyy/MM/dd HH:mm:ss
     */
    public static SimpleDateFormat getDateTimeFormat2() {
        return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    }

    public static SimpleDateFormat getDateTimeFormatNoSecond(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm");
    }



    /**
     * @return 返回yyyy-MM-dd
     */
    public static SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd");
    }

    /**
     * @return 返回yyyy/MM/dd
     */
    public static SimpleDateFormat getDateFormat2() {
        return new SimpleDateFormat("yyyy/MM/dd");
    }

    /**
     * @return 返回yyyy年mm月dd日
     */
    public static SimpleDateFormat getDateFormatText() {
        return new SimpleDateFormat("yyyy年mm月dd日");
    }

    /**
     * @param date
     * @return 根据日期返回yyyy年mm月dd日格式
     */
    public static String getDateText(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return year + "年" + month + "月" + day + "日";
    }
}

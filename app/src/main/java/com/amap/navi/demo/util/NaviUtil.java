package com.amap.navi.demo.util;

import java.text.DecimalFormat;

/**
 * Created by my94493 on 2017/7/19.
 */

public class NaviUtil {
    public static final String Kilometer = "\u516c\u91cc";// "公里";
    public static final String Meter = "\u7c73";// "米";

    public static String getFriendlyLength(int lenMeter) {
//        if (lenMeter > 10000) // 10 km
//        {
//            int dis = lenMeter / 1000;
//            return dis + Kilometer;
//        }

        if (lenMeter > 1000) {
            float dis = (float) lenMeter / 1000;
            DecimalFormat fnum = new DecimalFormat("##0.0");
            String dstr = fnum.format(dis);
            return dstr + Kilometer;
        }

//        if (lenMeter > 100) {
//            int dis = lenMeter / 50 * 50;
//            return dis + Meter;
//        }
//
//        int dis = lenMeter / 10 * 10;
//        if (dis == 0) {
//            dis = 10;
//        }

        int dis = lenMeter;
        return dis + Meter;
    }

    public static String getFriendlyTime(int second) {
        if (second > 3600) {
            int hour = second / 3600;
            int miniate = (second % 3600) / 60;
            return hour + "小时" + miniate + "分钟";
        }
        if (second >= 60) {
            int miniate = second / 60;
            return miniate + "分钟";
        }
        return second + "秒";
    }
}

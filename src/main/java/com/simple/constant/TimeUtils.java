package com.simple.constant;

import java.text.SimpleDateFormat;
import java.util.logging.SimpleFormatter;

public class TimeUtils {

    public static String now() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(System.currentTimeMillis());
    }
}

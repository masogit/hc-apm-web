package com.ge.apm.view.SparePart;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OrderNumberGenerator {
    private static final String DATE_FORMAT = "yyMMddHHmmssSSS";

    public static String generateOrderNumber(String str) {
        StringBuilder orderNumber = new StringBuilder();

        // 生成日期部分
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        String datePart = dateFormat.format(new Date());
        orderNumber.append(str).append(datePart);

        return orderNumber.toString();
    }

    public static String generateOrderNumber(String str, Date date) {
        StringBuilder orderNumber = new StringBuilder();

        // 生成日期部分
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        String datePart = dateFormat.format(date);
        orderNumber.append(str).append(datePart);

        return orderNumber.toString();
    }

}
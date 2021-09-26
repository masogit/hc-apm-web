package com.ge.apm.service.zkb;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author : 503189362
 * @date : 2020/10/23
 * @description :
 */
public class CopyByInvok {

    public static String EMAIL_URL_TYPE_LASTDAY = "lastDay";
    public static String EMAIL_URL_TYPE_UNORDER = "unOrder";

    public static String T = "t";
    public static String F = "f";
    public static String FOUR_TO_ONE = "fourToOne";
    public static String NORMAL_ONE = "1";
    public static String NORMAL_ZERO = "0";
    public static String NORMAL_TWO = "2";
    public static String NORMAL_THREE = "3";

    public static <T> T translateField(Object souce, Class<T> target) throws Exception {
        T tarObj = target.newInstance();
        Class tempClass = souce.getClass();
        List<Field> fieldList = new ArrayList();
        fieldList = Arrays.asList(tempClass.getDeclaredFields());
        for (Field f : fieldList) {
            String name = f.getName();
            if("serialVersionUID".equals(name)){
                continue;
            }
            f.setAccessible(true);
            f.set(tarObj, f.get(souce));
        }
        return tarObj;
    }

    public static int getBetweenDay(Date date1, Date date2) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String sd1 = format.format(date1);
        String sd2 = format.format(date2);
        try {
            date1 = format.parse(sd1);
            date2 = format.parse(sd2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long days = (date2.getTime() - date1.getTime()) / (24*3600*1000);
        if((date2.getTime() - date1.getTime())% (24*3600*1000) != 0){
            days++;
        }
        return (int)days;
    }
    public int getBetweenDay(String sd1, String sd2) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date1 = format.parse(sd1);
            Date date2 = format.parse(sd2);
            long days = (date2.getTime() - date1.getTime()) / (24*3600*1000);
            if((date2.getTime() - date1.getTime())% (24*3600*1000) != 0){
                days++;
            }
            return (int)days;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getBetweenYear(Date date1, Date date2) throws ParseException {
        int n = 0;
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        date1 = f.parse(f.format(date1));
        date2 = f.parse(f.format(date2));
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        try {
            c1.setTime(date1);
            c2.setTime(date2);
        } catch (Exception e3) {
            System.out.println("wrong occured");
        }

        while (!c1.after(c2)) {
            n++;
            c1.add(Calendar.DATE, 1);
        }
        if(n % 365 == 0){
            return n/365;
        }else{
            n = n-1;
            n = n/365;
            return n;
        }
    }

    public static String compareDateByYyyyMMdd(Date date1,Date date2){
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date1 = f.parse(f.format(date1));
            date2 = f.parse(f.format(date2));

            if(date1.getTime() > date2.getTime()){
                return "GT";
            }else if(date1.getTime() < date2.getTime()){
                return "LT";
            }else{
                return "EQ";
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}

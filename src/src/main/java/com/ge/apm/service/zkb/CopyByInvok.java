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

}

package com.ge.apm.service.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ge.apm.web.TimeForm;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeUtils {
    static Logger logger = LoggerFactory.getLogger(TimeUtils.class);

    public static void timeDeal(TimeForm tf,Date startTime,Date endTime){
        Calendar calendar =TimeUtils.getCalendar();
        if(startTime!=null){
            calendar.setTime(startTime);
            tf.setFrom(new SimpleDateFormat("yyyyMMddHHmmss").format(startTime));
        }else{
            String strFrom =TimeUtils.getCurrentStartYYMM();
            tf.setFrom(strFrom);
        }
        if(endTime!=null){
            calendar.setTime(endTime);
            tf.setTo(new SimpleDateFormat("yyyyMMddHHmmss").format(endTime));
        }else{
            String strTo =TimeUtils.getCurrentEndYYMM();
            tf.setTo(strTo);
        }
    }

    public static Date getDateFromStr(String installDate, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(installDate);
        } catch (ParseException e) {
            logger.error("time format exception ,str is " + installDate);
        }
        return null;
    }

    public static  String getInputStartDate(Date date){
        String sdf = new SimpleDateFormat("YYYYMMDDHHMMSS").format(date);
        sdf=sdf.substring(0,6)+"01000000";
        return sdf;
    }

    public static  String getInputEndDate(Date date){
        Calendar calendar= TimeUtils.getCalendar();
        String strTo =new SimpleDateFormat("yyyyMMddHHmmss").format(date);
        Integer lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        strTo=strTo.substring(0,6)+lastDay+"235959";
        return strTo;
    }
    //获取当月的第一个天的日期
    public static String getCurrentStartYYMM(){
        String sdf = new SimpleDateFormat("YYYYMMDDHHMMSS").format(new Date());
        sdf=sdf.substring(0,6)+"01000000";
        return sdf;
    }
    //按指定格式及字符串返回当月的最后一天日期
    public static String getCurrentEndYYMM(){
        Calendar calendar= TimeUtils.getCalendar();
        String strTo =new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        Integer lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        strTo=strTo.substring(0,6)+lastDay+"235959";
        return strTo;
    }

    public static String getSpecifiedMonthEndDay(Date date){
        Calendar calendar= TimeUtils.getCalendar();
        String strTo =new SimpleDateFormat("yyyyMMddHHmmss").format(date);
        Integer lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        strTo=strTo.substring(0,6)+lastDay+"235959";
        return strTo;
    }



    /*input:2019-12-30T16:00:00.000+0000
    output Date format: 2019-12-30*/
    public static Date convertStrToDate(String strDate){
        strDate=strDate.replace(".000+0000","");
        strDate=strDate.replace("T"," ");
        Date date=null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }





    //返回指定月的最后一天的日期 格式为:YYYYMMDDHHMMSS
    public static  Calendar getCalendar(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.MILLISECOND, -1);
        return calendar;
    }
    //按字符串格式返回指定日期 格式为:YYYYMMDDHHMMSS
    public static String getStrYHMS(Date date, String format) {
        Calendar calendar = TimeUtils.getCalendar();
        calendar.setTime(date);
        Integer day = calendar.get(Calendar.DAY_OF_MONTH);
        DateTime dt = new DateTime(date);
        String result = dt.toString(format);
        if(day<10) {
            result= result.substring(0, 6) + "0"+day.toString()+"000000";
        }else
            result= result.substring(0, 6) +day.toString()+"000000";
        return result;
    }

    public static Integer getMonthNumber(Date date){
        Calendar calendar = Calendar.getInstance();
        if(date ==null){
            calendar.setTime(new Date());
        }else{
            calendar.setTime(date);
        }

        Integer month = calendar.get(Calendar.MONTH) + 1;
        return month;

    }




    public static String getStrDate(Date date, String format) {
        if (format == null) {
            format = "yyyy-MM-dd";
        }
        DateTime dt = new DateTime(date);
        return dt.toString(format);
    }

    public static String getStrDateYYMM(Date currentTime,String timeFormat) {

        if (timeFormat == null) {
            timeFormat = "yyyy-MM";
        }
        SimpleDateFormat formatter = new SimpleDateFormat(timeFormat);
        String dateString = formatter.format(currentTime);
        return dateString;
    }


    Date currentTime = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");


    public static String getDateFormat(String date) {
        Pattern pattern = Pattern.compile("(\\d*)([-/])(\\d*)([-/])(\\d*)");
        Matcher matcher = pattern.matcher(date);
        if (!matcher.find()) {
            return null;
        }
        if (matcher.groupCount() != 5) {
            return null;
        }

        String separator = matcher.group(2);
        //2017/11/12
        if (matcher.group(1).length() == 4) {
            return String.format("yyyy%sMM%sdd", separator, separator);
        }

        //11/12/2017
        if (matcher.group(5).length() == 4) {
            return String.format("MM%sdd%syyyy", separator, separator);
        }

        return null;
    }

    public static Date toDateFromString(String timestr, String... formatArray) {
        if (null == timestr || timestr.isEmpty()) {
            return null;
        }
        Date time = null;
        for (String format : formatArray) {
            time = TimeUtils.getDateFromStr(timestr, format);
            if (time != null) {
                return time;
            }
        }
        return time;
    }

    public static Integer getDays(Date time) {
        DateTime now = new DateTime();
        DateTime lastTime = new DateTime(time);
        return Days.daysBetween(lastTime, now).getDays();
    }
}

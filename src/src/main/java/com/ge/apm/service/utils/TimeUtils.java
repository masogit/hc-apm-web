package com.ge.apm.service.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeUtils {

    static Logger logger = LoggerFactory.getLogger(TimeUtils.class);

    public static Date getDateFromStr(String installDate, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(installDate);
        } catch (ParseException e) {
            logger.error("time format exception ,str is " + installDate);
        }
        return null;
    }

    public static String getStrDate(Date date, String format) {
        if (format == null) {
            format = "yyyy-MM-dd";
        }
        DateTime dt = new DateTime(date);
        return dt.toString(format);
    }

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

package webapp.framework.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtil {

    /**
     * Compare 2 dates using seconds precision.
     * Useful for JPA equality concern when comparing Date from Java world (with millisec precision) with date
     * retrieved from Oracle (second precision only).
     */
    public static boolean equalsWithSecondPrecision(Date date1, Date date2) {
        return removeMilliseconds(date1).equals(removeMilliseconds(date2));
    }

    public static int hashCodeWithSecondPrecision(Date date) {
        return removeMilliseconds(date).hashCode();
    }

    private static String removeMilliseconds(Date date) {
        String asString = Long.toString(date.getTime());
        return asString.substring(0, asString.length() - 3);
    }
    
    public static boolean listContainsDate(List<Date> listDate,Date date){
    	for(int i=0;i<listDate.size();++i){
    		Date tempDate=listDate.get(i);
    		if(tempDate.getTime()==date.getTime()){
    			return true;
    		}
    	}
    	return false;
    }
    
    public static int getMonthDays(int year,int month){
    	if(month==0 || month ==2 || month==4 || month==6 || month==7 || month ==9 || month==11){
    		return 31;
    	}
    	else{
    		Calendar currentCalendar=Calendar.getInstance();
    		currentCalendar.set(Calendar.YEAR, year);
    		currentCalendar.set(Calendar.MONTH,month);
			currentCalendar.set(Calendar.DAY_OF_MONTH,1);
			int lastDay=currentCalendar.get(Calendar.DAY_OF_YEAR);
			currentCalendar.set(Calendar.MONTH,month+1);
			int newDay=currentCalendar.get(Calendar.DAY_OF_YEAR);
			return newDay-lastDay;
    	}
    }
	/**
	 * 给日期加月份
	 * @param date
	 * @return
	 */
	public static Date addMonth(Date date,Integer month){

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, month);
		return cal.getTime();
	}

	/**
	 * 判断两个日期是否是同年、同月
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static Boolean isSameMonth(Date date1,Date date2){

		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);
		return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
	}


	/**
	 * 根据指定日期指定格式，格式化日期
	 * @param date 时间
	 * @param format 日期格式
	 * @return 格式化日期字符串
	 */
	public static String getFormatStrByDate(Date date,String format)
	{
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		return simpleDateFormat.format(date);
	}

	/**
	 * 根据字符串类型时间和日期格式返回时间
	 * @param dateStr 时间
	 * @param format 日期格式
	 * @return
	 */
	public static Date getDateByFormatStr(String dateStr,String format)
	{
		SimpleDateFormat sdf=new SimpleDateFormat(format);
		Date date = null;
		try {
			date=sdf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 获取当月第一天
	 * @return first day
	 */
	public static Date currentMonthFirstDay () {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	/**
	 * 获取下个月最后一天
	 * @return last day
	 */
	public static Date nextMonthLastDay () {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.add(Calendar.MONTH, 2);
		cal.add(Calendar.MILLISECOND, -1);
		return cal.getTime();
	}
}

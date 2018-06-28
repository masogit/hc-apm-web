package webapp.framework.util;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}
package core.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Alexander on 12.05.2017.
 */
public class DateUtils {

    public static final String dateFormat = "dd.MM.yyyy";

    public static Date parseDate(String dateString){
        SimpleDateFormat parser = new SimpleDateFormat(dateFormat);
        try {
            Date date = parser.parse(dateString);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int[] getMinHour(Date date){
        int[] result = new int[2];
        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
        calendar.setTime(date);
        result[0] = calendar.get(Calendar.MINUTE);
        result[1] = calendar.get(Calendar.HOUR_OF_DAY);
        return result;
    }

    public static boolean isSameDay(Date date1, Date date2){
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }
}

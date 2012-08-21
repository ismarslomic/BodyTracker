
package no.slomic.body.measurements.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    // 04.07.2012
    public final static SimpleDateFormat SHORT_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    // Wednesday 4. July 2012
    public final static SimpleDateFormat MEDIUM_DATE_FORMAT = new SimpleDateFormat(
            "EEEE d. MMMMM yyyy");
    public final static SimpleDateFormat MEDIUM_DATE_FORMAT_EX_YEAR = new SimpleDateFormat(
            "EEEE d. MMMMM");

    public final static SimpleDateFormat LONG_DATE_FORMAT = new SimpleDateFormat(
            "dd.MM.yyyy HH:mm:ss");

    /**
     * @param date String date IN format dd.MM.yyyy
     * @return Calendar date representing date sent as input. Returns null if
     *         invalid date string
     */
    public static Calendar getDate(String date) {
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(SHORT_DATE_FORMAT.parse(date));
        } catch (ParseException e) {
            return null;
        }

        return c;
    }

    public static String formatToLongFormat(Date date) {
        return LONG_DATE_FORMAT.format(date);
    }

    public static String formatToShortFormat(Calendar c) {
        return SHORT_DATE_FORMAT.format(c.getTime());
    }

    public static String formatToMediumFormat(Calendar c) {
        return MEDIUM_DATE_FORMAT.format(c.getTime());
    }

    /**
     * @param c
     * @return Today - if the date is today<br>
     *         Yesterday - if the date was yesterday<br>
     *         Wednesday 9. july - if the date is this year<br>
     *         Wednesday 9. july 2011 - if the date is not this year
     */
    public static String formatToMediumFormatExtended(Calendar c) {
        Calendar today = Calendar.getInstance();
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);

        String todayDateString = SHORT_DATE_FORMAT.format(today.getTime());
        String yesterdayDateString = SHORT_DATE_FORMAT.format(yesterday.getTime());
        String dateString = SHORT_DATE_FORMAT.format(c.getTime());

        if (dateString.equals(todayDateString))
            return "Today";
        else if (dateString.equals(yesterdayDateString))
            return "Yesterday";
        else if (c.get(Calendar.YEAR) == today.get(Calendar.YEAR))
            return MEDIUM_DATE_FORMAT_EX_YEAR.format(c.getTime());
        else
            return formatToMediumFormat(c);
    }

    public static String dateNowShortFormat() {
        return SHORT_DATE_FORMAT.format(new Date());
    }

    public static String dateNowMediumFormat() {
        return MEDIUM_DATE_FORMAT.format(new Date());
    }

    public static String dateNowLongFormat() {
        return LONG_DATE_FORMAT.format(new Date());
    }
}

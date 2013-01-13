
package no.slomic.body.measurements.utils;

import android.content.Context;
import android.content.res.Resources;

import no.slomic.body.measurements.R;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

// TODO: sjekk hvilke klasser som benytter seg av hva i denne klassen og vurder om dette utgår med 
// bruk av Joda date
public class DateUtils {

    // 04.07.2012
    public final static DateTimeFormatter SHORT_DATE_FORMAT = DateTimeFormat
            .forPattern("dd.MM.yyyy");

    // Wednesday 4. July 2012
    public final static DateTimeFormatter MEDIUM_DATE_FORMAT = DateTimeFormat
            .forPattern("EEEE d. MMMMM yyyy");

    public final static DateTimeFormatter MEDIUM_DATE_FORMAT_EX_YEAR = DateTimeFormat
            .forPattern("EEEE d. MMMMM");

    public final static DateTimeFormatter LONG_DATE_FORMAT = DateTimeFormat
            .forPattern("dd.MM.yyyy HH:mm:ss");

    public static String formatToLongFormat(DateTime date) {
        return date.toString(LONG_DATE_FORMAT);
    }

    public static String formatToShortFormat(DateTime date) {
        return date.toString(SHORT_DATE_FORMAT);
    }

    public static String formatToMediumFormat(DateTime d) {
        return d.toString(MEDIUM_DATE_FORMAT);
    }

    /**
     * @param c
     * @return Today - if the date is today<br>
     *         Yesterday - if the date was yesterday<br>
     *         Wednesday 9. july - if the date is this mYear<br>
     *         Wednesday 9. july 2011 - if the date is not this mYear
     */
    public static String formatToMediumFormatExtended(DateTime d, Resources resources) {
        DateTime today = DateTime.now();
        DateTime yesterday = today.minusDays(1);
        DateTime tomorrow = today.plusDays(1);

        String todayDateString = today.toString(SHORT_DATE_FORMAT);
        String yesterdayDateString = yesterday.toString(SHORT_DATE_FORMAT);
        String tomorrowDateString = tomorrow.toString(SHORT_DATE_FORMAT);
        String dateString = d.toString(SHORT_DATE_FORMAT);

        if (dateString.equals(todayDateString))
            return resources.getString(R.string.today);
        else if (dateString.equals(yesterdayDateString))
            return resources.getString(R.string.yesterday);
        else if (dateString.equals(tomorrowDateString))
            return resources.getString(R.string.tomorrow);
        else if (d.getYear() == today.getYear())
            return d.toString(MEDIUM_DATE_FORMAT_EX_YEAR);
        else
            return formatToMediumFormat(d);
    }

    /**
     * @param birtdateInMillis date of birth in milliseconds from the java epoch
     * @param res the resources to load strings from
     * @return years if the age is 1 mYear or more, otherwise it returns only
     *         months
     */
    public static String getAge(long birtdateInMillis, Resources res) {
        DateMidnight birthdate = new DateMidnight(birtdateInMillis);
        DateTime now = new DateTime();
        Years years = Years.yearsBetween(birthdate, now);

        // If age is 1 mYear or more return only years
        if (years.getYears() >= 1) {
            return res.getQuantityString(R.plurals.numberOfYears, years.getYears(),
                    years.getYears());
        } else {
            Months months = Months.monthsBetween(birthdate, now);
            return res.getQuantityString(R.plurals.numberOfMonths, months.getMonths(),
                    months.getMonths());
        }
    }
}

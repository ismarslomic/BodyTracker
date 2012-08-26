/**
 * 
 */

package no.slomic.body.measurements.preferences;

import android.content.Context;
import android.util.AttributeSet;

import no.slomic.body.measurements.utils.DateUtils;

/**
 * @author ismar.slomic
 */
public class AgePickerPreference extends DatePickerPreference {


    /**
     * @param context
     * @param attrs
     */
    public AgePickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /*
     * (non-Javadoc)
     * @see
     * no.slomic.body.measurements.preferences.NumberPickerPreference#getSummary
     * ()
     */
    @Override
    public CharSequence getSummary() {
        long birtdateInMillis = getPersistedLong(super.mDefaultDate);
        return DateUtils.getAge(birtdateInMillis, getContext().getResources());
    }

}

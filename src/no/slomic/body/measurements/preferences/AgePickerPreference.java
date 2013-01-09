// Restrukturert: ok

package no.slomic.body.measurements.preferences;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import no.slomic.body.measurements.utils.DateUtils;

import org.joda.time.DateTime;

public class AgePickerPreference extends DatePickerPreference {

    /**
     * @param context
     * @param attrs
     */
    public AgePickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public CharSequence getSummary() {
        long birtdateInMillis = getPersistedLong(this.mSelectedDate.getMillis());
        return DateUtils.getAge(birtdateInMillis, getContext().getResources());
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        String defaultAgeSpecifiedInPref = a.getString(index);

        if (defaultAgeSpecifiedInPref != null) // if the default age is
                                               // specified in the preferences
        {
            try // try to parse it
            {
                int age = Integer.parseInt(defaultAgeSpecifiedInPref);
                this.mDefaultDate = DateTime.now().minusYears(age);
            } catch (Exception e) // if the parsing fails, just initiate the
                                  // default date variable
            {
                this.mDefaultDate = new DateTime();
            }
        } else // no default age is specified in the preferences
        {
            this.mDefaultDate = new DateTime();
        }

        return this.mDefaultDate.getMillis(); // return the date in millisecunds
    }
}

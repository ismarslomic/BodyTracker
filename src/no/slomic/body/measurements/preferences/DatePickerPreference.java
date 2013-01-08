// Restrukturert: ok

package no.slomic.body.measurements.preferences;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import no.slomic.body.measurements.R;
import no.slomic.body.measurements.utils.DateUtils;

import org.joda.time.DateTime;

public class DatePickerPreference extends DialogPreference implements
        DatePicker.OnDateChangedListener {

    protected DateTime mSelectedDate;
    protected DateTime mDefaultDate;
    private static final String DATE_TIME_NOW = "Now";
    private DatePicker mDatePicker;
    
    public DatePickerPreference(Context context, AttributeSet attrs) 
    {
        super(context, attrs);
    }

    @Override
    protected View onCreateDialogView() {
        // Inflate the layout
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_dialog_datepicker, null);
       
        // Initialize new date picker
        mDatePicker = (DatePicker) view.findViewById(R.id.date_picker);
        int day = mSelectedDate.getDayOfMonth();
        int month = mSelectedDate.getMonthOfYear()-1; // date picker months 0-11
        int year = mSelectedDate.getYear();
        mDatePicker.init(year, month, day, this);
        
        return view;
    }
    
    @Override
    public CharSequence getSummary() 
    {
        String summary = "";
        if (isPersistent()) {
            long persistedDateInMillis = getPersistedLong(mSelectedDate.getMillis());
            DateTime persistedDate = new DateTime(persistedDateInMillis);
            summary = DateUtils.formatToMediumFormat(persistedDate);
        }

        return summary;
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) 
    {
        if (restorePersistedValue) // user has already set date, get persisted value
        {
            // Restore existing state
            long persistedDate = this.getPersistedLong(mDefaultDate.getMillis());
            mSelectedDate = new DateTime(persistedDate);
        } 
        else 
        {
            // Set default state from the XML attribute
            long defaultDate = (Long) defaultValue;
            mSelectedDate = new DateTime(defaultDate);
            persistLong(defaultDate);
        }
    }
    
    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) 
    {
        String defaultDateSpecifiedInPref = a.getString(index);
        
        if(defaultDateSpecifiedInPref != null) // if the default date is specified in the preferences
        {
            if(defaultDateSpecifiedInPref.equals(DATE_TIME_NOW)) // if todays date should be as default
                mDefaultDate = DateTime.now();
            
            else // given date is specificed in the preferences
            {
                try // try to parse it
                {
                    mDefaultDate = DateTime.parse(defaultDateSpecifiedInPref, DateUtils.SHORT_DATE_FORMAT);
                }
                catch(Exception e) // if the parsing fails, just initiate the defafult date variable
                {
                    mDefaultDate = new DateTime();
                }
             }
        }
        else // no default date is specified in the preferences
        {
            mDefaultDate = new DateTime();
        }
        
        return mDefaultDate.getMillis(); // return the date in millisecunds
    }
    
    @Override
    protected void onDialogClosed(boolean positiveResult) 
    {
        super.onDialogClosed(positiveResult);

        // If the user clicks CANCEL
        if (!positiveResult) {
            return;
        }

        // Persist current value if needed
        if (shouldPersist()) 
        {
            persistLong(mSelectedDate.getMillis());
        }

        // Notify activity about changes (to update preference summary line)
        notifyChanged();
    }
    
    @Override
    protected Parcelable onSaveInstanceState() 
    {
        Parcelable superState = super.onSaveInstanceState();

        SavedState savedState = new SavedState(superState);
        savedState.stateToSave = this.mSelectedDate.getMillis();

        return savedState;
    }
    
    @Override
    protected void onRestoreInstanceState(Parcelable state) 
    {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        // get saved state
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());

        // set saved state to the class variables
        this.mSelectedDate = new DateTime(savedState.stateToSave);
        
        // is this necessary?
        //notifyChanged();
    }
    
    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) 
    {
        monthOfYear++; // Joda Datetime months 1-12, picker 0-11
        mSelectedDate = new DateTime(year, monthOfYear, dayOfMonth, 0, 0);
    }

    // More information at http://developer.android.com/guide/topics/ui/settings.html#CustomSaveState
    private static class SavedState extends BaseSavedState {
        // Member that holds the setting's value
        long stateToSave;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel source) {
            super(source);
            // Get the current preference's value
            this.stateToSave = source.readLong();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            // Write the preference's value
            dest.writeLong(this.stateToSave);
        }

        // Standard creator object using an instance of this class
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}

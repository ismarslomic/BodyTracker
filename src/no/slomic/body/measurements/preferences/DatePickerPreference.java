
package no.slomic.body.measurements.preferences;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import no.slomic.body.measurements.R;

import java.util.Calendar;

public class DatePickerPreference extends DialogPreference implements
        DatePicker.OnDateChangedListener {

    // Real defaults
    protected final long mDefaultDate;

    // Current value
    private long mCurrentDate;

    public DatePickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDefaultDate = Calendar.getInstance().getTimeInMillis();

    }

    @Override
    protected View onCreateDialogView() {
        // Inflate layout
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_dialog_datepicker, null);

        final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(mCurrentDate);
        int date = c.get(Calendar.DATE);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);

        datePicker.init(year, month, date, this);

        return view;
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        if (restoreValue) {
            // Restore state
            mCurrentDate = getPersistedLong(mDefaultDate);
        } else {
            // Set state
            mCurrentDate = (Long) defaultValue;
            persistLong(mCurrentDate);
        }
    }

    /*
     * @Override protected void onBindView(View view) { super.onBindView(view);
     * // Set value to the date picker widget final DatePicker datePicker =
     * (DatePicker) view.findViewById(R.id.date_picker); if (datePicker != null)
     * { Calendar c = Calendar.getInstance(); c.setTimeInMillis(mCurrentDate);
     * int date = c.get(Calendar.DATE); int month = c.get(Calendar.MONTH); int
     * year = c.get(Calendar.YEAR); datePicker.init(year, month, date, this); }
     * }
     */

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        // Return if change was cancelled
        if (!positiveResult) {
            return;
        }

        // Persist current value if needed
        if (shouldPersist()) {
            persistLong(mCurrentDate);
        }

        // Notify activity about changes (to update preference summary line)
        notifyChanged();
    }

    /*
     * (non-Javadoc)
     * @see
     * android.preference.DialogPreference#onRestoreInstanceState(android.os
     * .Parcelable)
     */
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        // end

        this.mCurrentDate = ss.stateToSave;
        notifyChanged();
    }

    /*
     * (non-Javadoc)
     * @see android.preference.DialogPreference#onSaveInstanceState()
     */
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        SavedState ss = new SavedState(superState);
        ss.stateToSave = this.mCurrentDate;

        return ss;
    }

    @Override
    public CharSequence getSummary() {
        String summary = "";
        if (isPersistent()) {
            long persistentDate = getPersistedLong(mCurrentDate);

            // summary = DateUtils.formatToMediumFormat(c);
        }

        return summary;
    }

    // TODO: det er strengt tatt ikke nødvendig å lagre verdien hver gang den
    // oppdateres, kan bare hente ut ved lukking av dialog
    /*
     * (non-Javadoc)
     * @see
     * android.widget.DatePicker.OnDateChangedListener#onDateChanged(android
     * .widget.DatePicker, int, int, int)
     */
    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(year, monthOfYear, dayOfMonth);

        mCurrentDate = c.getTimeInMillis();
        notifyChanged();
    }

    static class SavedState extends BaseSavedState {
        long stateToSave;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.stateToSave = in.readLong();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeLong(this.stateToSave);
        }

        // required field that makes Parcelables from a Parcel
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

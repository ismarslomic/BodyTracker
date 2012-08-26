
package no.slomic.body.measurements.preferences;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import no.slomic.body.measurements.R;

public class NumberPickerPreference extends DialogPreference implements
        NumberPicker.OnValueChangeListener {

    // Namespaces to read attributes
    private static final String PREFERENCE_NS = "http://schemas.android.com/apk/res/no.slomic.body.measurements";
    private static final String ANDROID_NS = "http://schemas.android.com/apk/res/android";

    // Attribute names
    private static final String ATTR_DEFAULT_VALUE = "defaultValue";
    private static final String ATTR_MIN_VALUE = "minValue";
    private static final String ATTR_MAX_VALUE = "maxValue";
    private static final String ATTR_UNIT = "unit";

    // Default values for defaults
    private static final int DEFAULT_CURRENT_VALUE = 50;
    private static final int DEFAULT_MIN_VALUE = 0;
    private static final int DEFAULT_MAX_VALUE = 100;

    // Real defaults
    protected final int mDefaultValue;
    private final int mMaxValue;
    private final int mMinValue;

    // Current value
    private int mCurrentValue;

    // View elements
    private NumberPicker mNumberPicker;

    public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Read parameters from attributes
        mMinValue = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_MIN_VALUE, DEFAULT_MIN_VALUE);
        mMaxValue = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_MAX_VALUE, DEFAULT_MAX_VALUE);
        mDefaultValue = attrs.getAttributeIntValue(ANDROID_NS, ATTR_DEFAULT_VALUE,
                DEFAULT_CURRENT_VALUE);
    }

    @Override
    protected View onCreateDialogView() {
        // Get current value from preferences
        mCurrentValue = getPersistedInt(mDefaultValue);

        // Inflate layout
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_dialog_numberpicker, null);

        // Setup NumberPicker
        mNumberPicker = (NumberPicker) view.findViewById(R.id.number_picker);
        mNumberPicker.setOnValueChangedListener(this);

        // Setup min, max and current value
        mNumberPicker.setMaxValue(mMaxValue);
        mNumberPicker.setMinValue(mMinValue);
        mNumberPicker.setValue(mCurrentValue);

        return view;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        // Return if change was cancelled
        if (!positiveResult) {
            return;
        }

        // Persist current value if needed
        if (shouldPersist()) {
            persistInt(mCurrentValue);
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

        this.mCurrentValue = ss.stateToSave;
        mNumberPicker.setValue(mCurrentValue);

    }

    /*
     * (non-Javadoc)
     * @see android.preference.DialogPreference#onSaveInstanceState()
     */
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        SavedState ss = new SavedState(superState);
        ss.stateToSave = this.mCurrentValue;

        return ss;
    }

    @Override
    public CharSequence getSummary() {
        // Format summary string with current value
        String summary = super.getSummary().toString();
        int value = getPersistedInt(mDefaultValue);
        return String.format(summary, value);
    }

    /*
     * (non-Javadoc)
     * @see
     * android.widget.NumberPicker.OnValueChangeListener#onValueChange(android
     * .widget.NumberPicker, int, int)
     */
    @Override
    public void onValueChange(NumberPicker picker, int oldValue, int newValue) {
        // Update current value
        mCurrentValue = newValue;

    }

    static class SavedState extends BaseSavedState {
        int stateToSave;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.stateToSave = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.stateToSave);
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

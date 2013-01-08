//Restrukturert: OK

package no.slomic.body.measurements.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker.Formatter;
import android.widget.Toast;

import no.slomic.body.measurements.R;
import no.slomic.body.measurements.activities.SettingsActivity;
import no.slomic.body.measurements.entities.Measurement;
import no.slomic.body.measurements.entities.Quantity;
import no.slomic.body.measurements.entities.WeightUnit;
import no.slomic.body.measurements.preferences.StaticPreferences;
import no.slomic.body.measurements.storage.WeightMeasurementDAO;
import no.slomic.body.measurements.utils.DateUtils;
import no.slomic.body.measurements.utils.QuantityStringFormat;

import org.joda.time.DateTime;

public class NewWeightMeasurement extends DialogFragment implements OnClickListener,
        OnDateSetListener, DialogInterface.OnClickListener {
    private Button mDateButton; // the measurement date UI controller

    // shared preference
    private SharedPreferences mSharedPreferences;
    private String mMetricUnits;
    private String mImperialUnits;
    private String mSystemOfMeasurement;

    // bundle arguments
    private static final String ARGS_DATE = "date";
    private static final String ARGS_VALUE_ARRAY_INDEX = "value_index";

    // values of the measurement date and value
    private DateTime mDate;
    private Quantity mQuantity;
    private int mValueArrayIndex;

    // callback listener to inform when new weight measurement is saved
    private OnWeightMeasurementCreatedListener mListener;

    private static final String TAG_DATE_PICKER = "datePicker";

    private String[] mQuantityValues = new String[3901];
    private CircularSeekBar seekBar = new CircularSeekBar();

    public static NewWeightMeasurement newInstance() {
        return new NewWeightMeasurement();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View v = factory.inflate(R.layout.layout_dialog_new_measurement, null);

        // Shared preferences
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mMetricUnits = getActivity().getResources().getString(R.string.metric_units);
        mImperialUnits = getActivity().getResources().getString(R.string.imperial_units);
        mSystemOfMeasurement = mSharedPreferences.getString(
                SettingsActivity.PREFERENCE_METRIC_SYSTEM_KEY, mMetricUnits);

        generateQuantityValues();
        initializeMeasurement(savedInstanceState);

        // Initialize the UI controller for measurement date
        mDateButton = (Button) v.findViewById(R.id.date_button);
        mDateButton.setOnClickListener(this);
        mDateButton.setText(DateUtils.formatToMediumFormatExtended(mDate));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_alert_dialog_new_weight);
        builder.setIconAttribute(R.drawable.add);
        builder.setView(v);
        builder.setPositiveButton(R.string.alert_dialog_ok, this);
        builder.setNegativeButton(R.string.alert_dialog_cancel, this);
        return builder.create();
    }

    /**
     * 
     */
    private void setQuantityValue() {
        String quantityValue = mQuantity.getValue() + "";
        int index = 0;

        for (int i = 0; i < mQuantityValues.length; i++) {
            if (mQuantityValues[i].equals(quantityValue)) {
                index = i;
                break;
            }
        }
        mQuantityValuePicker.setValue(index);
    }

    private void generateQuantityValues() {
        int arrayIndex = 0;
        double arrayValue = 0;
        Quantity q;

        while (arrayValue < 10) {
            q = new Quantity(arrayValue, WeightUnit.KG);
            mQuantityValues[arrayIndex] = (Math.round(arrayValue * 100.0) / 100.0) + "";
            // mSeekBarValues2.add(arrayIndex, Math.round(arrayValue * 100.0) /
            // 100.0);
            arrayValue += 0.01;
            arrayIndex++;
        }

        q = new Quantity(arrayValue, WeightUnit.KG);
        mQuantityValues[arrayIndex] = (Math.round(arrayValue * 10.0) / 10.0) + "";
        // mSeekBarValues[arrayIndex] = Double.toString(Math.round(arrayValue *
        // 100.0) / 100.0);
        // mSeekBarValues2.add(arrayIndex, Math.round(arrayValue * 100.0) /
        // 100.0);

        while (arrayValue < 300) {
            q = new Quantity(arrayValue, WeightUnit.KG);
            mQuantityValues[arrayIndex] = (Math.round(arrayValue * 10.0) / 10.0) + "";
            // mSeekBarValues[arrayIndex] =
            // Double.toString(Math.round(arrayValue * 100.0) / 100.0);
            // mSeekBarValues2.add(arrayIndex, Math.round(arrayValue * 100.0) /
            // 100.0);
            arrayValue += 0.1;
            arrayIndex++;
        }
    }

    public void initializeMeasurement(Bundle savedInstanceState) {
        // the fragment is restored
        if (savedInstanceState != null) {
            mDate = (DateTime) savedInstanceState.getSerializable(ARGS_DATE);
            mValueArrayIndex = savedInstanceState.getInt(ARGS_VALUE_ARRAY_INDEX);
            double value = Double.parseDouble(mQuantityValues[mValueArrayIndex]);
            mQuantity = new Quantity(value, WeightUnit.KG);

            DatePickerFragment dpf = (DatePickerFragment) getActivity().getSupportFragmentManager()
                    .findFragmentByTag(TAG_DATE_PICKER);
            if (dpf != null) {
                dpf.setListener(this);
            }
        }
        // the fragment is created for the first time
        else {
            mDate = DateTime.now();

            // get the latest measurement from storage
            WeightMeasurementDAO mDao = new WeightMeasurementDAO(getActivity());
            mDao.open();
            Measurement latestMeasurement = mDao.getLatest();
            mDao.close();

            // if latest measurement found set this as default
            if (latestMeasurement != null)
                mQuantity = latestMeasurement.getQuantity();
            // no measurement registered, use avarage weight for given sex as
            // default
            else
                mQuantity = getAvarageWeight();
        }
    }

    private Quantity getAvarageWeight() {
        // get the sex of current user (male or female) from shared preferences
        String male = getResources().getString(R.string.sex_male);
        String sex = mSharedPreferences
                .getString(SettingsActivity.PREFERENCE_ACCOUNT_SEX_KEY, male);

        // get avarage weight for given sex
        if (sex.equals(male)) // male
            return StaticPreferences.AVARAGE_MALE_WEIGHT;
        else
            // female
            return StaticPreferences.AVARAGE_FEMALE_WEIGHT;
    }

    private boolean save() {
        double value = Double.parseDouble(mQuantityValues[mQuantityValuePicker.getValue()]);
        Measurement measurement = new Measurement(new Quantity(value, WeightUnit.KG), mDate);

        WeightMeasurementDAO mDAO = new WeightMeasurementDAO(getActivity());
        mDAO.open();
        mDAO.create(measurement);
        mDAO.close();

        mListener.onWeightMeasurementCreated(measurement);
        return true;
    }

    // Save measurement date and the quantity value when for instance rotating
    // the screen
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ARGS_DATE, mDate);
        outState.putSerializable(ARGS_VALUE_ARRAY_INDEX, mQuantityValuePicker.getValue());
    }

    // When user clicks on the measurement date text field open new date picker
    // dialog
    @Override
    public void onClick(View v) {
        if (v == mDateButton) {
            DatePickerFragment datePickerDialog = DatePickerFragment.newInstance(this, mDate);
            datePickerDialog.show(getFragmentManager(), TAG_DATE_PICKER);
        }
    }

    // Add listener (MainActivity) for callback after new measurement is created
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnWeightMeasurementCreatedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnMeasurementCreatedListener");
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int whichButton) {
        switch (whichButton) {
            case DialogInterface.BUTTON_POSITIVE: // OK button pressed
                boolean saved = save();
                if (saved) // the input was valid and got saved
                    Toast.makeText(getActivity(),
                            R.string.message_toast_add_new_measurement_positive, Toast.LENGTH_SHORT)
                            .show();
                else
                    // the input was invalid and not saved
                    Toast.makeText(getActivity(),
                            R.string.message_toast_add_new_measurement_negative, Toast.LENGTH_SHORT)
                            .show();
                break;
            case DialogInterface.BUTTON_NEGATIVE: // CANCEL button pressed, do
                                                  // something below if you need
                                                  // to
                break;
            default: // Some strange button pressed, do something strange below
                     // if you need to
                break;
        }
    }

    // Called when new measurement is set from the date picker
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        monthOfYear++; // DatePickerFragment months goes from 0-11 while Joda
                       // DateTime 1-12
        mDate = new DateTime(year, monthOfYear, dayOfMonth, 0, 0);
        mDateButton.setText(DateUtils.formatToMediumFormatExtended(mDate));
    }

    // Callback interface when new measurement is created (and saved)
    public interface OnWeightMeasurementCreatedListener {
        public abstract void onWeightMeasurementCreated(Measurement measurement);
    }

    public String getFormattedValue(Quantity q) {
        if (mSystemOfMeasurement.equals(mMetricUnits))
            return QuantityStringFormat.formatWeightToMetric(q);
        else
            return QuantityStringFormat.formatWeightToImperial(q);
    }

    class WeightQuantityValueNumberFormatter implements Formatter {
        public WeightQuantityValueNumberFormatter() {
        }

        @Override
        public String format(int value) {
            Quantity q = new Quantity(Double.parseDouble(mQuantityValues[value]), WeightUnit.KG);

            if (mSystemOfMeasurement.equals(mMetricUnits))
                return QuantityStringFormat.formatWeightToMetric(q);
            else
                return QuantityStringFormat.formatWeightToImperial(q);
        }
    }
}

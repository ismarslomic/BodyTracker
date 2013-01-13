// Restrukturert: OK

package no.slomic.body.measurements.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
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
import no.slomic.body.measurements.views.CircularSeekBar;

import org.joda.time.DateTime;

public class NewWeightMeasurement extends DialogFragment implements OnClickListener,
        OnDateSetListener, DialogInterface.OnClickListener {
    private Button mDateButton; // the measurement date UI controller

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

    private double[] mQuantityValues = new double[3901];
    private CircularSeekBar mSeekBar;

    public static NewWeightMeasurement newInstance() {
        return new NewWeightMeasurement();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View v = factory.inflate(R.layout.layout_dialog_new_measurement, null);

        // Initialize the circular seek bar widget
        this.mSeekBar = (CircularSeekBar) v.findViewById(R.id.circularSeekBar);
        this.mSeekBar.setEmptyCircleColor(Color.GRAY);
        this.mSeekBar.setSelectedCircleColor(Color.WHITE);
        this.mSeekBar.setSeekBarThumsColor(Color.BLACK);
        this.mSeekBar.setButtonPushedColor(Color.LTGRAY);

        // Setting the values that seek bar will iterate through
        generateValueArray();
        this.mSeekBar.setValueArray(this.mQuantityValues);

        // Sets how many steps should be changed when pressing + and - buttons
        this.mSeekBar.setButtonChangeInterval(10);

        // Formatting the value into a string representation
        CircularSeekBar.Formatter f = new CircularSeekBar.Formatter() {
            @Override
            public String format(double value) {
                Quantity q = new Quantity(value, WeightUnit.KG);
                return QuantityStringFormat.formatQuantityValue(q,
                        PreferenceManager.getDefaultSharedPreferences(getActivity()),
                        getResources());
            }

        };
        this.mSeekBar.setFormatter(f);
        initializeMeasurement(savedInstanceState);

        // Initialize the UI controller for measurement date which is in the
        // title of the alertdialog
        final View dateView = factory.inflate(R.layout.layout_dialog_date_title, null);
        this.mDateButton = (Button) dateView.findViewById(R.id.dialog_date_button);
        this.mDateButton.setOnClickListener(this);
        this.mDateButton
                .setText(DateUtils.formatToMediumFormatExtended(this.mDate, getResources()));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCustomTitle(dateView);
        builder.setIconAttribute(R.drawable.add);
        builder.setView(v);
        builder.setPositiveButton(R.string.alert_dialog_ok, this);
        builder.setNegativeButton(R.string.alert_dialog_cancel, this);
        return builder.create();
    }

    /** Generates the values in the valueArray **/
    private void generateValueArray() {
        int arrayIndex = 0;
        double arrayValue = 0;

        while (arrayValue < 10) {
            this.mQuantityValues[arrayIndex] = (Math.round(arrayValue * 100.0) / 100.0);
            arrayValue += 0.01;
            arrayIndex++;
        }

        this.mQuantityValues[arrayIndex] = (Math.round(arrayValue * 10.0) / 10.0);

        while (arrayValue < 300) {
            this.mQuantityValues[arrayIndex] = (Math.round(arrayValue * 10.0) / 10.0);
            arrayValue += 0.1;
            arrayIndex++;
        }
    }

    public void initializeMeasurement(Bundle savedInstanceState) {
        // the fragment is restored
        if (savedInstanceState != null) {
            this.mDate = (DateTime) savedInstanceState.getSerializable(ARGS_DATE);
            this.mValueArrayIndex = savedInstanceState.getInt(ARGS_VALUE_ARRAY_INDEX);
            double value = this.mQuantityValues[this.mValueArrayIndex];
            this.mQuantity = new Quantity(value, WeightUnit.KG);

            DatePickerFragment dpf = (DatePickerFragment) getActivity().getSupportFragmentManager()
                    .findFragmentByTag(TAG_DATE_PICKER);
            if (dpf != null) {
                dpf.setListener(this);
            }

            this.mSeekBar.setSelectedStep(this.mValueArrayIndex);
        }
        // the fragment is created for the first time
        else {
            this.mDate = DateTime.now();

            // get the latest measurement from storage
            WeightMeasurementDAO mDao = new WeightMeasurementDAO(getActivity());
            mDao.open();
            Measurement latestMeasurement = mDao.getLatest();
            mDao.close();

            // if latest measurement found set this as default
            if (latestMeasurement != null)
                this.mQuantity = latestMeasurement.getQuantity();
            // no measurement registered, use avarage weight for given sex as
            // default
            else
                this.mQuantity = getAvarageWeight();

            this.mSeekBar.setSelectedStepForValue(this.mQuantity.getValue());
        }
    }

    private Quantity getAvarageWeight() {
        // get the sex of current user (male or female) from shared preferences
        String male = getResources().getString(R.string.sex_male);
        String sex = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(
                SettingsActivity.PREFERENCE_ACCOUNT_SEX_KEY, male);

        // get avarage weight for given sex
        if (sex.equals(male)) // male
            return StaticPreferences.AVARAGE_MALE_WEIGHT;
        else
            // female
            return StaticPreferences.AVARAGE_FEMALE_WEIGHT;
    }

    private boolean save() {
        double value = this.mSeekBar.getSelectedValue();
        Measurement measurement = new Measurement(new Quantity(value, WeightUnit.KG), this.mDate);

        WeightMeasurementDAO mDAO = new WeightMeasurementDAO(getActivity());
        mDAO.open();
        mDAO.create(measurement);
        mDAO.close();

        this.mListener.onWeightMeasurementCreated(measurement);
        return true;
    }

    // Save measurement date and the quantity value when for instance rotating
    // the screen
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ARGS_DATE, this.mDate);
        outState.putSerializable(ARGS_VALUE_ARRAY_INDEX, this.mSeekBar.getSelectedStep());
    }

    // When user clicks on the measurement date text field open new date picker
    // dialog
    @Override
    public void onClick(View v) {
        if (v == this.mDateButton) {
            DatePickerFragment datePickerDialog = DatePickerFragment.newInstance(this, this.mDate);
            datePickerDialog.show(getFragmentManager(), TAG_DATE_PICKER);
        }
    }

    // Add listener (MainActivity) for callback after new measurement is created
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.mListener = (OnWeightMeasurementCreatedListener) activity;
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
                Toast.makeText(getActivity(), R.string.message_toast_add_new_measurement_positive,
                        Toast.LENGTH_SHORT).show();
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
        this.mDate = new DateTime(year, monthOfYear, dayOfMonth, 0, 0);
        this.mDateButton
                .setText(DateUtils.formatToMediumFormatExtended(this.mDate, getResources()));
    }

    // Callback interface when new measurement is created (and saved)
    public interface OnWeightMeasurementCreatedListener {
        public abstract void onWeightMeasurementCreated(Measurement measurement);
    }
}

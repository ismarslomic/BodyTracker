
package no.slomic.body.measurements.fragments;

import no.slomic.body.measurements.R;
import no.slomic.body.measurements.activities.SettingsActivity;
import no.slomic.body.measurements.entities.LengthUnit;
import no.slomic.body.measurements.entities.Measurement;
import no.slomic.body.measurements.entities.Quantity;
import no.slomic.body.measurements.preferences.StaticPreferences;
import no.slomic.body.measurements.storage.MeasurementDAO;
import no.slomic.body.measurements.storage.WaistMeasurementDAO;
import no.slomic.body.measurements.utils.DateUtils;
import no.slomic.body.measurements.utils.QuantityStringFormat;
import no.slomic.body.measurements.views.CircularSeekBar;

import org.joda.time.DateTime;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class NewWaistMeasurement extends NewMeasurement implements DialogInterface.OnClickListener {
    protected OnWaistMeasurementCreatedListener mListener;

    public interface OnWaistMeasurementCreatedListener {
        public abstract void onWaistMeasurementCreated(Measurement measurement);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.mListener = (OnWaistMeasurementCreatedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnMeasurementCreatedListener");
        }
    }

    public static NewWaistMeasurement newInstance()
    {
        return new NewWaistMeasurement();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Initialize the circular seek bar widget
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View v = factory.inflate(R.layout.layout_dialog_new_measurement, null);

        initializeSeekBarLayout(v);

        // Setting the values that seek bar will iterate through
        generateValueArray();
        this.mSeekBar.setValueArray(this.mQuantityValues);

        // Sets how many steps should be changed when pressing + and - buttons
        this.mSeekBar.setButtonChangeInterval(5);

        // Formatting the value into a string representation
        CircularSeekBar.Formatter f = new CircularSeekBar.Formatter() {
            @Override
            public String format(double value) {
                Quantity q = new Quantity(value, LengthUnit.CM);
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
        mQuantityValues = new double[3001];

        int arrayIndex = 0;
        double arrayValue = 0;

        while (arrayValue < 300) {
            this.mQuantityValues[arrayIndex] = (Math.round(arrayValue * 10.0) / 10.0);
            arrayValue += 0.1;
            arrayIndex++;
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int whichButton) {
        switch (whichButton) {
            case DialogInterface.BUTTON_POSITIVE: // OK button pressed
                save();
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

    public void initializeMeasurement(Bundle savedInstanceState) {
        // the fragment is restored
        if (savedInstanceState != null) {
            this.mDate = (DateTime) savedInstanceState.getSerializable(ARGS_DATE);
            this.mValueArrayIndex = savedInstanceState.getInt(ARGS_VALUE_ARRAY_INDEX);
            double value = this.mQuantityValues[this.mValueArrayIndex];
            this.mQuantity = new Quantity(value, LengthUnit.CM);

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
            MeasurementDAO mDao = new WaistMeasurementDAO(getActivity());
            mDao.open();
            Measurement latestMeasurement = mDao.getLatest();
            mDao.close();

            // if latest measurement found set this as default
            if (latestMeasurement != null)
                this.mQuantity = latestMeasurement.getQuantity();
            // no measurement registered, use average waist for given sex as
            // default
            else
                this.mQuantity = getAverageWaist();

            this.mSeekBar.setSelectedStepForValue(this.mQuantity.showInUnits(LengthUnit.CM));
        }
    }

    private Quantity getAverageWaist() {
        // get the sex of current user (male or female) from shared preferences
        String male = getResources().getString(R.string.sex_male);
        String sex = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(
                SettingsActivity.PREFERENCE_ACCOUNT_SEX_KEY, male);

        // get average weight for given sex
        if (sex.equals(male)) // male
            return StaticPreferences.AVERAGE_MALE_WAIST;
        else
            // female
            return StaticPreferences.AVERAGE_FEMALE_WAIST;
    }

    private boolean save() {
        double value = this.mSeekBar.getSelectedValue();
        Measurement measurement = new Measurement(new Quantity(value, LengthUnit.CM), this.mDate);

        MeasurementDAO mDAO = new WaistMeasurementDAO(getActivity());
        mDAO.open();
        mDAO.create(measurement);
        mDAO.close();

        this.mListener.onWaistMeasurementCreated(measurement);
        return true;
    }
}

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
import android.widget.NumberPicker;
import android.widget.NumberPicker.Formatter;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
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

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class NewWeightMeasurementOLD extends DialogFragment implements OnClickListener,
        OnDateSetListener, DialogInterface.OnClickListener, OnSeekBarChangeListener {
    private Button mDateButton; // the measurement date UI controller

    // shared preference
    private SharedPreferences mSharedPreferences;
    private String mMetricUnits;
    private String mImperialUnits;
    private String mSystemOfMeasurement;

    // decimal format used within this class
    private static String DECIMAL_FORMAT_PATTERN = "#0.00";
    private static DecimalFormat DECIMAL_FORMAT;

    // bundle arguments
    private static final String ARGS_DATE = "date";
    private static final String ARGS_VALUE = "value";

    // values of the measurement date and value
    private DateTime mDate;
    private double mValue;
    private Quantity mQuantity;

    // Seek bar
    private SeekBar mSeekBar;
    private TextView mSeekBarValueText;
    private double mSeekBarMinValue;
    private double mSeekBarMaxValue;
    // used to set min and max values according to current quantity value
    private static final int SEEK_BAR_INTERVAL_ADULTS = 2;
    private static final double SEEK_BAR_INTERVAL_BABIES = 0.2;

    // callback listener to inform when new weight measurement is saved
    private OnWeightMeasurementCreatedListener mListener;

    private static final String TAG_DATE_PICKER = "datePicker";

    private String[] mSeekBarValues = new String[3901];
    private NumberPicker valuePicker;

    public static NewWeightMeasurementOLD newInstance() {
        return new NewWeightMeasurementOLD();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Instantiate the decimal format used within this class
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
        DECIMAL_FORMAT = (DecimalFormat) nf;
        DECIMAL_FORMAT.applyPattern(DECIMAL_FORMAT_PATTERN);

        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View v = factory.inflate(R.layout.layout_dialog_new_measurement, null);

        // Shared preferences
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mMetricUnits = getActivity().getResources().getString(R.string.metric_units);
        mImperialUnits = getActivity().getResources().getString(R.string.imperial_units);
        mSystemOfMeasurement = mSharedPreferences.getString(
                SettingsActivity.PREFERENCE_METRIC_SYSTEM_KEY, mMetricUnits);

        // Instantiate the measurement (date and quantity)
        initializeMeasurement(savedInstanceState);

        // Initialize the UI controller for measurement date
        mDateButton = (Button) v.findViewById(R.id.date_button);
        mDateButton.setOnClickListener(this);
        mDateButton.setText(DateUtils.formatToMediumFormatExtended(mDate));

        // Setup SeekBar
        mSeekBar = (SeekBar) v.findViewById(R.id.seek_bar);
        mSeekBar.setOnSeekBarChangeListener(this);
        mSeekBarValueText = (TextView) v.findViewById(R.id.current_value);
        updateSeekBar();

        generateQuantityValues();
        valuePicker = (NumberPicker) v.findViewById(R.id.number_picker);
        valuePicker.setMaxValue(3900);
        valuePicker.setMinValue(0);
        valuePicker.setFormatter(new WeightQuantityValueNumberFormatter());
        valuePicker.setOnLongPressUpdateInterval(-10);
 
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_alert_dialog_new_weight);
        builder.setIconAttribute(R.drawable.add);
        builder.setView(v);
        builder.setPositiveButton(R.string.alert_dialog_ok, this);
        builder.setNegativeButton(R.string.alert_dialog_cancel, this);
        return builder.create();
    }

    private void generateQuantityValues() {
        int arrayIndex = 0;
        double arrayValue = 0;
        Quantity q;

        while (arrayValue < 10) {
            q = new Quantity(arrayValue, WeightUnit.KG);
            mSeekBarValues[arrayIndex] = arrayValue + "";
            // mSeekBarValues2.add(arrayIndex, Math.round(arrayValue * 100.0) /
            // 100.0);
            arrayValue += 0.01;
            arrayIndex++;
        }

        q = new Quantity(arrayValue, WeightUnit.KG);
        mSeekBarValues[arrayIndex] = arrayValue + "";
        // mSeekBarValues[arrayIndex] = Double.toString(Math.round(arrayValue *
        // 100.0) / 100.0);
        // mSeekBarValues2.add(arrayIndex, Math.round(arrayValue * 100.0) /
        // 100.0);

        while (arrayValue < 300) {
            q = new Quantity(arrayValue, WeightUnit.KG);
            mSeekBarValues[arrayIndex] = arrayValue + "";
            // mSeekBarValues[arrayIndex] =
            // Double.toString(Math.round(arrayValue * 100.0) / 100.0);
            // mSeekBarValues2.add(arrayIndex, Math.round(arrayValue * 100.0) /
            // 100.0);
            arrayValue += 0.1;
            arrayIndex++;
        }
    }

    private void updateSeekBar() {
        int valueInKg = (int) mQuantity.showInUnits(WeightUnit.KG);
        double interval;

        if (valueInKg < 10)
            interval = SEEK_BAR_INTERVAL_BABIES;
        else
            interval = SEEK_BAR_INTERVAL_ADULTS;

        BigDecimal minValue = new BigDecimal(String.valueOf(mQuantity.getValue() - (interval * 1)))
                .setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal maxValue = new BigDecimal(String.valueOf(mQuantity.getValue() + (interval * 1)))
                .setScale(2, BigDecimal.ROUND_HALF_UP);

        // We don't want the minimum value in seek bar to be negative
        if (minValue.doubleValue() > 0)
            mSeekBarMinValue = minValue.doubleValue();
        else
            mSeekBarMinValue = 0;

        mSeekBarMaxValue = maxValue.doubleValue();

        // int seekBarRangeMin = (int) mQuantity.getValue() - (SEEK_BAR_INTERVAL
        // * 10);
        // int seekBarRangeMax = (int) mQuantity.getValue() + (SEEK_BAR_INTERVAL
        // * 10);
        // mSeekBar.setMax(seekBarRangeMax - seekBarRangeMin);
        // mSeekBar.setProgress((int) mQuantity.getValue() - seekBarRangeMin);

        mSeekBar.setMax(40);
        mSeekBar.setProgress(20);

        setValueText();
    }

    public void initializeMeasurement(Bundle savedInstanceState) {
        // the fragment is restored
        if (savedInstanceState != null) {
            mDate = (DateTime) savedInstanceState.getSerializable(ARGS_DATE);
            mValue = savedInstanceState.getDouble(ARGS_VALUE);
            mQuantity = new Quantity(mValue, WeightUnit.KG);

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
        Measurement measurement = new Measurement(mQuantity, mDate);

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
        outState.putSerializable(ARGS_VALUE, mQuantity.getValue());
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

    private int value = 20;
    private int previousPogressValue = 20;

    @Override
    public void onProgressChanged(SeekBar seek, int progressValue, boolean fromTouch) {
        if (!fromTouch)
            return;

        if (progressValue < previousPogressValue)
            value--;
        else if (progressValue > previousPogressValue)
            value++;

        previousPogressValue = progressValue;
        mSeekBarValueText.setText("" + value);
        /*
         * if (progressValue == 0) { mSeekBar.setProgress(1); } else if
         * (progressValue == 40) { mSeekBar.setProgress(39); }
         */
    }

    // @Override
    public void onProgressChanged2(SeekBar seek, int value, boolean fromTouch) {
        // If the change is not triggered by the user touch ignore the change.
        // This is necessery because updateSeekBar do set the max value and
        // progress
        if (!fromTouch)
            return;

        int valueInKg = (int) mQuantity.showInUnits(WeightUnit.KG);
        double progress;

        if (valueInKg < 10)
            progress = value / 100.00; // increment by 0.01 kg when quantity
                                       // value < 10
        else if (valueInKg > 10)
            progress = value / 10.00; // increment by 0.1 kg when quantity value
                                      // > 10
        else // when quantity value == 10 we need to check wether value is
             // increased or decreased in seek bar
        {
            progress = value / 100.00;
        }

        double newValue = progress + mSeekBarMinValue;
        BigDecimal roundedValue = new BigDecimal(String.valueOf(newValue)).setScale(2,
                BigDecimal.ROUND_HALF_UP);

        // We dont want to allow seek bar going below 0 for weight
        if (roundedValue.longValue() < 0)
            return;

        // Update current value
        mQuantity = new Quantity(roundedValue.doubleValue(), mQuantity.getUnit());

        // Update value text edit with current value
        setValueText();

        if (mQuantity.getValue() == mSeekBarMinValue) {
            updateSeekBar();
            mSeekBar.setProgress((mSeekBar.getMax() - mSeekBar.getMax()) + 1);
        } else if (mQuantity.getValue() == mSeekBarMaxValue) {
            updateSeekBar();
            mSeekBar.setProgress(mSeekBar.getMax() - 1);
        }
    }

    public void setValueText() {
        if (mSystemOfMeasurement.equals(mMetricUnits))
            mSeekBarValueText.setText(QuantityStringFormat.formatWeightToMetric(mQuantity));
        else if (mSystemOfMeasurement.equals(mImperialUnits))
            mSeekBarValueText.setText(QuantityStringFormat.formatWeightToImperial(mQuantity));
    }

    public String getFormattedValue(Quantity q) {
        if (mSystemOfMeasurement.equals(mMetricUnits))
            return QuantityStringFormat.formatWeightToMetric(q);
        else
            return QuantityStringFormat.formatWeightToImperial(q);
    }

    @Override
    public void onStartTrackingTouch(SeekBar arg0) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar arg0) {
        // If user reached min or max limit of seek bar, extend the limits
        // if(mQuantity.getValue() == mSeekBarMinValue || mQuantity.getValue()
        // == mSeekBarMaxValue)
        // updateSeekBar();
    }

    class WeightQuantityValueNumberFormatter implements Formatter {
        public WeightQuantityValueNumberFormatter() {
        }

        @Override
        public String format(int value) {
            Quantity q = new Quantity(Double.parseDouble(mSeekBarValues[value]), WeightUnit.KG);

            if (mSystemOfMeasurement.equals(mMetricUnits))
                return QuantityStringFormat.formatWeightToMetric(q);
            else
                return QuantityStringFormat.formatWeightToImperial(q);
        }
    }

}

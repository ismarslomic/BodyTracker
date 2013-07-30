
package no.slomic.body.measurements.fragments;

import no.slomic.body.measurements.R;
import no.slomic.body.measurements.entities.Quantity;
import no.slomic.body.measurements.utils.DateUtils;
import no.slomic.body.measurements.views.CircularSeekBar;

import org.joda.time.DateTime;

import android.app.DatePickerDialog.OnDateSetListener;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

public class NewMeasurement extends DialogFragment implements View.OnClickListener,
        OnDateSetListener {

    protected Button mDateButton;
    protected static final String ARGS_DATE = "date";
    protected static final String ARGS_VALUE_ARRAY_INDEX = "value_index";
    protected DateTime mDate;
    protected Quantity mQuantity;
    protected int mValueArrayIndex;

    protected static final String TAG_DATE_PICKER = "datePicker";
    protected double[] mQuantityValues;
    protected CircularSeekBar mSeekBar;

    protected void initializeSeekBarLayout(View v)
    {
        // Initialize the circular seek bar widget
        this.mSeekBar = (CircularSeekBar) v.findViewById(R.id.circularSeekBar);
        this.mSeekBar.setEmptyCircleColor(getResources().getColor(
                android.R.color.secondary_text_light));
        this.mSeekBar.setSelectedCircleColor(getResources().getColor(
                android.R.color.holo_blue_light));
        this.mSeekBar.setSeekBarThumbsColor(Color.BLACK);
        this.mSeekBar.setButtonPushedColor(Color.LTGRAY);
        this.mSeekBar.setTextColor(getResources().getColor(android.R.color.primary_text_light));
        this.mSeekBar.setEmptyCircleColor(getResources().getColor(android.R.color.darker_gray));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ARGS_DATE, this.mDate);
        outState.putSerializable(ARGS_VALUE_ARRAY_INDEX, this.mSeekBar.getSelectedStep());
    }

    @Override
    public void onClick(View v) {
        if (v == this.mDateButton) {
            DatePickerFragment datePickerDialog = DatePickerFragment.newInstance(this, this.mDate);
            datePickerDialog.show(getFragmentManager(), TAG_DATE_PICKER);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        monthOfYear++; // DatePickerFragment months goes from 0-11 while Joda
                       // DateTime 1-12
        this.mDate = new DateTime(year, monthOfYear, dayOfMonth, 0, 0);
        this.mDateButton
                .setText(DateUtils.formatToMediumFormatExtended(this.mDate, getResources()));
    }

    public NewMeasurement() {
        super();
    }
}

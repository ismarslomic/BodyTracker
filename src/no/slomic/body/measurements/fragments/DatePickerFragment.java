// Restrukturert: OK

package no.slomic.body.measurements.fragments;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import org.joda.time.DateTime;

public class DatePickerFragment extends DialogFragment {
    private static final String ARGS_YEAR = "year";
    private static final String ARGS_MONTH = "month";
    private static final String ARGS_DAY = "day";
    private static final String ARGS_INITIAL_DATE = "initialDate";
    public int mYear, mMonth, mDay;
    public static OnDateSetListener mListener;

    public static DatePickerFragment newInstance(OnDateSetListener listener, DateTime initialDate) {
        DatePickerFragment dpf = new DatePickerFragment();
        dpf.setListener(listener);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARGS_INITIAL_DATE, initialDate);
        dpf.setArguments(bundle);
        return dpf;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            this.mYear = savedInstanceState.getInt(ARGS_YEAR);
            this.mMonth = savedInstanceState.getInt(ARGS_MONTH);
            this.mDay = savedInstanceState.getInt(ARGS_DAY);
        } else {
            Bundle arguments = getArguments();
            DateTime initialDate = (DateTime) arguments.getSerializable(ARGS_INITIAL_DATE);
            this.mYear = initialDate.getYear();
            this.mMonth = initialDate.getMonthOfYear() - 1; // DatePickerFragment
                                                            // months goes from
                                                            // 0-11 while Joda
                                                            // DateTime 1-12
            this.mDay = initialDate.getDayOfMonth();
        }

        return new DatePickerDialog(getActivity(), mListener, this.mYear, this.mMonth, this.mDay);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARGS_YEAR, this.mYear);
        outState.putInt(ARGS_MONTH, this.mMonth);
        outState.putInt(ARGS_DAY, this.mDay);
    }

    public void setListener(OnDateSetListener listener) {
        mListener = listener;
    }
}

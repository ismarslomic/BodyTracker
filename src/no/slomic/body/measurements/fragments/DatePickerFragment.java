package no.slomic.body.measurements.fragments;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class DatePickerFragment extends DialogFragment {
	public int year, month, day;
	public OnDateSetListener listener;
	
	public DatePickerFragment()
	{
		
	}
	
	public DatePickerFragment(OnDateSetListener listener, Calendar initialDate)
	{
		year = initialDate.get(Calendar.YEAR);
        month = initialDate.get(Calendar.MONTH);
        day = initialDate.get(Calendar.DAY_OF_MONTH);
        this.listener = listener;
	}

	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) 
	{
        return new DatePickerDialog(getActivity(), listener, year, month, day);
	}
}

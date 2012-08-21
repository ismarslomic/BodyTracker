
package no.slomic.body.measurements.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import no.slomic.body.measurements.R;
import no.slomic.body.measurements.entities.LengthUnit;
import no.slomic.body.measurements.entities.Measurement;
import no.slomic.body.measurements.entities.Quantity;
import no.slomic.body.measurements.fragments.QuantityLengthPicker.OnQuantitySetListener;
import no.slomic.body.measurements.storage.HeightMeasurementDAO;
import no.slomic.body.measurements.utils.DateUtils;

import java.util.Calendar;

public class NewHeightMeasurement extends DialogFragment implements OnClickListener,
        OnDateSetListener, OnQuantitySetListener {
    private Button dateButton, valueButton;
    private Calendar date;
    private double value;
    private Quantity quantity;
    private OnMeasurementSetListener listener;

    public NewHeightMeasurement(OnMeasurementSetListener listener) {
        this.listener = listener;
    }

    public static NewHeightMeasurement newInstance(OnMeasurementSetListener listener) {
        NewHeightMeasurement f = new NewHeightMeasurement(listener);
        Bundle b = new Bundle();
        b.putSerializable("date", Calendar.getInstance());
        f.setArguments(b);
        return f;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("date", date);
        outState.putSerializable("value", quantity.getValue());
    }

    // TODO: når skjermen snues horisontalt kalles denne metoden på nytt, noe
    // som nullstiller eksisterende valg i vinduet
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View v = factory.inflate(R.layout.layout_dialog_new_measurement, null);

        dateButton = (Button) v.findViewById(R.id.date_button);
        dateButton.setOnClickListener(this);

        valueButton = (Button) v.findViewById(R.id.value_button);
        valueButton.setOnClickListener(this);

        if (savedInstanceState != null) {
            date = (Calendar) savedInstanceState.getSerializable("date");
            value = savedInstanceState.getDouble("value");
            quantity = new Quantity(value, LengthUnit.m);
        } else {
            date = Calendar.getInstance();
            getInitialQuantity();
        }

        updateButtonText();

        // TODO: fast tekst under bør flyttes ut av klassen
        // TODO: omstrukturer koden under slik det er gjord i
        // QuantityWeightPicker
        return new AlertDialog.Builder(getActivity())
                .setIconAttribute(R.drawable.add)
                .setTitle("Add height measurement")
                .setView(v)
                .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        retrieveAndSaveDataFromViews();
                        Toast.makeText(getActivity(), "Measurement added", Toast.LENGTH_SHORT)
                                .show();
                    }
                })
                .setNegativeButton(R.string.alert_dialog_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                /* User clicked cancel so do some stuff */
                            }
                        }).create();

    }

    // TODO: vurder å rename metoden under og flytte deler av onCreateDialog
    // over hit
    public void getInitialQuantity() {
        HeightMeasurementDAO mDao = new HeightMeasurementDAO(getActivity());
        mDao.open();
        Measurement latestMeasurement = mDao.getLatest();
        mDao.close();

        if (latestMeasurement != null) {
            quantity = latestMeasurement.getQuantity();
        } else {
            // TODO: fast verdi 40 under bør flyttes ut fra denne klassen
            quantity = new Quantity(0.040, LengthUnit.m);
        }
    }

    private void retrieveAndSaveDataFromViews() {
        Measurement measurement = new Measurement(quantity, date);

        HeightMeasurementDAO mDAO = new HeightMeasurementDAO(getActivity());
        mDAO.open();
        mDAO.create(measurement);
        mDAO.close();

        listener.onMeasurementSet(measurement);
    }

    @Override
    public void onClick(View v) {
        if (v == dateButton) {
            DatePickerFragment datePickerDialog = new DatePickerFragment(this, date);
            datePickerDialog.show(getFragmentManager(), "datePicker");
        } else {
            QuantityLengthPicker quantityPickerDialog = new QuantityLengthPicker(this, quantity);
            quantityPickerDialog.show(getFragmentManager(), "quantityLengthPicker");
        }

    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        date.set(year, monthOfYear, dayOfMonth);
        updateButtonText();
    }

    @Override
    public void onQuantitySet(Quantity quantity) {
        this.quantity = quantity;
        updateButtonText();
    }

    private void updateButtonText() {
        dateButton.setText(DateUtils.formatToMediumFormatExtended(date));
        valueButton.setText(quantity.toString());
    }

    public interface OnMeasurementSetListener {
        public abstract void onMeasurementSet(Measurement measurement);
    }

}

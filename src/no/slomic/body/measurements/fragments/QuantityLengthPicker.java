
package no.slomic.body.measurements.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.NumberPicker.Formatter;

import no.slomic.body.measurements.R;
import no.slomic.body.measurements.entities.LengthUnit;
import no.slomic.body.measurements.entities.Quantity;
import no.slomic.body.measurements.entities.Unit;

// TODO: denne klassen er en duplikat av QuantityWeightPicker
public class QuantityLengthPicker extends DialogFragment implements DialogInterface.OnClickListener {

    private OnQuantitySetListener listener;
    private NumberPicker meterPicker, centimeterPicker;
    private Quantity quantity;

    public QuantityLengthPicker() {

    }

    public QuantityLengthPicker(OnQuantitySetListener listener, Quantity initialQuantity) {
        this.listener = listener;
        this.quantity = initialQuantity;
    }

    // TODO: når skjermen snues horisontalt kalles denne metoden på nytt, noe
    // som fører til nullpointexception da tom construktør kalles
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View v = factory.inflate(R.layout.layout_dialog_quantity_picker, null);
        meterPicker = (NumberPicker) v.findViewById(R.id.whole_numbers_picker);
        centimeterPicker = (NumberPicker) v.findViewById(R.id.fractional_numbers_picker);

        meterPicker.setMaxValue(2);
        meterPicker.setMinValue(0);
        meterPicker.setFormatter(new WholeNumberFormatter(quantity.getUnit()));
        meterPicker.setWrapSelectorWheel(false);

        centimeterPicker.setMaxValue(99);
        centimeterPicker.setMinValue(0);
        centimeterPicker.setFormatter(new FractionalNumberFormatter(quantity.getUnit()));
        centimeterPicker.setWrapSelectorWheel(false);
        centimeterPicker.setOnLongPressUpdateInterval(20);

        int meters = (int) quantity.convert(LengthUnit.M).getValue();
        int centimeters = (int) quantity.subtract(new Quantity(meters, LengthUnit.M),
                LengthUnit.REF_UNIT).getValue();

        meterPicker.setValue(meters);
        centimeterPicker.setValue(centimeters);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // TODO: fast tekst under bør flyttes ut av klassen
        builder.setTitle("Set quantity");
        builder.setView(v);
        builder.setPositiveButton(R.string.alert_dialog_ok, this);
        builder.setNegativeButton(R.string.alert_dialog_cancel, this);
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                int wholeNumbers = meterPicker.getValue();
                int fractionalNumbers = centimeterPicker.getValue();

                Quantity quantity = new Quantity(wholeNumbers, LengthUnit.M);
                Quantity centimeters = new Quantity(fractionalNumbers, LengthUnit.CM);
                quantity = quantity.add(centimeters, LengthUnit.REF_UNIT);

                double value = quantity.getValue();
                listener.onQuantitySet(quantity);
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                // Cancel button pressed
                break;
            default:
                // Some strange button pressed
                break;
        }
    }

    // TODO: denne må oppdateres til å ta hensyn til amerikansk units
    class WholeNumberFormatter implements Formatter {
        private Unit unit;

        public WholeNumberFormatter(Unit unit) {
            this.unit = unit;
        }

        @Override
        public String format(int value) {
            return value + " " + LengthUnit.M.getSymbol().toString();
        }
    }

    class FractionalNumberFormatter implements Formatter {
        private Unit unit;

        public FractionalNumberFormatter(Unit unit) {
            this.unit = unit;
        }

        @Override
        public String format(int value) {
            Unit refUnit = unit.getSystemUnit();
            return value + " " + refUnit.getSymbol().toString();
        }
    }

    public interface OnQuantitySetListener {
        public abstract void onQuantitySet(Quantity quantity);
    }

}

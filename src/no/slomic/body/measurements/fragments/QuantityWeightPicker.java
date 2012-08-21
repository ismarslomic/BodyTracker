
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
import no.slomic.body.measurements.entities.Quantity;
import no.slomic.body.measurements.entities.Unit;
import no.slomic.body.measurements.utils.DecimalUtils;

public class QuantityWeightPicker extends DialogFragment implements DialogInterface.OnClickListener {

    private OnQuantitySetListener listener;
    private NumberPicker wholeNumbersPicker, fractionalNumbersPicker;
    private Quantity quantity;

    public QuantityWeightPicker() {

    }

    public QuantityWeightPicker(OnQuantitySetListener listener, Quantity initialQuantity) {
        this.listener = listener;
        this.quantity = initialQuantity;
    }

    // TODO: når skjermen snues horisontalt kalles denne metoden på nytt, noe
    // som fører til nullpointexception da tom construktør kalles
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View v = factory.inflate(R.layout.layout_dialog_quantity_picker, null);
        wholeNumbersPicker = (NumberPicker) v.findViewById(R.id.whole_numbers_picker);
        fractionalNumbersPicker = (NumberPicker) v.findViewById(R.id.fractional_numbers_picker);

        wholeNumbersPicker.setMaxValue(500);
        wholeNumbersPicker.setMinValue(0);
        wholeNumbersPicker.setFormatter(new WholeNumberFormatter(quantity.getUnit()));
        wholeNumbersPicker.setWrapSelectorWheel(false);

        fractionalNumbersPicker.setMaxValue(999);
        fractionalNumbersPicker.setMinValue(0);
        fractionalNumbersPicker.setFormatter(new FractionalNumberFormatter(quantity.getUnit()));
        fractionalNumbersPicker.setWrapSelectorWheel(false);
        fractionalNumbersPicker.setOnLongPressUpdateInterval(20);

        wholeNumbersPicker.setValue(DecimalUtils.getWholePartOfValue(quantity.getValue()));
        fractionalNumbersPicker
                .setValue(DecimalUtils.getFractionalPartOfValue(quantity.getValue()));

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
                int wholeNumbers = wholeNumbersPicker.getValue();
                int fractionalNumbers = fractionalNumbersPicker.getValue();
                double value = DecimalUtils.getValue(wholeNumbers, fractionalNumbers);
                quantity = new Quantity(value, quantity.getUnit());
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

    class WholeNumberFormatter implements Formatter {
        private Unit unit;

        public WholeNumberFormatter(Unit unit) {
            this.unit = unit;
        }

        @Override
        public String format(int value) {
            return value + " " + unit.getSymbol().toString();
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

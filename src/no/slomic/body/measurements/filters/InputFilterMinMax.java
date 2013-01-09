// Restrukturert: ok

package no.slomic.body.measurements.filters;

import android.text.InputFilter;
import android.text.Spanned;

public class InputFilterMinMax implements InputFilter {

    private double mMin, mMax;

    public InputFilterMinMax(double min, double max) {
        this.mMin = min;
        this.mMax = max;
    }

    public InputFilterMinMax(String min, String max) {
        this.mMin = Double.parseDouble(min);
        this.mMax = Double.parseDouble(max);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart,
            int dend) {
        try {
            double input = Double.parseDouble(dest.toString() + source.toString());
            if (isInRange(this.mMin, this.mMax, input))
                return null;
        } catch (NumberFormatException nfe) {
        }
        return "";
    }

    private boolean isInRange(double a, double b, double c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}

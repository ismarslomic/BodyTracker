// Restrukturert: ok

package no.slomic.body.measurements.holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import no.slomic.body.measurements.R;

public class MeasurementHolder {
    private View mRow;
    private TextView mMeasurementValue = null, mMeasurementDate = null, mDiffValue = null;
    private ImageView mDiffIcon = null;

    public MeasurementHolder(View row) {
        this.mRow = row;
    }

    public TextView getMeasurementValue() {
        if (this.mMeasurementValue == null) {
            this.mMeasurementValue = (TextView) this.mRow.findViewById(R.id.measurement_value);
        }
        return this.mMeasurementValue;
    }

    public TextView getMeasurementDate() {
        if (this.mMeasurementDate == null) {
            this.mMeasurementDate = (TextView) this.mRow.findViewById(R.id.measurement_date);
        }
        return this.mMeasurementDate;
    }

    public TextView getDiffValue() {
        if (this.mDiffValue == null) {
            this.mDiffValue = (TextView) this.mRow.findViewById(R.id.diff_value);
        }
        return this.mDiffValue;
    }

    public ImageView getDiffIcon() {
        if (this.mDiffIcon == null) {
            this.mDiffIcon = (ImageView) this.mRow.findViewById(R.id.diff_icon);
        }
        return this.mDiffIcon;
    }
}

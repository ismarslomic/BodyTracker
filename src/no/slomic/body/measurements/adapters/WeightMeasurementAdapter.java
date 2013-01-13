// Restrukturert: ok

package no.slomic.body.measurements.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import no.slomic.body.measurements.entities.Measurement;
import no.slomic.body.measurements.entities.Quantity;
import no.slomic.body.measurements.holders.MeasurementHolder;
import no.slomic.body.measurements.utils.DateUtils;
import no.slomic.body.measurements.utils.QuantityStringFormat;

import java.util.List;

public class WeightMeasurementAdapter extends MeasurementAdapter {
    private Context mContext;
    private int mLayoutResourceId;

    /**
     * @param Context
     * @param LayoutResourceId
     * @param measurements
     */
    public WeightMeasurementAdapter(Context context, int layoutResourceId,
            List<Measurement> measurements) {
        super(context, layoutResourceId, measurements);
        this.mLayoutResourceId = layoutResourceId;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        MeasurementHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) this.mContext).getLayoutInflater();
            row = inflater.inflate(this.mLayoutResourceId, parent, false);

            holder = new MeasurementHolder(row);
            row.setTag(holder);
        } else {
            holder = (MeasurementHolder) row.getTag();
        }

        Measurement measurement = this.mMeasurements.get(position);
        bindDataToViews(holder, measurement);

        return row;
    }

    public void bindDataToViews(MeasurementHolder holder, Measurement measurement) {
        // Set measurement date
        String date = DateUtils.formatToMediumFormatExtended(measurement.getDate(), getContext()
                .getResources());
        holder.getMeasurementDate().setText(date);

        // Set measurement value in preferred system of measurement
        String formattedValue = QuantityStringFormat.formatQuantityValue(measurement.getQuantity(),
                mSharedPreferences, getContext().getResources());

        holder.getMeasurementValue().setText(formattedValue);

        // Set diff value between this and previous measurement
        if (measurement.getPrevious() != null) {
            Quantity diff = measurement.getQuantity().subtract(
                    measurement.getPrevious().getQuantity(),
                    measurement.getQuantity().getUnit().getSystemUnit());

            String formattedDiffValue = QuantityStringFormat.formatQuantityValue(diff,
                    mSharedPreferences, getContext().getResources());
            holder.getDiffValue().setText(formattedDiffValue);

            // Set diff relational sign/icon
            if (diff.getValue() < 0)
                holder.getDiffIcon().setImageBitmap(this.mDownIcon);
            else if (diff.getValue() == 0)
                holder.getDiffIcon().setImageBitmap(this.mEqualIcon);
            else
                holder.getDiffIcon().setImageBitmap(this.mUpIcon);
        } else {
            holder.getDiffValue().setText("");
            holder.getDiffIcon().setImageDrawable(null);
        }
    }
}

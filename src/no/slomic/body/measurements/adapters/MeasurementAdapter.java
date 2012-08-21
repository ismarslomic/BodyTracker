
package no.slomic.body.measurements.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import no.slomic.body.measurements.R;
import no.slomic.body.measurements.entities.Measurement;
import no.slomic.body.measurements.entities.Quantity;
import no.slomic.body.measurements.holders.MeasurementHolder;
import no.slomic.body.measurements.utils.DateUtils;

import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

public class MeasurementAdapter extends ArrayAdapter<Measurement> {
    private Context context;
    private int layoutResourceId;
    public List<Measurement> measurements = null;
    public TreeSet<Measurement> measurementSet = new TreeSet<Measurement>();
    private Bitmap upIcon, downIcon, equalIcon;

    public MeasurementAdapter(Context context, int layoutResourceId, List<Measurement> measurements) {
        super(context, layoutResourceId, measurements);
        this.measurements = measurements;
        this.measurementSet.addAll(measurements);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        upIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.up);
        downIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.down);
        equalIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.equal);

        sortAndSetPrevious();

        setNotifyOnChange(true);
    }

    private void sortAndSetPrevious() {
        measurements.clear();

        Iterator<Measurement> it = measurementSet.descendingIterator();
        Measurement previous = null;

        while (it.hasNext()) {
            Measurement current = it.next();
            current.setPrevious(previous);
            previous = current;
        }

        measurements.addAll(measurementSet);
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        MeasurementHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new MeasurementHolder(row);
            row.setTag(holder);
        } else {
            holder = (MeasurementHolder) row.getTag();
        }

        Measurement measurement = measurements.get(position);
        bindDataToViews(holder, measurement);

        return row;
    }

    public void bindDataToViews(MeasurementHolder holder, Measurement measurement) {
        // Set measurement date
        String date = DateUtils.formatToMediumFormatExtended(measurement.getDate());
        holder.getMeasurementDate().setText(date);

        // Set measured value
        holder.getMeasurementValue().setText(measurement.getQuantity().toString());

        // Set diff value between this and previous measurement
        if (measurement.getPrevious() != null) {
            Quantity diff = measurement.getQuantity().subtract(
                    measurement.getPrevious().getQuantity(),
                    measurement.getQuantity().getUnit().getSystemUnit());
            holder.getDiffValue().setText(diff.toString());

            // Set diff relational sign
            if (diff.getValue() < 0)
                holder.getDiffIcon().setImageBitmap(downIcon);
            else if (diff.getValue() == 0)
                holder.getDiffIcon().setImageBitmap(equalIcon);
            else
                holder.getDiffIcon().setImageBitmap(upIcon);
        } else {
            holder.getDiffValue().setText("");
            holder.getDiffIcon().setImageDrawable(null);
        }
    }

    @Override
    public void add(Measurement measurement) {
        measurementSet.add(measurement);
        sortAndSetPrevious();
    }

    @Override
    public void remove(Measurement measurement) {
        measurementSet.remove(measurement);
        sortAndSetPrevious();
    }

    public void removeAll(List<Measurement> measurementList) {
        measurementSet.removeAll(measurementList);
        sortAndSetPrevious();
    }
}

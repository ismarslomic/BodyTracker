
package no.slomic.body.measurements.entities;

import android.app.Activity;

import no.slomic.body.measurements.storage.HeightMeasurementDAO;
import no.slomic.body.measurements.storage.WeightMeasurementDAO;

import java.util.TreeSet;

// TODO: her er det en del duplisering. Mulig metodene bør flyttes til DAO-klassene. Se på den overordnede arkitekturen
public class MeasurementStatistics {

    /**
     * @param activity
     * @return null - if no measurements registered, 0 if only one measurement,
     *         the diff value otherwise
     */
    public static Quantity lastWeekHeight(Activity activity) {
        HeightMeasurementDAO heightMeasurementDAO = new HeightMeasurementDAO(activity);
        heightMeasurementDAO.open();
        TreeSet<Measurement> measurements = heightMeasurementDAO.getAllLastWeek();
        heightMeasurementDAO.close();

        if (measurements == null || measurements.size() == 0)
            return null;

        Measurement latest = measurements.first();
        Measurement oldest = measurements.last();

        // No measurements last weeks
        if (latest == null)
            return new Quantity(0.000, LengthUnit.REF_UNIT);
        // Only one measurement last week
        else if (oldest == null || latest.equals(oldest))
            return new Quantity(0.000, LengthUnit.REF_UNIT);
        // At least two measurements last week
        else {
            Quantity diff = latest.getQuantity().subtract(oldest.getQuantity(),
                    latest.getQuantity().getUnit().getSystemUnit());
            return diff;
        }
    }

    /**
     * @param activity
     * @return null - if no measurements registered, 0 if only one measurement,
     *         the diff value otherwise
     */
    public static Quantity lastWeekWeight(Activity activity) {
        WeightMeasurementDAO weightMeasurementDAO = new WeightMeasurementDAO(activity);
        weightMeasurementDAO.open();
        TreeSet<Measurement> measurements = weightMeasurementDAO.getAllLastWeek();
        weightMeasurementDAO.close();

        if (measurements == null || measurements.size() == 0)
            return null;

        Measurement latest = measurements.first();
        Measurement oldest = measurements.last();

        // No measurements last weeks
        if (latest == null)
            return new Quantity(0.000, WeightUnit.KG);
        // Only one measurement last week
        else if (oldest == null || latest.equals(oldest))
            return new Quantity(0.000, WeightUnit.KG);
        // At least two measurements last week
        else {
            Quantity diff = latest.getQuantity().subtract(oldest.getQuantity(),
                    latest.getQuantity().getUnit().getSystemUnit());
            return diff;
        }
    }
}

// Restrukturert: ok

package no.slomic.body.measurements.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import no.slomic.body.measurements.entities.Measurement;
import no.slomic.body.measurements.entities.Quantity;
import no.slomic.body.measurements.entities.Unit;
import no.slomic.body.measurements.entities.WeightUnit;
import no.slomic.body.measurements.utils.DateUtils;

import org.joda.time.DateTime;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class WeightMeasurementDAO {
    // Database fields
    private SQLiteDatabase mDatabase;
    private SQLiteHelper mDbHelper;
    private String[] mAllColumns = {
            WeightMeasurementTable.COLUMN_MEASUREMENT_DATE,
            WeightMeasurementTable.COLUMN_QUANTITY_VALUE,
            WeightMeasurementTable.COLUMN_UNIT_SYMBOL, WeightMeasurementTable.COLUMN_CREATED_DATE
    };
    private static final String LOG_TAG = "WeightMeasurementDAO";
    private static final boolean DEBUG = true;

    public WeightMeasurementDAO(Context context) {
        this.mDbHelper = new SQLiteHelper(context);
    }

    public void open() throws SQLException {
        this.mDatabase = this.mDbHelper.getWritableDatabase();
    }

    public void close() {
        this.mDbHelper.close();
    }

    public Measurement create(Measurement newMeasurement) {
        long l = newMeasurement.getDate().getMillis();
        ContentValues values = new ContentValues();
        values.put(WeightMeasurementTable.COLUMN_QUANTITY_VALUE, newMeasurement.getQuantity()
                .getValue());
        values.put(WeightMeasurementTable.COLUMN_MEASUREMENT_DATE, l);
        values.put(WeightMeasurementTable.COLUMN_UNIT_SYMBOL, newMeasurement.getQuantity()
                .getUnit().getSymbol());
        values.put(WeightMeasurementTable.COLUMN_CREATED_DATE, DateTime.now().getMillis());

        this.mDatabase.insert(WeightMeasurementTable.TABLE_NAME, null, values);
        Cursor cursor = this.mDatabase.query(WeightMeasurementTable.TABLE_NAME, this.mAllColumns,
                WeightMeasurementTable.COLUMN_MEASUREMENT_DATE + " = '"
                        + newMeasurement.getDate().getMillis() + "' ", null, null, null, null);
        cursor.moveToFirst();

        newMeasurement = cursorToMeasurement(cursor);
        cursor.close();
        if (DEBUG)
            Log.d(LOG_TAG, "Created new Weight Measurement");

        return newMeasurement;
    }

    public void delete(Measurement measurement) {
        this.mDatabase.delete(WeightMeasurementTable.TABLE_NAME,
                WeightMeasurementTable.COLUMN_MEASUREMENT_DATE + " = "
                        + measurement.getDate().getMillis() + " ", null);
        if (DEBUG)
            Log.d(LOG_TAG, "Deleted Weight Measurement " + measurement);
    }

    public void deleteAll(List<Measurement> measurements) {
        // TODO: denne kan sikkert optimaliseres i én spørring
        for (Measurement measurement : measurements) {
            delete(measurement);
        }
    }

    public List<Measurement> getAll() {
        List<Measurement> measurements = new ArrayList<Measurement>();

        Cursor cursor = this.mDatabase.query(WeightMeasurementTable.TABLE_NAME, this.mAllColumns,
                null, null, null, null, WeightMeasurementTable.COLUMN_MEASUREMENT_DATE + " ASC");

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Measurement m = cursorToMeasurement(cursor);
            measurements.add(m);
            cursor.moveToNext();
        }
        cursor.close();
        if (DEBUG)
            Log.d(LOG_TAG, "Getting all Weight Measurements. List size: " + measurements.size());
        return measurements;
    }

    public void exportAll(File exportFile) throws IOException {
        final String[] header = new String[] {
                "MeasurementDate", "QuantityValue", "UnitSymbol", "CreatedDate"
        };
        Cursor cursor = this.mDatabase.query(WeightMeasurementTable.TABLE_NAME, this.mAllColumns,
                null, null, null, null, WeightMeasurementTable.COLUMN_MEASUREMENT_DATE + " ASC");

        ICsvMapWriter mapWriter = null;
        int count = 0;
        try {
            mapWriter = new CsvMapWriter(new FileWriter(exportFile),
                    CsvPreference.STANDARD_PREFERENCE);

            final CellProcessor[] processors = getProcessors();

            // write the header
            mapWriter.writeHeader(header);

            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                Map<String, Object> measurement = new HashMap<String, Object>();
                DateTime measurementDate = new DateTime(cursor.getLong(0));
                DateTime createdDate = new DateTime(cursor.getLong(3));
                measurement.put(header[0], DateUtils.formatToShortFormat(measurementDate));
                measurement.put(header[1], cursor.getDouble(1));
                measurement.put(header[2], cursor.getString(2));
                measurement.put(header[3], DateUtils.formatToLongFormat(createdDate));

                mapWriter.write(measurement, header, processors);
                count++;
                cursor.moveToNext();
            }

            cursor.close();
        } finally {
            if (mapWriter != null) {
                mapWriter.close();
            }
        }
        if (DEBUG)
            Log.d(WeightMeasurementDAO.class.getName(), "Exported " + count
                    + " weight measurements.");

    }

    private static CellProcessor[] getProcessors() {

        final CellProcessor[] processors = new CellProcessor[] {
                new NotNull(), // MeasurementDate (not null)
                new NotNull(), // QuantityValue (not null)
                new NotNull(), // UnitSymbol (not null)
                new NotNull(), // CreatedDate (not null)
        };

        return processors;
    }

    public TreeSet<Measurement> getAllLastWeek() {
        DateTime today = DateTime.now();
        DateTime sevenDaysAgo = today.minusDays(8);

        String selection = WeightMeasurementTable.COLUMN_MEASUREMENT_DATE + " >= "
                + sevenDaysAgo.getMillis() + " AND "
                + WeightMeasurementTable.COLUMN_MEASUREMENT_DATE + " <= " + today.getMillis() + " ";

        if (DEBUG)
            Log.d("DAO", "selection = " + selection);

        TreeSet<Measurement> measurements = new TreeSet<Measurement>();

        Cursor cursor = this.mDatabase.query(WeightMeasurementTable.TABLE_NAME, this.mAllColumns,
                selection, null, null, null, WeightMeasurementTable.COLUMN_MEASUREMENT_DATE
                        + " ASC");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Measurement m = cursorToMeasurement(cursor);
            measurements.add(m);
            cursor.moveToNext();
        }
        cursor.close();

        if (DEBUG)
            Log.d(LOG_TAG, "Getting all Weight Measurements for the last week. List size: "
                    + measurements.size());
        return measurements;
    }

    public Quantity lastWeekStatistics() {
        TreeSet<Measurement> measurements = getAllLastWeek();

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

    /**
     * @return the latest measurement of weight or null if none registered
     */
    public Measurement getLatest() {
        Cursor cursor = this.mDatabase.query(WeightMeasurementTable.TABLE_NAME, this.mAllColumns,
                null, null, null, null, WeightMeasurementTable.COLUMN_MEASUREMENT_DATE
                        + " desc LIMIT 1");

        if (DEBUG)
            Log.d(LOG_TAG, "Found latest items: " + cursor.getCount());

        Measurement measurement = null;
        if (cursor.moveToFirst()) {
            measurement = cursorToMeasurement(cursor);
            if (DEBUG)
                Log.d(LOG_TAG, "Retrieved latest Weight Measurements. [Date:"
                        + measurement.getDate().toString() + ", quantity: "
                        + measurement.getQuantity().toString());
        } else if (DEBUG)
            Log.d(LOG_TAG, "No latest weight measurement found.");

        cursor.close();

        return measurement;
    }

    private Measurement cursorToMeasurement(Cursor cursor) {
        // Measurement date
        long milliseconds = cursor.getLong(0);
        DateTime measurementDate = new DateTime(milliseconds);

        // Quantity value
        double value = cursor.getDouble(1);

        // Unit symbol
        String unitSymbol = cursor.getString(2);
        Unit unit = getUnit(unitSymbol);
        Quantity quantity = new Quantity(value, unit);

        return new Measurement(quantity, measurementDate);
    }

    public WeightUnit getUnit(String unitSymbol) {
        if (unitSymbol.equals(WeightUnit.LB.getSymbol()))
            return WeightUnit.LB;
        else if (unitSymbol.equals(WeightUnit.KG.getSymbol()))
            return WeightUnit.KG;
        else
            return WeightUnit.G;
    }
}

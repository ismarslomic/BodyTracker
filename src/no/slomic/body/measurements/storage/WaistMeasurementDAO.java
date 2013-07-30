// Restrukturert: ok

package no.slomic.body.measurements.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import no.slomic.body.measurements.entities.LengthUnit;
import no.slomic.body.measurements.entities.Measurement;
import no.slomic.body.measurements.entities.Quantity;
import no.slomic.body.measurements.entities.Unit;
import no.slomic.body.measurements.utils.DateUtils;

import org.joda.time.DateTime;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class WaistMeasurementDAO implements MeasurementDAO {
    // Database fields
    private SQLiteDatabase mDatabase;
    private SQLiteHelper mDbHelper;
    private String[] mAllColumns = {
            WaistMeasurementTable.COLUMN_MEASUREMENT_DATE,
            WaistMeasurementTable.COLUMN_QUANTITY_VALUE,
            WaistMeasurementTable.COLUMN_UNIT_SYMBOL, WaistMeasurementTable.COLUMN_CREATED_DATE
    };
    private static final String LOG_TAG = "WaistMeasurementDAO";
    private static final boolean DEBUG = true;

    public WaistMeasurementDAO(Context context) {
        this.mDbHelper = new SQLiteHelper(context);
    }

    /*
     * (non-Javadoc)
     * @see no.slomic.body.measurements.storage.MeasurementDAO#open()
     */
    @Override
    public void open() throws SQLException {
        this.mDatabase = this.mDbHelper.getWritableDatabase();
    }

    /*
     * (non-Javadoc)
     * @see no.slomic.body.measurements.storage.MeasurementDAO#close()
     */
    @Override
    public void close() {
        this.mDbHelper.close();
    }

    /*
     * (non-Javadoc)
     * @see
     * no.slomic.body.measurements.storage.MeasurementDAO#create(no.slomic.body
     * .measurements.entities.Measurement)
     */
    @Override
    public Measurement create(Measurement newMeasurement) {
        long l = newMeasurement.getDate().getMillis();
        ContentValues values = new ContentValues();
        values.put(WaistMeasurementTable.COLUMN_QUANTITY_VALUE, newMeasurement.getQuantity()
                .getValue());
        values.put(WaistMeasurementTable.COLUMN_MEASUREMENT_DATE, l);
        values.put(WaistMeasurementTable.COLUMN_UNIT_SYMBOL, newMeasurement.getQuantity()
                .getUnit().getSymbol());
        values.put(WaistMeasurementTable.COLUMN_CREATED_DATE, DateTime.now().getMillis());

        this.mDatabase.insert(WaistMeasurementTable.TABLE_NAME, null, values);
        Cursor cursor = this.mDatabase.query(WaistMeasurementTable.TABLE_NAME, this.mAllColumns,
                WaistMeasurementTable.COLUMN_MEASUREMENT_DATE + " = '"
                        + newMeasurement.getDate().getMillis() + "' ", null, null, null, null);
        cursor.moveToFirst();

        newMeasurement = cursorToMeasurement(cursor);
        cursor.close();
        if (DEBUG)
            Log.d(LOG_TAG, "Created new Waist Measurement");

        return newMeasurement;
    }

    /*
     * (non-Javadoc)
     * @see
     * no.slomic.body.measurements.storage.MeasurementDAO#delete(no.slomic.body
     * .measurements.entities.Measurement)
     */
    @Override
    public void delete(Measurement measurement) {
        this.mDatabase.delete(WaistMeasurementTable.TABLE_NAME,
                WaistMeasurementTable.COLUMN_MEASUREMENT_DATE + " = "
                        + measurement.getDate().getMillis() + " ", null);
        if (DEBUG)
            Log.d(LOG_TAG, "Deleted Waist Measurement " + measurement);
    }

    /*
     * (non-Javadoc)
     * @see
     * no.slomic.body.measurements.storage.MeasurementDAO#deleteAll(java.util
     * .List)
     */
    @Override
    public void deleteAll(List<Measurement> measurements) {
        // TODO: denne kan sikkert optimaliseres i én spørring
        for (Measurement measurement : measurements) {
            delete(measurement);
        }
    }

    /*
     * (non-Javadoc)
     * @see no.slomic.body.measurements.storage.MeasurementDAO#getAll()
     */
    @Override
    public List<Measurement> getAll() {
        List<Measurement> measurements = new ArrayList<Measurement>();

        Cursor cursor = this.mDatabase.query(WaistMeasurementTable.TABLE_NAME, this.mAllColumns,
                null, null, null, null, WaistMeasurementTable.COLUMN_MEASUREMENT_DATE + " ASC");

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Measurement m = cursorToMeasurement(cursor);
            measurements.add(m);
            cursor.moveToNext();
        }
        cursor.close();
        if (DEBUG)
            Log.d(LOG_TAG, "Getting all Waist Measurements. List size: " + measurements.size());
        return measurements;
    }

    /*
     * (non-Javadoc)
     * @see
     * no.slomic.body.measurements.storage.MeasurementDAO#exportAll(java.io.
     * File)
     */
    @Override
    public void exportAll(File exportFile) throws IOException {
        final String[] header = new String[] {
                "MeasurementDate", "QuantityValue", "UnitSymbol", "CreatedDate"
        };
        Cursor cursor = this.mDatabase.query(WaistMeasurementTable.TABLE_NAME, this.mAllColumns,
                null, null, null, null, WaistMeasurementTable.COLUMN_MEASUREMENT_DATE + " ASC");

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
            Log.d(WaistMeasurementDAO.class.getName(), "Exported " + count
                    + " waist measurements.");

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

    /*
     * (non-Javadoc)
     * @see no.slomic.body.measurements.storage.MeasurementDAO#getAllLastWeek()
     */
    @Override
    public TreeSet<Measurement> getAllLastWeek() {
        DateTime today = DateTime.now();
        DateTime sevenDaysAgo = today.minusDays(8);

        String selection = WaistMeasurementTable.COLUMN_MEASUREMENT_DATE + " >= "
                + sevenDaysAgo.getMillis() + " AND "
                + WaistMeasurementTable.COLUMN_MEASUREMENT_DATE + " <= " + today.getMillis() + " ";

        if (DEBUG)
            Log.d("DAO", "selection = " + selection);

        TreeSet<Measurement> measurements = new TreeSet<Measurement>();

        Cursor cursor = this.mDatabase.query(WaistMeasurementTable.TABLE_NAME, this.mAllColumns,
                selection, null, null, null, WaistMeasurementTable.COLUMN_MEASUREMENT_DATE
                        + " ASC");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Measurement m = cursorToMeasurement(cursor);
            measurements.add(m);
            cursor.moveToNext();
        }
        cursor.close();

        if (DEBUG)
            Log.d(LOG_TAG, "Getting all Waist Measurements for the last week. List size: "
                    + measurements.size());
        return measurements;
    }

    /*
     * (non-Javadoc)
     * @see
     * no.slomic.body.measurements.storage.MeasurementDAO#lastWeekStatistics()
     */
    @Override
    public Quantity lastWeekStatistics() {
        TreeSet<Measurement> measurements = getAllLastWeek();

        if (measurements == null || measurements.size() == 0)
            return null;

        Measurement latest = measurements.first();
        Measurement oldest = measurements.last();

        // No measurements last weeks
        if (latest == null)
            return new Quantity(0.000, LengthUnit.CM);
        // Only one measurement last week
        else if (oldest == null || latest.equals(oldest))
            return new Quantity(0.000, LengthUnit.CM);
        // At least two measurements last week
        else {
            Quantity diff = latest.getQuantity().subtract(oldest.getQuantity(),
                    latest.getQuantity().getUnit().getSystemUnit());
            return diff;
        }
    }

    /*
     * (non-Javadoc)
     * @see no.slomic.body.measurements.storage.MeasurementDAO#getLatest()
     */
    @Override
    public Measurement getLatest() {
        Cursor cursor = this.mDatabase.query(WaistMeasurementTable.TABLE_NAME, this.mAllColumns,
                null, null, null, null, WaistMeasurementTable.COLUMN_MEASUREMENT_DATE
                        + " desc LIMIT 1");

        if (DEBUG)
            Log.d(LOG_TAG, "Found latest items: " + cursor.getCount());

        Measurement measurement = null;
        if (cursor.moveToFirst()) {
            measurement = cursorToMeasurement(cursor);
            if (DEBUG)
                Log.d(LOG_TAG, "Retrieved latest Waist Measurements. [Date:"
                        + measurement.getDate().toString() + ", quantity: "
                        + measurement.getQuantity().toString());
        } else if (DEBUG)
            Log.d(LOG_TAG, "No latest waist measurement found.");

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

    public LengthUnit getUnit(String unitSymbol) {
        if (unitSymbol.equals(LengthUnit.M.getSymbol()))
            return LengthUnit.M;
        else if (unitSymbol.equals(LengthUnit.MM.getSymbol()))
            return LengthUnit.MM;
        else if (unitSymbol.equals(LengthUnit.IN.getSymbol()))
            return LengthUnit.IN;
        else if (unitSymbol.equals(LengthUnit.FT.getSymbol()))
            return LengthUnit.FT;
        else
            return LengthUnit.CM;
    }
}
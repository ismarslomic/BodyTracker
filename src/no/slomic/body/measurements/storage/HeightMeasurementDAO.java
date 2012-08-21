
package no.slomic.body.measurements.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import no.slomic.body.measurements.entities.LengthUnit;
import no.slomic.body.measurements.entities.Measurement;
import no.slomic.body.measurements.entities.Quantity;
import no.slomic.body.measurements.entities.Unit;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TreeSet;

public class HeightMeasurementDAO {
    // Database fields
    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;
    private String[] allColumns = {
            HeightMeasurementTable.COLUMN_MEASUREMENT_DATE,
            HeightMeasurementTable.COLUMN_QUANTITY_VALUE,
            HeightMeasurementTable.COLUMN_UNIT_SYMBOL, HeightMeasurementTable.COLUMN_CREATED_DATE
    };

    public HeightMeasurementDAO(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Measurement create(Measurement newMeasurement) {
        long l = newMeasurement.getDate().getTimeInMillis();
        ContentValues values = new ContentValues();
        values.put(HeightMeasurementTable.COLUMN_QUANTITY_VALUE, newMeasurement.getQuantity()
                .getValue());
        values.put(HeightMeasurementTable.COLUMN_MEASUREMENT_DATE, l);
        values.put(HeightMeasurementTable.COLUMN_UNIT_SYMBOL, newMeasurement.getQuantity()
                .getUnit().getSymbol());
        values.put(HeightMeasurementTable.COLUMN_CREATED_DATE, Calendar.getInstance()
                .getTimeInMillis());

        database.insert(HeightMeasurementTable.TABLE_NAME, null, values);
        Cursor cursor = database
                .query(HeightMeasurementTable.TABLE_NAME, allColumns,
                        HeightMeasurementTable.COLUMN_MEASUREMENT_DATE + " = '"
                                + newMeasurement.getDate().getTimeInMillis() + "' ", null, null,
                        null, null);
        cursor.moveToFirst();

        newMeasurement = cursorToMeasurement(cursor);
        cursor.close();

        Log.d(SQLiteHelper.class.getName(), "Created new Height Measurement");

        return newMeasurement;
    }

    public void delete(Measurement measurement) {
        database.delete(HeightMeasurementTable.TABLE_NAME,
                HeightMeasurementTable.COLUMN_MEASUREMENT_DATE + " = "
                        + measurement.getDate().getTimeInMillis() + " ", null);

        Log.d(SQLiteHelper.class.getName(), "Deleted Height Measurement " + measurement);
    }

    public void deleteAll(List<Measurement> measurements) {
        // TODO: denne kan sikkert optimaliseres i én spørring
        for (Measurement measurement : measurements) {
            delete(measurement);
        }
    }

    public List<Measurement> getAll() {
        List<Measurement> measurements = new ArrayList<Measurement>();

        Cursor cursor = database.query(HeightMeasurementTable.TABLE_NAME, allColumns, null, null,
                null, null, HeightMeasurementTable.COLUMN_MEASUREMENT_DATE + " ASC");

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Measurement m = cursorToMeasurement(cursor);
            measurements.add(m);
            cursor.moveToNext();
        }
        cursor.close();

        Log.d(SQLiteHelper.class.getName(), "Getting all Height Measurements. List size: "
                + measurements.size());
        return measurements;
    }

    public TreeSet<Measurement> getAllLastWeek() {
        Calendar today = Calendar.getInstance();
        Calendar sevenDaysAgo = Calendar.getInstance();
        sevenDaysAgo.add(Calendar.DATE, -8);

        String selection = HeightMeasurementTable.COLUMN_MEASUREMENT_DATE + " >= "
                + sevenDaysAgo.getTimeInMillis() + " AND "
                + HeightMeasurementTable.COLUMN_MEASUREMENT_DATE + " <= " + today.getTimeInMillis()
                + " ";

        Log.d("DAO", "selection = " + selection);

        TreeSet<Measurement> measurements = new TreeSet<Measurement>();

        Cursor cursor = database.query(HeightMeasurementTable.TABLE_NAME, allColumns, selection,
                null, null, null, HeightMeasurementTable.COLUMN_MEASUREMENT_DATE + " ASC");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Measurement m = cursorToMeasurement(cursor);
            measurements.add(m);
            cursor.moveToNext();
        }
        cursor.close();

        Log.d(SQLiteHelper.class.getName(),
                "Getting all Height Measurements for the last week. List size: "
                        + measurements.size());
        return measurements;
    }

    /**
     * @return the latest measurement of Height or null if none registered
     */
    public Measurement getLatest() {
        Cursor cursor = database.query(HeightMeasurementTable.TABLE_NAME, allColumns, null, null,
                null, null, HeightMeasurementTable.COLUMN_MEASUREMENT_DATE + " desc LIMIT 1");

        Log.d(SQLiteHelper.class.getName(), "Found latest items: " + cursor.getCount());

        Measurement measurement = null;
        if (cursor.moveToFirst()) {
            measurement = cursorToMeasurement(cursor);

            Log.d(SQLiteHelper.class.getName(), "Retrieved latest Height Measurements. [Date:"
                    + measurement.getDate().toString() + ", quantity: "
                    + measurement.getQuantity().toString());
        } else
            Log.d(SQLiteHelper.class.getName(), "No latest Height measurement found.");

        cursor.close();

        return measurement;
    }

    private Measurement cursorToMeasurement(Cursor cursor) {
        // Measurement date
        long milliseconds = cursor.getLong(0);
        Calendar measurementDate = Calendar.getInstance();
        measurementDate.setTimeInMillis(milliseconds);

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
        else if (unitSymbol.equals(LengthUnit.CM.getSymbol()))
            return LengthUnit.CM;
        else
            return LengthUnit.IN;
    }
}

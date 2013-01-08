// Restrukturert: ok

package no.slomic.body.measurements.storage;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class WeightMeasurementTable {
    public static final String TABLE_NAME = "WeightMeasurement";
    public static final String COLUMN_MEASUREMENT_DATE = "MeasurementDate";
    public static final String COLUMN_QUANTITY_VALUE = "QuantityValue";
    public static final String COLUMN_UNIT_SYMBOL = "UnitSymbol";
    public static final String COLUMN_CREATED_DATE = "CreatedDate";
    private static final String LOG_TAG = "WeightMeasurementTable";
    private static final boolean DEBUG = true;

    private static final String CREATE_TABLE = "create table " + TABLE_NAME + " ("
            + COLUMN_MEASUREMENT_DATE + " integer primary key, " + COLUMN_QUANTITY_VALUE
            + " double, " + COLUMN_UNIT_SYMBOL + " string, " + COLUMN_CREATED_DATE + " integer);";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE);
        if (DEBUG)
            Log.d(LOG_TAG, "Creating new table " + TABLE_NAME);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}

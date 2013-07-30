// Restrukturert: ok

package no.slomic.body.measurements.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {

    // Database
    public static final String DATABASE_NAME = "measurements.db";
    public static final int DATABASE_VERSION = 2;

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase database) {
        WeightMeasurementTable.onCreate(database);
        WaistMeasurementTable.onCreate(database);
    }

    // Method is called during an upgrade of the database,
    // e.g. if you increase the database version
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        WeightMeasurementTable.onUpgrade(db, oldVersion, newVersion);
        WaistMeasurementTable.onUpgrade(db, oldVersion, newVersion);
    }
}

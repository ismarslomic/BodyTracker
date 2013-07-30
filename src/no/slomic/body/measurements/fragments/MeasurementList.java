
package no.slomic.body.measurements.fragments;

import java.io.File;
import java.io.IOException;

import no.slomic.body.measurements.R;
import no.slomic.body.measurements.adapters.MeasurementAdapter;
import no.slomic.body.measurements.entities.Measurement;
import no.slomic.body.measurements.entities.Quantity;
import no.slomic.body.measurements.storage.MeasurementDAO;
import no.slomic.body.measurements.storage.SQLiteHelper;
import no.slomic.body.measurements.utils.QuantityStringFormat;

import org.joda.time.DateTime;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

//TODO: bruk string.xml i stedet for faste tekster
public class MeasurementList extends ListFragment {

    protected MeasurementDAO dao;
    protected Activity activity;

    public MeasurementList() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_measurement_list, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.mainmenu, menu);
    }


    protected void exportData() {
        File dbFile = getActivity().getDatabasePath(SQLiteHelper.DATABASE_NAME);
        if (dbFile != null && dbFile.exists()) {
            Log.d(WeightMeasurementList.class.getName(), "exportData: Found the database file "
                    + dbFile);

            File exportDir = new File(Environment.getExternalStorageDirectory()
                    + "/body.measurement.export");
            if (!exportDir.exists()) {
                boolean createdExportDir = exportDir.mkdirs();
                Log.d(WeightMeasurementList.class.getName(), "exportData: The new directory "
                        + exportDir + " was created: " + createdExportDir);
            }

            String exportFileName = "Body Measurement_" + DateTime.now() + ".csv";
            File exportFile = new File(exportDir, exportFileName);
            try {
                boolean creteadExportFile = exportFile.createNewFile();
                Log.d(WeightMeasurementList.class.getName(), "exportData: The new file "
                        + exportFile + " was created: " + creteadExportFile);

            } catch (IOException e) {
                Log.e(WeightMeasurementList.class.getName(), e.getMessage());
            }

            this.dao.open();
            try {
                this.dao.exportAll(exportFile);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.e(WeightMeasurementList.class.getName(), e.getMessage());
            }
            this.dao.close();
        }

    }

    protected void showWeeklyStatistics() {
        String text;
        this.dao.open();
        Quantity lastWeek = this.dao.lastWeekStatistics();
        this.dao.close();

        String lastWeekString = "";
        lastWeekString = QuantityStringFormat.formatQuantityValue(lastWeek,
                PreferenceManager.getDefaultSharedPreferences(getActivity()), getResources());

        if (lastWeek == null)
            text = getResources().getString(R.string.toast_no_measurements_weekly_stats);
        else if (lastWeek.getValue() < 0)
            text = getResources().getString(R.string.toast_reduced_measurements_weekly_stats,
                    lastWeekString);
        else if (lastWeek.getValue() == 0)
            text = getResources().getString(R.string.toast_equal_measurements_weekly_stats);
        else
            text = getResources().getString(R.string.toast_increased_measurements_weekly_stats,
                    lastWeekString);

        Toast toast = Toast.makeText(this.activity, text, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

    public void addMeasurement(Measurement measurement) {
        MeasurementAdapter listAdapter = (MeasurementAdapter) getListAdapter();
        listAdapter.add(measurement);
    }
}
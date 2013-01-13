
package no.slomic.body.measurements.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import no.slomic.body.measurements.R;
import no.slomic.body.measurements.activities.SettingsActivity;
import no.slomic.body.measurements.adapters.MeasurementAdapter;
import no.slomic.body.measurements.adapters.WeightMeasurementAdapter;
import no.slomic.body.measurements.entities.Measurement;
import no.slomic.body.measurements.entities.Quantity;
import no.slomic.body.measurements.storage.SQLiteHelper;
import no.slomic.body.measurements.storage.WeightMeasurementDAO;
import no.slomic.body.measurements.utils.QuantityStringFormat;

import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

//TODO: hele denne klassen bør omstruktureres og avlastes. Spesielt klassene nederst
//TODO: legg til LoaderManager i denne klassen (http://developer.android.com/guide/components/loaders.html). Se LoaderThrottleSupport i View pager tab test
//TODO: bruk string.xml i stedet for faste tekster
public class WeightMeasurementList extends ListFragment {
    private WeightMeasurementDAO weightMeasurementDAO;
    private Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String tag = getTag();
        return inflater.inflate(R.layout.layout_measurement_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // TODO: denne skaper IllegalStateException: Cant be used with a custom
        // content view. Finn ut hvordan en slik tekst allikevel kan plasseres
        // et sted.
        // setEmptyText("No data. Add measurement by pressing add button.");
        setHasOptionsMenu(true);

        ListView lv = getListView();
        lv.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        lv.setMultiChoiceModeListener(new ModeCallback());

        this.activity = getActivity();
        this.weightMeasurementDAO = new WeightMeasurementDAO(this.activity);
        this.weightMeasurementDAO.open();

        List<Measurement> measurements = this.weightMeasurementDAO.getAll();

        this.weightMeasurementDAO.close();

        WeightMeasurementAdapter wma = new WeightMeasurementAdapter(this.activity,
                R.layout.layout_measurement_list_row, measurements);

        setListAdapter(wma);
    }

    private class ModeCallback implements ListView.MultiChoiceModeListener {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = WeightMeasurementList.this.activity.getMenuInflater();
            inflater.inflate(R.menu.list_select_menu, menu);
            mode.setTitle(getResources().getString(R.string.message_select_items_measurement_list));
            setSubtitle(mode);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.deleteMeasurement:

                    SparseBooleanArray selectedPos = getListView().getCheckedItemPositions();

                    MeasurementAdapter lAdapter = (MeasurementAdapter) getListAdapter();
                    List<Measurement> selectedMeasurements = new LinkedList<Measurement>();
                    for (int i = 0; i < lAdapter.getCount(); i++) {
                        if (selectedPos.get(i)) {
                            Measurement measurementToDelete = lAdapter.getItem(i);
                            selectedMeasurements.add(measurementToDelete);
                        }
                    }
                    lAdapter.removeAll(selectedMeasurements);
                    WeightMeasurementList.this.weightMeasurementDAO.open();
                    WeightMeasurementList.this.weightMeasurementDAO.deleteAll(selectedMeasurements);
                    WeightMeasurementList.this.weightMeasurementDAO.close();
                    mode.finish();
                    break;
                default:
                    Toast.makeText(WeightMeasurementList.this.activity,
                            "Clicked " + item.getTitle(), Toast.LENGTH_SHORT).show();
                    break;
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
        }

        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id,
                boolean checked) {
            setSubtitle(mode);
        }

        private void setSubtitle(ActionMode mode) {
            final int checkedCount = getListView().getCheckedItemCount();
            switch (checkedCount) {
                case 0:
                    mode.setSubtitle(null);
                    break;
                case 1:
                    mode.setSubtitle(getResources().getString(
                            R.string.message_one_item_selected_measurement_list));
                    break;
                default:
                    mode.setSubtitle(getResources().getString(
                            R.string.message_several_item_selected_measurement_list, checkedCount));
                    break;
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.mainmenu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.weekStat:
                showWeeklyStatistics();
                break;
            case R.id.addMeasurement:
                NewWeightMeasurement newMeasurementDialog = NewWeightMeasurement.newInstance();
                newMeasurementDialog.show(getFragmentManager(), "newWeightMeasurementDialog");
                break;
            case R.id.settings:
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.exportData:
                exportData();
                break;
            default:
                break;
        }

        return true;
    }

    /**
     * 
     */
    private void exportData() {
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

            this.weightMeasurementDAO.open();
            try {
                this.weightMeasurementDAO.exportAll(exportFile);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.e(WeightMeasurementList.class.getName(), e.getMessage());
            }
            this.weightMeasurementDAO.close();
        }

    }

    private void showWeeklyStatistics() {
        String text;
        this.weightMeasurementDAO.open();
        Quantity lastWeek = this.weightMeasurementDAO.lastWeekStatistics();
        this.weightMeasurementDAO.close();

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

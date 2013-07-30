
package no.slomic.body.measurements.fragments;

import java.util.List;

import no.slomic.body.measurements.R;
import no.slomic.body.measurements.activities.SettingsActivity;
import no.slomic.body.measurements.adapters.MeasurementAdapter;
import no.slomic.body.measurements.entities.Measurement;
import no.slomic.body.measurements.storage.WeightMeasurementDAO;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;

//TODO: legg til LoaderManager i denne klassen (http://developer.android.com/guide/components/loaders.html). Se LoaderThrottleSupport i View pager tab test
public class WeightMeasurementList extends MeasurementList {
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);

        this.activity = getActivity();
        this.dao = new WeightMeasurementDAO(this.activity);

        ListView lv = getListView();
        lv.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        lv.setMultiChoiceModeListener(new ModeCallback(this, dao));

        this.dao.open();

        List<Measurement> measurements = this.dao.getAll();

        this.dao.close();

        MeasurementAdapter wma = new MeasurementAdapter(this.activity,
                R.layout.layout_measurement_list_row, measurements);

        setListAdapter(wma);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.weekStat:
                showWeeklyStatistics();
                break;
            case R.id.addMeasurement:
                NewMeasurement newMeasurementDialog = NewWeightMeasurement.newInstance();
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
}

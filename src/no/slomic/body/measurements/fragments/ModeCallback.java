
package no.slomic.body.measurements.fragments;

import java.util.LinkedList;
import java.util.List;

import no.slomic.body.measurements.R;
import no.slomic.body.measurements.adapters.MeasurementAdapter;
import no.slomic.body.measurements.entities.Measurement;
import no.slomic.body.measurements.storage.MeasurementDAO;
import android.support.v4.app.ListFragment;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

public class ModeCallback implements ListView.MultiChoiceModeListener {
    private ListFragment listFragment;
    private MeasurementDAO dao;

    public ModeCallback(ListFragment listFragment, MeasurementDAO dao)
    {
        this.listFragment = listFragment;
        this.dao = dao;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deleteMeasurement:

                SparseBooleanArray selectedPos = listFragment.getListView()
                        .getCheckedItemPositions();

                MeasurementAdapter lAdapter = (MeasurementAdapter) listFragment.getListAdapter();
                List<Measurement> selectedMeasurements = new LinkedList<Measurement>();
                for (int i = 0; i < lAdapter.getCount(); i++) {
                    if (selectedPos.get(i)) {
                        Measurement measurementToDelete = lAdapter.getItem(i);
                        selectedMeasurements.add(measurementToDelete);
                    }
                }
                lAdapter.removeAll(selectedMeasurements);
                dao.open();
                dao.deleteAll(selectedMeasurements);
                dao.close();
                mode.finish();

                showConfirmMessage(listFragment.getResources().getQuantityString(
                        R.plurals.message_deleted_measurements, selectedMeasurements.size(),
                        selectedMeasurements.size()));
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = listFragment.getActivity().getMenuInflater();
        inflater.inflate(R.menu.list_select_menu, menu);
        mode.setTitle(listFragment.getResources().getString(
                R.string.message_select_items_measurement_list));
        setSubtitle(mode);
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return true;
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id,
            boolean checked) {
        setSubtitle(mode);
    }

    private void setSubtitle(ActionMode mode) {
        final int checkedCount = listFragment.getListView().getCheckedItemCount();
        switch (checkedCount) {
            case 0:
                mode.setSubtitle(null);
                break;
            case 1:
                mode.setSubtitle(listFragment.getResources().getString(
                        R.string.message_one_item_selected_measurement_list));
                break;
            default:
                mode.setSubtitle(listFragment.getResources().getString(
                        R.string.message_several_item_selected_measurement_list, checkedCount));
                break;
        }
    }

    private void showConfirmMessage(String message)
    {
        Toast toast = Toast.makeText(listFragment.getActivity(), message, Toast.LENGTH_SHORT);
        toast.show();
    }
}

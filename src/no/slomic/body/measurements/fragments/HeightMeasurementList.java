package no.slomic.body.measurements.fragments;

import java.util.LinkedList;
import java.util.List;

import no.slomic.body.measurements.R;
import no.slomic.body.measurements.adapters.MeasurementAdapter;
import no.slomic.body.measurements.entities.Measurement;
import no.slomic.body.measurements.entities.MeasurementStatistics;
import no.slomic.body.measurements.entities.Quantity;
import no.slomic.body.measurements.fragments.NewHeightMeasurement.OnMeasurementSetListener;
import no.slomic.body.measurements.storage.HeightMeasurementDAO;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class HeightMeasurementList extends ListFragment implements OnMeasurementSetListener
{
	private HeightMeasurementDAO heightMeasurementDAO;
	    
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return (LinearLayout) inflater.inflate(R.layout.layout_measurement_list,
				container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
    	
		ListView lv = getListView();
		lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		lv.setMultiChoiceModeListener(new ModeCallback());

		heightMeasurementDAO = new HeightMeasurementDAO(getActivity());
		heightMeasurementDAO.open();
		
		List<Measurement> measurements = heightMeasurementDAO.getAll();
		
		
		heightMeasurementDAO.close();
		
		MeasurementAdapter wma = new MeasurementAdapter(getActivity(),
				R.layout.layout_measurement_list_row, measurements);
		
		setListAdapter(wma);
		setHasOptionsMenu(true);
	}

	private class ModeCallback implements ListView.MultiChoiceModeListener {

		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = getActivity().getMenuInflater();
			inflater.inflate(R.menu.list_select_menu, menu);
			mode.setTitle("Select Items");
			setSubtitle(mode);
			return true;
		}

		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return true;
		}

		public boolean onActionItemClicked(ActionMode mode, MenuItem item) 
		{
			switch (item.getItemId()) 
			{
			case R.id.deleteMeasurement:
				
				SparseBooleanArray selectedPos = getListView()
		        .getCheckedItemPositions();

				MeasurementAdapter lAdapter = (MeasurementAdapter) getListAdapter();
				List<Measurement> selectedMeasurements = new LinkedList<Measurement>();
				for (int i = 0; i < lAdapter.getCount(); i++) 
				{
				    if (selectedPos.get(i)) 
				    {
				    	Measurement measurementToDelete = lAdapter.getItem(i);
				    	selectedMeasurements.add(measurementToDelete);
				    }
				}
		    	lAdapter.removeAll(selectedMeasurements);
				heightMeasurementDAO.open();
				heightMeasurementDAO.deleteAll(selectedMeasurements);
				heightMeasurementDAO.close();
				mode.finish();
				break;
			default:
				Toast.makeText(getActivity(), "Clicked " + item.getTitle(),
						Toast.LENGTH_SHORT).show();
				break;
			}
			return true;
		}

		public void onDestroyActionMode(ActionMode mode) {
		}

		public void onItemCheckedStateChanged(ActionMode mode, int position,
				long id, boolean checked) {
			setSubtitle(mode);
		}

		private void setSubtitle(ActionMode mode) {
			final int checkedCount = getListView().getCheckedItemCount();
			switch (checkedCount) {
			case 0:
				mode.setSubtitle(null);
				break;
			case 1:
				mode.setSubtitle("One item selected");
				break;
			default:
				mode.setSubtitle("" + checkedCount + " items selected");
				break;
			}
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) 
	{
		inflater.inflate(R.menu.mainmenu, menu);
	}

    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.weekStat:
			showWeeklyStatistics();
			break;
		case R.id.addMeasurement:
			NewHeightMeasurement newMeasurementDialog = NewHeightMeasurement.newInstance(this);
			newMeasurementDialog.show(getFragmentManager(), "newHeightMeasurementDialog");
			break;
		default:
			break;
		}

		return true;
	}
	
	private void showWeeklyStatistics()
	{/*
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View layout = inflater.inflate(R.layout.layout_toast_stat,
		                               (ViewGroup) getActivity().findViewById(R.id.toast_layout_root));
	 */
		
		String text;
		Quantity lastWeek = MeasurementStatistics.lastWeekHeight(getActivity());
		if( lastWeek == null )
			text = "No measurements registered";
		else if(lastWeek.getValue() < 0)
			text = "Last week you have reduced height with " + lastWeek.toString();
		else if(lastWeek.getValue() == 0)
			text = "Equal height as last week";
		else
			text = "Last week you increased height with " + lastWeek.toString();
		
		Toast toast = Toast.makeText(getActivity(), text, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		toast.show();
	}

	@Override
	public void onMeasurementSet(Measurement measurement) {
		MeasurementAdapter listAdapter = (MeasurementAdapter) getListAdapter();
		listAdapter.add(measurement);
	}

}

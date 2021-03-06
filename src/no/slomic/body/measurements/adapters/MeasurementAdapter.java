// Restrukturert: ok

package no.slomic.body.measurements.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ArrayAdapter;

import no.slomic.body.measurements.R;
import no.slomic.body.measurements.activities.SettingsActivity;
import no.slomic.body.measurements.entities.Measurement;

import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

public abstract class MeasurementAdapter extends ArrayAdapter<Measurement> implements
        OnSharedPreferenceChangeListener {
    public List<Measurement> mMeasurements = null;
    public TreeSet<Measurement> mMeasurementSet = new TreeSet<Measurement>();
    protected Bitmap mUpIcon, mDownIcon, mEqualIcon;
    protected SharedPreferences mSharedPreferences;
    protected String mMetricUnits;
    protected String mImperialUnits;
    protected String mSystemOfMeasurement;
    private static final String LOG_TAG = MeasurementAdapter.class.getName();
    private static final boolean DEBUG = true;

    public MeasurementAdapter(Context context, int layoutResourceId, List<Measurement> measurements) {
        super(context, layoutResourceId, measurements);

        // add measurement to the local list
        this.mMeasurements = measurements;
        this.mMeasurementSet.addAll(measurements);

        // init the icons
        this.mUpIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.up);
        this.mDownIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.down);
        this.mEqualIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.equal);

        // sort the measurement and set what was the previous measurement
        sortAndSetPrevious();

        // notify when the list of mMeasurements are changed
        setNotifyOnChange(true);

        // init the mSharedPreferences variable and listen to changes in the
        // shared preferences
        this.mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
        this.mMetricUnits = context.getResources().getString(R.string.metric_units);
        this.mImperialUnits = context.getResources().getString(R.string.imperial_units);
        this.mSystemOfMeasurement = this.mSharedPreferences.getString(
                SettingsActivity.PREFERENCE_METRIC_SYSTEM_KEY, this.mMetricUnits);
    }

    private void sortAndSetPrevious() {
        this.mMeasurements.clear();

        Iterator<Measurement> it = this.mMeasurementSet.descendingIterator();
        Measurement previous = null;

        while (it.hasNext()) {
            Measurement current = it.next();
            current.setPrevious(previous);
            previous = current;
        }

        this.mMeasurements.addAll(this.mMeasurementSet);
        this.notifyDataSetChanged();
    }

    @Override
    public void add(Measurement measurement) {
        this.mMeasurementSet.add(measurement);
        sortAndSetPrevious();
    }

    @Override
    public void remove(Measurement measurement) {
        this.mMeasurementSet.remove(measurement);
        sortAndSetPrevious();
    }

    public void removeAll(List<Measurement> measurementList) {
        this.mMeasurementSet.removeAll(measurementList);
        sortAndSetPrevious();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(SettingsActivity.PREFERENCE_METRIC_SYSTEM_KEY)) {
            if (DEBUG)
                Log.d(LOG_TAG, "Preference changed. Key: " + key);

            this.mSystemOfMeasurement = this.mSharedPreferences.getString(
                    SettingsActivity.PREFERENCE_METRIC_SYSTEM_KEY, this.mMetricUnits);
            this.notifyDataSetChanged();
        }
    }
}

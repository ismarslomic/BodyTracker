// Restrukturert: OK

package no.slomic.body.measurements.activities;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.TabHost;

import no.slomic.body.measurements.R;
import no.slomic.body.measurements.entities.Measurement;
import no.slomic.body.measurements.fragments.NewWeightMeasurement.OnWeightMeasurementCreatedListener;
import no.slomic.body.measurements.fragments.WeightMeasurementList;
import no.slomic.body.measurements.tabs.TabsAdapter;

/**
 * Main activity combines a TabHost with a ViewPager to implement a tab UI that
 * switches between tabs and also allows the user to perform horizontal flicks
 * to move between the tabs. This solution is based on the Android SDK Sample
 * project called Support4Demo. See FragmentTabsPager.java for more information.
 */
public class MainActivity extends FragmentActivity implements OnSharedPreferenceChangeListener,
        OnWeightMeasurementCreatedListener {
    private TabHost mTabHost;
    private ViewPager mViewPager;
    private TabsAdapter mTabsAdapter;
    private static final String LOG_TAG = MainActivity.class.getName();
    private static final boolean DEBUG = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_tabs_pager);
        this.mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        this.mTabHost.setup();

        this.mViewPager = (ViewPager) findViewById(R.id.pager);

        // Create our tab adapter
        this.mTabsAdapter = new TabsAdapter(this, this.mTabHost, this.mViewPager);

        addTabs();

        if (savedInstanceState != null) {
            // restore the last selected tab if we can
            this.mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        }
    }

    public void addTabs() {
        // Clear all existing tabs
        this.mTabsAdapter.clearAll();

        // Clear all existing fragments;
        if (DEBUG)
            Log.d(LOG_TAG, "Count of tabs: " + this.mTabsAdapter.getCount() + " after clearAll");

        // Get shared preferences to determine what tabs should be visible
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.registerOnSharedPreferenceChangeListener(this);

        Boolean activateHeightMeasurement = sharedPref.getBoolean(
                SettingsActivity.PREFERENCE_ACTIVATE_HEIGHT_MEASUREMENT, true);
        Boolean activateWeightMeasurement = sharedPref.getBoolean(
                SettingsActivity.PREFERENCE_ACTIVATE_WEIGHT_MEASUREMENT, true);

        // Add visible tabs to the tabs adapter
        if (activateWeightMeasurement)
            this.mTabsAdapter.addTab(
                    this.mTabHost.newTabSpec(getResources().getString(R.string.title_tab_weight))
                            .setIndicator(getResources().getString(R.string.title_tab_weight)), WeightMeasurementList.class,
                    null);

        /*
         * if (activateHeightMeasurement)
         * mTabsAdapter.addTab(mTabHost.newTabSpec
         * ("Height").setIndicator("Height"), HeightMeasurementList.class,
         * null);
         */
        if (DEBUG)
            Log.d(LOG_TAG, "Count of tabs: " + this.mTabsAdapter.getCount() + " after adding tabs");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tab", this.mTabHost.getCurrentTabTag());
    }

    /*
     * (non-Javadoc)
     * @see android.content.SharedPreferences.OnSharedPreferenceChangeListener#
     * onSharedPreferenceChanged(android.content.SharedPreferences,
     * java.lang.String)
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(SettingsActivity.PREFERENCE_ACTIVATE_HEIGHT_MEASUREMENT)
                || key.equals(SettingsActivity.PREFERENCE_ACTIVATE_WEIGHT_MEASUREMENT)) {
            if (DEBUG)
                Log.d(LOG_TAG, "Shared preference changed for key " + key);

            addTabs();
        }
    }

    @Override
    public void onWeightMeasurementCreated(Measurement measurement) {
        // Get the fragment of the current tab (Weight tab)
        int currentTabPosition = this.mTabHost.getCurrentTab();
        String tag = this.mTabsAdapter.getFragmentTag(currentTabPosition);
        Fragment f = getSupportFragmentManager().findFragmentByTag(tag);

        if (f != null && f instanceof WeightMeasurementList) {
            // Communicate the newly created measurement to the weight fragment
            WeightMeasurementList wml = (WeightMeasurementList) f;
            wml.addMeasurement(measurement);

            if (DEBUG)
                Log.d(LOG_TAG, "Found fragment at " + currentTabPosition + ": tag:" + f.getTag()
                        + ", content:" + f + " and calling addMeasurement method to update list.");
        } else {
            if (DEBUG)
                Log.e(LOG_TAG, "Could not find fragment at positon " + currentTabPosition
                        + " with tag" + tag
                        + ". Can't call the addMeassurement method to update list.");
        }

    }
}

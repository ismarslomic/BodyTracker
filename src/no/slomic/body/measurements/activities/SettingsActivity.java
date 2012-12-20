/**
 * 
 */

package no.slomic.body.measurements.activities;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import no.slomic.body.measurements.R;

/**
 * @author ismar.slomic
 */
public class SettingsActivity extends PreferenceActivity implements
        OnSharedPreferenceChangeListener {
    // Check /res/xml/preferences.xml file for this preference
    public static final String PREFERENCE_ACCOUNT_AGE_KEY = "account_age";
    public static final String PREFERENCE_ACCOUNT_NAME_KEY = "account_name";
    public static final String PREFERENCE_ACCOUNT_SEX_KEY = "account_sex";
    public static final String PREFERENCE_PERIOD_START_KEY = "period_start";
    public static final String PREFERENCE_PERIOD_END_KEY = "period_end";
    public static final String PREFERENCE_METRIC_SYSTEM_KEY = "metric_system";
    public static final String PREFERENCE_ACTIVATE_HEIGHT_MEASUREMENT = "activate_height_measurement";
    public static final String PREFERENCE_ACTIVATE_WEIGHT_MEASUREMENT = "activate_weight_measurement";
   
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        // Register for changes (for example only)
        SharedPreferences preferences = getPreferenceScreen().getSharedPreferences();
        preferences.registerOnSharedPreferenceChangeListener(this);

        // Set the summary of preferences to current value
        Preference namePreference = findPreference(PREFERENCE_ACCOUNT_NAME_KEY);
        namePreference.setSummary(preferences.getString(PREFERENCE_ACCOUNT_NAME_KEY, ""));
        
        Preference sexPreference = findPreference(PREFERENCE_ACCOUNT_SEX_KEY);
        sexPreference.setSummary(preferences.getString(PREFERENCE_ACCOUNT_SEX_KEY, ""));

        Preference metricSystemPreference = findPreference(PREFERENCE_METRIC_SYSTEM_KEY);
        metricSystemPreference.setSummary(preferences.getString(PREFERENCE_METRIC_SYSTEM_KEY, ""));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals(PREFERENCE_ACCOUNT_NAME_KEY)) {
            Preference preference = findPreference(key);
            preference.setSummary(sharedPreferences.getString(key, ""));
        }
        else if (key.equals(PREFERENCE_ACCOUNT_SEX_KEY)) {
            Preference preference = findPreference(PREFERENCE_ACCOUNT_SEX_KEY);
            preference.setSummary(sharedPreferences.getString(PREFERENCE_ACCOUNT_SEX_KEY, ""));
        }
        else if (key.equals(PREFERENCE_METRIC_SYSTEM_KEY)) {
            Preference preference = findPreference(PREFERENCE_METRIC_SYSTEM_KEY);
            preference.setSummary(sharedPreferences.getString(PREFERENCE_METRIC_SYSTEM_KEY, ""));
        }
    }

    @Override
    protected void onDestroy() {
        // Unregister from changes
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(
                this);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(
                this);
    }
}

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
    private static final String PREFERENCE_ACCOUNT_AGE_KEY = "account_age";
    private static final String PREFERENCE_ACCOUNT_NAME_KEY = "account_name";
    private static final String PREFERENCE_ACCOUNT_SEX_KEY = "account_sex";
    
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
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals(PREFERENCE_ACCOUNT_NAME_KEY)) {
            Preference namePreference = findPreference(key);
            namePreference.setSummary(sharedPreferences.getString(key, ""));
        }
        else if (key.equals(PREFERENCE_ACCOUNT_SEX_KEY)) {
            Preference sexPreference = findPreference(PREFERENCE_ACCOUNT_SEX_KEY);
            sexPreference.setSummary(sharedPreferences.getString(PREFERENCE_ACCOUNT_SEX_KEY, ""));
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

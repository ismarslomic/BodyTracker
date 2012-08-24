/**
 * 
 */

package no.slomic.body.measurements.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import no.slomic.body.measurements.R;

/**
 * @author ismar.slomic
 */
public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}

package com.example.yazan.earthquakesreports.activities;

import android.content.SharedPreferences;
import android.gesture.Prediction;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.yazan.earthquakesreports.R;

/**
 * Created by yazan on 2/8/17.
 */

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new EarthquakePreferenceFragment())
                .commit();
    }

    /**
     * Earthquake Preference Fragment
     */
    public static class EarthquakePreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            //get orderByPreference
            Preference orderByPreference = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummaryToValue(orderByPreference);

            Preference maxMagnitudePreference = findPreference(getString(R.string.settings_max_magnitude_key));
            bindPreferenceSummaryToValue(maxMagnitudePreference);
            //
            Preference minMagnitudePreference = findPreference(getString(R.string.settings_min_magnitude_key));
            bindPreferenceSummaryToValue(minMagnitudePreference);



        }

 /**
  * summary to be a description of the user's selection
  * */
        private void bindPreferenceSummaryToValue(Preference preference) {

            preference.setOnPreferenceChangeListener(this);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String preferenceValue = sharedPreferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceValue);


        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {

            String preferenceValue = newValue.toString();

            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;

                int prefIndex = listPreference.findIndexOfValue(preferenceValue);

                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    //set Summary for ListPreference
                    preference.setSummary(labels[prefIndex]);
                }
            } else {
                //set Summary for EditTextPreference
                preference.setSummary(preferenceValue);
            }
            return true;
        }
    }
}

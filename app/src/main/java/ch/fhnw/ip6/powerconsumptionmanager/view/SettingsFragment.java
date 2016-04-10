package ch.fhnw.ip6.powerconsumptionmanager.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.network.DataLoader;
import ch.fhnw.ip6.powerconsumptionmanager.network.DataLoaderCallback;
import ch.fhnw.ip6.powerconsumptionmanager.util.IPSettingDialog;
import ch.fhnw.ip6.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;

/**
 * Holds the different settings in a preference fragment and displays them accordingly.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener, DataLoaderCallback {
    private static final String TAG = "SettingsFragment";
    private SharedPreferences mSettings;
    private SettingsFragment mContext;

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        mSettings = getPreferenceScreen().getSharedPreferences();
        mContext = this;

        Preference syncPreference = findPreference("syncPlan");

        // Define click listener on preference to start synching process
        syncPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.toast_sync_started), Toast.LENGTH_SHORT).show();
                PowerConsumptionManagerAppContext context = (PowerConsumptionManagerAppContext) getActivity().getApplicationContext();
                DataLoader loader = new DataLoader(context, mContext);
                try {
                    loader.synchronizeChargePlan("http://" + context.getIPAdress() + ":" + getString(R.string.webservice_putChargePlan));
                } catch (ExecutionException | InterruptedException e) {
                    Log.e(TAG, "Exception while synchronizing charge plan.");
                    Toast.makeText(getActivity(), getString(R.string.toast_sync_ended_error_interrupted), Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });

        // Set present ip as summary of preference entry
        IPSettingDialog ipDialog = (IPSettingDialog) findPreference("IP");
        ipDialog.setSummary(mSettings.getString("IP", "192.168.0.1"));
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // Update summary of ip preference entry when ip changed
        Preference pref = findPreference(key);
        String newSummary = mSettings.getString("IP", "");
        pref.setSummary(newSummary);
    }

    /**** Return point from the sync request ****/
    @Override
    public void DataLoaderDidFinish() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), getString(R.string.toast_sync_ended_success), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void DataLoaderDidFail() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), getString(R.string.toast_sync_ended_error_loading), Toast.LENGTH_LONG).show();
            }
        });
    }
    /********/
}

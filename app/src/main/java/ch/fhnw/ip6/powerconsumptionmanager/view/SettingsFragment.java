package ch.fhnw.ip6.powerconsumptionmanager.view;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.activity.MainActivity;
import ch.fhnw.ip6.powerconsumptionmanager.network.DataLoader;
import ch.fhnw.ip6.powerconsumptionmanager.network.DataLoaderCallback;
import ch.fhnw.ip6.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;

/**
 * Holds the different settings in a preference fragment and displays them accordingly.
 */
public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener, DataLoaderCallback {
    private static final String TAG = "SettingsFragment";
    private SharedPreferences mSettings;
    private SettingsFragment mContext;
    private Preference mIPDialog;

    private EditText mIP1;
    private EditText mIP2;
    private EditText mIP3;
    private EditText mIP4;

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        mSettings = getPreferenceScreen().getSharedPreferences();
        mContext = this;

        // Setup ip dialog preference
        mIPDialog = findPreference("IP");
        mIPDialog.setSummary(mSettings.getString("IP", "192.168.0.1"));
        mIPDialog.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomDialogStyle);
                builder.setTitle("IP Address");

                LayoutInflater layoutInflater = getActivity().getLayoutInflater();
                View ipDialogView = layoutInflater.inflate(R.layout.dialog_ip_settings, null);
                builder.setView(ipDialogView);

                // Get the previously stored ip
                String ip = mSettings.getString("IP", "192.168.0.1");

                mIP1 = (EditText) ipDialogView.findViewById(R.id.editIP1);
                mIP2 = (EditText) ipDialogView.findViewById(R.id.editIP2);
                mIP3 = (EditText) ipDialogView.findViewById(R.id.editIP3);
                mIP4 = (EditText) ipDialogView.findViewById(R.id.editIP4);

                // Fill in the text boxes with the current values so the user can adapt them
                StringTokenizer token = new StringTokenizer(ip, ".");
                mIP1.setText(token.nextToken());
                mIP2.setText(token.nextToken());
                mIP3.setText(token.nextToken());
                mIP4.setText(token.nextToken());

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(isValidIPNumber(mIP1) && isValidIPNumber(mIP2) && isValidIPNumber(mIP3) && isValidIPNumber(mIP4)) {
                            // ... edit preference file and update the ip address
                            SharedPreferences.Editor editor = mSettings.edit();
                            String ip = mIP1.getText().toString() + "." +
                                        mIP2.getText().toString() + "." +
                                        mIP3.getText().toString() + "." +
                                        mIP4.getText().toString();
                            editor.putString("IP", ip);
                            editor.apply();

                            // Update application context
                            PowerConsumptionManagerAppContext context = (PowerConsumptionManagerAppContext) getContext().getApplicationContext();
                            context.setIPAdress(mSettings.getString("IP", "192.168.0.1"));

                            // Settings have changed
                            MainActivity.UPDATED = true;
                            Toast.makeText(getContext(), getContext().getString(R.string.toast_ip_updated), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), getContext().getString(R.string.toast_ip_invalid), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();
                return true;
            }
        });

        // Setup sync preference
        Preference syncPreference = findPreference("syncPlan");
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
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
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

    /**
     * Error detection for IP
     * @param ip The textfield where the content needs to be checked
     * @return true when ip number is valid, false otherwise
     */
    private boolean isValidIPNumber(EditText ip) {
        // IP field can't be empty
        if(ip.getText().length() <= 0) {
            return false;
        }

        // Entered number needs to be between 0 and 255
        int ipNumber = Integer.parseInt(ip.getText().toString());
        return !(ipNumber < 0 || ipNumber > 255);
    }
}

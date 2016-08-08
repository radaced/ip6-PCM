package ch.fhnw.ip6.powerconsumptionmanager.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.StringTokenizer;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.activity.MainActivity;
import ch.fhnw.ip6.powerconsumptionmanager.activity.SplashScreenActivity;
import ch.fhnw.ip6.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;

/**
 * Holds the different settings in a preference fragment and displays them accordingly.
 */
public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    private PowerConsumptionManagerAppContext mAppContext;

    private SharedPreferences mSharedPreferences;

    private SeekBar mUpdateInterval;
    private TextView mUpdateIntervalLabel;

    private NumberPicker mCostStatisticsPeriod;

    private EditText mIP1;
    private EditText mIP2;
    private EditText mIP3;
    private EditText mIP4;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        mAppContext = (PowerConsumptionManagerAppContext) getContext().getApplicationContext();
        mSharedPreferences = getPreferenceScreen().getSharedPreferences();

        // Setup google calendar checkbox preference
        final CheckBoxPreference googleCalendar = (CheckBoxPreference) findPreference("googleCalendar");
        setupGoogleCalendarPreference(googleCalendar);

        // Setup update automatically checkbox preference
        final CheckBoxPreference updateAutomatically = (CheckBoxPreference) findPreference("updateAutomatically");
        setupUpdateAutomaticallyPreference(updateAutomatically);

        // Setup update interval preference
        final Preference updateIntervalDialog = findPreference("updateInterval");
        setupUpdateIntervalDialog(updateIntervalDialog);

        // Setup cost statistics period preference
        final Preference costStatisticsPeriod = findPreference("costStatisticsPeriod");
        setupCostStatisticsPeriodDialog(costStatisticsPeriod);

        // Setup complete restart preference
        final Preference completeRestart = findPreference("completeRestart");
        setupCompleteRestartPreference(completeRestart);

        // Setup ip dialog preference
        final Preference ipDialog = findPreference("IP");
        setupIPDialog(ipDialog);
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
        // Update summaries of preferences when they are changed
        switch (key) {
            case "updateAutomatically":
                findPreference("updateInterval").setEnabled(mSharedPreferences.getBoolean("updateAutomatically", false));
                break;
            case "updateInterval":
                findPreference(key).setSummary(
                    getString(R.string.text_pref_update_interval_summary) +
                    " " +
                    mSharedPreferences.getInt("updateInterval", 10) +
                    " seconds"
                );
                break;
            case "costStatisticsPeriod":
                findPreference(key).setSummary(
                    getString(R.string.text_pref_cost_statistics_period_summary) +
                    " " +
                    mSharedPreferences.getInt("costStatisticsPeriod", 10) +
                    " " +
                    getString(R.string.text_pref_cost_statistics_period_summary)
                );
                break;
            case "IP":
                findPreference(key).setSummary(mSharedPreferences.getString("IP", ""));
                break;
            default:
                // Do nothing
                break;
        }
    }

    /**
     * Sets and displays the preference if the user wants to use his google calendar to manage the charge plan.
     * @param googleCalendarPreference The check box preference for this setting from the preferences.xml.
     */
    private void setupGoogleCalendarPreference(CheckBoxPreference googleCalendarPreference) {
        googleCalendarPreference.setChecked(mSharedPreferences.getBoolean("googleCalendar", false));
        googleCalendarPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object value) {
                if(value instanceof Boolean) {
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putBoolean("googleCalendar", (Boolean) value);
                    editor.apply();
                    mAppContext.setGoogleCalendar((Boolean) value);
                }

                return true;
            }
        });
    }

    /**
     * Sets and displays the preference if the user wants the app to update certain data (current data and consumption data)
     * automatically or not.
     * @param updateAutomaticallyPreference The check box preference for this setting from the preferences.xml.
     */
    private void setupUpdateAutomaticallyPreference(CheckBoxPreference updateAutomaticallyPreference) {
        updateAutomaticallyPreference.setChecked(mSharedPreferences.getBoolean("updateAutomatically", false));
        updateAutomaticallyPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object value) {
                if(value instanceof Boolean) {
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putBoolean("updateAutomatically", (Boolean) value);
                    editor.apply();
                    mAppContext.setUpdatingAutomatically((Boolean) value);
                }

                return true;
            }
        });
    }

    /**
     * Sets and displays a custom seek bar dialog for the user to specify the update interval.
     * @param updateIntervalDialog The preference for this setting from the preferences.xml.
     */
    private void setupUpdateIntervalDialog(Preference updateIntervalDialog) {
        // Set summary with actual preference data and if this preference is enabled (only when updating data automatically is wanted)
        updateIntervalDialog.setSummary(
            getString(R.string.text_pref_update_interval_summary) +
            " " +
            mSharedPreferences.getInt("updateInterval", 10) +
            " seconds"
        );
        updateIntervalDialog.setEnabled(mSharedPreferences.getBoolean("updateAutomatically", true));

        // Set the on click listener of the preference to display a custom seek bar dialog
        updateIntervalDialog.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomDialogStyle);
                builder.setTitle(getString(R.string.text_pref_update_interval_title));

                LayoutInflater layoutInflater = getActivity().getLayoutInflater();
                View updateIntervalDialogView = layoutInflater.inflate(R.layout.dialog_update_interval_settings, null);
                builder.setView(updateIntervalDialogView);

                // Get the previously stored update interval
                int updateInterval = mSharedPreferences.getInt("updateInterval", 10);

                mUpdateInterval = (SeekBar) updateIntervalDialogView.findViewById(R.id.sbUpdateInterval);
                mUpdateIntervalLabel = (TextView) updateIntervalDialogView.findViewById(R.id.tvUpdateIntervalLabel);

                // Set the change listener for the seekbar
                mUpdateInterval.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        mUpdateIntervalLabel.setText(Integer.toString((progress * 5) + 5) + "s");
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });

                // Fill layout with stored values
                mUpdateInterval.setProgress((updateInterval / 5) - 1);
                mUpdateIntervalLabel.setText(Integer.toString(updateInterval) + "s");

                // Edit shared preferences when dialog has been closed with the OK action
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        String updateIntervalString = (String) mUpdateIntervalLabel.getText();
                        int updateInterval = Integer.valueOf(updateIntervalString.substring(0, updateIntervalString.length()-1));
                        editor.putInt("updateInterval", updateInterval);
                        editor.apply();
                        mAppContext.setUpdateInterval(updateInterval);
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();

                return true;
            }
        });
    }

    /**
     * Sets and displays a custom number picker dialog for the user to specify the amount of days to load when loading the
     * cost statistics.
     * @param costStatisticsPeriodDialog The preference for this setting from the preferences.xml.
     */
    private void setupCostStatisticsPeriodDialog(Preference costStatisticsPeriodDialog) {
        // Set summary with actual preference data
        costStatisticsPeriodDialog.setSummary(
            getString(R.string.text_pref_cost_statistics_period_summary) +
            " " +
            mSharedPreferences.getInt("costStatisticsPeriod", 10) +
            " days"
        );

        // Set the on click listener of the preference to display a custom number picker dialog
        costStatisticsPeriodDialog.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomDialogStyle);
                builder.setTitle(getString(R.string.text_pref_cost_statistics_period_title));

                LayoutInflater layoutInflater = getActivity().getLayoutInflater();
                View costStatisticsPeriodDialogView = layoutInflater.inflate(R.layout.dialog_cost_statistics_period_settings, null);
                builder.setView(costStatisticsPeriodDialogView);

                // Get the previously stored amount of days to load
                int costStatisticsPeriod = mSharedPreferences.getInt("costStatisticsPeriod", 10);

                mCostStatisticsPeriod = (NumberPicker) costStatisticsPeriodDialogView.findViewById(R.id.npCostStatistics);
                mCostStatisticsPeriod.setMinValue(7);
                mCostStatisticsPeriod.setMaxValue(30);

                // Fill layout with stored values
                mCostStatisticsPeriod.setValue(costStatisticsPeriod);

                // Edit shared preferences when dialog has been closed with the OK action
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putInt("costStatisticsPeriod", mCostStatisticsPeriod.getValue());
                        editor.apply();
                        mAppContext.setCostStatisticsPeriod(mCostStatisticsPeriod.getValue());
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();

                return true;
            }
        });
    }

    /**
     * Reloads the app to load data of newly connected components.
     * @param completeRestart The preference for this setting from the preferences.xml.
     */
    private void setupCompleteRestartPreference(Preference completeRestart) {
        completeRestart.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getActivity(), SplashScreenActivity.class);
                intent.putExtra("status_info", getString(R.string.text_pref_complete_restart_to_load_new_components));
                getActivity().startActivity(intent);
                getActivity().finish();
                return true;
            }
        });
    }

    /**
     * Sets and displays a custom dialog for the user to specify the IP to connect to the PCM.
     * @param ipDialog The preference for this setting from the preferences.xml.
     */
    private void setupIPDialog(Preference ipDialog) {
        // Set summary with actual preference data
        ipDialog.setSummary(mSharedPreferences.getString("IP", "192.168.0.1"));

        // Set the on click listener of the preference to display a custom dialog
        ipDialog.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomDialogStyle);
                builder.setTitle(getString(R.string.text_pref_ip_title));

                LayoutInflater layoutInflater = getActivity().getLayoutInflater();
                View ipDialogView = layoutInflater.inflate(R.layout.dialog_ip_settings, null);
                builder.setView(ipDialogView);

                // Get the previously stored IP
                String ip = mSharedPreferences.getString("IP", "192.168.0.1");

                mIP1 = (EditText) ipDialogView.findViewById(R.id.etIP1);
                mIP2 = (EditText) ipDialogView.findViewById(R.id.etIP2);
                mIP3 = (EditText) ipDialogView.findViewById(R.id.etIP3);
                mIP4 = (EditText) ipDialogView.findViewById(R.id.etIP4);

                // Fill layout with stored values
                StringTokenizer token = new StringTokenizer(ip, ".");
                mIP1.setText(token.nextToken());
                mIP2.setText(token.nextToken());
                mIP3.setText(token.nextToken());
                mIP4.setText(token.nextToken());

                // Edit shared preferences when dialog has been closed with the OK action
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(isValidIPNumber(mIP1) && isValidIPNumber(mIP2) && isValidIPNumber(mIP3) && isValidIPNumber(mIP4)) {
                            // Update the IP address
                            String ip = mIP1.getText().toString() + "." +
                                    mIP2.getText().toString() + "." +
                                    mIP3.getText().toString() + "." +
                                    mIP4.getText().toString();

                            if(!ip.equals(mSharedPreferences.getString("IP", "192.168.0.1"))) {
                                SharedPreferences.Editor editor = mSharedPreferences.edit();
                                editor.putString("IP", ip);
                                editor.apply();
                                mAppContext.setIPAdress(mSharedPreferences.getString("IP", "192.168.0.1"));

                                // Settings have changed
                                MainActivity.SETTINGS_UPDATED = true;
                            }
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
    }

    /**
     * Error detection for IP
     * @param ip The text field where the content needs to be checked
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

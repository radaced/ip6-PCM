package ch.fhnw.ip6.powerconsumptionmanager.util;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.StringTokenizer;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.activity.SettingsActivity;

/**
 * Created by Patrik on 04.05.2016.
 */
public class IPSettingDialogFragment extends PreferenceDialogFragmentCompat {
    private SharedPreferences mSettings;
    private EditText mIP1;
    private EditText mIP2;
    private EditText mIP3;
    private EditText mIP4;

    public static IPSettingDialogFragment newInstance(Preference preference) {
        IPSettingDialogFragment fragment = new IPSettingDialogFragment();
        Bundle bundle = new Bundle(1);
        bundle.putString("key", preference.getKey());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        // Get the presently stored ip
        mSettings = PreferenceManager.getDefaultSharedPreferences(getContext());
        String ip = mSettings.getString("IP", "192.168.0.1");

        mIP1 = (EditText) view.findViewById(R.id.editIP1);
        mIP2 = (EditText) view.findViewById(R.id.editIP2);
        mIP3 = (EditText) view.findViewById(R.id.editIP3);
        mIP4 = (EditText) view.findViewById(R.id.editIP4);

        // Fill in the text boxes with the current values so the user can adapt them
        StringTokenizer token = new StringTokenizer(ip, ".");
        mIP1.setText(token.nextToken());
        mIP2.setText(token.nextToken());
        mIP3.setText(token.nextToken());
        mIP4.setText(token.nextToken());
    }

    @Override
    public void onDialogClosed(boolean b) {
        // When user pressed ok ...
        if(b) {
            if(isValidIPNumber(mIP1) && isValidIPNumber(mIP2) && isValidIPNumber(mIP3) && isValidIPNumber(mIP4)) {
                // ... edit preference file and update the ip address
                SharedPreferences.Editor editor = mSettings.edit();
                String ip = mIP1.getText().toString() + "." + mIP2.getText().toString() + "." + mIP3.getText().toString() + "." + mIP4.getText().toString();
                editor.putString("IP", ip);
                editor.apply();

                // Update application context
                PowerConsumptionManagerAppContext context = (PowerConsumptionManagerAppContext) getContext().getApplicationContext();
                context.setIPAdress(mSettings.getString("IP", "192.168.0.1"));

                // Settings have changed
                SettingsActivity.UPDATED = true;
                Toast.makeText(getContext(), getContext().getString(R.string.toast_ip_updated), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), getContext().getString(R.string.toast_ip_invalid), Toast.LENGTH_SHORT).show();
            }
        }
    }

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

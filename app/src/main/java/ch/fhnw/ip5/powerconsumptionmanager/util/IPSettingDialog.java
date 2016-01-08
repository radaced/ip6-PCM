package ch.fhnw.ip5.powerconsumptionmanager.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.StringTokenizer;

import ch.fhnw.ip5.powerconsumptionmanager.R;
import ch.fhnw.ip5.powerconsumptionmanager.activity.SettingsActivity;

/**
 * Custom preference dialog to change the ip that is stored in the preference file
 */
public class IPSettingDialog extends DialogPreference {
    private SharedPreferences mSettings;
    private EditText mIP1;
    private EditText mIP2;
    private EditText mIP3;
    private EditText mIP4;

    public IPSettingDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        // Get the presently stored ip
        mSettings = getSharedPreferences();
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
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        // When user pressed ok ...
        if(positiveResult) {
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

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);
        final Resources res = getContext().getResources();
        final Window window = getDialog().getWindow();

        // Change text color of dialog title
        final int titleId = res.getIdentifier("alertTitle", "id", "android");
        final View title = window.findViewById(titleId);
        if (title != null) {
            ((TextView) title).setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextPrimary));
        }

        // Change divider color (divides dialog title and content)
        final int titleDividerId = res.getIdentifier("titleDivider", "id", "android");
        final View titleDivider = window.findViewById(titleDividerId);
        if (titleDivider != null) {
            titleDivider.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorTextPrimary));
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

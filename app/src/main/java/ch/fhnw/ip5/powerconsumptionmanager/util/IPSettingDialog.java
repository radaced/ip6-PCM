package ch.fhnw.ip5.powerconsumptionmanager.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import java.util.StringTokenizer;

import ch.fhnw.ip5.powerconsumptionmanager.R;

/**
 * Created by Patrik on 16.12.2015.
 */
public class IPSettingDialog extends DialogPreference {

    private SharedPreferences mSettings;
    private EditText mIP1;
    private EditText mIP2;
    private EditText mIP3;
    private EditText mIP4;

    public IPSettingDialog(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.preference_ipsetting);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);

        setDialogIcon(null);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        mSettings = getSharedPreferences();
        String ip = mSettings.getString("IP", "192.168.0.1");

        mIP1 = (EditText) view.findViewById(R.id.editIP1);
        mIP2 = (EditText) view.findViewById(R.id.editIP2);
        mIP3 = (EditText) view.findViewById(R.id.editIP3);
        mIP4 = (EditText) view.findViewById(R.id.editIP4);

        StringTokenizer token = new StringTokenizer(ip, ".");
        mIP1.setText(token.nextToken());
        mIP2.setText(token.nextToken());
        mIP3.setText(token.nextToken());
        mIP4.setText(token.nextToken());
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if(positiveResult) {
            SharedPreferences.Editor editor = mSettings.edit();
            String ip = mIP1.getText().toString() + "." + mIP2.getText().toString() + "." + mIP3.getText().toString() + "." + mIP4.getText().toString();
            editor.putString("IP", ip);
            editor.commit();
        }
    }
}

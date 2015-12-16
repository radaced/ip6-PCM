package ch.fhnw.ip5.powerconsumptionmanager.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);
        final Resources res = getContext().getResources();
        final Window window = getDialog().getWindow();

        final int titleId = res.getIdentifier("alertTitle", "id", "android");
        final View title = window.findViewById(titleId);
        if (title != null) {
            ((TextView) title).setTextColor(res.getColor(R.color.colorTextPrimary));
        }

        final int titleDividerId = res.getIdentifier("titleDivider", "id", "android");
        final View titleDivider = window.findViewById(titleDividerId);
        if (titleDivider != null) {
            titleDivider.setBackgroundColor(res.getColor(android.R.color.transparent));
        }
    }
}

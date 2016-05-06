package ch.fhnw.ip6.powerconsumptionmanager.util;

import android.content.Context;
import android.support.v7.preference.DialogPreference;
import android.util.AttributeSet;

/**
 * Custom preference dialog to change the ip that is stored in the preference file
 */
public class IPSettingDialog extends DialogPreference {

    public IPSettingDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}

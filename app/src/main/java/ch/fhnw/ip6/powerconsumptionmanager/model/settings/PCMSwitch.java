package ch.fhnw.ip6.powerconsumptionmanager.model.settings;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import ch.fhnw.ip6.powerconsumptionmanager.R;

public class PCMSwitch extends PCMSetting {
    private String mTextOn;
    private String mTextOff;
    private boolean mIsOn;

    private Switch mSwitch;

    public PCMSwitch(String name, String textOn, String textOff, boolean isOn) {
        super(name);
        mTextOn = textOn;
        mTextOff = textOff;
        mIsOn = isOn;
    }

    @Override
    public void inflateLayout(Context context, LinearLayout container) {
        float density = context.getResources().getDisplayMetrics().density;

        LinearLayout.LayoutParams llLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        llLayoutParams.setMargins(0, 0, 0, (int) (15 * density));


        LinearLayout llHorizontal = new LinearLayout(context);
        llHorizontal.setOrientation(LinearLayout.HORIZONTAL);
        llHorizontal.setLayoutParams(llLayoutParams);

        LinearLayout.LayoutParams tvLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1
        );

        TextView tvSettingDescription = new TextView(context);
        tvSettingDescription.setText(super.getName());
        tvSettingDescription.setTextSize(18);
        tvSettingDescription.setTextColor(ContextCompat.getColor(context, R.color.colorTextPrimary));
        tvSettingDescription.setLayoutParams(tvLayoutParams);

        llHorizontal.addView(tvSettingDescription);

        LinearLayout.LayoutParams swLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1
        );

        mSwitch = new Switch(context);
        mSwitch.setTextOn(mTextOn);
        mSwitch.setTextOff(mTextOff);
        mSwitch.setChecked(mIsOn);
        mSwitch.setLayoutParams(swLayoutParams);

        llHorizontal.addView(mSwitch);

        container.addView(llHorizontal);
    }

    @Override
    public String executeSaveOrGenerateSaveJson(Context context) {
        return "";
    }
}

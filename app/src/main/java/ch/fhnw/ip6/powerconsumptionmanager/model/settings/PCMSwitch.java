package ch.fhnw.ip6.powerconsumptionmanager.model.settings;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import ch.fhnw.ip6.powerconsumptionmanager.R;

/**
 * Represents a simple on/off switch as a setting from the PCM.
 */
public class PCMSwitch extends PCMSetting {
    private String mTextOn;
    private String mTextOff;
    private boolean mIsOn;
    private Switch mSwitch;

    /**
     * Constructor to create a new on/off switch setting.
     * @param name Name of the setting.
     * @param textOn Label that should be displayed when the switch state is true.
     * @param textOff Label that should be displayed when the switch state is false.
     * @param isOn State of switch.
     */
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

        // Horizontal container to display setting name and switch next to each other (1)
        LinearLayout llHorizontal = new LinearLayout(context);
        llHorizontal.setOrientation(LinearLayout.HORIZONTAL);
        llHorizontal.setLayoutParams(llLayoutParams);

        LinearLayout.LayoutParams tvLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1
        );

        // Label for setting name
        TextView tvSettingDescription = new TextView(context);
        tvSettingDescription.setText(super.getName());
        tvSettingDescription.setTextSize(18);
        tvSettingDescription.setTextColor(ContextCompat.getColor(context, R.color.colorTextPrimary));
        tvSettingDescription.setLayoutParams(tvLayoutParams);

        // Add generated UI element to (1)
        llHorizontal.addView(tvSettingDescription);

        LinearLayout.LayoutParams swLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1
        );

        // Generate and set fields for switch UI element
        mSwitch = new Switch(context);
        mSwitch.setTextOn(mTextOn);
        mSwitch.setTextOff(mTextOff);
        mSwitch.setChecked(mIsOn);
        mSwitch.setLayoutParams(swLayoutParams);

        // Add generated switch to (1)
        llHorizontal.addView(mSwitch);

        // Add the horizontal container to the main container which holds all setting layouts
        container.addView(llHorizontal);
    }

    @Override
    public String executeSaveOrGenerateJson(Context context) {
        // String builder to build JSON
        StringBuilder jsonStringBuilder = new StringBuilder(1000);

        // Build JSON with switch state
        String jsonString = "[[ProgramSettings]]{\"Niedertarif\": " + mSwitch.isChecked() + "}";

        return jsonStringBuilder.append(jsonString).toString();
    }
}

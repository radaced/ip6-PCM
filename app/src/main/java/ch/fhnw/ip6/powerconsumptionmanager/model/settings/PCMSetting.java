package ch.fhnw.ip6.powerconsumptionmanager.model.settings;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.LinearLayout;
import android.widget.TextView;

import ch.fhnw.ip6.powerconsumptionmanager.R;

/**
 * Abstract class for all the different PCM settings.
 */
public abstract class PCMSetting {
    protected static final int MARGIN_BOTTOM = 15;

    private String mName;

    /**
     * Constructor for a PCM setting.
     * @param name Name of the setting.
     */
    public PCMSetting(String name) {
        mName = name;
    }

    /**
     * Renders the layout per setting and adds it to the main layout container where all settings then are displayed.
     * @param context Context of the to be generated widget.
     * @param container The main layout container where the generated UI elements per setting are added.
     */
    public void inflateLayout(Context context, LinearLayout container) {
        LinearLayout.LayoutParams tvLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        // Display a label that shows the settings name
        TextView tvSettingDescription = new TextView(context);
        tvSettingDescription.setText(mName);
        tvSettingDescription.setTextSize(18);
        tvSettingDescription.setTextColor(ContextCompat.getColor(context, R.color.colorTextPrimary));
        tvSettingDescription.setLayoutParams(tvLayoutParams);

        // Add the label to the main layout
        container.addView(tvSettingDescription);
    }

    /**
     * Calls the save action generates a JSON to save the modified setting.
     * @param context Context of the save action.
     * @return The generated JSON to save the setting or empty string ("") when directly executed.
     */
    public abstract String executeSaveOrGenerateJson(Context context);

    /***********************
     * GETTERS AND SETTERS *
     ***********************/
    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }
}

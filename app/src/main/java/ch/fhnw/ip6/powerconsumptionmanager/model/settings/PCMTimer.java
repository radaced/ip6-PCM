package ch.fhnw.ip6.powerconsumptionmanager.model.settings;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TimePicker;

/**
 * Represents a program end setting from the PCM as a time picker with spinner style.
 */
public class PCMTimer extends PCMSetting {
    private int mHour;
    private int mMinute;
    private TimePicker tpTimer;

    /**
     * Constructor to create a new timer setting.
     * @param name Name of the setting.
     * @param hour Hour in 24 hour format (e.g. 17:34 --> 17)
     * @param minute Minute (e.g. 17:34 --> 34)
     */
    public PCMTimer(String name, int hour, int minute) {
        super(name);
        mHour = hour;
        mMinute = minute;
    }

    @Override
    public void inflateLayout(Context context, LinearLayout container) {
        super.inflateLayout(context, container);

        LinearLayout.LayoutParams tpTimerLayoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );

        // Setup the time picker
        tpTimer = new TimePicker(context, null, 1); // Use this constructor to display the time picker as a spinner!
        tpTimer.setIs24HourView(true);
        tpTimer.setCurrentHour(mHour);
        tpTimer.setCurrentMinute(mMinute);
        tpTimer.setLayoutParams(tpTimerLayoutParams);

        // Add the time picker to the main container that holds all settings
        container.addView(tpTimer);
    }

    @Override
    public String executeSaveOrGenerateJson(Context context) {
        /* TODO: Needs to be implemented by Zogg Energy Control first */
        return "";
    }
}

package ch.fhnw.ip6.powerconsumptionmanager.model.settings;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import ch.fhnw.ip6.powerconsumptionmanager.R;

/**
 * Represents a program end setting from the PCM as a time picker with spinner style.
 */
public class PCMTimer extends PCMSetting {
    private int mHour;
    private int mMinute;
    private boolean mAutomatic;
    private TimePicker mTimer;
    private Switch mSwitch;

    /**
     * Constructor to create a new timer setting.
     * @param name Name of the setting.
     * @param hour Hour in 24 hour format (e.g. 17:34 --> 17)
     * @param minute Minute (e.g. 17:34 --> 34)
     */
    public PCMTimer(String name, int hour, int minute, boolean automatic) {
        super(name);
        mHour = hour;
        mMinute = minute;
        mAutomatic = automatic;
    }

    @Override
    public void inflateLayout(Context context, LinearLayout container) throws IllegalArgumentException {
        super.inflateLayout(context, container);

        float density = context.getResources().getDisplayMetrics().density;

        LinearLayout.LayoutParams llTimerLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        llTimerLayoutParams.setMargins(0, 0, 0, (int) (MARGIN_BOTTOM * density));

        LinearLayout llTimerContainer = new LinearLayout(context);
        llTimerContainer.setOrientation(LinearLayout.VERTICAL);
        llTimerContainer.setLayoutParams(llTimerLayoutParams);

        final LinearLayout.LayoutParams tpTimerLayoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );

        // Setup the time picker
        mTimer = new TimePicker(context, null, 1); // Use this constructor to display the time picker as a spinner!
        mTimer.setIs24HourView(true);
        mTimer.setCurrentHour(mHour);
        mTimer.setCurrentMinute(mMinute);
        mTimer.setLayoutParams(tpTimerLayoutParams);
        mTimer.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                mSwitch.setChecked(true);
            }
        });

        llTimerContainer.addView(mTimer);

        LinearLayout.LayoutParams llAutomaticLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        LinearLayout llAutomaticContainer = new LinearLayout(context);
        llAutomaticContainer.setOrientation(LinearLayout.HORIZONTAL);
        llAutomaticContainer.setLayoutParams(llAutomaticLayoutParams);

        LinearLayout.LayoutParams AutomaticContainerLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1
        );

        TextView tvLabel = new TextView(context);
        tvLabel.setText(context.getString(R.string.text_automatic));
        tvLabel.setTextSize(15);
        tvLabel.setTextColor(ContextCompat.getColor(context, R.color.colorTextPrimary));
        tvLabel.setLayoutParams(AutomaticContainerLayoutParams);

        llAutomaticContainer.addView(tvLabel);

        mSwitch = new Switch(context);
        mSwitch.setChecked(mAutomatic);
        mSwitch.setLayoutParams(AutomaticContainerLayoutParams);

        llAutomaticContainer.addView(mSwitch);

        llTimerContainer.addView(llAutomaticContainer);

        // Add the time picker to the main container that holds all settings
        container.addView(llTimerContainer);
    }

    @Override
    public String executeSaveOrGenerateJson(Context context) {
        int endTimeSeconds = mTimer.getCurrentHour() * 3600 + mTimer.getCurrentMinute() * 60;
        String timerData = "{" +
                            "\"Endzeit\": " + endTimeSeconds + "," +
                            "\"Auto\": " + mSwitch.isChecked()  +
                            "}";

        return timerData;
    }
}

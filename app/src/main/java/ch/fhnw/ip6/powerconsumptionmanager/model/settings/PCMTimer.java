package ch.fhnw.ip6.powerconsumptionmanager.model.settings;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TimePicker;

public class PCMTimer extends PCMSetting {
    private int mHour;
    private int mMinute;

    private TimePicker tpTimer;

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

        tpTimer = new TimePicker(context, null, 1); // Use this constructor to display the time picker as a spinner!
        tpTimer.setIs24HourView(true);
        tpTimer.setCurrentHour(mHour);
        tpTimer.setCurrentMinute(mMinute);
        tpTimer.setLayoutParams(tpTimerLayoutParams);

        container.addView(tpTimer);
    }

    @Override
    public String executeSaveOrGenerateSaveJson(Context context) {
        return "";
    }
}

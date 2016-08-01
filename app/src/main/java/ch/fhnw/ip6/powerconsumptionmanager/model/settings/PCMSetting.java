package ch.fhnw.ip6.powerconsumptionmanager.model.settings;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.LinearLayout;
import android.widget.TextView;

import ch.fhnw.ip6.powerconsumptionmanager.R;

public abstract class PCMSetting {
    protected static final int MARGIN_BOTTOM = 15;

    private String mName;



    public PCMSetting(String name) {
        mName = name;
    }



    public void inflateLayout(Context context, LinearLayout container) {
        LinearLayout.LayoutParams tvLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        TextView tvSettingDescription = new TextView(context);
        tvSettingDescription.setText(mName);
        tvSettingDescription.setTextSize(18);
        tvSettingDescription.setTextColor(ContextCompat.getColor(context, R.color.colorTextPrimary));
        tvSettingDescription.setLayoutParams(tvLayoutParams);

        container.addView(tvSettingDescription);
    }

    public abstract String generateSaveJson(Context context);



    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }
}

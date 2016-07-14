package ch.fhnw.ip6.powerconsumptionmanager.model.settings;

import android.content.Context;
import android.widget.LinearLayout;

public abstract class PCMSetting {
    private String mName;



    public PCMSetting(String name) {
        mName = name;
    }

    public abstract void inflateLayout(Context context, LinearLayout container);
    public abstract String generateSaveJson();



    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }
}

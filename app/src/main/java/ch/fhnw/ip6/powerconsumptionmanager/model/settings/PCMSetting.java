package ch.fhnw.ip6.powerconsumptionmanager.model.settings;

import android.content.Context;
import android.widget.LinearLayout;

public abstract class PCMSetting {
    private Context mContext;
    private String mName;



    public PCMSetting(Context c, String name) {
        mContext = c;
        mName = name;
    }

    public abstract void inflateLayout(LinearLayout container);
    public abstract String generateSaveJson();


    public Context getContext() {
        return mContext;
    }

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }
}

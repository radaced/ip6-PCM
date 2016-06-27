package ch.fhnw.ip6.powerconsumptionmanager.model;

import java.util.ArrayList;

public class ComponentModel {
    private String mName;
    private double mPower;
    private double mEnergy;
    private double mCost;
    private ArrayList<PCMSetting> mSettings;

    public void renderLayout() {
        for (PCMSetting setting: mSettings) {
            setting.inflateLayout();
        }
    }
}

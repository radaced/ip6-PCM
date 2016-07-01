package ch.fhnw.ip6.powerconsumptionmanager.model.dashboard;

import java.util.LinkedHashMap;
import java.util.TreeMap;

public class CurrentPCMData {
    private double mAutarchy;
    private double mSelfsupply;
    private double mConsumption;
    private int mConsumptionColor;
    private LinkedHashMap<String, CurrentPCMComponentData> mCurrentPCMComponentData = new LinkedHashMap<>();

    public double getAutarchy() {
        return mAutarchy;
    }

    public void setAutarchy(double mAutarchy) {
        this.mAutarchy = mAutarchy;
    }

    public double getSelfsupply() {
        return mSelfsupply;
    }

    public void setSelfsupply(double mSelfsupply) {
        this.mSelfsupply = mSelfsupply;
    }

    public double getConsumption() {
        return mConsumption;
    }

    public void setConsumption(double mConsumption) {
        this.mConsumption = mConsumption;
    }

    public int getConsumptionColor() {
        return mConsumptionColor;
    }

    public void setConsumptionColor(int mConsumptionColor) {
        this.mConsumptionColor = mConsumptionColor;
    }

    public LinkedHashMap<String, CurrentPCMComponentData> getCurrentComponentData() {
        return mCurrentPCMComponentData;
    }
}

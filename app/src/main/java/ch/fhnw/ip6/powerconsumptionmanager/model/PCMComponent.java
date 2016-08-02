package ch.fhnw.ip6.powerconsumptionmanager.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

import ch.fhnw.ip6.powerconsumptionmanager.model.settings.PCMSetting;

public class PCMComponent {
    private String mName;
    private double mPower;
    private double mEnergy;
    private double mCost;
    private int mScaleMinArc, mScaleMaxArc;
    private boolean mIsDisplayedOnDashboard = false;
    private LinkedList<PCMSetting> mSettings = new LinkedList<>();
    private LinkedList<Double> mStatistics = new LinkedList<>();
    private LinkedList<ConsumptionData> mConsumptionData = new LinkedList<>();

    public PCMComponent(String name) {
        mName = name;
        mPower = 0;
        mEnergy = 0;
        mCost = 0;
        mScaleMinArc = 0;
        mScaleMaxArc = 0;
    }

    public void fillDashboardData(JSONObject componentData, int minScaleArc, int scaleMaxArc) throws JSONException {
        mIsDisplayedOnDashboard = true;
        mPower = componentData.getDouble("Leistung");
        mEnergy = componentData.getDouble("Energie");
        mCost = componentData.getDouble("Kosten");
        mScaleMinArc = minScaleArc;
        mScaleMaxArc = scaleMaxArc;
    }

    public void fillStatistics(JSONArray data) throws JSONException {
        for (int i = 0; i < data.length(); i++) {
            JSONObject stats = (JSONObject) data.get(i);
            mStatistics.add(stats.getDouble("Kosten(CHF)"));
        }
    }



    public String getName() {
        return mName;
    }

    public double getPower() {
        return mPower;
    }

    public double getEnergy() {
        return mEnergy;
    }

    public double getCost() {
        return mCost;
    }

    public int getScaleMinArc() {
        return mScaleMinArc;
    }

    public int getScaleMaxArc() {
        return mScaleMaxArc;
    }

    public boolean isDisplayedOnDashboard() {
        return mIsDisplayedOnDashboard;
    }

    public LinkedList<PCMSetting> getSettings() {
        return mSettings;
    }

    public LinkedList<Double> getStatistics() {
        return mStatistics;
    }

    public LinkedList<ConsumptionData> getConsumptionData() {
        return mConsumptionData;
    }
}

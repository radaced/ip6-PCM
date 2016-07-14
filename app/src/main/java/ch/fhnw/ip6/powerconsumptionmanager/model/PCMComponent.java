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
    private LinkedList<PCMSetting> mSettings = new LinkedList<>();
    private LinkedList<Double> mStatistics = new LinkedList<>();

    public PCMComponent(String name, JSONObject componentData, int minScaleArc, int scaleMaxArc) throws JSONException {
        mName = name;
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

    public int getMinArcScale() {
        return mScaleMinArc;
    }

    public int getMaxArcScale() {
        return mScaleMaxArc;
    }

    public LinkedList<PCMSetting> getSettings() {
        return mSettings;
    }

    public LinkedList<Double> getStatistics() {
        return mStatistics;
    }
}

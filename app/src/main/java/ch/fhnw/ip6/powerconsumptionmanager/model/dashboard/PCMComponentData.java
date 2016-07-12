package ch.fhnw.ip6.powerconsumptionmanager.model.dashboard;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.LinkedList;

import ch.fhnw.ip6.powerconsumptionmanager.model.settings.PCMSetting;

public class PCMComponentData {
    private String mName;
    private double mPower;
    private double mEnergy;
    private double mCost;
    private LinkedList<PCMSetting> mSettings = new LinkedList<>();
    private LinkedList<Double> mStatistics = new LinkedList<>();

    public PCMComponentData(String name, JSONObject componentData) throws JSONException {
        mName = name;
        mPower = componentData.getDouble("Leistung");
        mEnergy = componentData.getDouble("Energie");
        mCost = componentData.getDouble("Kosten");
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

    public LinkedList<PCMSetting> getSettings() {
        return mSettings;
    }

    public LinkedList<Double> getStatistics() {
        return mStatistics;
    }
}

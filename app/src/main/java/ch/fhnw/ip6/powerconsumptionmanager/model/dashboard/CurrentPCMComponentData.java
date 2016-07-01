package ch.fhnw.ip6.powerconsumptionmanager.model.dashboard;

import org.json.JSONException;
import org.json.JSONObject;

public class CurrentPCMComponentData {
    private double mPower;
    private double mEnergy;
    private double mCost;

    public CurrentPCMComponentData(JSONObject componentData) {
        try {
            mPower = componentData.getDouble("Leistung");
            mEnergy = componentData.getDouble("Energie");
            mCost = componentData.getDouble("Kosten");
        } catch (JSONException e) {

        }
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
}

package ch.fhnw.ip5.powerconsumptionmanager.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Patrik on 02.12.2015.
 */
public class ComponentDataModel {
    private String mTimestamp;
    private double mPowerkW;
    private static final double SECONDS_DIFFERENCE = 2082844800;

    public ComponentDataModel(JSONObject componentDataArray) {
        try{
            for(int i = 0; i < componentDataArray.length(); i++) {
                double timestamp = componentDataArray.getDouble("Zeit");
                timestamp -= SECONDS_DIFFERENCE; // Convert to Unix Timestamp
                mTimestamp = String.format("%.0f", timestamp);
                mPowerkW = componentDataArray.getDouble("Leistung");
            }

        } catch (JSONException e){

        }
    }

    public String getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(String timestamp) {
        this.mTimestamp = timestamp;
    }

    public double getPowerkW() {
        return mPowerkW;
    }

    public void setPowerkW(double mPowerkW) {
        this.mPowerkW = mPowerkW;
    }
}

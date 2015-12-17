package ch.fhnw.ip5.powerconsumptionmanager.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Holds one value pair (timestamp, power) for the graph of a device from a getData-Request
 */
public class ComponentDataModel {
    private String mTimestamp;
    private double mPowerkW;
    private static final double SECONDS_DIFFERENCE = 2082844800;

    // Read the JSON-Array with the value pair for the graph
    public ComponentDataModel(JSONObject componentDataArray) {
        try{
            for(int i = 0; i < componentDataArray.length(); i++) {
                double timestamp = componentDataArray.getDouble("Zeit");
                // Convert from lab view timestamp (01.01.1904) to unix timestamp (01.01.1970)
                timestamp -= SECONDS_DIFFERENCE;
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

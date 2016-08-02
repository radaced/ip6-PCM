package ch.fhnw.ip6.powerconsumptionmanager.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/* TODO: this data should be integrated into the PCMData/PCMComponent model
 * There is no clear separation as of now by the data received which components should be displayed
 * on which screen.
 */


/**
 * Holds one value pair (timestamp, power) for the graph of a device from a getData-Request
 */
public class ConsumptionData {
    private static final String TAG = "ComponentChartDM";
    private static final double SECONDS_DIFFERENCE = 2082844800;

    private String mTimestamp;
    private double mPowerkW;

    /**
     * Read the JSON-Array with the value pair for the graph
     * @param componentDataArray JSON object that holds an array of value pairs (timestamp, kW)
     */
    public ConsumptionData(JSONObject componentDataArray) {
        try{
            for(int i = 0; i < componentDataArray.length(); i++) {
                double timestamp = componentDataArray.getDouble("Zeit");
                // Convert from lab view timestamp (01.01.1904) to unix timestamp (01.01.1970)
                timestamp -= SECONDS_DIFFERENCE;
                mTimestamp = String.format(Locale.getDefault(), "%.0f", timestamp);
                mPowerkW = componentDataArray.getDouble("Leistung");
            }
        } catch (JSONException e){
            Log.e(TAG, "JSON exception while processing component data.");
        }
    }

    public String getTimestamp() {
        return mTimestamp;
    }

    public double getPowerkW() {
        return mPowerkW;
    }
}

package ch.fhnw.ip6.powerconsumptionmanager.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * Holds the power consumption at a certain point of time as consumption data for the line chart
 */
public class ConsumptionData {
    private static final String TAG = "ComponentChartDM";
    private static final double SECONDS_DIFFERENCE = 2082844800;

    private String mTimestamp;
    private double mPowerkW;

    /**
     * Read the JSON with the timestamp and the power of a component.
     * @param consumptionData JSON object that holds the power consumption of a component at a certain time.
     */
    public ConsumptionData(JSONObject consumptionData) {
        try{
            double timestamp = consumptionData.getDouble("Zeit");
            timestamp -= SECONDS_DIFFERENCE; // Convert from lab view timestamp (01.01.1904) to unix timestamp (01.01.1970)

            mTimestamp = String.format(Locale.getDefault(), "%.0f", timestamp);
            mPowerkW = consumptionData.getDouble("Leistung");
        } catch (JSONException e){
            Log.e(TAG, "JSON exception while processing component data.");
        }
    }

    /***********************
     * GETTERS AND SETTERS *
     ***********************/
    public String getTimestamp() {
        return mTimestamp;
    }

    public double getPowerkW() {
        return mPowerkW;
    }
}

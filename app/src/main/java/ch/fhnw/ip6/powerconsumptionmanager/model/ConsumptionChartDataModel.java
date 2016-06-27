package ch.fhnw.ip6.powerconsumptionmanager.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds all value pairs (timestamp, power) for the graph of a single device from a getData-Request
 */
public class ConsumptionChartDataModel {
    private static final String TAG = "ConsumptionChartDM";

    private String mComponentName;
    private ArrayList<ComponentChartDataModel> mComponentData = new ArrayList<>();

    /**
     * Read device name and store all value pairs in a list
     * @param fullData JSON object that contains all information to a component
     */
    public ConsumptionChartDataModel(JSONObject fullData){
        try{
            mComponentName = fullData.getString("Name");
            JSONArray componentDataJSON = fullData.getJSONArray("Data");
            for(int j = 0; j < componentDataJSON.length(); j++){
                mComponentData.add(new ComponentChartDataModel(componentDataJSON.getJSONObject(j)));
            }
        } catch (JSONException e){
            Log.e(TAG, "JSON exception while processing consumption data.");
        }
    }

    public String getComponentName() {
        return mComponentName;
    }

    public List<ComponentChartDataModel> getComponentData() {
        return mComponentData;
    }
}

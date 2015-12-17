package ch.fhnw.ip5.powerconsumptionmanager.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds all value pairs (timestamp, power) for the graph of a single device from a getData-Request
 */
public class ConsumptionDataModel {
    private String mComponentName;
    private ArrayList<ComponentDataModel> mComponentData = new ArrayList<ComponentDataModel>();

    // Read device name and store all value pairs in a list
    public ConsumptionDataModel(JSONObject fullData){
        try{
            mComponentName = fullData.getString("Name");
            JSONArray componentDataJSON = fullData.getJSONArray("Data");
            for(int j = 0; j < componentDataJSON.length(); j++){
                mComponentData.add(new ComponentDataModel(componentDataJSON.getJSONObject(j)));
            }

        } catch (JSONException e){

        }
    }

    public String getComponentName() {
        return mComponentName;
    }

    public void setComponentName(String name) {
        this.mComponentName = name;
    }

    public List<ComponentDataModel> getComponentData() {
        return mComponentData;
    }

    public void setComponentData(ArrayList<ComponentDataModel> mComponentData) {
        this.mComponentData = mComponentData;
    }
}

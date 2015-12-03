package ch.fhnw.ip5.powerconsumptionmanager.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Patrik on 02.12.2015.
 */
public class ConsumptionDataModel {
    private String componentName;
    private List<ComponentDataModel> componentData = new ArrayList<>();

    public ConsumptionDataModel(JSONArray fullDataArray){
        try{
            for(int i = 0; i < fullDataArray.length(); i++){
                JSONObject object = fullDataArray.getJSONObject(i);
                componentName = object.getString("Name");

                JSONArray componentDataJSON = object.getJSONArray("Data");
                for(int j = 0; j < componentDataJSON.length(); j++){
                    componentData.add(new ComponentDataModel(componentDataJSON.getJSONArray(i)));
                }
            }
        } catch (JSONException e){

        }
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String name) {
        this.componentName = name;
    }

    public List<ComponentDataModel> getComponentData() {
        return componentData;
    }

    public void setComponentData(List<ComponentDataModel> componentData) {
        this.componentData = componentData;
    }
}

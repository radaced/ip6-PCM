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
    private String name;
    private List<ComponentDataModel> data = new ArrayList<>();

    public ConsumptionDataModel(JSONArray fullDataArray){
        try{

            for(int i = 0; i < fullDataArray.length(); i++){
                JSONObject object = fullDataArray.getJSONObject(i);
                name = object.getString("Name");

                JSONArray componentDataJSON = object.getJSONArray("Data");
                for(int j = 0; j < componentDataJSON.length(); j++){
                    data.add(new ComponentDataModel(componentDataJSON.getJSONObject(i)));
                }
            }
        } catch (JSONException e){

        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ComponentDataModel> getData() {
        return data;
    }

    public void setData(List<ComponentDataModel> data) {
        this.data = data;
    }
}

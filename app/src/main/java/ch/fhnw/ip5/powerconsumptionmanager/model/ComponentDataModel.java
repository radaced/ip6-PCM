package ch.fhnw.ip5.powerconsumptionmanager.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by Patrik on 02.12.2015.
 */
public class ComponentDataModel {
    private double timestamp;
    private double powerkW;
    private static final double SECONDS_DIFFERENCE = 2082844800;

    public ComponentDataModel(JSONArray componentDataArray) {
        try{
            JSONObject object = componentDataArray.getJSONObject(0);
            timestamp = object.getDouble("Zeit");
            timestamp -= SECONDS_DIFFERENCE; // Convert to Unix Timestamp

            object = componentDataArray.getJSONObject(1);
            powerkW = object.getDouble("Leistung");
        } catch (JSONException e){

        }
    }

    public double getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(double timestamp) {
        this.timestamp = timestamp;
    }

    public double getPowerkW() {
        return powerkW;
    }

    public void setPowerkW(double powerkW) {
        this.powerkW = powerkW;
    }
}

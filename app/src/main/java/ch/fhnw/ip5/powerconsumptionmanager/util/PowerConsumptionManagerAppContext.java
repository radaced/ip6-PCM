package ch.fhnw.ip5.powerconsumptionmanager.util;

import android.app.Application;

import java.util.ArrayList;

import ch.fhnw.ip5.powerconsumptionmanager.model.ConsumptionDataModel;

/**
 * Created by Patrik on 02.12.2015.
 */
public class PowerConsumptionManagerAppContext extends Application{
    private String mIPAdress;
    private ArrayList<ConsumptionDataModel> mConsumptionData = new ArrayList<ConsumptionDataModel>();
    private ArrayList<String> mComponents = new ArrayList<String>();

    public ArrayList<ConsumptionDataModel> getConsumptionData() {
        return mConsumptionData;
    }

    public ArrayList<String> getComponents() {
        return mComponents;
    }

    public String getIPAdress() {
        return mIPAdress;
    }

    public void setIPAdress(String mIPAdress) {
        this.mIPAdress = mIPAdress;
    }
}

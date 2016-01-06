package ch.fhnw.ip5.powerconsumptionmanager.util;

import android.app.Application;

import java.util.ArrayList;

import ch.fhnw.ip5.powerconsumptionmanager.model.ConsumptionDataModel;
import ch.fhnw.ip5.powerconsumptionmanager.model.RouteInformationModel;

/**
 * Application context (storage of global app data and data received from web requests)
 */
public class PowerConsumptionManagerAppContext extends Application{
    private String mIPAdress;
    private boolean isOnline;
    private ArrayList<ConsumptionDataModel> mConsumptionData = new ArrayList<>();
    private ArrayList<String> mComponents = new ArrayList<>();
    private RouteInformationModel mRouteInformation;

    public String getIPAdress() {
        return mIPAdress;
    }

    public void setIPAdress(String mIPAdress) {
        this.mIPAdress = mIPAdress;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setIsOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    public void setConsumptionData(ArrayList<ConsumptionDataModel> mConsumptionData) {
        this.mConsumptionData = mConsumptionData;
    }

    public ArrayList<ConsumptionDataModel> getConsumptionData() {
        return mConsumptionData;
    }

    public void setComponents(ArrayList<String> mComponents) {
        this.mComponents = mComponents;
    }

    public ArrayList<String> getComponents() {
        return mComponents;
    }

    public RouteInformationModel getRouteInformation() {
        return mRouteInformation;
    }

    public void setRouteInformation(RouteInformationModel mRouteInformation) {
        this.mRouteInformation = mRouteInformation;
    }
}

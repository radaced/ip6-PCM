package ch.fhnw.ip6.powerconsumptionmanager.util;

import android.app.Application;

import java.util.ArrayList;

import ch.fhnw.ip6.powerconsumptionmanager.model.ConsumptionChartDataModel;
import ch.fhnw.ip6.powerconsumptionmanager.model.RouteInformationModel;

/**
 * Application context (storage of global app data and data received from web requests)
 */
public class PowerConsumptionManagerAppContext extends Application {
    public final String UNIT_KW = "kW";
    public final String UNIT_KWH = "kWh";
    public final String UNIT_PERCENTAGE = "%";

    private String mIPAdress;
    private boolean isOnline;
    private ArrayList<ConsumptionChartDataModel> mConsumptionData = new ArrayList<>();
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

    public void setConsumptionData(ArrayList<ConsumptionChartDataModel> mConsumptionData) {
        this.mConsumptionData = mConsumptionData;
    }

    public ArrayList<ConsumptionChartDataModel> getConsumptionData() {
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

package ch.fhnw.ip6.powerconsumptionmanager.util;

import android.app.Application;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import ch.fhnw.ip6.powerconsumptionmanager.model.ConsumptionChartDataModel;
import ch.fhnw.ip6.powerconsumptionmanager.model.RouteInformationModel;
import ch.fhnw.ip6.powerconsumptionmanager.model.dashboard.CurrentPCMData;
import okhttp3.OkHttpClient;

/**
 * Application context (storage of global app data and data received from web requests)
 */
public class PowerConsumptionManagerAppContext extends Application {
    public final String UNIT_KW = "kW";
    public final String UNIT_KWH = "kWh";
    public final String UNIT_PERCENTAGE = "%";

    private final OkHttpClient mOkHTTPClient = new OkHttpClient().newBuilder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .build();


    private String mIPAdress;



    private boolean isOnline = true;

    /* Dashboard data */
    private CurrentPCMData mCurrentPCMData;



    private ArrayList<ConsumptionChartDataModel> mConsumptionData = new ArrayList<>();
    private ArrayList<String> mComponents = new ArrayList<>();
    private RouteInformationModel mRouteInformation;


    public OkHttpClient getOkHTTPClient() {
        return mOkHTTPClient;
    }

    public String getIPAdress() {
        return mIPAdress;
    }

    public void setIPAdress(String mIPAdress) {
        this.mIPAdress = mIPAdress;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    public CurrentPCMData getCurrentPCMData() {
        return mCurrentPCMData;
    }

    public void setCurrentPCMData(CurrentPCMData mCurrentPCMData) {
        this.mCurrentPCMData = mCurrentPCMData;
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

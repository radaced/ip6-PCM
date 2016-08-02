package ch.fhnw.ip6.powerconsumptionmanager.util;

import android.app.Application;

import java.util.concurrent.TimeUnit;

import ch.fhnw.ip6.powerconsumptionmanager.model.chargeplan.RouteInformation;
import ch.fhnw.ip6.powerconsumptionmanager.model.PCMData;
import okhttp3.OkHttpClient;

/**
 * Application context (storage of global app data and data received from web requests)
 */
public class PowerConsumptionManagerAppContext extends Application {

    /* HTTP client for web requests */
    private final OkHttpClient mOkHTTPClient = new OkHttpClient().newBuilder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .build();


    /* Status */
    private boolean mIsOnline = true;

    /* Preferences */
    private boolean mUseGoogleCalendar;
    private boolean mIsUpdatingAutomatically;
    private int mUpdateInterval;
    private int mCostStatisticsPeriod;
    private String mIPAdress;

    /* Data */
    private PCMData mPCMData;

    private RouteInformation mRouteInformation;



    public OkHttpClient getOkHTTPClient() {
        return mOkHTTPClient;
    }

    public boolean usesGoogleCalendar() {
        return mUseGoogleCalendar;
    }

    public void setGoogleCalendar(boolean mUseGoogleCalendar) {
        this.mUseGoogleCalendar = mUseGoogleCalendar;
    }

    public boolean isUpdatingAutomatically() {
        return mIsUpdatingAutomatically;
    }

    public void setUpdatingAutomatically(boolean mIsUpdatingAutomatically) {
        this.mIsUpdatingAutomatically = mIsUpdatingAutomatically;
    }

    public int getUpdateInterval() {
        return mUpdateInterval;
    }

    public void setUpdateInterval(int mUpdateInterval) {
        this.mUpdateInterval = mUpdateInterval;
    }

    public int getCostStatisticsPeriod() {
        return mCostStatisticsPeriod;
    }

    public void setCostStatisticsPeriod(int mCostStatisticsPeriod) {
        this.mCostStatisticsPeriod = mCostStatisticsPeriod;
    }

    public String getIPAdress() {
        return mIPAdress;
    }

    public void setIPAdress(String mIPAdress) {
        this.mIPAdress = mIPAdress;
    }

    public boolean isOnline() {
        return mIsOnline;
    }

    public void setOnline(boolean isOnline) {
        this.mIsOnline = isOnline;
    }

    public PCMData getPCMData() {
        return mPCMData;
    }

    public void setPCMData(PCMData mPCMData) {
        this.mPCMData = mPCMData;
    }

    public RouteInformation getRouteInformation() {
        return mRouteInformation;
    }

    public void setRouteInformation(RouteInformation mRouteInformation) {
        this.mRouteInformation = mRouteInformation;
    }
}

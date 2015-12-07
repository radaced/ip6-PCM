package ch.fhnw.ip5.powerconsumptionmanager.network;

/**
 * Created by Patrik on 02.12.2015.
 */
public interface DataLoaderCallback {
    void UsageDataLoaderDidFinish();
    void UsageDataLoaderDidFail();
}

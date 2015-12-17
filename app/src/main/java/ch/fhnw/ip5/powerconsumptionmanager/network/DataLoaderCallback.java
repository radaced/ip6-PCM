package ch.fhnw.ip5.powerconsumptionmanager.network;

/**
 * Callback function for asynchronous web requests
 */
public interface DataLoaderCallback {
    void DataLoaderDidFinish();
    void DataLoaderDidFail();
}

package ch.fhnw.ip6.powerconsumptionmanager.network;

/**
 * Callback functions for asynchronous web requests
 */
public interface DataLoaderCallback {
    void DataLoaderDidFinish();
    void DataLoaderDidFail();
}

package ch.fhnw.ip6.powerconsumptionmanager.network;

/**
 * Callback interface for async tasks
 */
public interface AsyncTaskCallback {
    void asyncTaskFinished(boolean result, String opType);
}

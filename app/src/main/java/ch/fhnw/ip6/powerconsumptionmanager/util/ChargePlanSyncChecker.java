package ch.fhnw.ip6.powerconsumptionmanager.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import ch.fhnw.ip6.powerconsumptionmanager.network.SynchronizeChargePlanAsyncTask;

/**
 * Checks if a charge plan sync is pending or not and executes the according tasks.
 */
public class ChargePlanSyncChecker {

    /**
     * Checks if a charge plan sync is pending or not and executes the according tasks.
     * @param appContext The application context.
     * @param settings The shared preferences of this application.
     */
    public static void executeSyncIfPending(PowerConsumptionManagerAppContext appContext, SharedPreferences settings) {
        if(settings.contains("brChargePlanSyncPending")) {
            // Get information about the active network connection
            ConnectivityManager connManager = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();

            if(activeNetwork != null) {
                // Only perform sync task when connected to a WIFI
                if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    boolean syncPending = settings.getBoolean("brChargePlanSyncPending", false);
                    // When a sync is pending then execute the synchronize charge plan task
                    if(syncPending) {
                        new SynchronizeChargePlanAsyncTask(appContext, null).execute();
                    }
                }
            }
        }
    }
}

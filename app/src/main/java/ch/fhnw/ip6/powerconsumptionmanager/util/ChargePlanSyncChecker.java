package ch.fhnw.ip6.powerconsumptionmanager.util;

import android.content.SharedPreferences;

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
            boolean syncPending = settings.getBoolean("brChargePlanSyncPending", false);
            // When a sync is pending then execute the synchronize charge plan task
            if(syncPending) {
                new SynchronizeChargePlanAsyncTask(appContext, null).execute();
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("brChargePlanSyncPending", false);
                editor.apply();
            }
        }
    }
}

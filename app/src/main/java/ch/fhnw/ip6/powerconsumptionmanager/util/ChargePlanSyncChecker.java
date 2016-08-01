package ch.fhnw.ip6.powerconsumptionmanager.util;

import android.content.SharedPreferences;

import ch.fhnw.ip6.powerconsumptionmanager.network.SynchronizeChargePlanAsyncTask;

public class ChargePlanSyncChecker {

    public static void executeSyncIfPending(PowerConsumptionManagerAppContext appContext, SharedPreferences settings) {
        if(settings.contains("brChargePlanSyncPending")) {
            boolean syncPending = settings.getBoolean("brChargePlanSyncPending", false);
            if(syncPending) {
                new SynchronizeChargePlanAsyncTask(appContext, null).execute();
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("brChargePlanSyncPending", false);
                editor.apply();
            }
        }
    }
}

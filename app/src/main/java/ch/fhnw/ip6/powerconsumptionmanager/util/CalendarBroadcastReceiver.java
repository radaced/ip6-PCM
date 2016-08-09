package ch.fhnw.ip6.powerconsumptionmanager.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.widget.Toast;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.network.AsyncTaskCallback;
import ch.fhnw.ip6.powerconsumptionmanager.network.SynchronizeChargePlanAsyncTask;

/**
 * Broadcast receiver that listens to changes of the calendar and the connectivity and performs an automatic
 * sync of the charge plan if necessary.
 */
public class CalendarBroadcastReceiver extends BroadcastReceiver implements AsyncTaskCallback {
    private PowerConsumptionManagerAppContext mAppContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mAppContext = (PowerConsumptionManagerAppContext) context.getApplicationContext();

        // Check if the user manages the charge plan over the google calendar
        if(mAppContext.usesGoogleCalendar()) {
            // Get information about the active network connection
            ConnectivityManager connManager = (ConnectivityManager) mAppContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();

            if(activeNetwork != null) {
                // Only perform sync task when connected to a WIFI
                if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    new SynchronizeChargePlanAsyncTask(mAppContext, this, null).execute();
                } else {
                    // When the calendar changed and there is no WIFI set a preference that a sync is pending on next startup
                    if(intent.getAction().equals(Intent.ACTION_PROVIDER_CHANGED)) {
                        setChargePlanSyncPending(true);
                    }
                }
            }
        }
    }

    /**
     * Return point from requests that sync the charge plan in the background.
     * @param result Status if the data could be sent successfully or not.
     * @param opType Type of operation that has completed.
     */
    @Override
    public void asyncTaskFinished(boolean result, String opType) {
        if(result) {
            //Toast.makeText(mAppContext, mAppContext.getString(R.string.toast_sync_ended_success), Toast.LENGTH_SHORT).show();
            setChargePlanSyncPending(false);
        } else {
            //Toast.makeText(mAppContext, mAppContext.getString(R.string.toast_br_sync_no_connection), Toast.LENGTH_SHORT).show();
            setChargePlanSyncPending(true);
        }
    }

    /**
     * Edits the shared preferences to set the flag if a sync is still pending or not.
     * @param pending If a synchronisation is still outstanding or not.
     */
    private void setChargePlanSyncPending(boolean pending) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mAppContext);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("brChargePlanSyncPending", pending);
        editor.apply();
    }
}

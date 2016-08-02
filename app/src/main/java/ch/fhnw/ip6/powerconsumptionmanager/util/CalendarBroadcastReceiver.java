package ch.fhnw.ip6.powerconsumptionmanager.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.widget.Toast;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.network.AsyncTaskCallback;
import ch.fhnw.ip6.powerconsumptionmanager.network.SynchronizeChargePlanAsyncTask;

public class CalendarBroadcastReceiver extends BroadcastReceiver implements AsyncTaskCallback {
    private PowerConsumptionManagerAppContext mAppContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mAppContext = (PowerConsumptionManagerAppContext) context.getApplicationContext();
        ConnectivityManager connManager = (ConnectivityManager) mAppContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();

        if(activeNetwork != null) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI && mAppContext.usesGoogleCalendar()) {
                new SynchronizeChargePlanAsyncTask(mAppContext, this, null).execute();
            } else {
                if(intent.getAction().equals(Intent.ACTION_PROVIDER_CHANGED)) {
                    setChargePlanSyncPending();
                }
            }
        }
    }

    @Override
    public void asyncTaskFinished(boolean result) {
        if(result) {
            Toast.makeText(mAppContext, mAppContext.getString(R.string.toast_sync_ended_success), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mAppContext, mAppContext.getString(R.string.toast_br_sync_no_connection), Toast.LENGTH_SHORT).show();
            setChargePlanSyncPending();
        }
    }

    private void setChargePlanSyncPending() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mAppContext);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("brChargePlanSyncPending", true);
        editor.apply();
    }
}

package ch.fhnw.ip6.powerconsumptionmanager.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
        new SynchronizeChargePlanAsyncTask(mAppContext, this, null).execute();
    }

    @Override
    public void asyncTaskFinished(boolean result) {
        if(result) {
            Toast.makeText(mAppContext, mAppContext.getString(R.string.toast_sync_ended_success), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mAppContext, mAppContext.getString(R.string.toast_br_sync_no_connection), Toast.LENGTH_SHORT).show();

            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mAppContext);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("brChargePlanSyncPending", true);
            editor.apply();
        }
    }
}

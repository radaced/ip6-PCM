package ch.fhnw.ip6.powerconsumptionmanager.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import org.bouncycastle.crypto.util.Pack;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.network.AsyncTaskCallback;
import ch.fhnw.ip6.powerconsumptionmanager.network.SynchronizeChargePlanAsyncTask;
import ch.fhnw.ip6.powerconsumptionmanager.util.ChargePlanSyncChecker;
import ch.fhnw.ip6.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;
import ch.fhnw.ip6.powerconsumptionmanager.view.startup.InitFragment;
import ch.fhnw.ip6.powerconsumptionmanager.view.startup.SplashFragment;

/**
 * This Activity is called when the app gets started (launch activity). It requests to grant permissions
 * on the initial startup or if they're not set. Contains some navigation logic and delegates the setting of
 * initial settings or loads the settings from the shared preferences file. Delegates to call the first
 * webservice for data from the PCM and change to the main activity after the loading process is done.
 */
public class SplashScreenActivity extends AppCompatActivity implements AsyncTaskCallback {
    private static final int PERMISSIONS_REQUEST = 0;

    private PowerConsumptionManagerAppContext mAppContext;

    private final Object mAsyncTaskFinishedLock = new Object();
    private volatile int pendingDataToLoad = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Check permissions on runtime and request them if they're not granted yet (android 6.0 and higher)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                this,
                new String[] {
                    Manifest.permission.INTERNET,
                    Manifest.permission.READ_CALENDAR,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE
                },
                PERMISSIONS_REQUEST
            );
        } else {
            continueStartup();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean permissionsGranted = true;

        // Check if the permissions have been granted
        switch (requestCode) {
            case PERMISSIONS_REQUEST:
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        permissionsGranted = false;
                    }
                }
        }

        // All permissions need to be granted otherwise a user info is shown to the user and the app exits
        if(permissionsGranted) {
            continueStartup();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.text_permission_warning), Toast.LENGTH_LONG).show();
            this.finish();
        }
    }

    /**
     *  Return point from requests that load the initial data for the app. This is the loading of all devices
     *  that are connected to the PCM and the current data of the devices that are being displayed on the dashboard.
     *  @param result Status if the data could be loaded successfully or not.
     */
    @Override
    public void asyncTaskFinished(boolean result) {
        // Lock this section because two different async task load the initial data
        synchronized (mAsyncTaskFinishedLock) {
            if(mAppContext.isOnline()) {
                mAppContext.setOnline(result);
            }

            pendingDataToLoad--;

            // Only proceed to the main activity after both async task have finished
            if(pendingDataToLoad == 0) {
                changeToMain();
            }
        }
    }

    /**
     * Startup application after permission check.
     */
    private void continueStartup() {
        // Load application context and preferences
        mAppContext = (PowerConsumptionManagerAppContext) getApplicationContext();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment;

        // On initial startup show mask to enter ip, otherwise directly load initial data to display
        if(settings.contains("IP")) {
            // Load preferences from file into application context for easier access
            mAppContext.setGoogleCalendar(settings.getBoolean("googleCalendar", false));
            mAppContext.setUpdatingAutomatically(settings.getBoolean("updateAutomatically", false));
            mAppContext.setUpdateInterval(settings.getInt("updateInterval", 10));
            mAppContext.setCostStatisticsPeriod(settings.getInt("costStatisticsPeriod", 7));
            mAppContext.setIPAdress(settings.getString("IP", "192.168.0.1"));

            fragment = SplashFragment.newInstance();
        } else {
            fragment = InitFragment.newInstance();
        }

        transaction.replace(R.id.flStartupContentContainer, fragment);
        transaction.commitAllowingStateLoss();
    }

    /**
     * Transition to main activity.
     */
    private void changeToMain() {
        Intent mainIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
        SplashScreenActivity.this.startActivity(mainIntent);
        SplashScreenActivity.this.finish();
    }
}

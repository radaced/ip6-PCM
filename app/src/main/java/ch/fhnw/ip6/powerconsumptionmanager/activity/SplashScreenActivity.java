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

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.network.AsyncTaskCallback;
import ch.fhnw.ip6.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;
import ch.fhnw.ip6.powerconsumptionmanager.view.startup.InitFragment;
import ch.fhnw.ip6.powerconsumptionmanager.view.startup.SplashFragment;

/**
 * Activity that is called when the app gets started. Contains some navigation logic and delegates
 * the initial settings logic and the loading/reading of data to fragments.
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

        // Check permissions on runtime (Android 6.0 and higher)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                this,
                new String[] {
                    Manifest.permission.INTERNET,
                    Manifest.permission.READ_CALENDAR,
                    Manifest.permission.ACCESS_FINE_LOCATION
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
        switch (requestCode) {
            case PERMISSIONS_REQUEST:
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        permissionsGranted = false;
                    }
                }
        }

        if(permissionsGranted) {
            continueStartup();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.text_permission_warning), Toast.LENGTH_LONG).show();
            this.finish();
        }
    }

    /**** Return point from requests that load the consumption data ****/
    @Override
    public void asyncTaskFinished(boolean result) {
        synchronized (mAsyncTaskFinishedLock) {
            if(mAppContext.isOnline()) {
                mAppContext.setOnline(result);
            }

            pendingDataToLoad--;

            if(pendingDataToLoad == 0) {
                changeToMain();
            }
        }
    }

    /**
     * Transition to main activity
     */
    private void changeToMain() {
        Intent mainIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
        SplashScreenActivity.this.startActivity(mainIntent);
        SplashScreenActivity.this.finish();
    }

    /**
     * Startup application after permission check
     */
    private void continueStartup() {
        // Load application context and preferences
        mAppContext = (PowerConsumptionManagerAppContext) getApplicationContext();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment;

        // On initial startup show mask to enter ip, otherwise directly load data to display
        if (settings.contains("IP")) {
            // Load preferences from file into application context for easier access
            mAppContext.setGoogleCalendar(settings.getBoolean("googleCalendar", false));
            mAppContext.setUpdatingAutomatically(settings.getBoolean("updateAutomatically", true));
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
}

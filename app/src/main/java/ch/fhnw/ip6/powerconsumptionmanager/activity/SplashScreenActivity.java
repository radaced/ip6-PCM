package ch.fhnw.ip6.powerconsumptionmanager.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.network.DataLoaderCallback;
import ch.fhnw.ip6.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;
import ch.fhnw.ip6.powerconsumptionmanager.view.InitFragment;
import ch.fhnw.ip6.powerconsumptionmanager.view.SplashFragment;

/**
 * Activity that is called when the app gets started. Contains some navigation logic and delegates
 * the initial settings logic and the loading/reading of data to fragments.
 */
public class SplashScreenActivity extends AppCompatActivity implements DataLoaderCallback {
    private PowerConsumptionManagerAppContext mAppContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Load application context and preferences
        mAppContext = (PowerConsumptionManagerAppContext) getApplicationContext();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // On initial startup show mask to enter ip, otherwise directly load data to display
        if(settings.contains("IP")) {
            // Load IP address from preference file into application context for easier access
            mAppContext.setIPAdress(settings.getString("IP", "192.168.0.1"));

            SplashFragment fragment = SplashFragment.newInstance();
            transaction.replace(R.id.startup_fragment, fragment);
        } else {
            InitFragment fragment = InitFragment.newInstance();
            transaction.replace(R.id.startup_fragment, fragment);
        }

        transaction.commit();
    }

    /**** Return point from requests that load the consumption data ****/
    @Override
    public void DataLoaderDidFinish() {
        mAppContext.setIsOnline(true);
        changeToMain();
    }

    @Override
    public void DataLoaderDidFail() {
        mAppContext.setIsOnline(false);
        changeToMain();
    }
    /********/

    /**
     * Transition to main activity
     */
    private void changeToMain() {
        Intent mainIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
        SplashScreenActivity.this.startActivity(mainIntent);
        SplashScreenActivity.this.finish();
    }
}

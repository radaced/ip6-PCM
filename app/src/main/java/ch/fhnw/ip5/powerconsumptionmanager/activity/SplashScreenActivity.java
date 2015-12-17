package ch.fhnw.ip5.powerconsumptionmanager.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ch.fhnw.ip5.powerconsumptionmanager.R;
import ch.fhnw.ip5.powerconsumptionmanager.network.DataLoaderCallback;
import ch.fhnw.ip5.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;
import ch.fhnw.ip5.powerconsumptionmanager.view.InitFragment;
import ch.fhnw.ip5.powerconsumptionmanager.view.SplashFragment;

public class SplashScreenActivity extends AppCompatActivity implements DataLoaderCallback {
    private PowerConsumptionManagerAppContext mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Load application context and preferences
        mContext = (PowerConsumptionManagerAppContext) getApplicationContext();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // On initial startup show mask to enter ip, otherwise directly load data to display
        if(settings.contains("IP")) {
            // Load IP address from preference file into application context for easier access
            mContext.setIPAdress(settings.getString("IP", "192.168.0.1"));
            SplashFragment fragment = SplashFragment.newInstance();
            transaction.replace(R.id.splash_fragment, fragment);
        } else {
            InitFragment fragment = InitFragment.newInstance();
            transaction.replace(R.id.splash_fragment, fragment);
        }

        transaction.commit();
    }

    /************* Loader Callbacks *************/
    @Override
    public void DataLoaderDidFinish() {
        mContext.setIsOnline(true);
        changeToMain();
    }

    @Override
    public void DataLoaderDidFail() {
        mContext.setIsOnline(false);
        changeToMain();
    }

    private void changeToMain() {
        Intent mainIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
        SplashScreenActivity.this.startActivity(mainIntent);
        SplashScreenActivity.this.finish();
    }
}

package ch.fhnw.ip5.powerconsumptionmanager.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ch.fhnw.ip5.powerconsumptionmanager.R;
import ch.fhnw.ip5.powerconsumptionmanager.network.DataLoaderCallback;
import ch.fhnw.ip5.powerconsumptionmanager.view.InitFragment;
import ch.fhnw.ip5.powerconsumptionmanager.view.SplashFragment;

public class SplashScreenActivity extends AppCompatActivity implements DataLoaderCallback {
    private final int SPLASH_DISPLAY_LENGTH = 1000;
    public static final String CONNECTION_SETTINGS = "connection_settings";
    public static String IP_ADDRESS;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        SharedPreferences settings = getSharedPreferences(CONNECTION_SETTINGS, MODE_PRIVATE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if(settings.contains("IP")) {
            SplashFragment fragment = SplashFragment.newInstance();
            transaction.replace(R.id.splash_fragment, fragment);
        } else {
            InitFragment fragment = InitFragment.newInstance();
            transaction.replace(R.id.splash_fragment, fragment);
        }

        transaction.commit();

        /*
        final DataLoader loader = new DataLoader((PowerConsumptionManagerAppContext) getApplicationContext(), this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loader.loadComponents(getString(R.string.webservice_getComponents));
                loader.loadConsumptionData(getString(R.string.webservice_getData));
            }
        }, SPLASH_DISPLAY_LENGTH);
        */
    }

    @Override
    public void UsageDataLoaderDidFinish() {
        Intent mainIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
        SplashScreenActivity.this.startActivity(mainIntent);
        SplashScreenActivity.this.finish();
    }

    @Override
    public void UsageDataLoaderDidFail() {

    }
}

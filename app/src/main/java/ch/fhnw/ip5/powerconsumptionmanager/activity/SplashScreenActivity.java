package ch.fhnw.ip5.powerconsumptionmanager.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ch.fhnw.ip5.powerconsumptionmanager.R;
import ch.fhnw.ip5.powerconsumptionmanager.network.DataLoader;
import ch.fhnw.ip5.powerconsumptionmanager.network.DataLoaderCallback;
import ch.fhnw.ip5.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;

public class SplashScreenActivity extends AppCompatActivity implements DataLoaderCallback {
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_splash_screen);

        //Network test
        final DataLoader loader = new DataLoader((PowerConsumptionManagerAppContext) getApplicationContext(), this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loader.loadComponents(getString(R.string.webservice_getComponents));
                loader.loadConsumptionData(getString(R.string.webservice_getData));
            }
        }, SPLASH_DISPLAY_LENGTH);
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

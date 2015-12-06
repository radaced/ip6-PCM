package ch.fhnw.ip5.powerconsumptionmanager.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ch.fhnw.ip5.powerconsumptionmanager.R;
import ch.fhnw.ip5.powerconsumptionmanager.network.ConsumptionDataLoader;
import ch.fhnw.ip5.powerconsumptionmanager.network.ConsumptionDataLoaderCallback;
import ch.fhnw.ip5.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;

public class SplashScreenActivity extends AppCompatActivity implements ConsumptionDataLoaderCallback {
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_splash_screen);

        //Network test
        final ConsumptionDataLoader loader = new ConsumptionDataLoader(
                (PowerConsumptionManagerAppContext) getApplicationContext(),
                this,
                getString(R.string.webservice_getData));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loader.LoadUsageData();
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

package ch.fhnw.ip5.powerconsumptionmanager.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ch.fhnw.ip5.powerconsumptionmanager.R;
import ch.fhnw.ip5.powerconsumptionmanager.network.DataLoaderCallback;
import ch.fhnw.ip5.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;
import ch.fhnw.ip5.powerconsumptionmanager.view.InitFragment;
import ch.fhnw.ip5.powerconsumptionmanager.view.SplashFragment;

public class SplashScreenActivity extends AppCompatActivity implements DataLoaderCallback {
    public static final String CONNECTION_SETTINGS = "connection_settings";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        SharedPreferences settings = getSharedPreferences(CONNECTION_SETTINGS, MODE_PRIVATE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        final PowerConsumptionManagerAppContext context = (PowerConsumptionManagerAppContext) getApplicationContext();

        if(settings.contains("IP")) {
            context.setIPAdress(settings.getString("IP", null));
            SplashFragment fragment = SplashFragment.newInstance();
            transaction.replace(R.id.splash_fragment, fragment);
        } else {
            InitFragment fragment = InitFragment.newInstance();
            transaction.replace(R.id.splash_fragment, fragment);
        }

        transaction.commit();
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

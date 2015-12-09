package ch.fhnw.ip5.powerconsumptionmanager.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import ch.fhnw.ip5.powerconsumptionmanager.R;
import ch.fhnw.ip5.powerconsumptionmanager.network.DataLoader;
import ch.fhnw.ip5.powerconsumptionmanager.network.DataLoaderCallback;
import ch.fhnw.ip5.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;
import ch.fhnw.ip5.powerconsumptionmanager.view.InitFragment;
import ch.fhnw.ip5.powerconsumptionmanager.view.SplashFragment;

public class SplashScreenActivity extends AppCompatActivity implements DataLoaderCallback {
    private final int SPLASH_DISPLAY_LENGTH = 1000;
    public static final String CONNECTION_SETTINGS = "connection_settings";
    public static String IP_ADDRESS;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_splash_screen);

        SharedPreferences settings = getSharedPreferences(CONNECTION_SETTINGS, MODE_PRIVATE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if(settings.contains("IP")) {
            SplashFragment fragment = new SplashFragment();
            transaction.replace(R.id.splash_fragment, fragment);
        } else {
            InitFragment fragment = new InitFragment();
            transaction.replace(R.id.splash_fragment, fragment);

            /* WEITERMACHEN */
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("IP", "Bla");
            editor.commit();
        }

        transaction.commit();

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
    protected void onStop() {
        super.onStop();


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

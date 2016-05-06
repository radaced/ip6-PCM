package ch.fhnw.ip6.powerconsumptionmanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.view.SettingsFragment;

/**
 * The settings activity displays the settings over a fragment and contains some
 * navigation logic.
 */
public class SettingsActivity extends AppCompatActivity {
    // Flag if settings have changed
    public static boolean UPDATED = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Load preferences in separate fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.ip_setting_fragment, new SettingsFragment()).commit();

        // Toolbar settings
        Toolbar tb = (Toolbar) findViewById(R.id.settings_toolbar);
        tb.setTitle("Settings");
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Close settings activity when finished with editing settings
            case android.R.id.home:
                if(SettingsActivity.UPDATED) {
                    // Navigate to splash screen activity to reload data with new settings
                    Intent mainIntent = new Intent(SettingsActivity.this, SplashScreenActivity.class);
                    mainIntent.putExtra("settings_changed", getString(R.string.text_splash_settings_changed));
                    SettingsActivity.this.startActivity(mainIntent);
                    SettingsActivity.this.finish();
                } else {
                    Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
                    SettingsActivity.this.startActivity(mainIntent);
                    SettingsActivity.this.finish();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
        SettingsActivity.this.startActivity(mainIntent);
        SettingsActivity.this.finish();
    }
}


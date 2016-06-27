package ch.fhnw.ip6.powerconsumptionmanager.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;

public class ComponentSettingsActivity extends AppCompatActivity {

    PowerConsumptionManagerAppContext mAppContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_component_settings);

        mAppContext = (PowerConsumptionManagerAppContext) getApplicationContext();

        Toolbar toolbar = (Toolbar) findViewById(R.id.tbComponentSettings);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Bundle extras = this.getIntent().getExtras();
        if(extras != null) {
            int index = extras.getInt("component_position");
            getSupportActionBar().setTitle(getString(R.string.title_activity_actionbar) + " " + mAppContext.getComponents().get(index));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

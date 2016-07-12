package ch.fhnw.ip6.powerconsumptionmanager.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.model.settings.PCMSetting;
import ch.fhnw.ip6.powerconsumptionmanager.network.AsyncTaskCallback;
import ch.fhnw.ip6.powerconsumptionmanager.network.GetComponentSettingsAsyncTask;
import ch.fhnw.ip6.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;

public class ComponentSettingsActivity extends AppCompatActivity implements AsyncTaskCallback {

    private PowerConsumptionManagerAppContext mAppContext;
    private String mComponentName;
    private LinearLayout mLoadingLayout;
    private LinearLayout mComponentSettingsLayout;
    private LinearLayout mSettingsContainer;

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

        mLoadingLayout = (LinearLayout) findViewById(R.id.llLoading);
        mComponentSettingsLayout = (LinearLayout) findViewById(R.id.llComponentSettings);
        mSettingsContainer = (LinearLayout) findViewById(R.id.llSettingsContainer);

        Bundle extras = this.getIntent().getExtras();
        if(extras != null) {
            int index = extras.getInt("component_position");
            mComponentName = mAppContext.getComponents().get(index);

            getSupportActionBar().setTitle(getString(R.string.title_activity_actionbar) + " " + mComponentName);
            new GetComponentSettingsAsyncTask(this, this, mComponentName).execute();
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

    @Override
    public void asyncTaskFinished(boolean result) {
        for (PCMSetting setting : mAppContext.getPCMData().getComponentData().get(mComponentName).getSettings()) {
            setting.inflateLayout(mSettingsContainer);
        }

        mLoadingLayout.setVisibility(View.GONE);
        mComponentSettingsLayout.setVisibility(View.VISIBLE);
    }
}

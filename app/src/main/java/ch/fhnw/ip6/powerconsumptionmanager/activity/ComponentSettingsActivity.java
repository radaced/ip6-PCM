package ch.fhnw.ip6.powerconsumptionmanager.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.model.settings.PCMSetting;
import ch.fhnw.ip6.powerconsumptionmanager.network.AsyncTaskCallback;
import ch.fhnw.ip6.powerconsumptionmanager.network.GetComponentSettingsAsyncTask;
import ch.fhnw.ip6.powerconsumptionmanager.network.PutComponentSettingsAsyncTask;
import ch.fhnw.ip6.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;

/**
 * This activity loads and displays the different settings of a component from the PCM.
 */
public class ComponentSettingsActivity extends AppCompatActivity implements AsyncTaskCallback {

    private PowerConsumptionManagerAppContext mAppContext;

    private String mComponentName;
    private LinearLayout mLoadingLayout;
    private LinearLayout mComponentSettingsLayout;
    private LinearLayout mOnErrorComponentSettingsLayout;
    private LinearLayout mSettingsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_component_settings);

        mAppContext = (PowerConsumptionManagerAppContext) getApplicationContext();

        // Set the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.tbComponentSettings);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            // Set the "back" button functionality
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Load the different layouts from the XML
        mLoadingLayout = (LinearLayout) findViewById(R.id.llLoading);
        mComponentSettingsLayout = (LinearLayout) findViewById(R.id.llComponentSettings);
        mOnErrorComponentSettingsLayout = (LinearLayout) findViewById(R.id.llOnErrorComponentSettings);
        mSettingsContainer = (LinearLayout) findViewById(R.id.llSettingsContainer);

        // Get the component name of which the settings need to be displayed from the intent
        Bundle extras = this.getIntent().getExtras();
        if(extras != null) {
            mComponentName = extras.getString("componentName");

            getSupportActionBar().setTitle(getString(R.string.title_activity_actionbar) + " " + mComponentName);
            // Load the component settings
            new GetComponentSettingsAsyncTask(mAppContext, this, mComponentName).execute();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.component_settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Close activity if the user presses on the "back" button in the top left
            case android.R.id.home:
                this.finish();
                break;
            // Save the settings
            case R.id.action_save_settings:
                String json = "[";
                Toast.makeText(this, getString(R.string.toast_save_component_settings_info), Toast.LENGTH_SHORT).show();

                // Generate the json or execute the save action for every setting of this component
                for (PCMSetting setting : mAppContext.getPCMData().getComponentData().get(mComponentName).getSettings()) {
                    String settingJson = setting.executeSaveOrGenerateJson(this);
                    if(!settingJson.equals("")) {
                        json = json + setting.executeSaveOrGenerateJson(this) + ",";
                    }
                }

                json = json.substring(0, json.length()-1);
                json = json + "]";

                // Save the settings
                new PutComponentSettingsAsyncTask(mAppContext, this, json, mComponentName).execute();
                break;
        }

        return true;
    }

    /**
     * Return point when the settings of the component have finished loading from the webservice
     * and now can be displayed/rendered on the activity or when the settings have been saved.
     * @param result Status if the data could be loaded/sent successfully or not.
     * @param opType Type of operation that has completed.
     */
    @Override
    public void asyncTaskFinished(boolean result, String opType) {
        // Hide the loading section
        mLoadingLayout.setVisibility(View.GONE);

        // Check if the callback is from getting the component settings or the saving of the component settings
        if(opType.equals(mAppContext.OP_TYPES[0])) {
            // Inflate all the layouts of the loaded settings for the component or display an error message
            if(result) {
                mAppContext.getPCMData().getComponentData().get(mComponentName).renderSettings(this, mSettingsContainer);
                mComponentSettingsLayout.setVisibility(View.VISIBLE);
            } else {
                mOnErrorComponentSettingsLayout.setVisibility(View.VISIBLE);
            }
        } else {
            if(result) {
                Toast.makeText(getApplicationContext(), R.string.toast_save_component_settings_status, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), R.string.toast_error_save_component_settings_status, Toast.LENGTH_SHORT).show();
            }
        }
    }
}

package ch.fhnw.ip6.powerconsumptionmanager.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.util.ChargePlanSyncChecker;
import ch.fhnw.ip6.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;
import ch.fhnw.ip6.powerconsumptionmanager.view.ConnectedDevicesFragment;
import ch.fhnw.ip6.powerconsumptionmanager.view.ConsumptionFragment;
import ch.fhnw.ip6.powerconsumptionmanager.view.CostStatisticsFragment;
import ch.fhnw.ip6.powerconsumptionmanager.view.OfflineFragment;
import ch.fhnw.ip6.powerconsumptionmanager.view.dashboard.OverviewFragment;
import ch.fhnw.ip6.powerconsumptionmanager.view.SettingsFragment;

/**
 * The main activity is called after the initial data loading on the splash screen
 * activity. It contains the navigation drawer and its primary task is swapping between
 * the different screens.
 */
public class MainActivity extends AppCompatActivity {
    // Flag if the settings in the shared preferences file have changed
    public static boolean SETTINGS_UPDATED = false;

    private PowerConsumptionManagerAppContext mAppContext;

    private DrawerLayout mDrawerLayout;
    private NavigationView mDrawerNavView;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAppContext = (PowerConsumptionManagerAppContext) getApplicationContext();

        // Load the layout for the navigation drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerNavView = (NavigationView) findViewById(R.id.navView);

        // Setup main toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.tbMain);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            // Set drawer icon
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_drawer);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Ties the drawer layout and the action bar together
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        // Setup the content that is displayed in the navigation drawer
        setupDrawerContent(mDrawerNavView);

        // On initial startup set the title to overview because this is the first screen to be shown after startup
        if(savedInstanceState == null) {
            getSupportActionBar().setTitle(R.string.title_frag_overview);
            // Check if the loading of the initial data was successful and display the according screens
            if (mAppContext.isOnline()) {
                getSupportFragmentManager().beginTransaction().replace(R.id.flMainContentContainer, new OverviewFragment()).commit();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.flMainContentContainer, new OfflineFragment()).commit();
            }
        } else {
            // Restore the toolbar title
            getSupportActionBar().setTitle(savedInstanceState.getCharSequence("TOOLBAR_TITLE"));
        }
    }

    /**
     * Defines what happens when an item in the navigation drawer is being selected.
     * @param navigationView The navigation view loaded from a XML file.
     */
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                selectDrawerItem(menuItem);
                return true;
            }
        });
    }

    /**
     * Exchanges the screen with the content/screen that the user requested of the navigation drawer menu.
     * @param menuItem The menu item that has been selected by the user.
     */
    private void selectDrawerItem(MenuItem menuItem) {
        Fragment fragment;

        if(SETTINGS_UPDATED) {
            // Navigate to splash screen activity to reload data with new settings
            Intent intent = new Intent(MainActivity.this, SplashScreenActivity.class);
            intent.putExtra("status_info", getString(R.string.text_splash_settings_changed));
            MainActivity.this.startActivity(intent);
            MainActivity.this.finish();
        } else {
            // When the initial loading of data has been successful swap the fragments accordingly
            if(mAppContext.isOnline()) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_home:
                        fragment = OverviewFragment.newInstance();
                        break;
                    case R.id.nav_connected_devices:
                        fragment = ConnectedDevicesFragment.newInstance();
                        break;
                    case R.id.nav_consumption_data:
                        fragment = ConsumptionFragment.newInstance();
                        break;
                    case R.id.nav_cost_statistic:
                        fragment = CostStatisticsFragment.newInstance();
                        break;
                    case R.id.nav_settings:
                        fragment = SettingsFragment.newInstance();
                        break;
                    default:
                        fragment = OverviewFragment.newInstance();
                        break;
                }
            } else {
                // If the initial data couldn't be loaded only the settings screen can be accessed
                if(menuItem.getItemId() == R.id.nav_settings) {
                    fragment = SettingsFragment.newInstance();
                } else {
                    fragment = OfflineFragment.newInstance();
                }
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.flMainContentContainer, fragment).commit();

            // Update selected item and title, then close the drawer
            menuItem.setChecked(true);
            setTitle(menuItem.getTitle());
            mDrawerLayout.closeDrawer(mDrawerNavView);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(CharSequence title) {
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(getSupportActionBar() != null) {
            // Save the toolbar title
            outState.putCharSequence("TOOLBAR_TITLE", getSupportActionBar().getTitle());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Makes sure that the drawer icon state and the drawer state itself is synced properly after the activity restored
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Let the drawer layout now when configurations (e.g. orientation) changed
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();
        /* Check if the charge plan needs to be synced because changes to the calendar have been made when no connection
         * to the PCM was available.
         */
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        ChargePlanSyncChecker.executeSyncIfPending(mAppContext, settings);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Reset the flags for when settings have changed or if the app has connection to the PCM
        SETTINGS_UPDATED = false;
        mAppContext.setOnline(true);
    }
}
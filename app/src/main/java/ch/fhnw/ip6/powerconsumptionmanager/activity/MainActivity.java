package ch.fhnw.ip6.powerconsumptionmanager.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;
import ch.fhnw.ip6.powerconsumptionmanager.view.ConnectedDevicesFragment;
import ch.fhnw.ip6.powerconsumptionmanager.view.ConsumptionFragment;
import ch.fhnw.ip6.powerconsumptionmanager.view.CostStatisticsFragment;
import ch.fhnw.ip6.powerconsumptionmanager.view.OfflineFragment;
import ch.fhnw.ip6.powerconsumptionmanager.view.dashboard.OverviewFragment;
import ch.fhnw.ip6.powerconsumptionmanager.view.SettingsFragment;

/**
 * The main activity is called after the initial data loading on the splash screen
 * activity. It contains the two pages for displaying the consumption data and the
 * charge plan.
 */
public class MainActivity extends AppCompatActivity {
    // Flag if settings have changed
    public static boolean SETTINGS_UPDATED = false;

    PowerConsumptionManagerAppContext mAppContext;

    private DrawerLayout mDrawerLayout;
    private NavigationView mDrawerNavView;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mTitle;

    /**
     * Setup main menu
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAppContext = (PowerConsumptionManagerAppContext) getApplicationContext();

        mTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerNavView = (NavigationView) findViewById(R.id.navView);

        // Main toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.tbMain);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_drawer);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        setupDrawerContent(mDrawerNavView);

        if(savedInstanceState == null) {
            setTitle(R.string.title_frag_overview);
            if (mAppContext.isOnline()) {
                getSupportFragmentManager().beginTransaction().replace(R.id.flMainContentContainer, new OverviewFragment()).commit();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.flMainContentContainer, new OfflineFragment()).commit();
            }
        }
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                selectDrawerItem(menuItem);
                return true;
            }
        });
    }

    private void selectDrawerItem(MenuItem menuItem) {
        Fragment fragment;

        if(SETTINGS_UPDATED) {
            // Navigate to splash screen activity to reload data with new settings
            Intent intent = new Intent(MainActivity.this, SplashScreenActivity.class);
            intent.putExtra("status_info", getString(R.string.text_splash_settings_changed));
            MainActivity.this.startActivity(intent);
            MainActivity.this.finish();
        } else {
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
        mTitle = title;
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mTitle);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SETTINGS_UPDATED = false;
        mAppContext.setOnline(true);
    }
}
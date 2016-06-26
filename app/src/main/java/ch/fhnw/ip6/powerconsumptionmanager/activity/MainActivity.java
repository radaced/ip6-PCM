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
import ch.fhnw.ip6.powerconsumptionmanager.view.ConnectedDevicesFragment;
import ch.fhnw.ip6.powerconsumptionmanager.view.ConsumptionFragment;
import ch.fhnw.ip6.powerconsumptionmanager.view.dashboard.OverviewFragment;
import ch.fhnw.ip6.powerconsumptionmanager.view.SettingsFragment;

/**
 * The main activity is called after the initial data loading on the splash screen
 * activity. It contains the two pages for displaying the consumption data and the
 * charge plan.
 */
public class MainActivity extends AppCompatActivity {
    // Flag if settings have changed
    public static boolean UPDATED = false;

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
            getSupportFragmentManager().beginTransaction().replace(R.id.flMainContentContainer, new OverviewFragment()).commit();
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

        if(UPDATED) {
            // Navigate to splash screen activity to reload data with new settings
            Intent intent = new Intent(MainActivity.this, SplashScreenActivity.class);
            intent.putExtra("settings_changed", getString(R.string.text_splash_settings_changed));
            MainActivity.this.startActivity(intent);
            MainActivity.this.finish();
        } else {
            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    fragment = new OverviewFragment();
                    break;
                case R.id.nav_connected_devices:
                    fragment = new ConnectedDevicesFragment();
                    break;
                case R.id.nav_consumption_data:
                    fragment = new ConsumptionFragment();
                    break;
                case R.id.nav_cost_statistic:
                    fragment = new ConsumptionFragment();
                    break;
                case R.id.nav_settings:
                    fragment = new SettingsFragment();
                    break;
                default:
                    fragment = new ConsumptionFragment();
                    break;
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
        UPDATED = false;
    }
}
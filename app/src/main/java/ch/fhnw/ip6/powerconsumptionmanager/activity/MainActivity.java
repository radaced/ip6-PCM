package ch.fhnw.ip6.powerconsumptionmanager.activity;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.adapter.EVMPagerAdapter;
import ch.fhnw.ip6.powerconsumptionmanager.model.PagerItemModel;
import ch.fhnw.ip6.powerconsumptionmanager.util.SlidingTabLayout;

/**
 * The main activity is called after the initial data loading on the splash screen
 * activity. It contains the two pages for displaying the consumption data and the
 * charge plan.
 */
public class MainActivity extends AppCompatActivity {
    private List<PagerItemModel> mTabs = new ArrayList<PagerItemModel>();

    /**
     * Setup main menu
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View view = findViewById(android.R.id.content);

        createViewPagerTabs();

        // Main toolbar
        Toolbar tb = (Toolbar) findViewById(R.id.main_toolbar);
        tb.setTitle(getString(R.string.title_activity_main));
        setSupportActionBar(tb);

        // Sliding layout
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewPager.setAdapter(new EVMPagerAdapter(getSupportFragmentManager(), mTabs));

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(viewPager);

        // Colorized indicator that shows which tab is currently displayed
        slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return mTabs.get(position).getIndicatorColor();
            }

            @Override
            public int getDividerColor(int position) {
                return mTabs.get(position).getDividerColor();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Open settings activity when icon is pressed in actionbar
            case R.id.action_settings:
                Intent mainIntent = new Intent(MainActivity.this, SettingsActivity.class);
                MainActivity.this.startActivity(mainIntent);
                MainActivity.this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Generate tabs as pager models to later display in sliding tab layout
     */
    private void createViewPagerTabs() {
        mTabs.add(new PagerItemModel(
                getString(R.string.title_frag_consumption),
                ContextCompat.getColor(this, R.color.colorSlideTabIndicator),
                ContextCompat.getColor(this, R.color.colorSlideTabDivider)
        ));
        mTabs.add(new PagerItemModel(
                getString(R.string.title_frag_plan),
                ContextCompat.getColor(this, R.color.colorSlideTabIndicator),
                ContextCompat.getColor(this, R.color.colorSlideTabDivider)
        ));
        /*
        mTabs.add(new PagerItemModel(
                getString(R.string.title_frag_tesla),
                ContextCompat.getColor(this, R.color.colorSlideTabIndicator),
                ContextCompat.getColor(this, R.color.colorSlideTabDivider)
        ));
        */
    }
}

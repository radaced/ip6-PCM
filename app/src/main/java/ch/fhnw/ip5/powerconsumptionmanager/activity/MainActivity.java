package ch.fhnw.ip5.powerconsumptionmanager.activity;

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

import ch.fhnw.ip5.powerconsumptionmanager.R;
import ch.fhnw.ip5.powerconsumptionmanager.adapter.EVMPagerAdapter;
import ch.fhnw.ip5.powerconsumptionmanager.model.PagerItemModel;
import ch.fhnw.ip5.powerconsumptionmanager.util.SlidingTabLayout;

public class MainActivity extends AppCompatActivity {
    private SlidingTabLayout mSlidingTabLayout;
    private View mView;
    private ViewPager mViewPager;
    private List<PagerItemModel> mTabs = new ArrayList<PagerItemModel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mView = findViewById(android.R.id.content);

        /************* Setup main menu *************/
        createViewPagerTabs();

        // Main toolbar
        Toolbar tb = (Toolbar) findViewById(R.id.main_toolbar);
        tb.setTitle(getString(R.string.title_activity_main));
        setSupportActionBar(tb);

        // Sliding layout
        mViewPager = (ViewPager) mView.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new EVMPagerAdapter(getSupportFragmentManager(), mTabs));

        mSlidingTabLayout = (SlidingTabLayout) mView.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(mViewPager);

        // Colorized indicator that shows which tab is currently displayed
        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
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
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Generate tabs as pager models to later display in sliding tab layout
    private void createViewPagerTabs() {
        mTabs.add(new PagerItemModel(
                getString(R.string.data_frag_text),
                ContextCompat.getColor(this, R.color.colorSlideTabIndicator),
                ContextCompat.getColor(this, R.color.colorSlideTabDivider)
        ));
        mTabs.add(new PagerItemModel(
                getString(R.string.plan_frag_text),
                ContextCompat.getColor(this, R.color.colorSlideTabIndicator),
                ContextCompat.getColor(this, R.color.colorSlideTabDivider)
        ));
        mTabs.add(new PagerItemModel(
                getString(R.string.tesla_frag_text),
                ContextCompat.getColor(this, R.color.colorSlideTabIndicator),
                ContextCompat.getColor(this, R.color.colorSlideTabDivider)
        ));
    }
}

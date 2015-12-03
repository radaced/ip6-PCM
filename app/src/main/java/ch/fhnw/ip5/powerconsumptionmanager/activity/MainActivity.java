package ch.fhnw.ip5.powerconsumptionmanager.activity;

import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ch.fhnw.ip5.powerconsumptionmanager.R;
import ch.fhnw.ip5.powerconsumptionmanager.adapter.EVMPagerAdapter;
import ch.fhnw.ip5.powerconsumptionmanager.model.PagerItem;
import ch.fhnw.ip5.powerconsumptionmanager.network.ConsumptionDataLoader;
import ch.fhnw.ip5.powerconsumptionmanager.network.ConsumptionDataLoaderCallback;
import ch.fhnw.ip5.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;
import ch.fhnw.ip5.powerconsumptionmanager.util.SlidingTabLayout;

public class MainActivity extends AppCompatActivity implements ConsumptionDataLoaderCallback {
    private SlidingTabLayout mSlidingTabLayout;
    private View mView;
    private ViewPager mViewPager;
    private List<PagerItem> mTabs = new ArrayList<PagerItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mView = findViewById(android.R.id.content);

        mTabs.add(new PagerItem(
                getString(R.string.data_frag_text),
                ContextCompat.getColor(this, R.color.colorPrimary),
                ContextCompat.getColor(this, R.color.colorPrimary)
        ));

        mTabs.add(new PagerItem(
                getString(R.string.plan_frag_text),
                ContextCompat.getColor(this, R.color.colorPrimary),
                ContextCompat.getColor(this, R.color.colorPrimary)
        ));

        mTabs.add(new PagerItem(
                getString(R.string.tesla_frag_text),
                ContextCompat.getColor(this, R.color.colorPrimary),
                ContextCompat.getColor(this, R.color.colorPrimary)
        ));

        mViewPager = (ViewPager) mView.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new EVMPagerAdapter(getSupportFragmentManager(), mTabs));

        mSlidingTabLayout = (SlidingTabLayout) mView.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(mViewPager);

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

        //Network test
        ConsumptionDataLoader loader = new ConsumptionDataLoader((PowerConsumptionManagerAppContext) getApplicationContext(), this, getString(R.string.webservice_getData));
        loader.LoadUsageData();
    }

    @Override
    public void UsageDataLoaderDidFinish() {

    }

    @Override
    public void UsageDataLoaderDidFail() {
        Toast.makeText(this, "Fail", Toast.LENGTH_LONG);
    }
}

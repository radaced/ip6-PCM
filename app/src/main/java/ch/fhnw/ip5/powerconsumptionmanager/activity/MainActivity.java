package ch.fhnw.ip5.powerconsumptionmanager.activity;

import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    }

    private void createViewPagerTabs() {
        mTabs.add(new PagerItemModel(
                getString(R.string.data_frag_text),
                ContextCompat.getColor(this, R.color.colorPrimary),
                ContextCompat.getColor(this, R.color.colorPrimary)
        ));
        mTabs.add(new PagerItemModel(
                getString(R.string.plan_frag_text),
                ContextCompat.getColor(this, R.color.colorPrimary),
                ContextCompat.getColor(this, R.color.colorPrimary)
        ));
        mTabs.add(new PagerItemModel(
                getString(R.string.tesla_frag_text),
                ContextCompat.getColor(this, R.color.colorPrimary),
                ContextCompat.getColor(this, R.color.colorPrimary)
        ));
    }
}

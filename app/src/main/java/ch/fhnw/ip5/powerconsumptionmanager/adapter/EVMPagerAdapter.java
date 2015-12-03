package ch.fhnw.ip5.powerconsumptionmanager.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import ch.fhnw.ip5.powerconsumptionmanager.model.PagerItem;

public class EVMPagerAdapter extends FragmentPagerAdapter {
    List<PagerItem> mTabs;

    public EVMPagerAdapter(FragmentManager fm, List<PagerItem> tabs) {
        super(fm);
        mTabs = tabs;
    }

    @Override
    public android.support.v4.app.Fragment getItem(int i) {
        return mTabs.get(i).createFragment();
    }

    @Override
    public int getCount() {
        return mTabs.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabs.get(position).getTitle();
    }

}

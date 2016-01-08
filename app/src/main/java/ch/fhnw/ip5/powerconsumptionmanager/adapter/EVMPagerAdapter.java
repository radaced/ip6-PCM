package ch.fhnw.ip5.powerconsumptionmanager.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import ch.fhnw.ip5.powerconsumptionmanager.model.PagerItemModel;

/**
 * Pager adapter to show correct fragment for each tab
 */
public class EVMPagerAdapter extends FragmentPagerAdapter {
    List<PagerItemModel> mTabs;

    public EVMPagerAdapter(FragmentManager fm, List<PagerItemModel> tabs) {
        super(fm);
        mTabs = tabs;
    }

    @Override
    public Fragment getItem(int i) {
        return mTabs.get(i).createFragment(i);
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

package ch.fhnw.ip6.powerconsumptionmanager.model;


import android.support.v4.app.Fragment;

import ch.fhnw.ip6.powerconsumptionmanager.view.ConsumptionFragment;
import ch.fhnw.ip6.powerconsumptionmanager.view.PlanFragment;
import ch.fhnw.ip6.powerconsumptionmanager.view.TeslaFragment;

/**
 * Stores the tab data like title, indicator color (border under the selected tab) and divider
 * color (border between tabs)
 */
public class PagerItemModel {
    private final CharSequence mTitle;
    private final int mIndicatorColor;
    private final int mDividerColor;

    public PagerItemModel(CharSequence title, int indicatorColor, int dividerColor) {
        mTitle = title;
        mIndicatorColor = indicatorColor;
        mDividerColor = dividerColor;
    }

    /**
     * Creation of the new fragment when page changed to the right fragment class
     * @param i Index from the viewpager tab that gets active
     * @return Fragment instance
     */
    //
    public Fragment createFragment(int i) {
        switch (i) {
            case 0:
                return ConsumptionFragment.newInstance();
            case 1:
                return PlanFragment.newInstance();
            case 2:
                return TeslaFragment.newInstance();
            default:
                return null;
        }
    }

    public CharSequence getTitle() {
        return mTitle;
    }

    public int getIndicatorColor() {
        return mIndicatorColor;
    }

    public int getDividerColor() {
        return mDividerColor;
    }
}

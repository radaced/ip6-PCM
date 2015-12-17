package ch.fhnw.ip5.powerconsumptionmanager.model;


import android.support.v4.app.Fragment;

import ch.fhnw.ip5.powerconsumptionmanager.view.ConsumptionFragment;

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

    // Delegate the creation of the new fragment when page changed to the right fragment class
    public Fragment createFragment() {
        return ConsumptionFragment.newInstance();
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

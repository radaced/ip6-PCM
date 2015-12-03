package ch.fhnw.ip5.powerconsumptionmanager.model;


import ch.fhnw.ip5.powerconsumptionmanager.view.ConsumptionFragment;

public class PagerItem {
    private final CharSequence mTitle;
    private final int mIndicatorColor;
    private final int mDividerColor;

    public PagerItem(CharSequence title, int indicatorColor, int dividerColor) {
        mTitle = title;
        mIndicatorColor = indicatorColor;
        mDividerColor = dividerColor;
    }

    public android.support.v4.app.Fragment createFragment() {
        return ConsumptionFragment.newInstance(mTitle, mIndicatorColor, mDividerColor);
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

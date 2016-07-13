package ch.fhnw.ip6.powerconsumptionmanager.model.settings;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appyvet.rangebar.IRangeBarFormatter;
import com.appyvet.rangebar.RangeBar;

import ch.fhnw.ip6.powerconsumptionmanager.R;


public class PCMSlider extends PCMSetting {
    private String mUnit;
    private float mMinScale, mMaxScale;
    private float mMinValue, mMaxValue;
    private boolean mIsRange;
    private boolean mStartsNegative;

    private RangeBar mRangebar;

    public PCMSlider(Context c,
                     String name,
                     String unit,
                     float minScale,
                     float maxScale,
                     float minValue,
                     float maxValue,
                     boolean isRange,
                     boolean startsNegative) {
        super(c, name);
        mUnit = unit;
        mMinScale = minScale;
        mMaxScale = maxScale;
        mMinValue = minValue;
        if(mIsRange = isRange) {
            mMaxValue = maxValue;
        }
        mStartsNegative = startsNegative;
    }

    @Override
    public void inflateLayout(LinearLayout container) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        TextView tvSettingDescription = new TextView(super.getContext());
        tvSettingDescription.setText(super.getName());
        tvSettingDescription.setTextSize(12);
        tvSettingDescription.setTextColor(ContextCompat.getColor(super.getContext(), R.color.colorTextPrimary));
        tvSettingDescription.setLayoutParams(layoutParams);

        container.addView(tvSettingDescription);

        mRangebar = new RangeBar(super.getContext(), null);

        if(mStartsNegative) {
            mRangebar.setTickStart(0);
            mRangebar.setTickEnd((mMinScale * (-1)) + mMaxScale);
        } else {
            mRangebar.setTickStart(mMinScale);
            mRangebar.setTickEnd(mMaxScale);
        }
        mRangebar.setTickInterval((float) 0.5);

        mRangebar.setRangeBarEnabled(mIsRange);
        if(mIsRange) {
            mRangebar.setRangePinsByValue(mMinValue, mMaxValue);
        } else {
            mRangebar.setSeekPinByValue(mMinValue);
        }

        mRangebar.setFormatter(new IRangeBarFormatter() {
            @Override
            public String format(String value) {
                if(mStartsNegative) {
                    if(Double.valueOf(value) < (mMinScale * (-1))) {
                        value = "-" + (Double.valueOf(value) - (mMinScale * (-1)));
                    }
                }
                return value + " " + mUnit;
            }
        });
        mRangebar.setLayoutParams(layoutParams);

        container.addView(mRangebar);
    }

    @Override
    public String generateSaveJson() {
        return null;
    }
}

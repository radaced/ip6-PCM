package ch.fhnw.ip6.powerconsumptionmanager.model.settings;

import android.content.Context;
import android.widget.LinearLayout;

import com.appyvet.rangebar.IRangeBarFormatter;
import com.appyvet.rangebar.RangeBar;

import java.text.NumberFormat;
import java.util.LinkedHashMap;


public class PCMSlider extends PCMSetting {
    private String mUnit;
    private float mMinScale, mMaxScale;
    private float mMinValue, mMaxValue;
    private boolean mIsRange;
    private boolean mStartsNegative;
    private LinkedHashMap<Float, Float> mPCMValueToRangeBarValue = new LinkedHashMap<>();
    private LinkedHashMap<Float, Float> mRangeBarValueToPCMValue = new LinkedHashMap<>();

    private RangeBar mRangebar;

    public PCMSlider(String name, String unit, float minScale, float maxScale, float minValue, float maxValue, boolean isRange) {
        super(name);
        mUnit = unit;
        mMinScale = minScale;
        mMaxScale = maxScale;
        mMinValue = minValue;
        if(mIsRange = isRange) {
            mMaxValue = maxValue;
        }
        mStartsNegative = minScale < 0;
    }

    @Override
    public void inflateLayout(Context context, LinearLayout container) {
        super.inflateLayout(context, container);

        final NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMinimumFractionDigits(1);

        float density = context.getResources().getDisplayMetrics().density;

        LinearLayout.LayoutParams rbLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (int) (80 * density)
        );
        rbLayoutParams.setMargins(0, 0, 0, (int) (MARGIN_BOTTOM * density));

        // Use this constructor new Rangebar(context, attributeset) specifically, otherwise an essential map from the library
        // gets not instantiated (mTickMap)!
        mRangebar = new RangeBar(context, null);

        for (int i = 0; i <= mMaxScale - mMinScale; i++) {
            mPCMValueToRangeBarValue.put(mMinScale + i, (float) i);
            mPCMValueToRangeBarValue.put((float) (mMinScale + i + 0.5), (float) (i + 0.5));
            mRangeBarValueToPCMValue.put((float) i, mMinScale + i);
            mRangeBarValueToPCMValue.put((float) (i + 0.5), (float) (mMinScale + i + 0.5));
        }

        if(mStartsNegative) {
            mRangebar.setTickEnd(mMaxScale - mMinScale);
            mRangebar.setTickStart(0);

        } else {
            mRangebar.setTickEnd(mMaxScale);
            mRangebar.setTickStart(mMinScale);
        }
        mRangebar.setTickInterval((float) 0.5);

        mRangebar.setRangeBarEnabled(mIsRange);
        if(mIsRange) {
            mRangebar.setRangePinsByValue(mPCMValueToRangeBarValue.get(mMinValue) + mMinScale, mPCMValueToRangeBarValue.get(mMaxValue) + mMinScale);
        } else {
            mRangebar.setSeekPinByValue(mPCMValueToRangeBarValue.get(mMinValue));
        }

        mRangebar.setPinRadius(75);
        mRangebar.setFormatter(new IRangeBarFormatter() {
            @Override
            public String format(String value) {
                if(!mStartsNegative) {
                    return numberFormat.format(Float.valueOf(value)) + " " + mUnit;
                } else {
                    return numberFormat.format(mRangeBarValueToPCMValue.get(Float.valueOf(value))) + " " + mUnit;
                }
            }
        });
        mRangebar.setLayoutParams(rbLayoutParams);
        container.addView(mRangebar);
    }

    @Override
    public String generateSaveJson() {
        return null;
    }
}

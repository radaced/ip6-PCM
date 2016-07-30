package ch.fhnw.ip6.powerconsumptionmanager.model.settings;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appyvet.rangebar.IRangeBarFormatter;
import com.appyvet.rangebar.RangeBar;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.zip.DeflaterInputStream;

import ch.fhnw.ip6.powerconsumptionmanager.R;


public class PCMSlider extends PCMSetting {
    private String mUnit;
    private float mMinScale, mMaxScale;
    private float mMinValue, mMaxValue;
    private boolean mIsRange;
    private boolean mStartsNegative;
    private HashMap<Float, Float> mScaleMap = new HashMap<>();

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
            mScaleMap.put(mMinScale + i, (float) i);
            mScaleMap.put((float) (mMinScale + i + 0.5), (float) (i + 0.5));
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
            mRangebar.setRangePinsByValue(mScaleMap.get(mMinValue) + mMinScale, mScaleMap.get(mMaxValue) + mMinScale);
        } else {
            mRangebar.setSeekPinByValue(mScaleMap.get(mMinValue) + mMinScale);
        }

        mRangebar.setPinRadius(75);
        mRangebar.setFormatter(new IRangeBarFormatter() {
            @Override
            public String format(String value) {
                return numberFormat.format(Double.valueOf(value)) + " " + mUnit;
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

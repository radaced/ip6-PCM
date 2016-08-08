package ch.fhnw.ip6.powerconsumptionmanager.model.settings;

import android.content.Context;
import android.widget.LinearLayout;

import com.appyvet.rangebar.IRangeBarFormatter;
import com.appyvet.rangebar.RangeBar;

import java.text.NumberFormat;
import java.util.HashMap;

/**
 * Represents a slider with one or to thumbs as a setting from the PCM.
 */
public class PCMSlider extends PCMSetting {
    private String mUnit;
    private float mMinScale, mMaxScale;
    private float mMinValue, mMaxValue;
    private boolean mIsRange;
    private boolean mStartsNegative;
    private HashMap<Float, Float> mPCMValueToRangeBarValue = new HashMap<>();
    private HashMap<Float, Float> mRangeBarValueToPCMValue = new HashMap<>();
    private RangeBar mRangebar;

    /**
     * Constructor to create a new slider setting.
     * @param name Name of the setting.
     * @param unit Unit of the modifiable values.
     * @param minScale The minimum value that is selectable.
     * @param maxScale The maximum value that is selectable.
     * @param minValue The minimum value that is set currently on the PCM for this setting.
     * @param maxValue The maximum value that is set currently on the PCM for this setting. When the slider
     *                 only has one thumb the maximum value is undefined.
     * @param isRange Declares if the slider has two or just one thumb.
     */
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

        // Defines that the number displayed in the pin of the range bar has minimum one digit after the comma
        final NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMinimumFractionDigits(1);

        float density = context.getResources().getDisplayMetrics().density;

        LinearLayout.LayoutParams rbLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (int) (80 * density)
        );
        rbLayoutParams.setMargins(0, 0, 0, (int) (MARGIN_BOTTOM * density));

        /* Use this constructor new Rangebar(context, attributeset) specifically, otherwise an essential map from the library
         * gets not instantiated (mTickMap)!
         */
        mRangebar = new RangeBar(context, null);

        // The range bar library can't handle negative values so create two maps that map the range bar and PCM setting values
        for (int i = 0; i <= mMaxScale - mMinScale; i++) {
            mPCMValueToRangeBarValue.put(mMinScale + i, (float) i);
            mPCMValueToRangeBarValue.put((float) (mMinScale + i + 0.5), (float) (i + 0.5));
            mRangeBarValueToPCMValue.put((float) i, mMinScale + i);
            mRangeBarValueToPCMValue.put((float) (i + 0.5), (float) (mMinScale + i + 0.5));
        }

        // Set the minimum and maximum selectable value
        if(mStartsNegative) {
            mRangebar.setTickEnd(mMaxScale - mMinScale);
            mRangebar.setTickStart(0);

        } else {
            mRangebar.setTickEnd(mMaxScale);
            mRangebar.setTickStart(mMinScale);
        }
        // Set the tick interval
        mRangebar.setTickInterval((float) 0.5);
        // Define if the slider is a seek bar or a range bar (one or two thumbs)
        mRangebar.setRangeBarEnabled(mIsRange);
        if(mIsRange) {
            mRangebar.setRangePinsByValue(mPCMValueToRangeBarValue.get(mMinValue) + mMinScale, mPCMValueToRangeBarValue.get(mMaxValue) + mMinScale);
        } else {
            mRangebar.setSeekPinByValue(mPCMValueToRangeBarValue.get(mMinValue));
        }
        // Set the radius of the pin where the selected value is being shown
        mRangebar.setPinRadius(75);
        // Format the value accordingly
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

        // Add the generated slider layout to the main layout container
        container.addView(mRangebar);
    }

    @Override
    public String executeSaveOrGenerateJson(Context context) {
        // String builder to build JSON
        StringBuilder jsonStringBuilder = new StringBuilder(1000);
        String minValue, maxValue;
        String isRangeThenMax = "";

        // Get the correct values from the range bar
        if(mIsRange) {
            if(mStartsNegative) {
                Float minFloat = mRangeBarValueToPCMValue.get(Float.parseFloat(mRangebar.getLeftPinValue()));
                Float maxFloat = mRangeBarValueToPCMValue.get(Float.parseFloat(mRangebar.getRightPinValue()));
                minValue = minFloat.toString();
                maxValue = maxFloat.toString();
            } else {
                minValue = mRangebar.getLeftPinValue();
                maxValue = mRangebar.getRightPinValue();
            }
            isRangeThenMax = ", \"Max\": \"" + maxValue  + "\"";
        } else {
            if (mStartsNegative) {
                Float minFloat = mRangeBarValueToPCMValue.get(Float.parseFloat(mRangebar.getRightPinValue()));
                minValue = minFloat.toString();
            } else {
                minValue = mRangebar.getRightPinValue();
            }
        }

        // Build JSON with the new values
        String sliderData = "{" +
                            "\"Signal\": \"" + super.getName() + "\"," +
                            "\"Min\": \"" + minValue + "\"" +
                            isRangeThenMax +
                            "}";

        return jsonStringBuilder.append(sliderData).toString();
    }
}

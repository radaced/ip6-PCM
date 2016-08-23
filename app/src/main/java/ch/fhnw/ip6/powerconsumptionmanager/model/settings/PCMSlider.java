package ch.fhnw.ip6.powerconsumptionmanager.model.settings;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appyvet.rangebar.IRangeBarFormatter;
import com.appyvet.rangebar.RangeBar;

import java.text.NumberFormat;
import java.util.HashMap;

import ch.fhnw.ip6.powerconsumptionmanager.R;

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
    private TextView tvValue1, tvValue2;

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
        if(density > 2) { //xxhdpi and xxxhdpi
            mRangebar.setPinRadius(55);
        } else if(density > 1) { //xhdpi and hdpi
            mRangebar.setPinRadius(30);
        } else { //mdpi and ldpi
            mRangebar.setPinRadius(15);
        }

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

        LinearLayout.LayoutParams llValueDisplayLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        llValueDisplayLayoutParams.setMargins(0, 0, 0, (int) (MARGIN_BOTTOM * density));

        LinearLayout llValueDisplay = new LinearLayout(context);
        llValueDisplay.setOrientation(LinearLayout.HORIZONTAL);
        llValueDisplay.setLayoutParams(llValueDisplayLayoutParams);

        LinearLayout.LayoutParams tvLayoutParams = new LinearLayout.LayoutParams(
                50 * (int) density,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        LinearLayout.LayoutParams llValueLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1
        );

        LinearLayout llValue1 = new LinearLayout(context);
        llValue1.setOrientation(LinearLayout.HORIZONTAL);
        llValue1.setGravity(Gravity.CENTER_HORIZONTAL);
        llValue1.setLayoutParams(llValueLayoutParams);

        final TextView tvLabelValue1 = new TextView(context);
        tvLabelValue1.setText(context.getString(R.string.text_value));
        tvLabelValue1.setTextSize(15);
        tvLabelValue1.setTextColor(ContextCompat.getColor(context, R.color.colorTextPrimary));
        tvLabelValue1.setLayoutParams(tvLayoutParams);

        tvValue1 = new TextView(context);
        tvValue1.setText(mMinValue + "");
        tvValue1.setTextSize(15);
        tvValue1.setTextColor(ContextCompat.getColor(context, R.color.colorTextPrimary));
        tvValue1.setLayoutParams(tvLayoutParams);

        llValue1.addView(tvLabelValue1);
        llValue1.addView(tvValue1);

        llValueDisplay.addView(llValue1);

        if(mIsRange) {
            tvLabelValue1.setText(context.getString(R.string.text_min));

            LinearLayout llValue2 = new LinearLayout(context);
            llValue2.setOrientation(LinearLayout.HORIZONTAL);
            llValue2.setGravity(Gravity.CENTER_HORIZONTAL);
            llValue2.setLayoutParams(llValueLayoutParams);

            TextView tvLabelValue2 = new TextView(context);
            tvLabelValue2.setText(context.getString(R.string.text_max));
            tvLabelValue2.setTextSize(15);
            tvLabelValue2.setTextColor(ContextCompat.getColor(context, R.color.colorTextPrimary));
            tvLabelValue2.setLayoutParams(tvLayoutParams);

            tvValue2 = new TextView(context);
            tvValue2.setText(mMaxValue + "");
            tvValue2.setTextSize(15);
            tvValue2.setTextColor(ContextCompat.getColor(context, R.color.colorTextPrimary));
            tvValue2.setLayoutParams(tvLayoutParams);

            llValue2.addView(tvLabelValue2);
            llValue2.addView(tvValue2);

            llValueDisplay.addView(llValue2);
        }

        mRangebar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
                if(mIsRange) {
                    if(mStartsNegative) {
                        tvValue1.setText(mRangeBarValueToPCMValue.get(Float.parseFloat(mRangebar.getLeftPinValue())).toString());
                        tvValue2.setText(mRangeBarValueToPCMValue.get(Float.parseFloat(mRangebar.getRightPinValue())).toString());
                    } else {
                        tvValue1.setText(mRangebar.getLeftPinValue());
                        tvValue2.setText(mRangebar.getRightPinValue());
                    }
                } else {
                    if(mStartsNegative) {
                        tvValue1.setText(mRangeBarValueToPCMValue.get(Float.parseFloat(mRangebar.getRightPinValue())).toString());
                    } else {
                        tvValue1.setText(mRangebar.getRightPinValue());
                    }
                }
            }
        });

        // Add the generated slider layout to the main layout container
        container.addView(mRangebar);
        container.addView(llValueDisplay);
    }

    @Override
    public String executeSaveOrGenerateJson(Context context) {
        String minValue, maxValue;

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
        } else {
            if(mStartsNegative) {
                Float minFloat = mRangeBarValueToPCMValue.get(Float.parseFloat(mRangebar.getRightPinValue()));
                minValue = minFloat.toString();
                maxValue = minFloat.toString();
            } else {
                minValue = mRangebar.getRightPinValue();
                maxValue = mRangebar.getRightPinValue();
            }
        }

        // Build JSON with the new values
        String sliderData = "{" +
                "\"Signal\": \"" + super.getName() + "\"," +
                "\"Min\": " + minValue + "," +
                "\"Max\": " + maxValue + "" +
                "}";

        return sliderData;
    }
}

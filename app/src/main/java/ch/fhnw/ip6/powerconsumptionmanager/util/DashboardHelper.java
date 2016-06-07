package ch.fhnw.ip6.powerconsumptionmanager.util;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gigamole.library.ArcProgressStackView;
import com.gigamole.library.ArcProgressStackView.IndicatorOrientation;
import com.gigamole.library.ArcProgressStackView.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.view.OverviewFragment;
import me.itangqi.waveloadingview.WaveLoadingView;

public class DashboardHelper {
    private static DashboardHelper mInstance;

    private static final AtomicInteger GENERATED_ID = new AtomicInteger(1);
    private static final HashMap<String, Integer> GENERATED_ARCSV_IDS = new HashMap<>();
    private static final HashMap<String, Integer> GENERATED_POWER_IDS = new HashMap<>();
    private static final HashMap<String, Integer> GENERATED_LABELCONTAINER_IDS = new HashMap<>();

    private Context mContext;
    private HashMap<String, ArcProgressStackView> mComponentViews;
    private HashMap<String, WaveLoadingView> mSummaryViews;
    private LinearLayout mDynamicLayoutContainer;
    private int mDynamicLayoutContainerWidth = 0;
    private int mDynamicLayoutContainerHeight = 0;
    private float mDensity;

    public DashboardHelper() {}

    public static synchronized DashboardHelper getInstance() {
        if(mInstance == null) {
            mInstance = new DashboardHelper();
        }

        return mInstance;
    }

    // Implementation from google
    public static int generateViewId() {
        for (;;) {
            final int result = GENERATED_ID.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (GENERATED_ID.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    public void init(Context c, LinearLayout ll) {
        mContext = c;
        mDynamicLayoutContainer = ll;
        mComponentViews = new HashMap<>();
        mSummaryViews = new HashMap<>();
        mDensity = mContext.getResources().getDisplayMetrics().density;
    }

    public void addSummaryView(String key, WaveLoadingView wlv, String unit) {
        wlv.setTopTitle(key);
        wlv.setBottomTitle("(" + unit + ")");
        mSummaryViews.put(key, wlv);
    }

    public void setSummaryRatio(String key, int ratio) {
        WaveLoadingView wlv = mSummaryViews.get(key);

        // TODO: make dynamic
        if(OverviewFragment.OCCUPATION.equals(key)) {
            if(ratio > 3) {
                wlv.setWaveColor(ContextCompat.getColor(mContext, R.color.colorProgressPositive));
            } else if (ratio < -3) {
                wlv.setWaveColor(ContextCompat.getColor(mContext, R.color.colorProgressNegative));
            } else {
                wlv.setWaveColor(ContextCompat.getColor(mContext, R.color.colorProgressNeutral));
            }
            int absRatio = Math.abs(ratio);
            int progress = (Math.signum((double) ratio) >= 0) ? (int) (50 + absRatio * 2.5) : (int) (50 - absRatio * 2.5);
            wlv.setProgressValue(progress);
        } else {
            wlv.setProgressValue(ratio);
        }
        wlv.setCenterTitle(String.valueOf(ratio));
    }

    public void updateSummaryRatio(String key, int ratio, String unit) {
        this.setSummaryRatio(key, ratio);
    }

    public void addComponentView(String key, ArcProgressStackView apsv) {
        mComponentViews.put(key, apsv);
    }

    public void setPowerForComponent(String key, ArrayList<Model> apsvModels) {
        mComponentViews.get(key).setModels(apsvModels);
    }

    public void updatePowerForComponent(String key, int power) {
        for(ArcProgressStackView.Model model : mComponentViews.get(key).getModels()) {
            model.setProgress(power);
        }
    }

    public ArrayList<Model> generateModelForComponent(String description, int progress, int bgProgressColor, int progressColor) {
        Model m = new Model(description, progress, bgProgressColor, progressColor);
        ArrayList<Model> models = new ArrayList<>();
        models.add(m);
        return models;
    }

    public void displayNonAnimated() {
        for (ArcProgressStackView apsv : mComponentViews.values()) {
            apsv.invalidate();
        }
    }

    public void displayAnimated() {
        for (ArcProgressStackView apsv : mComponentViews.values()) {
            apsv.animateProgress();
        }
    }

    public void generateDynamicComponentsLayout(String componentId, int color) {
        // RelativeLayout container
        RelativeLayout rlContainer = new RelativeLayout(mContext);
        LinearLayout.LayoutParams rlContainerLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        rlContainer.setLayoutParams(rlContainerLayoutParams);

        mDynamicLayoutContainer.addView(rlContainer);

        // ArcProgressStackView
        ArcProgressStackView arcsv = new ArcProgressStackView(mContext);
        arcsv.setId(handleNewId(componentId + "arcsvId", GENERATED_ARCSV_IDS));
        arcsv.setIsShadowed(true);
        arcsv.setShadowDistance(1);
        arcsv.setShadowRadius(2);
        arcsv.setIsAnimated(true);
        arcsv.setAnimationDuration(2000);
        arcsv.setIsDragged(false);
        arcsv.setTextColor(ContextCompat.getColor(mContext, android.R.color.transparent));
        arcsv.setDrawWidthFraction((float) 0.15);
        arcsv.setModelBgEnabled(true);
        arcsv.setStartAngle(135);
        arcsv.setSweepAngle(270);
        arcsv.setIndicatorOrientation(IndicatorOrientation.HORIZONTAL);
        this.addComponentView(componentId, arcsv);
        this.setPowerForComponent(
            componentId,
            this.generateModelForComponent(
                "",
                30, // TODO Progress
                ContextCompat.getColor(mContext, R.color.colorArcBackground),
                color
            )
        );

        /**
         * LayoutParams for the ArcProgressStackView are set after layout dimensions for parent container
         * have been set. See setupViewTreeObserver in OverviewFragment.
         */
        if(mDynamicLayoutContainerWidth != 0 && mDynamicLayoutContainerHeight != 0) {
            RelativeLayout.LayoutParams arcsvLayoutParams = new RelativeLayout.LayoutParams(
                    mDynamicLayoutContainerWidth,
                    mDynamicLayoutContainerHeight
            );
            arcsvLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            int margins = (int) mDensity * 8;
            arcsvLayoutParams.setMargins(margins, margins, margins, margins);
            arcsv.setLayoutParams(arcsvLayoutParams);
        }

        rlContainer.addView(arcsv);

        // LinearLayout for labels
        RelativeLayout.LayoutParams llLabelContainerLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        llLabelContainerLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        LinearLayout llLabelContainer = new LinearLayout(mContext);
        llLabelContainer.setId(handleNewId(componentId + "llLabelContainerId", GENERATED_LABELCONTAINER_IDS));
        llLabelContainer.setOrientation(LinearLayout.VERTICAL);
        llLabelContainer.setGravity(Gravity.CENTER);
        llLabelContainer.setLayoutParams(llLabelContainerLayoutParams);

        rlContainer.addView(llLabelContainer);

        // LinearLayout for power values
        LinearLayout.LayoutParams llValueLabelContainerLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        LinearLayout llValueLabelContainer = new LinearLayout(mContext);
        llValueLabelContainer.setOrientation(LinearLayout.HORIZONTAL);
        llValueLabelContainer.setLayoutParams(llValueLabelContainerLayoutParams);

        llLabelContainer.addView(llValueLabelContainer);

        // Textviews for values
        LinearLayout.LayoutParams tvValueLabelLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        tvValueLabelLayoutParams.gravity = Gravity.BOTTOM;

        TextView tvValue = new TextView(mContext);
        tvValue.setId(handleNewId(componentId + "powerId", GENERATED_POWER_IDS));
        tvValue.setText("5.6");
        tvValue.setTextSize(40);
        tvValue.setTextColor(ContextCompat.getColor(mContext, R.color.colorTextPrimary));
        tvValue.setLayoutParams(tvValueLabelLayoutParams);
        llValueLabelContainer.addView(tvValue);

        TextView tvValueUnit = new TextView(mContext);
        tvValueUnit.setText("kW");
        tvValueUnit.setTextSize(10);
        tvValueUnit.setTextColor(ContextCompat.getColor(mContext, R.color.colorTextPrimary));
        tvValueUnit.setLayoutParams(tvValueLabelLayoutParams);
        llValueLabelContainer.addView(tvValueUnit);

        // Textview for component description
        LinearLayout.LayoutParams tvComponentDescLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        tvComponentDescLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;

        TextView tvComponent = new TextView(mContext);
        tvComponent.setText(componentId);
        tvComponent.setTextSize(14);
        tvComponent.setTextColor(ContextCompat.getColor(mContext, R.color.colorTextPrimary));
        tvComponent.setLayoutParams(tvComponentDescLayoutParams);
        llLabelContainer.addView(tvComponent);
    }

    public HashMap<String, Integer> getArcsvIdsMap() {
        return GENERATED_ARCSV_IDS;
    }

    public int getDynamicLayoutContainerWidth() {
        return mDynamicLayoutContainerWidth;
    }

    public void setDynamicLayoutContainerWidth(int mDynamicLayoutContainerWidth) {
        this.mDynamicLayoutContainerWidth = mDynamicLayoutContainerWidth;
    }

    public int getDynamicLayoutContainerHeight() {
        return mDynamicLayoutContainerHeight;
    }

    public void setDynamicLayoutContainerHeight(int mDynamicLayoutContainerHeight) {
        this.mDynamicLayoutContainerHeight = mDynamicLayoutContainerHeight;
    }

    public float getDensity() {
        return mDensity;
    }

    private int handleNewId(String key, HashMap<String, Integer> map) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            map.put(key, generateViewId());
        } else {
            map.put(key, View.generateViewId());
        }
        return map.get(key);
    }
}

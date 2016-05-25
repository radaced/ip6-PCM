package ch.fhnw.ip6.powerconsumptionmanager.util;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gigamole.library.ArcProgressStackView;
import com.gigamole.library.ArcProgressStackView.IndicatorOrientation;
import com.gigamole.library.ArcProgressStackView.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import ch.fhnw.ip6.powerconsumptionmanager.R;

public class DashboardHelper {
    private static final AtomicInteger GENERATED_ID = new AtomicInteger(1);
    private static final HashMap<String, Integer> GENERATED_ARCSV_IDS = new HashMap<>();
    private static final HashMap<String, Integer> GENERATED_POWER_IDS = new HashMap<>();
    private static final HashMap<String, Integer> GENERATED_VALUECONTAINER_IDS = new HashMap<>();

    private HashMap<String, ArcProgressStackView> mComponentViews;
    private HashMap<String, ProgressBar> mVerticalProgressbars;
    private LinearLayout mLinearLayout;
    private Context mContext;
    private float mDensity;

    public DashboardHelper(Context c, LinearLayout ll) {
        mContext = c;
        mLinearLayout = ll;
        mComponentViews = new HashMap<>();
        mVerticalProgressbars = new HashMap<>();
        mDensity = mContext.getResources().getDisplayMetrics().density;
    }

    public HashMap<String, Integer> getArcsvIdsMap() {
        return GENERATED_ARCSV_IDS;
    }

    public float getDensity() {
        return mDensity;
    }

    public void addVerticalProgressbar(String key, ProgressBar pb) { mVerticalProgressbars.put(key, pb); }

    public void setVerticalProgress(String key, int progress) {
        ProgressBar pb = mVerticalProgressbars.get(key);
        double lowerMiddle = pb.getMax()/2 - pb.getMax() * 0.05;
        double upperMiddle = pb.getMax()/2 + pb.getMax() * 0.05;

        if(key.equals("Bezug")) {
            if(progress <= lowerMiddle) {
                pb.setProgressDrawable(ContextCompat.getDrawable(mContext, R.drawable.progressbar_vertical_positive));
            } else if(progress > lowerMiddle && progress < upperMiddle) {
                pb.setProgressDrawable(ContextCompat.getDrawable(mContext, R.drawable.progressbar_vertical_neutral));
            } else {
                pb.setProgressDrawable(ContextCompat.getDrawable(mContext, R.drawable.progressbar_vertical_negative));
            }
        }

        pb.setProgress(progress);
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

        // ArcProgressStackView
        ArcProgressStackView arcsv = new ArcProgressStackView(mContext);
        arcsv.setId(handleNewId(componentId + "arcsvId", GENERATED_ARCSV_IDS));
        arcsv.setIsShadowed(true);
        arcsv.setShadowDistance(1);
        arcsv.setShadowRadius(2);
        arcsv.setIsAnimated(true);
        arcsv.setAnimationDuration(2000);
        arcsv.setIsDragged(false);
        arcsv.setTextColor(color);
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
         * LayoutParams for the ArcProgressStackView is set after layout dimensions for parent container have been set.
         * See setupViewTreeObserver in OverviewFragment.
         */

        rlContainer.addView(arcsv);

        // LinearLayout for values
        LinearLayout llValueContainer = new LinearLayout(mContext);
        llValueContainer.setId(handleNewId(componentId + "llValueContainerId", GENERATED_VALUECONTAINER_IDS));
        llValueContainer.setOrientation(LinearLayout.HORIZONTAL);
        llValueContainer.setGravity(Gravity.CENTER);

        RelativeLayout.LayoutParams llValueContainerLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        llValueContainerLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        llValueContainer.setLayoutParams(llValueContainerLayoutParams);

        // Textviews for values
        TextView tvValue = new TextView(mContext);
        tvValue.setId(handleNewId(componentId + "powerId", GENERATED_POWER_IDS));
        tvValue.setText("5.6");
        tvValue.setTextSize(25);
        tvValue.setTextColor(ContextCompat.getColor(mContext, R.color.colorTextPrimary));

        TextView tvValueMax = new TextView(mContext);
        tvValueMax.setText("/6.0 kW");
        tvValueMax.setTextSize(10);
        tvValueMax.setTextColor(ContextCompat.getColor(mContext, R.color.colorTextPrimary));

        LinearLayout.LayoutParams tvValueLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        tvValueLayoutParams.gravity = Gravity.BOTTOM;

        tvValue.setLayoutParams(tvValueLayoutParams);
        tvValueMax.setLayoutParams(tvValueLayoutParams);

        llValueContainer.addView(tvValue);
        llValueContainer.addView(tvValueMax);

        rlContainer.addView(llValueContainer);

        // Textview component description
        TextView tvComponent = new TextView(mContext);
        tvComponent.setText(componentId);
        tvComponent.setTextSize(14);
        tvComponent.setTextColor(ContextCompat.getColor(mContext, R.color.colorTextPrimary));

        RelativeLayout.LayoutParams tvComponentLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        tvComponentLayoutParams.setMargins(0, (int) mDensity * 35, 0, 0);
        tvComponentLayoutParams.addRule(RelativeLayout.BELOW, GENERATED_VALUECONTAINER_IDS.get(componentId + "llValueContainerId"));
        tvComponentLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        tvComponent.setLayoutParams(tvComponentLayoutParams);

        rlContainer.addView(tvComponent);

        mLinearLayout.addView(rlContainer);
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

    private int handleNewId(String key, HashMap<String, Integer> map) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            map.put(key, generateViewId());
        } else {
            map.put(key, View.generateViewId());
        }
        return map.get(key);
    }
}

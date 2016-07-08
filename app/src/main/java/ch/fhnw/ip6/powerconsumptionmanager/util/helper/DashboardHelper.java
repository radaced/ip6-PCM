package ch.fhnw.ip6.powerconsumptionmanager.util.helper;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gigamole.library.ArcProgressStackView;
import com.gigamole.library.ArcProgressStackView.Model;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.model.dashboard.CurrentPCMComponentData;
import ch.fhnw.ip6.powerconsumptionmanager.model.dashboard.CurrentPCMData;
import ch.fhnw.ip6.powerconsumptionmanager.util.formatter.CostValueFormatter;
import ch.fhnw.ip6.powerconsumptionmanager.util.formatter.EnergyValueFormatter;
import ch.fhnw.ip6.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;
import me.itangqi.waveloadingview.WaveLoadingView;

public class DashboardHelper {
    private static DashboardHelper mInstance;

    private PowerConsumptionManagerAppContext mAppContext;
    private Context mOverviewContext;
    private Context mCurrentValuesContext;
    private Context mDailyValuesContext;

    private HashMap<String, ArcProgressStackView> mComponentViews = new HashMap<>();
    private HashMap<String, TextView> mComponentPowerLabels = new HashMap<>();
    private HashMap<String, WaveLoadingView> mSummaryViews = new HashMap<>();
    private DecimalFormat mOneDigitAfterCommaFormat = new DecimalFormat("#.#");
    private float mDensity;

    private LinearLayout mDynamicLayoutContainer;
    private int mDynamicLayoutContainerWidth = 0;
    private int mDynamicLayoutContainerHeight = 0;

    private BarChart mBCDailyData;

    public DashboardHelper() {}


    /**
     * SINGLETON
     */
    public static synchronized DashboardHelper getInstance() {
        if(mInstance == null) {
            mInstance = new DashboardHelper();
        }

        return mInstance;
    }



    /**
     * CONTEXTS
     */
    public void initOverviewContext(Context c) {
        mOverviewContext = c;
        mAppContext = (PowerConsumptionManagerAppContext) mOverviewContext.getApplicationContext();
        mDensity = mOverviewContext.getResources().getDisplayMetrics().density;
        mOneDigitAfterCommaFormat.setRoundingMode(RoundingMode.HALF_UP);
    }

    public void initCurrentValuesContext(Context c) {
        mCurrentValuesContext = c;
    }

    public void initDailyValuesContext(Context c) {
        mDailyValuesContext = c;
    }



    /**
     * OVERVIEW FRAGMENT
     */
    public void addSummaryView(String key, WaveLoadingView wlv, String unit) {
        wlv.setTopTitle(key);
        wlv.setBottomTitle("(" + unit + ")");
        mSummaryViews.put(key, wlv);
    }

    public void setSummaryRatio(String key, double ratio) {
        WaveLoadingView wlv = mSummaryViews.get(key);

        double progress;
        if(mOverviewContext.getString(R.string.text_consumption).equals(key)) {
            wlv.setWaveColor(mAppContext.getCurrentPCMData().getConsumptionColor());
            // TODO: Make dynamic (currently fixed scale with +15 to -15 --> 3.3)
            int absRatio = (int) Math.abs(ratio);
            progress = (Math.signum(ratio) >= 0) ? (int) (50 + absRatio * 3.3) : (int) (50 - absRatio * 2.5);
            progress = Math.round(progress);
        } else {
            progress = Math.round(ratio);
        }

        wlv.setProgressValue((int) progress);
        wlv.setCenterTitle(String.valueOf((int) Math.round(ratio)));
    }

    public void updateSummaryRatio(String key, double ratio) {
        this.setSummaryRatio(key, ratio);
    }

    public void updateOverview() {
        CurrentPCMData currentData = mAppContext.getCurrentPCMData();

        updateSummaryRatio(mOverviewContext.getString(R.string.text_autarchy), currentData.getAutarchy());
        updateSummaryRatio(mOverviewContext.getString(R.string.text_selfsupply), currentData.getSelfsupply());
        updateSummaryRatio(mOverviewContext.getString(R.string.text_consumption), currentData.getConsumption());
    }



    /**
     * CURRENTVALUES FRAGMENT
     */
    public void addComponentView(String key, ArcProgressStackView apsv) {
        mComponentViews.put(key, apsv);
    }

    public void addComponentPowerLabel(String key, TextView tv) {
        mComponentPowerLabels.put(key, tv);
    }

    public void setPowerForComponent(String key, ArrayList<Model> apsvModels) {
        mComponentViews.get(key).setModels(apsvModels);
    }

    public void updatePowerForComponent(String key, double power) {
        for(ArcProgressStackView.Model model : mComponentViews.get(key).getModels()) {
            model.setProgress((int) power);
        }
    }

    public void updatePowerLabel(final String key, double power) {
        TextView powerLabel = mComponentPowerLabels.get(key);

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(Float.parseFloat(powerLabel.getText().toString()), (float) power);
        valueAnimator.setDuration(2000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mComponentPowerLabels.get(key).setText(mOneDigitAfterCommaFormat.format(valueAnimator.getAnimatedValue()));
            }
        });

        valueAnimator.start();
    }

    public void updateCurrentValues() {
        LinkedHashMap<String, CurrentPCMComponentData> dataMap = mAppContext.getCurrentPCMData().getCurrentComponentData();

        for(Map.Entry<String, CurrentPCMComponentData> entry : dataMap.entrySet()) {
            /* TODO: Make dynamic (currently fixed scale with 0 to 10 kW) */
            updatePowerForComponent(entry.getKey(), entry.getValue().getPower() * 10);
            updatePowerLabel(entry.getKey(), entry.getValue().getPower());
        }

        displayAnimated();
    }

    public ArrayList<Model> generateModelForComponent(String description, int progress, int bgProgressColor, int progressColor) {
        Model m = new Model(description, progress, bgProgressColor, progressColor);
        ArrayList<Model> models = new ArrayList<>();
        models.add(m);
        return models;
    }

    public void displayAnimated() {
        for (ArcProgressStackView apsv : mComponentViews.values()) {
            apsv.animateProgress();
        }
    }

    public void generateComponentUIElement(String componentId, int color) {
        CurrentPCMComponentData componentData = mAppContext.getCurrentPCMData().getCurrentComponentData().get(componentId);

        // RelativeLayout container
        RelativeLayout rlContainer = new RelativeLayout(mCurrentValuesContext);
        LinearLayout.LayoutParams rlContainerLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        rlContainer.setLayoutParams(rlContainerLayoutParams);

        mDynamicLayoutContainer.addView(rlContainer);

        // ArcProgressStackView
        ArcProgressStackView arcsv = new ArcProgressStackView(mCurrentValuesContext);
        // If shadowing is enabled this can result in nasty performance issues!
        arcsv.setIsShadowed(false);
        //arcsv.setShadowDistance(1);
        //arcsv.setShadowRadius(2);
        arcsv.setIsAnimated(true);
        arcsv.setAnimationDuration(2000);
        arcsv.setIsDragged(false);
        arcsv.setTextColor(ContextCompat.getColor(mCurrentValuesContext, android.R.color.transparent));
        arcsv.setDrawWidthFraction((float) 0.17);
        arcsv.setModelBgEnabled(true);
        arcsv.setStartAngle(135);
        arcsv.setSweepAngle(270);
        this.addComponentView(componentId, arcsv);
        this.setPowerForComponent(
            componentId,
            this.generateModelForComponent(
                "",
                /* TODO: Make dynamic (currently fixed scale with 0 to 10 kW) */
                (int) componentData.getPower() * 10,
                ContextCompat.getColor(mCurrentValuesContext, R.color.colorArcBackground),
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

        LinearLayout llLabelContainer = new LinearLayout(mCurrentValuesContext);
        llLabelContainer.setOrientation(LinearLayout.VERTICAL);
        llLabelContainer.setGravity(Gravity.CENTER);
        llLabelContainer.setLayoutParams(llLabelContainerLayoutParams);

        rlContainer.addView(llLabelContainer);

        // LinearLayout for power values
        LinearLayout.LayoutParams llValueLabelContainerLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        LinearLayout llValueLabelContainer = new LinearLayout(mCurrentValuesContext);
        llValueLabelContainer.setOrientation(LinearLayout.HORIZONTAL);
        llValueLabelContainer.setLayoutParams(llValueLabelContainerLayoutParams);

        llLabelContainer.addView(llValueLabelContainer);

        // Textviews for values
        LinearLayout.LayoutParams tvValueLabelLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        tvValueLabelLayoutParams.gravity = Gravity.BOTTOM;

        TextView tvValue = new TextView(mCurrentValuesContext);
        tvValue.setText(mOneDigitAfterCommaFormat.format(componentData.getPower()));
        tvValue.setTextSize(40);
        tvValue.setTextColor(ContextCompat.getColor(mCurrentValuesContext, R.color.colorTextPrimary));
        tvValue.setLayoutParams(tvValueLabelLayoutParams);
        this.addComponentPowerLabel(componentId, tvValue);

        llValueLabelContainer.addView(tvValue);

        TextView tvValueUnit = new TextView(mCurrentValuesContext);
        tvValueUnit.setText(mAppContext.UNIT_KW);
        tvValueUnit.setTextSize(10);
        tvValueUnit.setTextColor(ContextCompat.getColor(mCurrentValuesContext, R.color.colorTextPrimary));
        tvValueUnit.setLayoutParams(tvValueLabelLayoutParams);

        llValueLabelContainer.addView(tvValueUnit);

        // Textview for component description
        LinearLayout.LayoutParams tvComponentDescLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        tvComponentDescLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;

        TextView tvComponent = new TextView(mCurrentValuesContext);
        tvComponent.setText(componentId);
        tvComponent.setTextSize(14);
        tvComponent.setTextColor(ContextCompat.getColor(mCurrentValuesContext, R.color.colorTextPrimary));
        tvComponent.setLayoutParams(tvComponentDescLayoutParams);
        llLabelContainer.addView(tvComponent);
    }



    /**
     * DAILYVALUES FRAGMENT
     */
    public void setupDailyBarChartStyle() {
        // Set looks
        mBCDailyData.setBackgroundColor(ContextCompat.getColor(mDailyValuesContext, R.color.colorBackground));
        mBCDailyData.setDrawGridBackground(false);
        mBCDailyData.setDrawBorders(false);
        mBCDailyData.setDescription(null);
        mBCDailyData.setNoDataText(mDailyValuesContext.getString(R.string.chart_no_data));
        mBCDailyData.getPaint(Chart.PAINT_INFO).setColor(ContextCompat.getColor(mDailyValuesContext, R.color.colorTextPrimary));
        mBCDailyData.setMinimumWidth((int) (mAppContext.getComponents().size() * mDensity * 120));
        mBCDailyData.setHighlightPerTapEnabled(false);
        mBCDailyData.setHighlightPerDragEnabled(false);

        YAxis leftAxis = mBCDailyData.getAxisLeft();
        leftAxis.setDrawAxisLine(false);
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawZeroLine(true);
        leftAxis.setZeroLineColor(Color.WHITE);
        leftAxis.setTextColor(ContextCompat.getColor(mDailyValuesContext, R.color.colorTextPrimary));
        leftAxis.setSpaceTop(40);

        mBCDailyData.getAxisRight().setEnabled(false);

        XAxis xAxis = mBCDailyData.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
        xAxis.setLabelsToSkip(0);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(true);
        xAxis.setGridColor(Color.WHITE);
        xAxis.setTextColor(ContextCompat.getColor(mDailyValuesContext, R.color.colorTextPrimary));
        xAxis.setTextSize(12f);

        Legend l = mBCDailyData.getLegend();
        l.setPosition(Legend.LegendPosition.ABOVE_CHART_LEFT);
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setTextColor(ContextCompat.getColor(mDailyValuesContext, R.color.colorTextPrimary));

        // Set functionality
        mBCDailyData.setDoubleTapToZoomEnabled(true);
        mBCDailyData.setTouchEnabled(true);
        mBCDailyData.setDragEnabled(true);
        mBCDailyData.setScaleEnabled(true);
        mBCDailyData.setPinchZoom(true);

        // Set marker view
        //MarkerView mv = new BarChartMarkerView(mDailyValuesContext, R.layout.barchart_markerview);
        //mBCDailyData.setMarkerView(mv);
    }

    public void setupDailyBarChartData() {
        LinkedHashMap<String, CurrentPCMComponentData> dataMap = mAppContext.getCurrentPCMData().getCurrentComponentData();
        ArrayList<String> xValues = new ArrayList<>(dataMap.keySet());
        ArrayList<BarEntry> yValuesEnergy = new ArrayList<>();
        ArrayList<BarEntry> yValuesCost = new ArrayList<>();

        fillDataSets(dataMap, yValuesEnergy, yValuesCost);

        BarDataSet energySet, costSet;
        energySet = new BarDataSet(yValuesEnergy, "Energy");
        energySet.setColor(ContextCompat.getColor(mDailyValuesContext, R.color.colorChartEnergyBar));
        energySet.setValueFormatter(new EnergyValueFormatter());
        energySet.setValueTextColor(ContextCompat.getColor(mDailyValuesContext, R.color.colorTextPrimary));
        energySet.setValueTextSize(10f);
        costSet = new BarDataSet(yValuesCost, "Costs");
        costSet.setColor(ContextCompat.getColor(mDailyValuesContext, R.color.colorChartCostBar));
        costSet.setValueFormatter(new CostValueFormatter());
        costSet.setValueTextColor(ContextCompat.getColor(mDailyValuesContext, R.color.colorTextPrimary));
        costSet.setValueTextSize(10f);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(energySet);
        dataSets.add(costSet);

        BarData data = new BarData(xValues, dataSets);
        data.setGroupSpace(50f);

        mBCDailyData.setData(data);
        mBCDailyData.animateY(3000);
    }

    public void updateDailyValues() {
        if(mBCDailyData.getData() != null && mBCDailyData.getData().getDataSetCount() > 0) {
            LinkedHashMap<String, CurrentPCMComponentData> dataMap = mAppContext.getCurrentPCMData().getCurrentComponentData();
            ArrayList<BarEntry> yValuesEnergy = new ArrayList<>();
            ArrayList<BarEntry> yValuesCost = new ArrayList<>();

            fillDataSets(dataMap, yValuesEnergy, yValuesCost);

            BarDataSet energySet, costSet;
            energySet = (BarDataSet) mBCDailyData.getData().getDataSetByIndex(0);
            costSet = (BarDataSet) mBCDailyData.getData().getDataSetByIndex(1);
            energySet.setYVals(yValuesEnergy);
            costSet.setYVals(yValuesCost);

            mBCDailyData.getData().notifyDataChanged();
            mBCDailyData.notifyDataSetChanged();
            mBCDailyData.invalidate();
        } else {
            setupDailyBarChartData();
        }
    }

    private void fillDataSets(LinkedHashMap<String, CurrentPCMComponentData> dataMap, ArrayList<BarEntry> yValuesEnergy, ArrayList<BarEntry> yValuesCost) {
        int i = 0;
        for (Map.Entry<String, CurrentPCMComponentData> entry : dataMap.entrySet()) {
            yValuesEnergy.add(new BarEntry((float) entry.getValue().getEnergy(), i));
            yValuesCost.add(new BarEntry((float) entry.getValue().getCost(), i++));
        }
    }



    /**
     * HELPER FUNCTIONS
     */
    public void setDynamicLayoutContainer(LinearLayout ll) {
        this.mDynamicLayoutContainer = ll;
    }

    public void setDailyDataBarChart(BarChart bc) {
        mBCDailyData = bc;
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

    public HashMap<String, ArcProgressStackView> getComponentViews() {
        return mComponentViews;
    }

    public float getDensity() {
        return mDensity;
    }
}

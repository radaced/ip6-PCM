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
import ch.fhnw.ip6.powerconsumptionmanager.model.PCMComponent;
import ch.fhnw.ip6.powerconsumptionmanager.model.PCMData;
import ch.fhnw.ip6.powerconsumptionmanager.util.formatter.CostValueFormatter;
import ch.fhnw.ip6.powerconsumptionmanager.util.formatter.EnergyValueFormatter;
import ch.fhnw.ip6.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;
import me.itangqi.waveloadingview.WaveLoadingView;

/**
 * Helper class to handle and modify all UI elements that are displayed on the dashboard.
 */
public class DashboardHelper {
    private static DashboardHelper mInstance;

    private PowerConsumptionManagerAppContext mAppContext;
    private Context mOverviewContext;
    private Context mCurrentValuesContext;
    private Context mDailyValuesContext;

    // Hash maps that hold references to the displayed UI elements
    private HashMap<String, ArcProgressStackView> mComponentViews = new HashMap<>();
    private HashMap<String, TextView> mComponentPowerLabels = new HashMap<>();
    private HashMap<String, WaveLoadingView> mSummaryViews = new HashMap<>();

    private DecimalFormat mOneDigitAfterCommaFormat = new DecimalFormat("#.#");
    private float mDensity;

    private LinearLayout mDynamicLayoutContainer;
    private int mDynamicLayoutContainerWidth = 0;
    private int mDynamicLayoutContainerHeight = 0;

    // The bar chart that displays the daily values
    private BarChart mBCDailyData;

    public DashboardHelper() {}

    /**
     * Constructor. Use this class as a singleton.
     * @return The single instance of this helper class.
     */
    public static synchronized DashboardHelper getInstance() {
        if(mInstance == null) {
            mInstance = new DashboardHelper();
        }

        return mInstance;
    }

    //////////////////////////////
    // CONTEXTS
    //////////////////////////////
    /**
     * Set the overview context and other important members of this helper instance.
     * @param c The context of UI elements in the overview fragment.
     */
    public void initOverviewContext(Context c) {
        mOverviewContext = c;
        mAppContext = (PowerConsumptionManagerAppContext) mOverviewContext.getApplicationContext();
        mDensity = mOverviewContext.getResources().getDisplayMetrics().density;
        mOneDigitAfterCommaFormat.setRoundingMode(RoundingMode.HALF_UP);
    }

    /**
     * Set the context for the current values fragment.
     * @param c The context of UI elements in the current values fragment.
     */
    public void initCurrentValuesContext(Context c) {
        mCurrentValuesContext = c;
    }

    /**
     * Set the context for the daily values fragment.
     * @param c The context of UI elements in the daily values fragment.
     */
    public void initDailyValuesContext(Context c) {
        mDailyValuesContext = c;
    }

    //////////////////////////////
    // OVERVIEW FRAGMENT
    //////////////////////////////
    /**
     * Add a summary view / wave loading view to the mSummaryViews hash map.
     * @param key The key or id to reference this summary view / wave loading view.
     * @param wlv The wave loading view widget.
     * @param unit The unit of the values that the wave loading view displays.
     */
    public void addSummaryView(String key, WaveLoadingView wlv, String unit) {
        wlv.setTopTitle(key);
        wlv.setBottomTitle("(" + unit + ")");
        mSummaryViews.put(key, wlv);
    }

    /**
     * Modifies the ratio (filling of circle) of a summary view / wave loading view.
     * @param key The key or id of the summary view / wave loading view to modify.
     * @param ratio The ratio to set to the summary view / wave loading view.
     */
    public void setSummaryRatio(String key, double ratio) {
        WaveLoadingView wlv = mSummaryViews.get(key);
        double progress = Math.round(ratio);
        wlv.setProgressValue((int) progress);
        wlv.setCenterTitle(String.valueOf((int) Math.round(ratio)));
    }

    /**
     * Modifies the ratio (filling of circle) of a summary view / wave loading view with custom a custom
     * scale (standard is 0 to 100).
     * @param key The key or id of the summary view / wave loading view to modify.
     * @param ratio The ratio to set to the summary view / wave loading view.
     * @param scaleMin Minimum of the scale.
     * @param scaleMax Maximum of the scale.
     */
    public void setSummaryRatio(String key, double ratio, int scaleMin, int scaleMax) {
        WaveLoadingView wlv = mSummaryViews.get(key);
        wlv.setWaveColor(mAppContext.getPCMData().getConsumptionColor());
        int absScale;
        if(scaleMin < 0) {
            if(scaleMax < 0) {
                absScale = scaleMax - scaleMin;
            } else {
                absScale = Math.abs(scaleMin) + scaleMax;
            }
        } else {
            absScale = scaleMax - scaleMin;
        }
        int absRatio = (int) Math.abs(ratio);
        double progress = (ratio >= scaleMin + (absScale / 2)) ?
                          (50 + absRatio * (100 / absScale)) :
                          (50 - absRatio * (100 / absScale));
        progress = Math.round(progress);

        wlv.setProgressValue((int) progress);
        wlv.setCenterTitle(String.valueOf((int) Math.round(ratio)));
    }

    /**
     * Updates the ratio (filling of circle) of a summary view / wave loading view.
     * @param key The key or id of the summary view / wave loading view to modify.
     * @param ratio The ratio to set to the summary view / wave loading view.
     */
    public void updateSummaryRatio(String key, double ratio) {
        this.setSummaryRatio(key, ratio);
    }

    /**
     * Updates the ratio (filling of circle) of a summary view / wave loading view with custom a custom
     * scale (standard is 0 to 100).
     * @param key The key or id of the summary view / wave loading view to modify.
     * @param ratio The ratio to set to the summary view / wave loading view.
     * @param scaleMin Minimum of the scale.
     * @param scaleMax Maximum of the scale.
     */
    public void updateSummaryRatio(String key, double ratio, int scaleMin, int scaleMax) {
        this.setSummaryRatio(key, ratio, scaleMin, scaleMax);
    }

    /**
     * Update all UI elements that are on the overview fragment and display them.
     */
    public void updateOverview() {
        PCMData pcmData = mAppContext.getPCMData();

        updateSummaryRatio(mOverviewContext.getString(R.string.text_autarchy), pcmData.getAutarchy());
        updateSummaryRatio(mOverviewContext.getString(R.string.text_selfsupply), pcmData.getSelfsupply());
        updateSummaryRatio(
            mOverviewContext.getString(R.string.text_consumption),
            pcmData.getConsumption(),
            pcmData.getMinScaleConsumption(),
            pcmData.getMaxScaleConsumption()
        );
    }

    //////////////////////////////
    // CURRENT VALUES FRAGMENT
    //////////////////////////////
    /**
     * Add an arc progress view to the mComponentViews hash map.
     * @param key The key or id to reference this arc progress view.
     * @param apsv The arc progress view reference.
     */
    public void addComponentView(String key, ArcProgressStackView apsv) {
        mComponentViews.put(key, apsv);
    }

    /**
     * Add a power label (text view) to the mComponentPowerLabels hash map.
     * @param key The key or id to reference this text view.
     * @param tv The text view reference.
     */
    public void addComponentPowerLabel(String key, TextView tv) {
        mComponentPowerLabels.put(key, tv);
    }

    /**
     * Sets the arc progress view values. In this implementation an arc progress view always only has one model
     * (see also generateModelForComponent).
     * @param key The key or id of the arc progress view to modify.
     * @param apsvModels The array list with generated models to add to the arc progress view.
     */
    public void setPowerForComponent(String key, ArrayList<Model> apsvModels) {
        mComponentViews.get(key).setModels(apsvModels);
    }

    /**
     * Updates the arc progress view values (see also generateModelForComponent).
     * @param key The key or id of the arc progress view to modify its models.
     * @param power The new progress (current power consumption/generation of component) of the arc progress view.
     */
    public void updatePowerForComponent(String key, double power) {
        for(ArcProgressStackView.Model model : mComponentViews.get(key).getModels()) {
            model.setProgress((int) power);
        }
    }

    /**
     * Updates the power label (text view) with the current consumption/generation of a component.
     * @param key The key or id of the text view to modify.
     * @param power The current power consumption/generation of the component.
     */
    public void updatePowerLabel(final String key, double power) {
        TextView powerLabel = mComponentPowerLabels.get(key);

        // Value animator to animate changes of the value
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

    /**
     * Generates ONE (!) model for every arc progress view that represents the current values of a component.
     * @param description Description to display on the arc progress view.
     * @param progress Progress of the arc progress view.
     * @param bgProgressColor Back ground color of the arc progress bar.
     * @param progressColor Color of the arc progress bar.
     * @return Model to add to the arc progress view (framework offers possibility to add multiple).
     */
    public ArrayList<Model> generateModelForComponent(String description, int progress, int bgProgressColor, int progressColor) {
        Model m = new Model(description, progress, bgProgressColor, progressColor);
        ArrayList<Model> models = new ArrayList<>();
        models.add(m);
        return models;
    }

    /**
     * Animated display of the progress of the arc progress views.
     */
    public void displayAnimated() {
        for (ArcProgressStackView apsv : mComponentViews.values()) {
            apsv.animateProgress();
        }
    }

    /**
     * Generates a widget to display the current values of a component.
     * @param componentId The id/name of the component.
     * @param color The color of the arc progress bar for this component.
     */
    public void generateComponentUIElement(String componentId, int color) {
        PCMComponent componentData = mAppContext.getPCMData().getComponentData().get(componentId);

        LinearLayout.LayoutParams rlContainerLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );

        // Relative layout container (1)
        RelativeLayout rlContainer = new RelativeLayout(mCurrentValuesContext);
        rlContainer.setLayoutParams(rlContainerLayoutParams);

        mDynamicLayoutContainer.addView(rlContainer);

        // Generate arc progress view
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
        int scaleMin = componentData.getScaleMinArc();
        int scaleMax = componentData.getScaleMaxArc();
        this.setPowerForComponent(
            componentId,
            this.generateModelForComponent(
                "",
                (int) componentData.getPower() * (100 / (scaleMax - scaleMin)),
                ContextCompat.getColor(mCurrentValuesContext, R.color.colorArcBackground),
                color
            )
        );

        /**
         * LayoutParams for the arc progress view are set after layout dimensions for parent container
         * have been set. See setupViewTreeObserver in overview fragment.
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

        // Add the arc progress view to (1)
        rlContainer.addView(arcsv);

        RelativeLayout.LayoutParams llLabelContainerLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        llLabelContainerLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        // Vertical linear layout container for labels (2)
        LinearLayout llLabelContainer = new LinearLayout(mCurrentValuesContext);
        llLabelContainer.setOrientation(LinearLayout.VERTICAL);
        llLabelContainer.setGravity(Gravity.CENTER);
        llLabelContainer.setLayoutParams(llLabelContainerLayoutParams);

        // Add label container to (1)
        rlContainer.addView(llLabelContainer);

        LinearLayout.LayoutParams llValueLabelContainerLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        // Horizontal linear layout container for value labels (3)
        LinearLayout llValueLabelContainer = new LinearLayout(mCurrentValuesContext);
        llValueLabelContainer.setOrientation(LinearLayout.HORIZONTAL);
        llValueLabelContainer.setLayoutParams(llValueLabelContainerLayoutParams);

        // Add value label container to (2)
        llLabelContainer.addView(llValueLabelContainer);

        LinearLayout.LayoutParams tvValueLabelLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        tvValueLabelLayoutParams.gravity = Gravity.BOTTOM;

        // Text view to display power of component
        TextView tvValue = new TextView(mCurrentValuesContext);
        tvValue.setText(mOneDigitAfterCommaFormat.format(componentData.getPower()));
        tvValue.setTextSize(40);
        tvValue.setTextColor(ContextCompat.getColor(mCurrentValuesContext, R.color.colorTextPrimary));
        tvValue.setLayoutParams(tvValueLabelLayoutParams);
        this.addComponentPowerLabel(componentId, tvValue);

        // Add text view to (3)
        llValueLabelContainer.addView(tvValue);

        // Text view to display unit
        TextView tvValueUnit = new TextView(mCurrentValuesContext);
        tvValueUnit.setText(mAppContext.getString(R.string.unit_kw));
        tvValueUnit.setTextSize(10);
        tvValueUnit.setTextColor(ContextCompat.getColor(mCurrentValuesContext, R.color.colorTextPrimary));
        tvValueUnit.setLayoutParams(tvValueLabelLayoutParams);

        // Add text view to (3)
        llValueLabelContainer.addView(tvValueUnit);

        LinearLayout.LayoutParams tvComponentDescLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        tvComponentDescLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;

        // Text view for component description
        TextView tvComponent = new TextView(mCurrentValuesContext);
        tvComponent.setText(componentId);
        tvComponent.setTextSize(14);
        tvComponent.setTextColor(ContextCompat.getColor(mCurrentValuesContext, R.color.colorTextPrimary));
        tvComponent.setLayoutParams(tvComponentDescLayoutParams);

        // Add text view to (2)
        llLabelContainer.addView(tvComponent);
    }

    /**
     * Update all UI elements that are on the current values fragment and display them.
     */
    public void updateCurrentValues() {
        LinkedHashMap<String, PCMComponent> dataMap = mAppContext.getPCMData().getComponentData();

        for(Map.Entry<String, PCMComponent> entry : dataMap.entrySet()) {
            if(entry.getValue().isDisplayedOnDashboard()) {
                int scaleMin = entry.getValue().getScaleMinArc();
                int scaleMax = entry.getValue().getScaleMaxArc();

                updatePowerForComponent(entry.getKey(), entry.getValue().getPower() * (100 / (scaleMax - scaleMin)));
                updatePowerLabel(entry.getKey(), entry.getValue().getPower());
            }
        }

        displayAnimated();
    }

    //////////////////////////////
    // DAILY VALUES FRAGMENT
    //////////////////////////////
    /**
     * Set up look and feel of the daily bar chart.
     */
    public void setupDailyBarChartStyle() {
        // Set looks
        mBCDailyData.setBackgroundColor(ContextCompat.getColor(mDailyValuesContext, R.color.colorBackground));
        mBCDailyData.setDrawGridBackground(false);
        mBCDailyData.setDrawBorders(false);
        mBCDailyData.setDescription(null);
        mBCDailyData.setNoDataText(mDailyValuesContext.getString(R.string.chart_no_data));
        mBCDailyData.getPaint(Chart.PAINT_INFO).setColor(ContextCompat.getColor(mDailyValuesContext, R.color.colorTextPrimary));
        mBCDailyData.setMinimumWidth((int) (mAppContext.getPCMData().getComponentData().size() * mDensity * 120));
        mBCDailyData.setHighlightPerTapEnabled(false);
        mBCDailyData.setHighlightPerDragEnabled(false);

        // Modify axis
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

        // Modify chart legend
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

    /**
     * Prepares and displays all daily data.
     */
    public void setupDailyBarChartData() {
        LinkedHashMap<String, PCMComponent> componentData = mAppContext.getPCMData().getComponentData();
        // Generate X-axis values
        ArrayList<String> xValues = new ArrayList<>();
        for (Map.Entry<String, PCMComponent> entry : componentData.entrySet()) {
            if(entry.getValue().isDisplayedOnDashboard()) {
                xValues.add(entry.getKey());
            }
        }
        // Lists to hold the Y-axis of both bars for energy and costs
        ArrayList<BarEntry> yValuesEnergy = new ArrayList<>();
        ArrayList<BarEntry> yValuesCost = new ArrayList<>();

        // Fill the above lists
        fillDataSets(componentData, yValuesEnergy, yValuesCost);

        // Setup the two bar chart data sets
        BarDataSet energySet, costSet;

        // Add the Y-axis value list to the bar data set and modify the looks
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

        // Add both data sets to one list
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(energySet);
        dataSets.add(costSet);

        BarData data = new BarData(xValues, dataSets);
        data.setGroupSpace(50f);

        // Add the data to the bar chart and display it
        mBCDailyData.setData(data);
        mBCDailyData.animateY(3000);
    }

    /**
     * Updates the daily values of every component and displays the data accordingly.
     */
    public void updateDailyValues() {
        // Check if data already exists in the bar chart
        if(mBCDailyData.getData() != null && mBCDailyData.getData().getDataSetCount() > 0) {
            LinkedHashMap<String, PCMComponent> componentData = mAppContext.getPCMData().getComponentData();
            ArrayList<BarEntry> yValuesEnergy = new ArrayList<>();
            ArrayList<BarEntry> yValuesCost = new ArrayList<>();

            fillDataSets(componentData, yValuesEnergy, yValuesCost);

            BarDataSet energySet, costSet;
            // Update the data sets
            energySet = (BarDataSet) mBCDailyData.getData().getDataSetByIndex(0);
            costSet = (BarDataSet) mBCDailyData.getData().getDataSetByIndex(1);
            energySet.setYVals(yValuesEnergy);
            costSet.setYVals(yValuesCost);

            // Notify chart that its data has changed
            mBCDailyData.getData().notifyDataChanged();
            mBCDailyData.notifyDataSetChanged();
            mBCDailyData.invalidate();
        } else {
            setupDailyBarChartData();
        }
    }

    /**
     * Fill the Y-axis value lists for energy and costs of a component.
     * @param dataMap The linked hash map with all connected components.
     * @param yValuesEnergy The list to hold all Y-axis values for the energy of a component.
     * @param yValuesCost The list to hold all Y-axis values for the costs of a component.
     */
    private void fillDataSets(LinkedHashMap<String, PCMComponent> dataMap, ArrayList<BarEntry> yValuesEnergy, ArrayList<BarEntry> yValuesCost) {
        int i = 0;
        for (Map.Entry<String, PCMComponent> entry : dataMap.entrySet()) {
            if(entry.getValue().isDisplayedOnDashboard()) {
                yValuesEnergy.add(new BarEntry((float) entry.getValue().getEnergy(), i));
                yValuesCost.add(new BarEntry((float) entry.getValue().getCost(), i++));
            }
        }
    }

    /***********************
     * GETTERS AND SETTERS *
     ***********************/
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

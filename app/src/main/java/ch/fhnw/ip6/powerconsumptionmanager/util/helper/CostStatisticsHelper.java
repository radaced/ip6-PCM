package ch.fhnw.ip6.powerconsumptionmanager.util.helper;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.model.PCMComponent;
import ch.fhnw.ip6.powerconsumptionmanager.model.PCMData;
import ch.fhnw.ip6.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;
import ch.fhnw.ip6.powerconsumptionmanager.util.formatter.CostStatisticsFormatter;
import ch.fhnw.ip6.powerconsumptionmanager.util.formatter.XAxisDateFormatter;

/**
 * Helper class to handle and modify the bar charts that display the cost statistics.
 */
public class CostStatisticsHelper {

    private PowerConsumptionManagerAppContext mAppContext;
    private Context mContext;

    // Reference to the stacked bar charts
    private BarChart mSBCGeneralStatistics;
    private BarChart mSBCComponentStatistics;

    /**
     * Constructs a helper class object to easily setup and modify the look and feel of the cost statistics data chart.
     * @param context Context of the statistics.
     * @param generalStatistics Stacked bar chart for the general statistics.
     * @param componentStatistics Stacked bar chart for the statistics per component.
     */
    public CostStatisticsHelper(Context context, BarChart generalStatistics, BarChart componentStatistics) {
        mContext = context;
        mAppContext = (PowerConsumptionManagerAppContext) mContext.getApplicationContext();
        mSBCGeneralStatistics = generalStatistics;
        mSBCComponentStatistics = componentStatistics;
        setupStackedBarChartStyle(mSBCGeneralStatistics);
        setupStackedBarChartStyle(mSBCComponentStatistics);
    }

    /**
     * Set up look and feel of a stacked bar chart.
     * @param chart The chart where the looks, axis and so on are being modified.
     */
    public void setupStackedBarChartStyle(BarChart chart) {
        // Set looks
        chart.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorBackground));
        chart.setDrawGridBackground(false);
        chart.setDrawBorders(false);
        chart.setDescription(null);
        chart.setNoDataText(mContext.getString(R.string.chart_no_data));
        chart.getPaint(Chart.PAINT_INFO).setColor(ContextCompat.getColor(mContext, R.color.colorTextPrimary));
        chart.setHighlightPerTapEnabled(false);
        chart.setHighlightPerDragEnabled(false);

        // Modify axis
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawAxisLine(false);
        leftAxis.setDrawGridLines(true);
        leftAxis.setDrawZeroLine(true);
        leftAxis.setGridColor(Color.WHITE);
        leftAxis.setZeroLineColor(Color.WHITE);
        leftAxis.setTextColor(ContextCompat.getColor(mContext, R.color.colorTextPrimary));
        leftAxis.setSpaceTop(40);

        chart.getAxisRight().setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(ContextCompat.getColor(mContext, R.color.colorTextPrimary));
        xAxis.setTextSize(12f);
        xAxis.setValueFormatter(new XAxisDateFormatter());

        // Modify chart legend
        Legend l = chart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_CENTER);
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setTextColor(ContextCompat.getColor(mContext, R.color.colorTextPrimary));

        // Set functionality
        chart.setDoubleTapToZoomEnabled(true);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);
    }

    /**
     * Prepares and displays all cost statistics data.
     */
    public void setupStackedBarChartData() {
        PCMData pcmData = mAppContext.getPCMData();
        // X-axis values are the same for both stacked bar charts
        ArrayList<String> xValues = new ArrayList<>(pcmData.getStatisticsDates());
        // Lists to hold the Y-axis values of both charts
        ArrayList<BarEntry> yValuesGeneralStatistics = new ArrayList<>();
        ArrayList<BarEntry> yValuesComponentsStatistics = new ArrayList<>();

        // Define a linked list of all components where the statistics need to be shown
        LinkedList<String> components = new LinkedList<>();
        for (Map.Entry<String, PCMComponent> entry : pcmData.getComponentData().entrySet()) {
            if(!(entry.getKey().equals("Photovoltaik") || entry.getKey().equals("VerbrauchTot")) && entry.getValue().isDisplayedOnDashboard()) {
                components.add(entry.getKey());
            }
        }

        // Fill the stacked bar Y-axis value lists
        for(int i = 0; i < xValues.size(); i++) {
            // Generate stacked bar for one date for general statistics
            yValuesGeneralStatistics.add(
                new BarEntry(
                    // Negative values always need to be added first in the array
                    new float[]{
                        pcmData.getSurplusStatisticsValues().get(i).floatValue() * (-1),
                        pcmData.getSelfSupplyStatisticsValues().get(i).floatValue(),
                        pcmData.getConsumptionStatisticsValues().get(i).floatValue()
                    },
                    i
                )
            );


            // Generate stacked bar for one date for component statistics
            float[] componentValuesPerDate = new float[components.size()];
            int j = 0;

            for (PCMComponent componentData : pcmData.getComponentData().values()) {
                // Ignores the components Photovoltaik and VerbrauchTot because they are not listed in the cost statistics
                if(componentData.getStatistics().size() != 0 && componentData.isDisplayedOnDashboard()) {
                    componentValuesPerDate[j] = componentData.getStatistics().get(i).floatValue();
                    j++;
                }
            }

            yValuesComponentsStatistics.add(new BarEntry(componentValuesPerDate, i));
        }

        // Setup the two stacked bar chart data sets
        BarDataSet generalStatisticsSet, componentStatisticsSet;

        // Add the Y-axis value list to the stacked bar data set and modify the looks
        generalStatisticsSet = new BarDataSet(yValuesGeneralStatistics, "");
        generalStatisticsSet.setColors(mContext.getResources().getIntArray(R.array.colorsGeneralStatistics));
        generalStatisticsSet.setStackLabels(mContext.getResources().getStringArray(R.array.general_statistics_labels));
        generalStatisticsSet.setValueFormatter(new CostStatisticsFormatter(true, " CHF", 2));
        generalStatisticsSet.setValueTextColor(ContextCompat.getColor(mContext, R.color.colorTextPrimary));

        componentStatisticsSet = new BarDataSet(yValuesComponentsStatistics, "");
        componentStatisticsSet.setColors(getColors(components.size()));
        componentStatisticsSet.setStackLabels(components.toArray(new String[components.size()]));
        componentStatisticsSet.setValueFormatter(new CostStatisticsFormatter(false, " CHF", 2));
        componentStatisticsSet.setValueTextColor(ContextCompat.getColor(mContext, R.color.colorTextPrimary));

        BarData generalStatisticsData = new BarData(xValues, generalStatisticsSet);
        BarData componentStatisticsData = new BarData(xValues, componentStatisticsSet);

        // Set the data and display the stacked bar charts
        mSBCGeneralStatistics.setData(generalStatisticsData);
        mSBCGeneralStatistics.animateY(3000);
        mSBCComponentStatistics.setData(componentStatisticsData);
        mSBCComponentStatistics.animateY(3000);
    }

    /**
     * Get an array of predefined color codes with certain length.
     * @param size Amount of components being display in the component statistics.
     * @return An integer array with predefined colors exactly as long as the amount of components displayed in the component statistics.
     */
    private int[] getColors(int size) {
        int[] colorResources = mContext.getResources().getIntArray(R.array.colorsComponentStatistics);
        int[] colors = new int[size];
        System.arraycopy(colorResources, 0, colors, 0, colors.length);
        return colors;
    }
}

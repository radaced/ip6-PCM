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
import java.util.Set;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.model.dashboard.PCMComponentData;
import ch.fhnw.ip6.powerconsumptionmanager.model.dashboard.PCMData;
import ch.fhnw.ip6.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;
import ch.fhnw.ip6.powerconsumptionmanager.util.formatter.CostStatisticsFormatter;
import ch.fhnw.ip6.powerconsumptionmanager.util.formatter.XAxisDateFormatter;

public class StatisticsHelper {

    private PowerConsumptionManagerAppContext mAppContext;
    private Context mContext;

    private BarChart mSBCStatisticsOverview;
    private BarChart mSBCStatisticsComponent;

    public StatisticsHelper(Context c, BarChart statisticsOverview, BarChart statisticsComponent) {
        mContext = c;
        mAppContext = (PowerConsumptionManagerAppContext) mContext.getApplicationContext();
        mSBCStatisticsOverview = statisticsOverview;
        mSBCStatisticsComponent = statisticsComponent;
        setupStackedBarChartStyle(mSBCStatisticsOverview);
        setupStackedBarChartStyle(mSBCStatisticsComponent);
    }

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

    public void setupStackedBarChartData() {
        PCMData pcmData = mAppContext.getPCMData();
        ArrayList<String> xValues = new ArrayList<>(pcmData.getStatisticsDates());
        ArrayList<BarEntry> yValuesGeneralStatistics = new ArrayList<>();
        ArrayList<BarEntry> yValuesComponentsStatistics = new ArrayList<>();
        int amountOfComponents = pcmData.getComponentData().size() - 2;

        for(int i = 0; i < xValues.size(); i++) {
            yValuesGeneralStatistics.add(
                new BarEntry(
                    new float[]{
                        pcmData.getSurplusStatisticsValues().get(i).floatValue() * (-1),
                        pcmData.getSelfSupplyStatisticsValues().get(i).floatValue(),
                        pcmData.getConsumptionStatisticsValues().get(i).floatValue()
                    },
                    i
                )
            );

            float[] componentValuesPerDate = new float[amountOfComponents];
            int j = 0;
            for (PCMComponentData componentData : pcmData.getComponentData().values()) {
                // Ignores the components Photovoltaik and VerbrauchTot because they are not listed in the cost statistics
                if(componentData.getStatistics().size() != 0) {
                    componentValuesPerDate[j] = componentData.getStatistics().get(i).floatValue();
                    j++;
                }
            }

            yValuesComponentsStatistics.add(new BarEntry(componentValuesPerDate, i));
        }

        BarDataSet generalStatisticsSet, componentStatisticsSet;

        generalStatisticsSet = new BarDataSet(yValuesGeneralStatistics, "");
        generalStatisticsSet.setColors(mContext.getResources().getIntArray(R.array.colorsGeneralStatistics));
        generalStatisticsSet.setStackLabels(mContext.getResources().getStringArray(R.array.general_statistics_labels));
        generalStatisticsSet.setValueFormatter(new CostStatisticsFormatter(true, " CHF", 2));
        generalStatisticsSet.setValueTextColor(ContextCompat.getColor(mContext, R.color.colorTextPrimary));

        componentStatisticsSet = new BarDataSet(yValuesComponentsStatistics, "");
        componentStatisticsSet.setColors(getColors(amountOfComponents));
        String[] components = new String[amountOfComponents];
        int i = 0;
        for (String component : pcmData.getComponentData().keySet()) {
            if(!(component.equals("Photovoltaik") || component.equals("VerbrauchTot"))) {
                components[i] = component;
                i++;
            }
        }
        componentStatisticsSet.setStackLabels(components);
        componentStatisticsSet.setValueFormatter(new CostStatisticsFormatter(false, " CHF", 2));
        componentStatisticsSet.setValueTextColor(ContextCompat.getColor(mContext, R.color.colorTextPrimary));

        BarData generalStatisticsData = new BarData(xValues, generalStatisticsSet);
        BarData componentStatisticsData = new BarData(xValues, componentStatisticsSet);

        mSBCStatisticsOverview.setData(generalStatisticsData);
        mSBCStatisticsOverview.animateY(3000);
        mSBCStatisticsComponent.setData(componentStatisticsData);
        mSBCStatisticsComponent.animateY(3000);
    }

    private int[] getColors(int size) {
        int[] colorResources = mContext.getResources().getIntArray(R.array.colorsComponentStatistics);
        int[] colors = new int[size];
        System.arraycopy(colorResources, 0, colors, 0, colors.length);
        return colors;
    }
}

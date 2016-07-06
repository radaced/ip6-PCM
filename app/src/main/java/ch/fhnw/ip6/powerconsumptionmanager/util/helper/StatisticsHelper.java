package ch.fhnw.ip6.powerconsumptionmanager.util.helper;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;

import ch.fhnw.ip6.powerconsumptionmanager.R;

public class StatisticsHelper {

    private Context mContext;

    private BarChart mSBCStatisticsOverview;
    private BarChart mSBCStatisticsComponent;

    public StatisticsHelper(Context c, BarChart statisticsOverview, BarChart statisticsComponent) {
        mContext = c;
        mSBCStatisticsOverview = statisticsOverview;
        mSBCStatisticsComponent = statisticsComponent;
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
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawZeroLine(true);
        leftAxis.setZeroLineColor(Color.WHITE);
        leftAxis.setTextColor(ContextCompat.getColor(mContext, R.color.colorTextPrimary));
        leftAxis.setSpaceTop(40);

        chart.getAxisRight().setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
        xAxis.setLabelsToSkip(0);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(true);
        xAxis.setGridColor(Color.WHITE);
        xAxis.setTextColor(ContextCompat.getColor(mContext, R.color.colorTextPrimary));
        xAxis.setTextSize(12f);

        Legend l = chart.getLegend();
        l.setPosition(Legend.LegendPosition.ABOVE_CHART_LEFT);
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setTextColor(ContextCompat.getColor(mContext, R.color.colorTextPrimary));

        // Set functionality
        chart.setDoubleTapToZoomEnabled(true);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);

        // Set marker view
        //MarkerView mv = new BarChartMarkerView(mDailyValuesContext, R.layout.barchart_markerview);
        //mDailyDataBarChart.setMarkerView(mv);
    }
}

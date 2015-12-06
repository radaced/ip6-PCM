package ch.fhnw.ip5.powerconsumptionmanager.util;

import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import ch.fhnw.ip5.powerconsumptionmanager.model.ComponentDataModel;
import ch.fhnw.ip5.powerconsumptionmanager.model.ConsumptionDataModel;
import ch.fhnw.ip5.powerconsumptionmanager.view.ConsumptionFragment;

/**
 * Created by Patrik on 06.12.2015.
 */
public class ChartHelper {
    private LineChart mChart;
    private ConsumptionFragment mContext;
    private ArrayList<String> mXValues = new ArrayList<String>();;
    private ArrayList<LineDataSet> mLineDataSets = new ArrayList<LineDataSet>();

    public ChartHelper(LineChart chart, ConsumptionFragment context) {
        mChart = chart;
        mContext = context;
    }

    public void setup() {
        mChart.setBackgroundColor(Color.LTGRAY);
        mChart.setDrawGridBackground(false);
        mChart.setDrawBorders(false);
        mChart.setDescription(null);
        mChart.setDoubleTapToZoomEnabled(true);

        mChart.getAxisLeft().setDrawAxisLine(true);
        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getXAxis().setDrawAxisLine(false);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.getXAxis().setValueFormatter(new XAxisDateFormatter());

        mChart.setTouchEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setPinchZoom(true);
        mChart.setOnChartValueSelectedListener(mContext);
    }

    public void setLegend() {
        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
    }

    public void generateXValues(ConsumptionDataModel data) {

        for (int i = 0; i < data.getComponentData().size(); i++) {
            // Only 1 day
            if(i > 287) {
                continue;
            }
            mXValues.add(data.getComponentData().get(i).getTimestamp());
        }

        /*
        for (int i = 0; i < data.getComponentData().size(); i++) {
            mXValues.add((i) + "");
        }
        */
    }

    public void generateDataSet(ConsumptionDataModel data, int iteration) {
        ArrayList<Entry> values = new ArrayList<Entry>();

        for (int i = 0; i < data.getComponentData().size(); i++) {
            // Only 1 day
            if(i > 287) {
                continue;
            }
            values.add(new Entry((float) data.getComponentData().get(i).getPowerkW(), i));
        }

        LineDataSet d = new LineDataSet(values, data.getComponentName());
        d.setLineWidth(1.5f);
        d.setCircleSize(2f);

        d.setColor(ColorTemplate.PASTEL_COLORS[iteration]);
        d.setCircleColor(ColorTemplate.PASTEL_COLORS[iteration]);
        mLineDataSets.add(d);
    }

    public void displayData() {
        LineData data = new LineData(this.getXValues(), this.getLineDataSets());
        mChart.setData(data);
        mChart.animateY(2000);
    }

    public ArrayList<String> getXValues() {
        return mXValues;
    }

    public ArrayList<LineDataSet> getLineDataSets() {
        return mLineDataSets;
    }
}

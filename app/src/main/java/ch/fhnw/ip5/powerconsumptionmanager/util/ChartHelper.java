package ch.fhnw.ip5.powerconsumptionmanager.util;

import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;

import ch.fhnw.ip5.powerconsumptionmanager.model.ConsumptionDataModel;
import ch.fhnw.ip5.powerconsumptionmanager.view.ConsumptionFragment;

/**
 * Created by Patrik on 06.12.2015.
 */
public class ChartHelper {
    private LineChart mChart;
    private ConsumptionFragment mContext;
    private ArrayList<String> mXValues = new ArrayList<String>();
    private HashMap<Integer, LineDataSet> mConsumptionDataSet = new HashMap<Integer, LineDataSet>();

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

        LineDataSet lds = new LineDataSet(values, data.getComponentName());
        lds.setLineWidth(1.5f);
        lds.setCircleSize(2f);

        lds.setColor(ColorTemplate.PASTEL_COLORS[iteration]);
        lds.setCircleColor(ColorTemplate.PASTEL_COLORS[iteration]);
        mConsumptionDataSet.put(iteration, lds);
    }

    public void initChartData() {
        ArrayList<LineDataSet> list = new ArrayList<LineDataSet>();
        for(int i = 0; i < mConsumptionDataSet.size(); i++) {
            list.add(mConsumptionDataSet.get(i));
        }

        LineData data = new LineData(this.getXValues(), list);
        mChart.setData(data);
    }

    public void updateChartData(ArrayList<Integer> ignoreList) {
        ArrayList<LineDataSet> list = new ArrayList<LineDataSet>();
        boolean skip = false;
        for(int i = 0; i < mConsumptionDataSet.size(); i++) {
            for(int j = 0; j < ignoreList.size(); j++) {
                if(i == ignoreList.get(j)) {
                    skip = true;
                    break;
                }
            }
            if(skip) {
                skip = false;
                continue;
            }
            list.add(mConsumptionDataSet.get(i));
        }

        LineData data = new LineData(this.getXValues(), list);
        mChart.setData(data);
    }

    public void displayAnimated() {
        mChart.animateY(2000);
    }

    public void displayNoneAnimated() {
        mChart.invalidate();
    }

    public ArrayList<String> getXValues() {
        return mXValues;
    }

    public HashMap<Integer, LineDataSet> getConsumptionDataSet() {
        return mConsumptionDataSet;
    }

    public LineChart getChart() {
        return mChart;
    }
}

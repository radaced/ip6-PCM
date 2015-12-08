package ch.fhnw.ip5.powerconsumptionmanager.util;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;

import ch.fhnw.ip5.powerconsumptionmanager.R;
import ch.fhnw.ip5.powerconsumptionmanager.model.ConsumptionDataModel;
import ch.fhnw.ip5.powerconsumptionmanager.view.ConsumptionFragment;

/**
 * Created by Patrik on 06.12.2015.
 */
public class ChartHelper {
    private LineChart mConsumptionChart;
    private ConsumptionFragment mContext;
    private ArrayList<String> mXValues = new ArrayList<String>();
    private HashMap<Integer, LineDataSet> mConsumptionDataSet = new HashMap<Integer, LineDataSet>();
    private int[] mGraphColors;


    public ChartHelper(LineChart chart, ConsumptionFragment context) {
        mConsumptionChart = chart;
        mContext = context;
        mGraphColors = context.getResources().getIntArray(R.array.colorsGraph);
    }

    public void setup() {
        mConsumptionChart.setBackgroundColor(ContextCompat.getColor(mContext.getActivity(), R.color.colorChartBackground));
        mConsumptionChart.setDrawGridBackground(false);
        mConsumptionChart.setDrawBorders(false);
        mConsumptionChart.setDescription(null);
        mConsumptionChart.setDoubleTapToZoomEnabled(true);

        mConsumptionChart.getAxisLeft().setDrawAxisLine(true);
        mConsumptionChart.getAxisLeft().setDrawGridLines(true);
        mConsumptionChart.getAxisLeft().setGridColor(Color.BLACK);
        mConsumptionChart.getAxisLeft().setAxisLineColor(Color.BLUE);
        mConsumptionChart.getAxisRight().setEnabled(false);
        mConsumptionChart.getXAxis().setDrawAxisLine(false);
        mConsumptionChart.getXAxis().setDrawGridLines(false);
        mConsumptionChart.getXAxis().setValueFormatter(new XAxisDateFormatter());

        mConsumptionChart.setTouchEnabled(true);
        mConsumptionChart.setDragEnabled(true);
        mConsumptionChart.setScaleEnabled(true);
        mConsumptionChart.setPinchZoom(true);
        mConsumptionChart.setOnChartValueSelectedListener(mContext);
    }

    public void setLegend(boolean enable) {
        Legend l = mConsumptionChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        l.setEnabled(enable);
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

        lds.setColor(getGraphColor(iteration));
        lds.setCircleColor(getGraphColor(iteration));
        mConsumptionDataSet.put(iteration, lds);
    }

    public void initChartData() {
        ArrayList<LineDataSet> list = new ArrayList<LineDataSet>();
        for(int i = 0; i < mConsumptionDataSet.size(); i++) {
            list.add(mConsumptionDataSet.get(i));
        }

        LineData data = new LineData(this.getXValues(), list);
        mConsumptionChart.setData(data);
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
        mConsumptionChart.setData(data);
    }

    public void displayAnimated() {
        mConsumptionChart.animateY(2000);
    }

    public void displayNoneAnimated() {
        mConsumptionChart.invalidate();
    }

    public ArrayList<String> getXValues() {
        return mXValues;
    }

    public LineChart getChart() {
        return mConsumptionChart;
    }

    public int getGraphColor(int index) {
        return mGraphColors[index];
    }
}

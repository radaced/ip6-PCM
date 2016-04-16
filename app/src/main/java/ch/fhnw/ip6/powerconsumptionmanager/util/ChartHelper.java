package ch.fhnw.ip6.powerconsumptionmanager.util;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.HashMap;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.model.ConsumptionDataModel;
import ch.fhnw.ip6.powerconsumptionmanager.view.ConsumptionFragment;

/**
 * Helper class to handle and modify the chart
 */
public class ChartHelper {
    private LineChart mConsumptionChart;
    private ConsumptionFragment mContext;
    // Holds x values (timestamps)
    private ArrayList<String> mXValues;
    // Holds all data sets from the chart
    private HashMap<Integer, LineDataSet> mConsumptionDataSet;
    // Holds the unselected dataset indices
    private ArrayList<Integer> mRemovedDataSetIndexes;
    // Members to modify colors
    private int[] mGraphColors;
    private Paint mChartDrawer;


    public ChartHelper(LineChart chart, ConsumptionFragment context) {
        mConsumptionChart = chart;
        mContext = context;

        mXValues = new ArrayList<>();
        mConsumptionDataSet = new HashMap<>();
        mRemovedDataSetIndexes = new ArrayList<>();

        mGraphColors = context.getResources().getIntArray(R.array.colorsGraph);
        mChartDrawer = chart.getPaint(Chart.PAINT_INFO);
    }

    /**
     * Inital settings for the chart
     */
    public void setup() {
        // Set looks
        mConsumptionChart.setBackgroundColor(ContextCompat.getColor(mContext.getActivity(), R.color.colorChartBackground));
        mConsumptionChart.setDrawGridBackground(false);
        mConsumptionChart.setDrawBorders(false);
        mConsumptionChart.setDescription(null);
        mConsumptionChart.setNoDataText(mContext.getActivity().getString(R.string.chart_no_data));
        mChartDrawer.setColor(ContextCompat.getColor(mContext.getActivity(), R.color.colorTextPrimaryInverse));

        // Define which axis to display
        mConsumptionChart.getAxisLeft().setDrawAxisLine(true);
        mConsumptionChart.getAxisLeft().setDrawGridLines(true);
        mConsumptionChart.getAxisLeft().setGridColor(Color.BLACK);
        mConsumptionChart.getAxisLeft().setAxisLineColor(Color.BLUE);
        mConsumptionChart.getAxisRight().setEnabled(false);
        mConsumptionChart.getXAxis().setDrawAxisLine(false);
        mConsumptionChart.getXAxis().setDrawGridLines(false);
        mConsumptionChart.getXAxis().setValueFormatter(new XAxisDateFormatter());

        // Set functionality
        mConsumptionChart.setDoubleTapToZoomEnabled(true);
        mConsumptionChart.setTouchEnabled(true);
        mConsumptionChart.setDragEnabled(true);
        mConsumptionChart.setScaleEnabled(true);
        mConsumptionChart.setPinchZoom(true);
        mConsumptionChart.setOnChartValueSelectedListener(mContext);
    }

    /**
     * Set error messages for empty chart
     */
    public void setupOnError() {
        mConsumptionChart.setNoDataText(mContext.getActivity().getString(R.string.chart_connection_error_title));
        mConsumptionChart.setNoDataTextDescription(mContext.getActivity().getString(R.string.chart_connection_error_description));
        mConsumptionChart.setBackgroundColor(ContextCompat.getColor(mContext.getActivity(), R.color.colorChartBackground));
        mChartDrawer.setColor(ContextCompat.getColor(mContext.getActivity(), R.color.colorTextPrimaryInverse));
    }

    /**
     * Shows or hides the legend
     * @param enable true to show, false to hide
     */
    public void setLegend(boolean enable) {
        Legend l = mConsumptionChart.getLegend();
        if(enable) {
            l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        }
        l.setEnabled(enable);
    }

    /**
     * Generates the x values of the chart (amount of x and y values need to be equal)
     * @param data Loaded consumption data of a single device
     */
    public void generateXValues(ConsumptionDataModel data) {
        if(!mXValues.isEmpty()) {
            mXValues.clear();
        }

        for (int i = 0; i < data.getComponentData().size(); i++) {
            mXValues.add(data.getComponentData().get(i).getTimestamp());
        }
    }

    /**
     * Generates a single data set and adds it to the hash map that holds all data sets to display
     * @param data Loaded consumption data of a single device
     * @param colorCode Array index to set color for the graph (data set)
     */
    // Generates a single data set and adds it to the hash map that holds all data sets
    public void generateDataSet(ConsumptionDataModel data, int colorCode) {
        ArrayList<Entry> values = new ArrayList<>();

        for (int i = 0; i < data.getComponentData().size(); i++) {
            /* TODO Only read data of 1 day in Eigenverbrauchsmanager v2.36 */
            if(i > 287) {
                continue;
            }
            values.add(new Entry((float) data.getComponentData().get(i).getPowerkW(), i));
        }

        LineDataSet lds = new LineDataSet(values, data.getComponentName());
        lds.setLineWidth(1.5f);
        lds.setCircleSize(2f);

        // Set the graph colors as they appear in the server component
        lds.setColor(getGraphColor(colorCode));
        lds.setCircleColor(getGraphColor(colorCode));
        mConsumptionDataSet.put(colorCode, lds);
    }

    /**
     * Show all data sets
     */
    public void initChartData() {
        ArrayList<LineDataSet> list = new ArrayList<>();
        for(int i = 0; i < mConsumptionDataSet.size(); i++) {
            list.add(mConsumptionDataSet.get(i));
        }

        LineData data = new LineData(this.getXValues(), list);
        mConsumptionChart.setData(data);
    }

    /**
     * Show only data sets where the toggle button for each device is active. Pass a parameter (list
     * that holds the indices to ignore)
     */
    public void updateChartData() {
        ArrayList<LineDataSet> list = new ArrayList<>();
        for(int i = 0; i < mConsumptionDataSet.size(); i++) {
            if(mRemovedDataSetIndexes.contains(i)) {
                continue;
            }
            list.add(mConsumptionDataSet.get(i));
        }

        LineData data = new LineData(this.getXValues(), list);
        mConsumptionChart.setData(data);
    }


    /**
     * Display chart data animated (chart data fades in from bottom to top)
     */
    public void displayAnimated() {
        mConsumptionChart.animateY(2000);
    }

    /**
     * Display without animation
     */
    public void displayNoneAnimated() {
        mConsumptionChart.invalidate();
    }

    public ArrayList<String> getXValues() {
        return mXValues;
    }

    public LineChart getChart() {
        return mConsumptionChart;
    }

    public ArrayList<Integer> getRemovedDataSetIndexes() {
        return mRemovedDataSetIndexes;
    }

    public int getGraphColor(int index) {
        return mGraphColors[index];
    }
}

package ch.fhnw.ip6.powerconsumptionmanager.util.helper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.HashMap;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.model.consumptiondata.ConsumptionComponentModel;
import ch.fhnw.ip6.powerconsumptionmanager.util.formatter.XAxisTimeFormatter;

/**
 * Helper class to handle and modify the chart
 */
public class ConsumptionDataHelper {
    private Context mContext;
    private LineChart mLCConsumption;
    // Holds x values (timestamps)
    private ArrayList<String> mXValues;
    // Holds all data sets from the chart
    private HashMap<Integer, LineDataSet> mConsumptionDataSet;
    // Holds the unselected dataset indices
    private ArrayList<Integer> mRemovedDataSetIndexes;
    // Members to modify colors
    private int[] mGraphColors;
    private Paint mChartDrawer;


    public ConsumptionDataHelper(Context context, LineChart chart) {
        mContext = context;
        mLCConsumption = chart;

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
        mLCConsumption.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorChartBackground));
        mLCConsumption.setDrawGridBackground(false);
        mLCConsumption.setDrawBorders(false);
        mLCConsumption.setDescription(null);
        mLCConsumption.setNoDataText(mContext.getString(R.string.chart_no_data));
        mChartDrawer.setColor(ContextCompat.getColor(mContext, R.color.colorTextPrimaryInverse));

        // Define which axis to display
        mLCConsumption.getAxisLeft().setDrawAxisLine(true);
        mLCConsumption.getAxisLeft().setDrawGridLines(true);
        mLCConsumption.getAxisLeft().setGridColor(Color.BLACK);
        mLCConsumption.getAxisLeft().setAxisLineColor(Color.BLUE);
        mLCConsumption.getAxisRight().setEnabled(false);
        mLCConsumption.getXAxis().setDrawAxisLine(false);
        mLCConsumption.getXAxis().setDrawGridLines(false);
        mLCConsumption.getXAxis().setValueFormatter(new XAxisTimeFormatter());

        // Set functionality
        mLCConsumption.setDoubleTapToZoomEnabled(true);
        mLCConsumption.setTouchEnabled(true);
        mLCConsumption.setDragEnabled(true);
        mLCConsumption.setScaleEnabled(true);
        mLCConsumption.setPinchZoom(true);
    }

    /**
     * Set error messages for empty chart
     */
    public void setupOnError() {
        mLCConsumption.setNoDataText(mContext.getString(R.string.chart_connection_error_title));
        mLCConsumption.setNoDataTextDescription(mContext.getString(R.string.chart_connection_error_description));
        mLCConsumption.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorChartBackground));
        mChartDrawer.setColor(ContextCompat.getColor(mContext, R.color.colorTextPrimaryInverse));
    }

    /**
     * Shows or hides the legend
     * @param enable true to show, false to hide
     */
    public void setLegend(boolean enable) {
        Legend l = mLCConsumption.getLegend();
        if(enable) {
            l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        }
        l.setEnabled(enable);
    }

    /**
     * Generates the x values of the chart (amount of x and y values need to be equal)
     * @param data Loaded consumption data of a single device
     */
    public void generateXValues(ConsumptionComponentModel data) {
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
    public void generateDataSet(ConsumptionComponentModel data, int colorCode) {
        ArrayList<Entry> values = new ArrayList<>();

        for (int i = 0; i < data.getComponentData().size(); i++) {
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
        ArrayList<ILineDataSet> list = new ArrayList<>();
        for(int i = 0; i < mConsumptionDataSet.size(); i++) {
            list.add(mConsumptionDataSet.get(i));
        }

        LineData data = new LineData(this.getXValues(), list);
        mLCConsumption.setData(data);
    }

    /**
     * Show only data sets where the toggle button for each device is active. Pass a parameter (list
     * that holds the indices to ignore)
     */
    public void updateChartData() {
        ArrayList<ILineDataSet> list = new ArrayList<>();
        for(int i = 0; i < mConsumptionDataSet.size(); i++) {
            if(mRemovedDataSetIndexes.contains(i)) {
                continue;
            }
            list.add(mConsumptionDataSet.get(i));
        }

        LineData data = new LineData(this.getXValues(), list);
        mLCConsumption.setData(data);

        mLCConsumption.getData().notifyDataChanged();
        mLCConsumption.notifyDataSetChanged();
        mLCConsumption.invalidate();
    }


    /**
     * Display chart data animated (chart data fades in from bottom to top)
     */
    public void displayAnimated() {
        mLCConsumption.animateY(2000);
    }

    /**
     * Display without animation
     */
    public void displayNoneAnimated() {
        mLCConsumption.invalidate();
    }

    public ArrayList<String> getXValues() {
        return mXValues;
    }

    public LineChart getChart() {
        return mLCConsumption;
    }

    public ArrayList<Integer> getRemovedDataSetIndexes() {
        return mRemovedDataSetIndexes;
    }

    public int getGraphColor(int index) {
        return mGraphColors[index];
    }
}

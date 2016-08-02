package ch.fhnw.ip6.powerconsumptionmanager.util.helper;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.model.PCMComponent;
import ch.fhnw.ip6.powerconsumptionmanager.model.ConsumptionData;
import ch.fhnw.ip6.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;
import ch.fhnw.ip6.powerconsumptionmanager.util.formatter.XAxisTimeFormatter;

/**
 * Helper class to handle and modify the chart
 */
public class ConsumptionDataHelper {

    private PowerConsumptionManagerAppContext mAppContext;
    private Context mContext;

    private LineChart mLCConsumption;
    // Holds all data sets from the chart
    private HashMap<Integer, LineDataSet> mConsumptionDataSet;
    // Holds the unselected dataset indices
    private ArrayList<Integer> mRemovedDataSetIndexes;
    // Members to modify colors
    private int[] mGraphColors;
    private String mFirstComponent;


    public ConsumptionDataHelper(Context context, LineChart chart) {
        mContext = context;
        mAppContext = (PowerConsumptionManagerAppContext) mContext.getApplicationContext();

        mLCConsumption = chart;
        mConsumptionDataSet = new HashMap<>();
        mRemovedDataSetIndexes = new ArrayList<>();

        mGraphColors = context.getResources().getIntArray(R.array.colorsGraph);
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
        mLCConsumption.getLegend().setEnabled(false);
        mLCConsumption.getPaint(Chart.PAINT_INFO).setColor(ContextCompat.getColor(mContext, R.color.colorTextPrimaryInverse));

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

    public void setupLineChartData() {
        LinkedHashMap<String, PCMComponent> componentData = mAppContext.getPCMData().getComponentData();
        mFirstComponent = (String) componentData.keySet().toArray()[0];
        ArrayList<String> xValues = generateXValues(componentData.get(mFirstComponent).getConsumptionData());

        // Generate the data sets to display
        int i = 0;
        for (PCMComponent component : componentData.values()) {
            generateDataSet(component.getName(), component.getConsumptionData(), i++);
        }

        setDataForChart(xValues);
        displayAnimated();
    }

    /**
     * Generates the x values of the chart (amount of x and y values need to be equal)
     * @param data Loaded consumption data of a single device
     */
    public ArrayList<String> generateXValues(LinkedList<ConsumptionData> data) {
        ArrayList<String> xValues = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            xValues.add(data.get(i).getTimestamp());
        }

        return xValues;
    }

    /**
     * Generates a single data set and adds it to the hash map that holds all data sets to display
     * @param data Loaded consumption data of a single device
     * @param colorCode Array index to set color for the graph (data set)
     */
    public void generateDataSet(String componentName, LinkedList<ConsumptionData> data, int colorCode) {
        ArrayList<Entry> values = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            values.add(new Entry((float) data.get(i).getPowerkW(), i));
        }

        LineDataSet lds = new LineDataSet(values, componentName);
        lds.setLineWidth(1.5f);
        lds.setCircleRadius(2f);

        // Set the graph colors as they appear in the server component
        lds.setColor(getGraphColor(colorCode));
        lds.setCircleColor(getGraphColor(colorCode));
        mConsumptionDataSet.put(colorCode, lds);
    }

    /**
     * Set data for the line chart
     */
    private void setDataForChart(ArrayList<String> xValues) {
        ArrayList<ILineDataSet> list = new ArrayList<>();
        for(int i = 0; i < mConsumptionDataSet.size(); i++) {
            list.add(mConsumptionDataSet.get(i));
        }

        LineData data = new LineData(xValues, list);
        mLCConsumption.setData(data);
    }

    /**
     * Show only data sets where the toggle button for each device is active. Pass a parameter (list
     * that holds the indices to ignore)
     */
    public void updateLineChartData() {
        LinkedHashMap<String, PCMComponent> componentData = mAppContext.getPCMData().getComponentData();
        ArrayList<String> xValues = generateXValues(componentData.get(mFirstComponent).getConsumptionData());

        int i = 0;
        for (PCMComponent component : componentData.values()) {
            generateDataSet(component.getName(), component.getConsumptionData(), i++);
        }

        ArrayList<ILineDataSet> list = new ArrayList<>();
        for(int j = 0; j < mConsumptionDataSet.size(); j++) {
            if(mRemovedDataSetIndexes.contains(j)) {
                continue;
            }
            list.add(mConsumptionDataSet.get(j));
        }

        LineData data = new LineData(xValues, list);
        mLCConsumption.setData(data);

        mLCConsumption.getData().notifyDataChanged();
        mLCConsumption.notifyDataSetChanged();
        displayNoneAnimated();
    }


    /**
     * Display chart data animated (chart data fades in from bottom to top)
     */
    private void displayAnimated() {
        mLCConsumption.animateY(2000);
    }

    /**
     * Display without animation
     */
    public void displayNoneAnimated() {
        mLCConsumption.invalidate();
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

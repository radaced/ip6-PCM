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
 * Helper class to handle and modify the line chart that holds the consumption data of each component.
 */
public class ConsumptionDataHelper {

    private PowerConsumptionManagerAppContext mAppContext;
    private Context mContext;

    // Reference to the line chart
    private LineChart mLCConsumption;
    // Holds all data sets from the chart
    private HashMap<Integer, LineDataSet> mConsumptionDataSet;
    // Holds the unselected data set indices
    private ArrayList<Integer> mRemovedDataSetIndexes;

    private int[] mGraphColors;
    private String mFirstComponent;

    /**
     * Constructs a helper class object to easily setup and modify the look and feel of the consumption data chart.
     * @param context Context of the consumption data.
     * @param chart Reference to the line chart element.
     */
    public ConsumptionDataHelper(Context context, LineChart chart) {
        mContext = context;
        mAppContext = (PowerConsumptionManagerAppContext) mContext.getApplicationContext();

        mLCConsumption = chart;
        mConsumptionDataSet = new HashMap<>();
        mRemovedDataSetIndexes = new ArrayList<>();

        mGraphColors = context.getResources().getIntArray(R.array.colorsGraph);
    }

    /**
     * Initial settings for the chart
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

        // Define which axis to display and how
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
     * Sets up the whole consumption data of every component and display the data.
     */
    public void setupLineChartData() {
        LinkedHashMap<String, PCMComponent> componentData = mAppContext.getPCMData().getComponentData();
        mFirstComponent = (String) componentData.keySet().toArray()[0];
        // X-axis values are the same for all the components so take the X-axis values of any component
        ArrayList<String> xValues = generateXValues(componentData.get(mFirstComponent).getConsumptionData());

        // Generate the data sets to display
        int i = 0;
        for (PCMComponent component : componentData.values()) {
            generateDataSet(component.getName(), component.getConsumptionData(), i++);
        }

        // Set the data to the chart and display it
        setDataForChart(xValues);
        displayAnimated();
    }

    /**
     * Generates the X-axis values of the chart.
     * @param data Loaded consumption data of a component.
     */
    public ArrayList<String> generateXValues(LinkedList<ConsumptionData> data) {
        ArrayList<String> xValues = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            xValues.add(data.get(i).getTimestamp());
        }

        return xValues;
    }

    /**
     * Generates a single data set and adds it to the hash map that holds all data sets to display.
     * @param componentName The name of the component that the data set is generated for.
     * @param data Loaded consumption data of a single device.
     * @param colorCode Array index to set color for the graph (data set).
     */
    public void generateDataSet(String componentName, LinkedList<ConsumptionData> data, int colorCode) {
        ArrayList<Entry> values = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            values.add(new Entry((float) data.get(i).getPowerkW(), i));
        }

        LineDataSet lds = new LineDataSet(values, componentName);
        lds.setLineWidth(1.5f);
        lds.setCircleRadius(2f);

        // Set the graph colors
        lds.setColor(getGraphColor(colorCode));
        lds.setCircleColor(getGraphColor(colorCode));

        // Save the data set in the hash map that holds all data sets to display
        mConsumptionDataSet.put(colorCode, lds);
    }

    /**
     * Set the data for the line chart.
     * @param xValues The string array list with the X-axis values.
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
     * Updates the whole consumption data of every component and displays the data accordingly.
     */
    public void updateLineChartData() {
        LinkedHashMap<String, PCMComponent> componentData = mAppContext.getPCMData().getComponentData();
        ArrayList<String> xValues = generateXValues(componentData.get(mFirstComponent).getConsumptionData());

        // Generate/update data sets with updated values for all components
        int i = 0;
        for (PCMComponent component : componentData.values()) {
            generateDataSet(component.getName(), component.getConsumptionData(), i++);
        }

        // Setup data set list with only data sets where the toggle button of a component is on active
        ArrayList<ILineDataSet> list = new ArrayList<>();
        for(int j = 0; j < mConsumptionDataSet.size(); j++) {
            if(mRemovedDataSetIndexes.contains(j)) {
                continue;
            }
            list.add(mConsumptionDataSet.get(j));
        }

        LineData data = new LineData(xValues, list);
        mLCConsumption.setData(data);

        // Notify the chart that its data changed
        mLCConsumption.getData().notifyDataChanged();
        mLCConsumption.notifyDataSetChanged();
        displayNoneAnimated();
    }


    /**
     * Display chart data animated (chart data fades in from bottom to top).
     */
    private void displayAnimated() {
        mLCConsumption.animateY(2000);
    }

    /**
     * Display without animation.
     */
    public void displayNoneAnimated() {
        mLCConsumption.invalidate();
    }

    /***********************
     * GETTERS AND SETTERS *
     ***********************/
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

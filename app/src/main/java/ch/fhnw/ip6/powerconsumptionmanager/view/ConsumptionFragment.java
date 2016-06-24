package ch.fhnw.ip6.powerconsumptionmanager.view;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.adapter.ConsumptionDeviceListAdapter;
import ch.fhnw.ip6.powerconsumptionmanager.network.DataLoader;
import ch.fhnw.ip6.powerconsumptionmanager.network.DataLoaderCallback;
import ch.fhnw.ip6.powerconsumptionmanager.util.helper.ChartHelper;
import ch.fhnw.ip6.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;

/**
 * This Fragment shows usage data in a chart and connected devices in a list
 */
public class ConsumptionFragment extends Fragment implements OnChartValueSelectedListener, DataLoaderCallback {
    private static final String TAG = "ConsumptionFragment";

    private Handler mUpdateHandler;
    private ChartHelper mChartHelper;
    private PowerConsumptionManagerAppContext mAppContext;
    private ConsumptionFragment mContext;

    public static ConsumptionFragment newInstance() {
        return new ConsumptionFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_consumption, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppContext = (PowerConsumptionManagerAppContext) getActivity().getApplicationContext();
        mContext = this;

        LineChart consumptionChart = (LineChart) view.findViewById(R.id.consumptionDataLineChart);
        mChartHelper = new ChartHelper(consumptionChart, this);
        ListView lvDevices = (ListView) view.findViewById(R.id.lvDevices);
        int layoutResource;
        ArrayList<String> listItems;

        // Check if web requests for consumption data were successful
        if (mAppContext.isOnline()) {
            // Set up the whole chart with data sets and so on with the helper class
            mChartHelper.setup();
            mChartHelper.setLegend(false);
            mChartHelper.generateXValues(mAppContext.getConsumptionData().get(0));

            // Generate the data sets to display
            for (int i = 0; i < mAppContext.getConsumptionData().size(); i++) {
                mChartHelper.generateDataSet(mAppContext.getConsumptionData().get(i), i);
            }

            // Display the chart
            mChartHelper.initChartData();
            mChartHelper.displayAnimated();

            // Instantiate the update handler
            mUpdateHandler = new Handler();

            // Define device list adapter parameters
            layoutResource = R.layout.list_connected_device;
            listItems = mAppContext.getComponents();
        } else {
            // Set up an empty chart with an error message
            mChartHelper.setupOnError();

            // Define device list adapter parameters
            layoutResource = R.layout.list_no_device;
            listItems = new ArrayList<>();
            listItems.add(getString(R.string.list_device_error));
        }

        // Set up the device list
        lvDevices.setAdapter(
                new ConsumptionDeviceListAdapter(
                        getActivity(),
                        layoutResource,
                        listItems,
                        mChartHelper,
                        mAppContext.isOnline()
                )
        );
    }


    /**
     * Determines if the fragment is currently visible in the view pager
     * @param isVisibleToUser true when currently visible, false otherwise
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(mUpdateHandler != null) {
            if(isVisibleToUser) {
                mUpdateHandler.postDelayed(updateData, 10000);
            } else {
                mUpdateHandler.removeCallbacks(updateData);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // Stop data updater if instantiated
        if(mUpdateHandler != null) {
            mUpdateHandler.removeCallbacks(updateData);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Start data updater if instantiated
        if(mUpdateHandler != null) {
            mUpdateHandler.postDelayed(updateData, 10000);
        }
    }

    /**** Return point from requests that update consumption data ****/
    @Override
    public void DataLoaderDidFinish() {
        try {
            mChartHelper.generateXValues(mAppContext.getConsumptionData().get(0));

            // Generate the updated data sets
            for (int z = 0; z < mAppContext.getConsumptionData().size(); z++) {
                mChartHelper.generateDataSet(mAppContext.getConsumptionData().get(z), z);
            }

            // Add the data sets to the chart
            mChartHelper.updateChartData();
            // Update the chart on the view
            mContext.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mChartHelper.displayNoneAnimated();
                }
            });
        } catch (NullPointerException e) {
            Log.e(TAG, "Activity/Fragment destroyed or changed while updating.");
        }
    }

    @Override
    public void DataLoaderDidFail() {
        try {
            mContext.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                Toast.makeText(
                    mAppContext.getApplicationContext(),
                    mAppContext.getString(R.string.toast_chart_update_error),
                    Toast.LENGTH_SHORT
                ).show();
                }
            });
        } catch (NullPointerException e) {
            Log.e(TAG, "Activity/Fragment destroyed or changed while updating.");
        }
    }
    /********/

    /**
     * Runnable for updating the chart (every 10 seconds)
     */
    private final Runnable updateData = new Runnable() {
        public void run() {
            DataLoader loader = new DataLoader(mAppContext, mContext);
            loader.loadConsumptionData("http://" + mAppContext.getIPAdress() + ":" + getString(R.string.webservice_getData));
            mUpdateHandler.postDelayed(this, 10000);
        }
    };

    /**** Maybe for future uses ****/
    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
    /********/
}
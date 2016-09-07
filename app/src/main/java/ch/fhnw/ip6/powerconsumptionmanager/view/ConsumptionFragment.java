package ch.fhnw.ip6.powerconsumptionmanager.view;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.adapter.ConsumptionDeviceListAdapter;
import ch.fhnw.ip6.powerconsumptionmanager.network.AsyncTaskCallback;
import ch.fhnw.ip6.powerconsumptionmanager.network.GetConsumptionDataAsyncTask;
import ch.fhnw.ip6.powerconsumptionmanager.util.helper.ConsumptionDataHelper;
import ch.fhnw.ip6.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;

/**
 * This fragment shows the consumption data of components in a chart and all connected devices in a list.
 */
public class ConsumptionFragment extends Fragment implements AsyncTaskCallback {
    private Handler mUpdateHandler;
    private ConsumptionDataHelper mConsumptionDataHelper;
    private PowerConsumptionManagerAppContext mAppContext;

    private LinearLayout mLoadingLayout;
    private LinearLayout mConsumptionDataLayout;
    private LinearLayout mOnErrorConsumptionDataLayout;

    private ListView mListViewDevices;
    private boolean mHasUpdated = false;

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

        // Load the different layouts into member fields for easier access
        mLoadingLayout = (LinearLayout) view.findViewById(R.id.llLoading);
        mConsumptionDataLayout = (LinearLayout) view.findViewById(R.id.llConsumptionData);
        mOnErrorConsumptionDataLayout = (LinearLayout) view.findViewById(R.id.llOnErrorConsumptionData);

        // Set up the helper class, the line chart and load the consumption data
        LineChart consumptionChart = (LineChart) view.findViewById(R.id.consumptionDataLineChart);
        mListViewDevices = (ListView) view.findViewById(R.id.lvDevices);
        mConsumptionDataHelper = new ConsumptionDataHelper(getContext(), consumptionChart);
        mConsumptionDataHelper.setup();

        // Restore the list with the indices of data sets from components that the user doesn't want to see
        if(savedInstanceState != null) {
            mConsumptionDataHelper.setRemovedDataSetIndices(savedInstanceState.getIntegerArrayList("REMOVED_DATASET_INDICES"));
        }

        new GetConsumptionDataAsyncTask(mAppContext, this).execute();
    }

    @Override
    public void onStop() {
        super.onStop();
        // Stop data updater if instantiated
        if(mUpdateHandler != null) {
            mUpdateHandler.removeCallbacks(updateConsumptionData);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Start data updater if instantiated
        if(mUpdateHandler != null) {
            mHasUpdated = true;
            mUpdateHandler.postDelayed(updateConsumptionData, mAppContext.getUpdateInterval() * 1000);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Save the list with the indices of data sets from components that the user doesn't want to see
        outState.putIntegerArrayList("REMOVED_DATASET_INDICES", mConsumptionDataHelper.getRemovedDataSetIndices());
        super.onSaveInstanceState(outState);
    }

    /**
     * Return point from requests that load the consumption data.
     * @param result Status if the data could be loaded successfully or not.
     * @param opType Type of operation that has completed.
     */
    @Override
    public void asyncTaskFinished(boolean result, String opType) {
        if(ConsumptionFragment.this.isVisible()) {
            // Hide the loading layout
            mLoadingLayout.setVisibility(View.GONE);

            if(result) {
                // Display the line chart with the consumption data
                mOnErrorConsumptionDataLayout.setVisibility(View.GONE);
                mConsumptionDataLayout.setVisibility(View.VISIBLE);

                if(!mHasUpdated) {
                    // Set up the whole chart data with the helper class and display it
                    mConsumptionDataHelper.setupLineChartData();
                    mConsumptionDataHelper.displayAnimated();

                    // Instantiate the update handler
                    mUpdateHandler = new Handler();

                    // Define device list adapter parameters
                    int layoutResource = R.layout.list_consumption_device;
                    ArrayList<String> listItems = new ArrayList<>(mAppContext.getPCMData().getComponentData().keySet());

                    // Set up the device list
                    mListViewDevices.setAdapter(
                            new ConsumptionDeviceListAdapter(
                                    getActivity(),
                                    layoutResource,
                                    listItems,
                                    mConsumptionDataHelper
                            )
                    );
                } else {
                    // Update the chart data and display it
                    mConsumptionDataHelper.updateLineChartData();
                    mConsumptionDataHelper.displayNoneAnimated();
                }
            } else {
                // Display an error message to the user
                mConsumptionDataLayout.setVisibility(View.GONE);
                mOnErrorConsumptionDataLayout.setVisibility(View.VISIBLE);
            }

            // Instantiate the automatic update handler if needed
            if(mAppContext.isUpdatingAutomatically() && !mHasUpdated) {
                mUpdateHandler = new Handler();
                onResume();
            }
        }
    }

    /**
     * Task to execute every few seconds (depending on settings) to load new consumption data.
     */
    private final Runnable updateConsumptionData = new Runnable() {
        public void run() {
            new GetConsumptionDataAsyncTask(
                mAppContext,
                getInstance()
            ).execute();
            mUpdateHandler.postDelayed(this, mAppContext.getUpdateInterval() * 1000);
        }
    };

    private ConsumptionFragment getInstance() { return this; }
}
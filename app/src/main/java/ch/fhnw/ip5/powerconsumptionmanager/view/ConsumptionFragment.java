/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.fhnw.ip5.powerconsumptionmanager.view;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
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

import ch.fhnw.ip5.powerconsumptionmanager.R;
import ch.fhnw.ip5.powerconsumptionmanager.adapter.DeviceListAdapter;
import ch.fhnw.ip5.powerconsumptionmanager.network.DataLoader;
import ch.fhnw.ip5.powerconsumptionmanager.network.DataLoaderCallback;
import ch.fhnw.ip5.powerconsumptionmanager.util.ChartHelper;
import ch.fhnw.ip5.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;

/**
 * This Fragment shows usage data in a chart and connected devices
 */
public class ConsumptionFragment extends Fragment implements OnChartValueSelectedListener, DataLoaderCallback {
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

        LineChart consumptionChart = (LineChart) view.findViewById(R.id.lineChart);
        mChartHelper = new ChartHelper(consumptionChart, this);
        ListView listView = (ListView) view.findViewById(R.id.deviceList);

        // Check if web requests for consumption data were successful
        if (!mAppContext.isOnline()) {
            // Set up an empty chart with an error message
            mChartHelper.setupOnError();

            ArrayList<String> errorMsg = new ArrayList<>();
            errorMsg.add(getString(R.string.list_device_error));
            listView.setAdapter(new DeviceListAdapter(getActivity(), R.layout.list_no_device, errorMsg, null));
        } else {
            // Set up the whole chart with data sets and so on with the helper class
            mChartHelper.setup();
            mChartHelper.setLegend(false);
            mChartHelper.generateXValues(mAppContext.getConsumptionData().get(0));

            // Generate the data sets to display
            for (int z = 0; z < mAppContext.getConsumptionData().size(); z++) {
                mChartHelper.generateDataSet(mAppContext.getConsumptionData().get(z), z);
            }

            // Display the chart
            mChartHelper.initChartData();
            mChartHelper.displayAnimated();

            // Set up the device list
            listView.setAdapter(
                new DeviceListAdapter(getActivity(),
                R.layout.list_device, mAppContext.getComponents(),
                mChartHelper
            ));

            // Instantiate the update handler
            mUpdateHandler = new Handler();
        }
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
        mChartHelper.generateXValues(mAppContext.getConsumptionData().get(0));

        for (int z = 0; z < mAppContext.getConsumptionData().size(); z++) {
            mChartHelper.generateDataSet(mAppContext.getConsumptionData().get(z), z);
        }

        mChartHelper.updateChartData();
        mContext.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mChartHelper.displayNoneAnimated();
            }
        });
    }

    @Override
    public void DataLoaderDidFail() {
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
    }
    /********/

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
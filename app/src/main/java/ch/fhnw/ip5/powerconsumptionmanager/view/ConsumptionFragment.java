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
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import ch.fhnw.ip5.powerconsumptionmanager.R;
import ch.fhnw.ip5.powerconsumptionmanager.adapter.DeviceListAdapter;
import ch.fhnw.ip5.powerconsumptionmanager.util.ChartHelper;
import ch.fhnw.ip5.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;

/**
 * Fragment is entry point of main application, shows usage data in a chart and connected devices
 */
public class ConsumptionFragment extends Fragment implements OnChartValueSelectedListener {
    private LineChart mConsumptionChart;

    public static ConsumptionFragment newInstance() {
        ConsumptionFragment fragment = new ConsumptionFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_consumption, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PowerConsumptionManagerAppContext context = (PowerConsumptionManagerAppContext) getActivity().getApplicationContext();

        mConsumptionChart = (LineChart) view.findViewById(R.id.lineChart);

        // Set up the whole chart with data sets and so on with the helper class
        ChartHelper chartHelper = new ChartHelper(mConsumptionChart, this);
        chartHelper.setup();
        chartHelper.setLegend(false);
        chartHelper.generateXValues(context.getConsumptionData().get(0));

        // Generate the data sets to display
        for (int z = 0; z < context.getConsumptionData().size(); z++) {
            chartHelper.generateDataSet(context.getConsumptionData().get(z), z);
        }

        // Display the chart
        chartHelper.initChartData();
        chartHelper.displayAnimated();

        // Set up the device list
        ListView listView = (ListView) view.findViewById(R.id.deviceList);
        listView.setAdapter(new DeviceListAdapter(getActivity(), R.layout.list_item_device, context.getComponents(), chartHelper));
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}

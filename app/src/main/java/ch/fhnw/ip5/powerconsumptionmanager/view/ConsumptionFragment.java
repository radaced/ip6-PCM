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

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.List;

import ch.fhnw.ip5.powerconsumptionmanager.R;
import ch.fhnw.ip5.powerconsumptionmanager.adapter.DeviceListAdapter;

public class ConsumptionFragment extends Fragment implements OnChartValueSelectedListener {
    private LineChart mLineChart;

    // Test Values
    ArrayList<String> devices = new ArrayList<String>();

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

        //TestData
        devices.add("Backofen");
        devices.add("Waschmaschine");
        devices.add("Toaster");

        int[] mColors = new int[] {
                getResources().getColor(R.color.Red),
                getResources().getColor(R.color.Green),
                getResources().getColor(R.color.Blue)
        };

        mLineChart = (LineChart) view.findViewById(R.id.lineChart);
        mLineChart.setOnChartValueSelectedListener(this);

        mLineChart.setBackgroundColor(Color.LTGRAY);
        mLineChart.setDrawGridBackground(false);
        mLineChart.setDrawBorders(false);
        mLineChart.setDescription(null);
        mLineChart.setDoubleTapToZoomEnabled(true);

        mLineChart.getAxisLeft().setDrawAxisLine(true);
        mLineChart.getAxisLeft().setDrawGridLines(false);
        mLineChart.getXAxis().setDrawAxisLine(false);
        mLineChart.getXAxis().setDrawGridLines(false);

        // enable touch gestures
        mLineChart.setTouchEnabled(true);

        // enable scaling and dragging
        mLineChart.setDragEnabled(true);
        mLineChart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mLineChart.setPinchZoom(true);

        Legend l = mLineChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < 15; i++) {
            xVals.add((i) + "");
        }

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();

        for (int z = 0; z < devices.size(); z++) {
            ArrayList<Entry> values = new ArrayList<Entry>();

            for (int i = 0; i < 15; i++) {
                double val = (Math.random() * 100) + 3;
                values.add(new Entry((float) val, i));
            }

            LineDataSet d = new LineDataSet(values, devices.get(z));
            d.setLineWidth(2.5f);
            d.setCircleSize(3f);

            int color = mColors[z];
            d.setColor(color);
            d.setCircleColor(color);
            dataSets.add(d);
        }

        /* make the first DataSet dashed
        dataSets.get(0).enableDashedLine(10, 10, 0);
        dataSets.get(0).setColors(ColorTemplate.VORDIPLOM_COLORS);
        dataSets.get(0).setCircleColors(ColorTemplate.VORDIPLOM_COLORS);
        */

        LineData data = new LineData(xVals, dataSets);
        mLineChart.setData(data);
        //mLineChart.invalidate();
        mLineChart.animateY(3000);

        final ListView listView = (ListView) view.findViewById(R.id.deviceList);

        ArrayAdapter<String> deviceAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_device, R.id.textDevice, devices);
        //listView.setAdapter(deviceAdapter);
        listView.setAdapter(new DeviceListAdapter(getActivity(), R.layout.list_item_device, devices));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int itemPosition = position;
                String itemValue = (String) listView.getItemAtPosition(position);
                Toast.makeText(getActivity(), "Position :" + itemPosition + "  ListItem : " + itemValue, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}

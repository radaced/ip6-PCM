package ch.fhnw.ip5.powerconsumptionmanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ch.fhnw.ip5.powerconsumptionmanager.R;

/**
 * Created by Patrik on 03.12.2015.
 */
public class DeviceListAdapter extends ArrayAdapter<String> {
    private int mLayout;
    private List<String> mDevices;
    private HashMap<Integer, LineDataSet> mConsumptionDataSet = new HashMap<Integer, LineDataSet>();
    private LineChart mConsumptionChart;

    public DeviceListAdapter(Context context, int resource, List<String> objects, LineChart chart) {
        super(context, resource, objects);
        mLayout = resource;
        mDevices = objects;
        mConsumptionChart = chart;
        for(int i = 0; i < chart.getLineData().getDataSetCount(); i++) {
            mConsumptionDataSet.put(i, chart.getLineData().getDataSetByIndex(i));
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder mainViewHolder = null;

        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(mLayout, parent, false);
            final ViewHolder vh = new ViewHolder();
            vh.textDevice = (TextView) convertView.findViewById(R.id.textDevice);
            vh.textDevice.setText(mDevices.get(position));
            vh.switchDevice = (Switch) convertView.findViewById(R.id.switchDevice);
            vh.switchDevice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!vh.switchDevice.isChecked()) {
                        mConsumptionDataSet.remove(position);
                        mConsumptionChart.getLineData().removeDataSet(position);
                    }
                    else {
                        ArrayList<String> xVals = new ArrayList<String>();
                        for (int i = 0; i < 15; i++) {
                            xVals.add((i) + "");
                        }

                        ArrayList<Entry> values = new ArrayList<Entry>();
                        for (int i = 0; i < 15; i++) {
                            double val = (Math.random() * 100) + 3;
                            values.add(new Entry((float) val, i));
                        }

                        LineDataSet d = new LineDataSet(values, mDevices.get(position));
                        d.setLineWidth(2.5f);
                        d.setCircleSize(3f);

                        mConsumptionDataSet.put(position, d);

                        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
                        for(int i = 0; i < mConsumptionDataSet.size(); i++) {
                            dataSets.add(mConsumptionDataSet.get(i));
                        }

                        LineData data = new LineData(xVals, dataSets);
                        mConsumptionChart.setData(data);
                    }
                    mConsumptionChart.invalidate();
                }
            });
            convertView.setTag(vh);
        }
        else {
            mainViewHolder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    static class ViewHolder {
        TextView textDevice;
        Switch switchDevice;
    }
}

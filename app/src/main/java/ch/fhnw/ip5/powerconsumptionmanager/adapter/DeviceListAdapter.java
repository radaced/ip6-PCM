package ch.fhnw.ip5.powerconsumptionmanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;

import java.util.List;

import ch.fhnw.ip5.powerconsumptionmanager.R;

/**
 * Created by Patrik on 03.12.2015.
 */
public class DeviceListAdapter extends ArrayAdapter<String> {
    private int mLayout;
    private List<String> mObjects;
    private LineChart mConsumptionChart;

    public DeviceListAdapter(Context context, int resource, List<String> objects, LineChart chart) {
        super(context, resource, objects);
        mLayout = resource;
        mObjects = objects;
        mConsumptionChart = chart;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder mainViewHolder = null;

        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(mLayout, parent, false);
            final ViewHolder vh = new ViewHolder();
            vh.textDevice = (TextView) convertView.findViewById(R.id.textDevice);
            vh.textDevice.setText(mObjects.get(position));
            vh.switchDevice = (Switch) convertView.findViewById(R.id.switchDevice);
            vh.switchDevice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LineData data = mConsumptionChart.getLineData();

                    if(!vh.switchDevice.isChecked()) {
                        data.removeDataSet(position);
                        mConsumptionChart.invalidate();
                    }

                    Toast.makeText(getContext(), "Switch switched " + position + " " + vh.switchDevice.isChecked(), Toast.LENGTH_SHORT).show();
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

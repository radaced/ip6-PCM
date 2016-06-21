package ch.fhnw.ip6.powerconsumptionmanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.util.helper.ChartHelper;
import ch.fhnw.ip6.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;

/**
 * List adapter to manage the device list that is shown below consumption chart and some logic for
 * the interaction with the device list.
 */
public class ConsumptionDeviceListAdapter extends ArrayAdapter<String> {
    private int mLayout;
    private List<String> mDevices;
    private ChartHelper mChartHelper;

    public ConsumptionDeviceListAdapter(Context context, int resource, ArrayList<String> objects, ChartHelper chart) {
        super(context, resource, objects);
        mLayout = resource;
        mDevices = objects;
        mChartHelper = chart;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(mLayout, parent, false);
        }

        // Check if devices/data could be loaded
        if(mChartHelper == null && mDevices.size() == 1) {
            TextView noDevice = (TextView) convertView.findViewById(R.id.textNoDevice);
            noDevice.setText(mDevices.get(position));
        } else {
            final ViewHolder vh = new ViewHolder();

            vh.tvDevice = (TextView) convertView.findViewById(R.id.tvDevice);
            vh.tvDevice.setText(mDevices.get(position));

            // Set color indicator to see which device is connected to each graph
            vh.vGraphColor = convertView.findViewById(R.id.vGraphColor);
            vh.vGraphColor.setBackgroundColor(mChartHelper.getGraphColor(position));

            vh.swDevice = (Switch) convertView.findViewById(R.id.swDevice);
            vh.swDevice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int shiftPos = getShiftPosition(position);
                    if (!vh.swDevice.isChecked()) {
                            /* Data sets are managed in a list and as soon one device graph is not shown in the chart anymore
                             * the indices are out of sync
                             */
                        mChartHelper.getChart().getLineData().removeDataSet(position - shiftPos);
                        mChartHelper.displayNoneAnimated();
                        // Add removed data set index to ignore list
                        mChartHelper.getRemovedDataSetIndexes().add(position);
                    } else {
                        PowerConsumptionManagerAppContext appContext = (PowerConsumptionManagerAppContext) getContext().getApplicationContext();
                        // Indicate that graph of certain device is visible again (remove from ignore list
                        mChartHelper.getRemovedDataSetIndexes().remove(Integer.valueOf(position));
                        // Generate new data set with the correct consumption data
                        mChartHelper.generateDataSet(appContext.getConsumptionData().get(position - shiftPos), position - shiftPos);
                        mChartHelper.updateChartData();
                        mChartHelper.displayNoneAnimated();
                    }
                }
            });
            convertView.setTag(vh);
        }

        return convertView;
    }

    /**
     * @param position Index of the device-list that has been clicked
     * @return Amount of not shown device-graphs in the chart that have a smaller index in the list view as position
     */
    public int getShiftPosition(int position) {
        int shiftPos = 0;
        for(int i = 0; i < mChartHelper.getRemovedDataSetIndexes().size(); i++) {
            if(mChartHelper.getRemovedDataSetIndexes().get(i) < position) {
                shiftPos++;
            }
        }
        return shiftPos;
    }

    /**
     * View holder contains all ui elements on a list item
     */
    static class ViewHolder {
        TextView tvDevice;
        View vGraphColor;
        Switch swDevice;
    }
}

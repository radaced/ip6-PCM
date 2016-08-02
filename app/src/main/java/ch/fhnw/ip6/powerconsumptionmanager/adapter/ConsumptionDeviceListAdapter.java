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
import ch.fhnw.ip6.powerconsumptionmanager.util.helper.ConsumptionDataHelper;
import ch.fhnw.ip6.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;

/**
 * List adapter to manage the device list that is shown below consumption chart and some logic for
 * the interaction with the device list.
 */
public class ConsumptionDeviceListAdapter extends ArrayAdapter<String> {

    PowerConsumptionManagerAppContext mAppContext;

    private int mLayout;
    private List<String> mDevices;
    private ConsumptionDataHelper mConsumptionDataHelper;

    /**
     * Constructor to initialize the consumption device list.
     * @param context Context of the list.
     * @param resource XML layout of the list item.
     * @param objects A list of strings that will be the labels per list item.
     * @param consumptionDataHelper A helper class instance for the consumption data.
     */
    public ConsumptionDeviceListAdapter(Context context, int resource, ArrayList<String> objects, ConsumptionDataHelper consumptionDataHelper) {
        super(context, resource, objects);
        mAppContext = (PowerConsumptionManagerAppContext) getContext().getApplicationContext();
        mLayout = resource;
        mDevices = objects;
        mConsumptionDataHelper = consumptionDataHelper;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Sets the layout of the list items from the xml
        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(mLayout, parent, false);
        }

        // View of one list item
        final ViewHolder vh = new ViewHolder();

        // Populate device names as a label per list item
        vh.tvDevice = (TextView) convertView.findViewById(R.id.tvDevice);
        vh.tvDevice.setText(mDevices.get(position));

        // Set color indicator to see which device is connected to which graph in the consumption data line chart
        vh.vGraphColor = convertView.findViewById(R.id.vGraphColor);
        vh.vGraphColor.setBackgroundColor(mConsumptionDataHelper.getGraphColor(position));

        // Define the switch button logic that is on every list item
        vh.swDevice = (Switch) convertView.findViewById(R.id.swDevice);
        vh.swDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int shiftPos = getShiftPosition(position);
                if (!vh.swDevice.isChecked()) {
                    /* Data sets are managed in a list and as soon as one device graph is not shown in the chart anymore
                     * the indices are out of sync
                     */
                    mConsumptionDataHelper.getChart().getLineData().removeDataSet(position - shiftPos);
                    mConsumptionDataHelper.displayNoneAnimated();
                    // Add removed data set index to ignore list
                    mConsumptionDataHelper.getRemovedDataSetIndexes().add(position);
                } else {
                    // Indicate that graph of certain device is visible again (remove from ignore list
                    mConsumptionDataHelper.getRemovedDataSetIndexes().remove(Integer.valueOf(position));
                    // Generate new data set with the correct consumption data
                    String device = mDevices.get(position - shiftPos);
                    mConsumptionDataHelper.generateDataSet(
                        device,
                        mAppContext.getPCMData().getComponentData().get(device).getConsumptionData(),
                        position - shiftPos
                    );
                    // Update
                    mConsumptionDataHelper.updateLineChartData();
                }
            }
        });
        convertView.setTag(vh);

        return convertView;
    }

    /**
     * Used to determine which line data sets need to be shown and which can be ignored.
     * @param position Index of the device-list that has been clicked.
     * @return Amount of not shown device-graphs in the chart that have a smaller index in the list view as position.
     */
    private int getShiftPosition(int position) {
        int shiftPos = 0;
        for(int i = 0; i < mConsumptionDataHelper.getRemovedDataSetIndexes().size(); i++) {
            if(mConsumptionDataHelper.getRemovedDataSetIndexes().get(i) < position) {
                shiftPos++;
            }
        }
        return shiftPos;
    }

    /**
     * View holder contains all ui elements on a list item.
     */
    static class ViewHolder {
        TextView tvDevice;
        View vGraphColor;
        Switch swDevice;
    }
}

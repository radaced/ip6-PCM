package ch.fhnw.ip6.powerconsumptionmanager.adapter;

import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ch.fhnw.ip6.powerconsumptionmanager.R;

/**
 * List adapter to display all devices that are connected to the PCM.
 */
public class ConnectedDeviceListAdapter extends ArrayAdapter<String> {

    private int mLayout;
    private List<String> mConnectedDevices;

    /**
     * Constructor to initialize the connected device list.
     * @param context Context of the list.
     * @param resource XML layout of the list item.
     * @param objects A list of strings that will be the labels per list item.
     */
    public ConnectedDeviceListAdapter(Context context, int resource, ArrayList<String> objects) {
        super(context, resource, objects);
        mLayout = resource;
        mConnectedDevices = objects;
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

        // Populate connected device names as a label per list item
        vh.tvConnectedDevice = (TextView) convertView.findViewById(R.id.tvConnectedDevice);
        vh.tvConnectedDevice.setText(mConnectedDevices.get(position));

        convertView.setTag(vh);
        return convertView;
    }

    /**
     * View holder contains all ui elements on a list item.
     */
    static class ViewHolder {
        TextView tvConnectedDevice;
    }
}

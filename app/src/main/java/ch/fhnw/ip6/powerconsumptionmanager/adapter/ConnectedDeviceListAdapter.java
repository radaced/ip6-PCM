package ch.fhnw.ip6.powerconsumptionmanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ch.fhnw.ip6.powerconsumptionmanager.R;

/**
 * Created by Patrik on 21.06.2016.
 */
public class ConnectedDeviceListAdapter extends ArrayAdapter<String> {
    private int mLayout;
    private List<String> mConnectedDevices;
    private boolean mIsOnline;

    public ConnectedDeviceListAdapter(Context context, int resource, ArrayList<String> objects, boolean isOnline) {
        super(context, resource, objects);
        mLayout = resource;
        mConnectedDevices = objects;
        mIsOnline = isOnline;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(mLayout, parent, false);
        }

        if(mIsOnline) {
            final ViewHolder vh = new ViewHolder();

            vh.tvConnectedDevice = (TextView) convertView.findViewById(R.id.tvConnectedDevice);
            vh.tvConnectedDevice.setText(mConnectedDevices.get(position));

            convertView.setTag(vh);
        } else {
            TextView noDevice = (TextView) convertView.findViewById(R.id.tvNoDevice);
            noDevice.setText(R.string.list_device_error);
        }

        return convertView;
    }

    /**
     * View holder contains all ui elements on a list item
     */
    static class ViewHolder {
        TextView tvConnectedDevice;
    }
}

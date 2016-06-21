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
import ch.fhnw.ip6.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;
import ch.fhnw.ip6.powerconsumptionmanager.util.helper.ChartHelper;

/**
 * Created by Patrik on 21.06.2016.
 */
public class ConnectedDeviceListAdapter extends ArrayAdapter<String> {
    private int mLayout;
    private List<String> mDevices;

    public ConnectedDeviceListAdapter(Context context, int resource, ArrayList<String> objects) {
        super(context, resource, objects);
        mLayout = resource;
        mDevices = objects;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        return convertView;
    }

    /**
     * View holder contains all ui elements on a list item
     */
    static class ViewHolder {
        TextView textDevice;
    }
}

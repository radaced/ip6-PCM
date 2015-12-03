package ch.fhnw.ip5.powerconsumptionmanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ch.fhnw.ip5.powerconsumptionmanager.R;

/**
 * Created by Patrik on 03.12.2015.
 */
public class DeviceListAdapter extends ArrayAdapter<String> {
    private int mLayout;
    private List<String> mObjects;

    public DeviceListAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        mLayout = resource;
        mObjects = objects;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder mainViewHolder = null;

        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(mLayout, parent, false);
            ViewHolder vh = new ViewHolder();
            vh.textDevice = (TextView) convertView.findViewById(R.id.textDevice);
            vh.textDevice.setText(mObjects.get(position));
            vh.switchDevice = (Switch) convertView.findViewById(R.id.switchDevice);
            vh.switchDevice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "Switch switched" + position, Toast.LENGTH_SHORT).show();
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

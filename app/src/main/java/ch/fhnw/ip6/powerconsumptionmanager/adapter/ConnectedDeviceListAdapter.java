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
            noDevice.setText(mConnectedDevices.get(position));
            if (Build.VERSION.SDK_INT < 23) {
                noDevice.setTextAppearance(getContext(), android.R.style.TextAppearance_Medium_Inverse);
            } else {
                noDevice.setTextAppearance(android.R.style.TextAppearance_Medium_Inverse);
            }
            LinearLayout.LayoutParams llParams = (LinearLayout.LayoutParams) noDevice.getLayoutParams();
            llParams.gravity = Gravity.CENTER;
            int density = (int) getContext().getResources().getDisplayMetrics().density;
            llParams.setMargins(
                0,
                8 * density,
                0,
                8 * density
            );
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

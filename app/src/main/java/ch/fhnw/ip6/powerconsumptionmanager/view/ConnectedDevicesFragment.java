package ch.fhnw.ip6.powerconsumptionmanager.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.adapter.ConnectedDeviceListAdapter;

public class ConnectedDevicesFragment extends ListFragment {

    public static ConnectedDevicesFragment newInstance() {
        return new ConnectedDevicesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_connected_devices, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView lvConnectedDevices = (ListView) view.findViewById(R.id.lv_ConnectedDevices);
//        lvConnectedDevices.setAdapter(new ConnectedDeviceListAdapter());
    }
}

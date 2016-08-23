package ch.fhnw.ip6.powerconsumptionmanager.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Map;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.activity.ComponentSettingsActivity;
import ch.fhnw.ip6.powerconsumptionmanager.adapter.ConnectedDeviceListAdapter;
import ch.fhnw.ip6.powerconsumptionmanager.model.PCMComponent;
import ch.fhnw.ip6.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;

/**
 * Displays all connected components/devices in a list.
 */
public class ConnectedDevicesFragment extends ListFragment {
    private ArrayList<String> mComponentNamesList;

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

        PowerConsumptionManagerAppContext appContext = (PowerConsumptionManagerAppContext) getContext().getApplicationContext();
        ListView lvConnectedDevices = (ListView) view.findViewById(android.R.id.list);
        int layoutResource;

        layoutResource = R.layout.list_connected_device;
        // Create a list of all connected devices with their name
        mComponentNamesList = new ArrayList<>();
        for (PCMComponent component : appContext.getPCMData().getComponentData().values()) {
            if(component.hasSettings()) {
                mComponentNamesList.add(component.getName());
            }
        }

        // Setup the list
        lvConnectedDevices.setAdapter(
            new ConnectedDeviceListAdapter(
                getContext(),
                layoutResource,
                mComponentNamesList
            )
        );
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        // Create a new intent to open the component settings activity to load the settings of the clicked item in the list
        Intent intent = new Intent(getActivity(), ComponentSettingsActivity.class);
        intent.putExtra("componentName", mComponentNamesList.get(position));
        getActivity().startActivity(intent);
    }
}

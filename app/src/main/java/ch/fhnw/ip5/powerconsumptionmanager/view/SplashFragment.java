package ch.fhnw.ip5.powerconsumptionmanager.view;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import ch.fhnw.ip5.powerconsumptionmanager.R;
import ch.fhnw.ip5.powerconsumptionmanager.network.DataLoader;
import ch.fhnw.ip5.powerconsumptionmanager.network.DataLoaderCallback;
import ch.fhnw.ip5.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;


public class SplashFragment extends Fragment {
    private PowerConsumptionManagerAppContext mContext;

    public static SplashFragment newInstance() {
        SplashFragment fragment = new SplashFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // When settings have changed edit info text that power consumption manager loads with new settings
        Bundle extras = getActivity().getIntent().getExtras();
        if(extras != null) {
            TextView loadingMsg = (TextView) view.findViewById(R.id.textLoadingMessage);
            loadingMsg.setText(extras.getString("settings_changed", getString(R.string.text_splash_info)));
        }

        mContext = (PowerConsumptionManagerAppContext) getActivity().getApplicationContext();
        DataLoader loader = new DataLoader(mContext, (DataLoaderCallback) getActivity());

        // Web request to load the consumption data
        loader.loadConsumptionData("http://" + mContext.getIPAdress() + ":" + getString(R.string.webservice_getData));
    }
}

package ch.fhnw.ip6.powerconsumptionmanager.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ch.fhnw.ip6.powerconsumptionmanager.R;


/**
 * This Fragment is not in use yet.
 */
public class TeslaFragment extends Fragment {
    public static TeslaFragment newInstance() {
        return new TeslaFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tesla, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        TeslaLoginFragment fragment = TeslaLoginFragment.newInstance();
        transaction.replace(R.id.tesla_content_fragment, fragment);
        transaction.commit();
    }
}

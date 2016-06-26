package ch.fhnw.ip6.powerconsumptionmanager.view.dashboard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.util.helper.DashboardHelper;
import ch.fhnw.ip6.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;
import me.itangqi.waveloadingview.WaveLoadingView;

public class OverviewFragment extends Fragment {
    private enum Mode { DAILY, NOW }

    private PowerConsumptionManagerAppContext mAppContext;
    private DashboardHelper mDashBoardHelper;
    private Mode mMode;

    public static OverviewFragment newInstance() {
        return new OverviewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_overview, container, false);
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppContext = (PowerConsumptionManagerAppContext) getActivity().getApplicationContext();
        mMode = Mode.NOW;

        mDashBoardHelper = DashboardHelper.getInstance();
        mDashBoardHelper.initOverviewContext(getContext());

        mDashBoardHelper.addSummaryView(
            getString(R.string.text_autarchy),
            (WaveLoadingView) getView().findViewById(R.id.wlvAutarchy),
            mAppContext.UNIT_PERCENTAGE
        );
        mDashBoardHelper.addSummaryView(
            getString(R.string.text_selfsupply),
            (WaveLoadingView) getView().findViewById(R.id.wlvSelfsupply),
            mAppContext.UNIT_PERCENTAGE
        );
        mDashBoardHelper.addSummaryView(
            getString(R.string.text_occupation),
            (WaveLoadingView) getView().findViewById(R.id.wlvOccupation),
            mAppContext.UNIT_KW
        );

        mDashBoardHelper.setSummaryRatio(getString(R.string.text_autarchy), 50);
        mDashBoardHelper.setSummaryRatio(getString(R.string.text_selfsupply), 20);
        mDashBoardHelper.setSummaryRatio(getString(R.string.text_occupation), -10);

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.dynamic_content_fragment, CurrentValuesFragment.newInstance());
        transaction.commit();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.overview_menu, menu);

        if(mMode == Mode.DAILY) {
            MenuItem now = menu.findItem(R.id.action_now);
            now.setVisible(false);
        } else {
            MenuItem daily = menu.findItem(R.id.action_daily);
            daily.setVisible(false);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        switch (item.getItemId()) {
            case R.id.action_daily:
                mMode = Mode.NOW;
                transaction.replace(R.id.dynamic_content_fragment, CurrentValuesFragment.newInstance());
                break;
            case R.id.action_now:
                mMode = Mode.DAILY;
                transaction.replace(R.id.dynamic_content_fragment, DailyValuesFragment.newInstance());
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        transaction.commit();
        getActivity().invalidateOptionsMenu();

        return true;
    }
}
package ch.fhnw.ip6.powerconsumptionmanager.view.dashboard;

import android.os.Bundle;
import android.os.Handler;
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
import ch.fhnw.ip6.powerconsumptionmanager.network.AsyncTaskCallback;
import ch.fhnw.ip6.powerconsumptionmanager.network.GetCurrentPCMDataAsyncTask;
import ch.fhnw.ip6.powerconsumptionmanager.util.helper.DashboardHelper;
import ch.fhnw.ip6.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;
import me.itangqi.waveloadingview.WaveLoadingView;

public class OverviewFragment extends Fragment implements AsyncTaskCallback {
    private enum Mode { DAILY, NOW }

    private PowerConsumptionManagerAppContext mAppContext;
    private DashboardHelper mDashboardHelper;
    private Handler mUpdateHandler;
    private Mode mMode;

    public static OverviewFragment newInstance() {
        return new OverviewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mAppContext = (PowerConsumptionManagerAppContext) getActivity().getApplicationContext();
        View view = inflater.inflate(R.layout.fragment_overview, container, false);
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMode = Mode.NOW;
        mDashboardHelper = DashboardHelper.getInstance();
        mDashboardHelper.initOverviewContext(getContext());

        mDashboardHelper.addSummaryView(
            getString(R.string.text_autarchy),
            (WaveLoadingView) getView().findViewById(R.id.wlvAutarchy),
            mAppContext.UNIT_PERCENTAGE
        );
        mDashboardHelper.addSummaryView(
            getString(R.string.text_selfsupply),
            (WaveLoadingView) getView().findViewById(R.id.wlvSelfsupply),
            mAppContext.UNIT_PERCENTAGE
        );
        mDashboardHelper.addSummaryView(
            getString(R.string.text_consumption),
            (WaveLoadingView) getView().findViewById(R.id.wlvConsumption),
            mAppContext.UNIT_KW
        );

        mDashboardHelper.setSummaryRatio(getString(R.string.text_autarchy), (int) mAppContext.getPCMData().getAutarchy());
        mDashboardHelper.setSummaryRatio(getString(R.string.text_selfsupply), (int) mAppContext.getPCMData().getSelfsupply());
        mDashboardHelper.setSummaryRatio(getString(R.string.text_consumption), (int) mAppContext.getPCMData().getConsumption());

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.dynamic_content_fragment, CurrentValuesFragment.newInstance());
        transaction.commit();

        // Instantiate the update handler
        mUpdateHandler = new Handler();
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

    @Override
    public void onStop() {
        super.onStop();
        if(mUpdateHandler != null) {
            mUpdateHandler.removeCallbacks(updateCurrentPCMData);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mUpdateHandler != null) {
            mUpdateHandler.postDelayed(updateCurrentPCMData, 10000);
        }
    }

    @Override
    public void asyncTaskFinished(boolean result) {
        mDashboardHelper.updateOverview();
        if(mMode == Mode.NOW) {
            mDashboardHelper.updateCurrentValues();
        } else {
            mDashboardHelper.updateDailyValues();
        }
    }

    private final Runnable updateCurrentPCMData = new Runnable() {
        public void run() {
        new GetCurrentPCMDataAsyncTask(
                mAppContext,
                getInstance()
        ).execute();
        mUpdateHandler.postDelayed(this, 10000);
        }
    };

    private OverviewFragment getInstance() { return this; }
}
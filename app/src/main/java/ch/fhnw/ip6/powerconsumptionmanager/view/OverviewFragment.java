package ch.fhnw.ip6.powerconsumptionmanager.view;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.gigamole.library.ArcProgressStackView;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.util.DashboardHelper;
import ch.fhnw.ip6.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;
import hirondelle.date4j.DateTime;
import me.itangqi.waveloadingview.WaveLoadingView;

public class OverviewFragment extends Fragment {
    public final static String AUTARCHY = "Autarchy";
    public final static String SELF_CONSUMPTION = "Self consumption";
    public final static String OCCUPATION = "Occupation";

    private enum Mode { DAILY, NOW };
    private final String TAG_DAILY = "daily";
    private final String TAG_NOW = "now";

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

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.dynamic_content_fragment, new NowFragment(), TAG_NOW);
        transaction.add(R.id.dynamic_content_fragment, new DailyFragment(), TAG_DAILY);
        transaction.commit();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppContext = (PowerConsumptionManagerAppContext) getActivity().getApplicationContext();
        mMode = Mode.NOW;

        mDashBoardHelper = DashboardHelper.getInstance();
        mDashBoardHelper.init(getContext(), null);

        mDashBoardHelper.addSummaryView(AUTARCHY, (WaveLoadingView) getView().findViewById(R.id.wlvAutarchy), mAppContext.UNIT_PERCENTAGE);
        mDashBoardHelper.addSummaryView(SELF_CONSUMPTION, (WaveLoadingView) getView().findViewById(R.id.wlvSelfConsumption), mAppContext.UNIT_PERCENTAGE);
        mDashBoardHelper.addSummaryView(OCCUPATION, (WaveLoadingView) getView().findViewById(R.id.wlvOccupation), mAppContext.UNIT_KW);

        mDashBoardHelper.setSummaryRatio(AUTARCHY, 50);
        mDashBoardHelper.setSummaryRatio(SELF_CONSUMPTION, 20);
        mDashBoardHelper.setSummaryRatio(OCCUPATION, -10);
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
        switch (item.getItemId()) {
            case R.id.action_daily:
                mMode = Mode.NOW;
                switchFragments(TAG_NOW, TAG_DAILY);
                break;
            case R.id.action_now:
                mMode = Mode.DAILY;
                switchFragments(TAG_DAILY, TAG_NOW);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        getActivity().invalidateOptionsMenu();

        return true;
    }

    private void switchFragments(String attachTag, String detachTag) {
        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(attachTag);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.detach(getActivity().getSupportFragmentManager().findFragmentByTag(detachTag));
        transaction.attach(fragment);
        transaction.commit();
    }
}
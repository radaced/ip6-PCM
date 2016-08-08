package ch.fhnw.ip6.powerconsumptionmanager.view.dashboard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.util.helper.DashboardHelper;

/**
 * Displays a bar chart that shows daily statistics to every component.
 */
public class DailyValuesFragment extends Fragment {
    private DashboardHelper mDashboardHelper;

    public static DailyValuesFragment newInstance() {
        return new DailyValuesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily, container, false);

        // Setup helper instance
        mDashboardHelper = DashboardHelper.getInstance();
        mDashboardHelper.initDailyValuesContext(getContext());
        mDashboardHelper.setDailyDataBarChart((BarChart) view.findViewById(R.id.bcDailyData));

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup the bar chart
        mDashboardHelper.setupDailyBarChartStyle();
        mDashboardHelper.setupDailyBarChartData();
    }
}

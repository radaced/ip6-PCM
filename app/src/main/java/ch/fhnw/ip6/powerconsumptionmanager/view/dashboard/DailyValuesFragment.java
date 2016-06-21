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

public class DailyValuesFragment extends Fragment {
    private DashboardHelper mDashBoardHelper;

    public static DailyValuesFragment newInstance() {
        return new DailyValuesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily, container, false);

        mDashBoardHelper = DashboardHelper.getInstance();
        mDashBoardHelper.initDailyValuesContext(getContext());
        mDashBoardHelper.setDailyDataBarChart((BarChart) view.findViewById(R.id.dailyDataBarChart));

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDashBoardHelper.setupDailyBarChartStyle();
        mDashBoardHelper.setupDailyBarChartData();
    }
}

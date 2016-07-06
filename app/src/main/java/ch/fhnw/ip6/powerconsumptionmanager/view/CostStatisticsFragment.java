package ch.fhnw.ip6.powerconsumptionmanager.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;

import ch.fhnw.ip6.powerconsumptionmanager.R;

public class CostStatisticsFragment extends Fragment {

    public static CostStatisticsFragment newInstance() { return new CostStatisticsFragment(); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cost_statistics, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BarChart sbcStatisticsOverview = (BarChart) view.findViewById(R.id.sbcStatisticsOverview);
        BarChart sbcComponentOverview = (BarChart) view.findViewById(R.id.sbcStatisticsComponent);
    }
}

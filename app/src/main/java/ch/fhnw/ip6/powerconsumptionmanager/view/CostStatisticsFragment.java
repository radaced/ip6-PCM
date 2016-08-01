package ch.fhnw.ip6.powerconsumptionmanager.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.network.AsyncTaskCallback;
import ch.fhnw.ip6.powerconsumptionmanager.network.GetStatisticsAsyncTask;
import ch.fhnw.ip6.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;
import ch.fhnw.ip6.powerconsumptionmanager.util.helper.CostStatisticsHelper;

public class CostStatisticsFragment extends Fragment implements AsyncTaskCallback {

    private PowerConsumptionManagerAppContext mAppContext;
    private CostStatisticsHelper mCostStatisticsHelper;
    private LinearLayout mLoadingLayout;
    private LinearLayout mCostStatisticsLayout;
    private LinearLayout mOnErrorCostStatisticsLayout;
    private TextView mLoadingInfo;

    public static CostStatisticsFragment newInstance() { return new CostStatisticsFragment(); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cost_statistics, container, false);
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppContext = (PowerConsumptionManagerAppContext) getActivity().getApplicationContext();

        mLoadingLayout = (LinearLayout) view.findViewById(R.id.llLoading);
        mCostStatisticsLayout = (LinearLayout) view.findViewById(R.id.llCostStatistics);
        mOnErrorCostStatisticsLayout = (LinearLayout) view.findViewById(R.id.llOnErrorCostStatistics);
        mLoadingInfo = (TextView) view.findViewById(R.id.tvLoadingInfo);

        BarChart sbcStatisticsOverview = (BarChart) view.findViewById(R.id.sbcStatisticsOverview);
        BarChart sbcComponentOverview = (BarChart) view.findViewById(R.id.sbcStatisticsComponent);
        mCostStatisticsHelper = new CostStatisticsHelper(getContext(), sbcStatisticsOverview, sbcComponentOverview);

        new GetStatisticsAsyncTask(mAppContext, this).execute();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.cost_statistics_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                mCostStatisticsLayout.setVisibility(View.GONE);
                mLoadingInfo.setText(getString(R.string.text_refreshing_cost_statistics));
                mLoadingLayout.setVisibility(View.VISIBLE);
                new GetStatisticsAsyncTask(mAppContext, this).execute();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public void asyncTaskFinished(boolean result) {
        mLoadingLayout.setVisibility(View.GONE);

        if(result) {
            mOnErrorCostStatisticsLayout.setVisibility(View.GONE);
            mCostStatisticsLayout.setVisibility(View.VISIBLE);

            mCostStatisticsHelper.setupStackedBarChartData();
        } else {
            mCostStatisticsLayout.setVisibility(View.GONE);
            mOnErrorCostStatisticsLayout.setVisibility(View.VISIBLE);
        }
    }
}

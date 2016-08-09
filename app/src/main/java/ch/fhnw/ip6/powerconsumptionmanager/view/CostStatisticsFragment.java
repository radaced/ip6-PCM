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

/**
 * This fragment shows the cost statistics data of components in two stacked bar charts.
 */
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

        // Load the different layouts into member fields for easier access
        mLoadingLayout = (LinearLayout) view.findViewById(R.id.llLoading);
        mCostStatisticsLayout = (LinearLayout) view.findViewById(R.id.llCostStatistics);
        mOnErrorCostStatisticsLayout = (LinearLayout) view.findViewById(R.id.llOnErrorCostStatistics);
        mLoadingInfo = (TextView) view.findViewById(R.id.tvLoadingInfo);

        // Set up the helper class and load the statistics data
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
            // Refresh the cost statistics data when the options menu item was pressed
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

    /**
     * Return point from requests that load the cost statistics data.
     * @param result Status if the data could be loaded successfully or not.
     * @param opType Type of operation that has completed.
     */
    @Override
    public void asyncTaskFinished(boolean result, String opType) {
        if(CostStatisticsFragment.this.isVisible()) {
            // Hide the loading layout
            mLoadingLayout.setVisibility(View.GONE);

            if (result) {
                // Display the stacked bar charts with the loaded data
                mOnErrorCostStatisticsLayout.setVisibility(View.GONE);
                mCostStatisticsLayout.setVisibility(View.VISIBLE);

                mCostStatisticsHelper.setupStackedBarChartData();
            } else {
                // Display an error message to the user
                mCostStatisticsLayout.setVisibility(View.GONE);
                mOnErrorCostStatisticsLayout.setVisibility(View.VISIBLE);
            }
        }
    }
}

package ch.fhnw.ip5.powerconsumptionmanager.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.roomorama.caldroid.CaldroidFragment;

import java.util.Calendar;
import java.util.Date;

import ch.fhnw.ip5.powerconsumptionmanager.R;
import ch.fhnw.ip5.powerconsumptionmanager.util.PlanHelper;

/**
 * This Fragment shows the charge plan created by the user, but managed over the standard
 * google calendar.
 */
public class PlanFragment extends Fragment {
    CaldroidFragment mCaldroidFragment;
    PlanHelper mPlanHelper;
    TextView mTitle;
    TextView mTimeRange;
    TextView mRoute;
    TextView mRouteInformation;
    TextView mDescription;

    public static PlanFragment newInstance() {
        return new PlanFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_plan, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the UI elements
        mTitle = (TextView) view.findViewById(R.id.caldroid_info_title);
        mTimeRange = (TextView) view.findViewById(R.id.caldroid_info_timerange);
        mRoute = (TextView) view.findViewById(R.id.caldroid_info_route);
        mRouteInformation = (TextView) view.findViewById(R.id.caldroid_info_route_information);
        mDescription = (TextView) view.findViewById(R.id.caldroid_info_description);

        // Objects to set up caldroid
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        mCaldroidFragment = new CaldroidFragment();
        mPlanHelper = new PlanHelper(mCaldroidFragment, this);
        Calendar cal = Calendar.getInstance();

        // Check if caldroid fragment already exists (after screen rotation) and set saved data if needed
        if (savedInstanceState != null) {
            mCaldroidFragment.restoreStatesFromKey(savedInstanceState, "CALDROID_SAVED_STATE");
            long selectedDay = savedInstanceState.getLong("selectedDay");
            if(selectedDay != 0) {
                mCaldroidFragment.setBackgroundResourceForDate(R.drawable.caldroid_selected_day, new Date(selectedDay));
                mPlanHelper.setSelectedDate(new Date(selectedDay));
            }
            mTitle.setText(savedInstanceState.getString("mTitle"));
            mTimeRange.setText(savedInstanceState.getString("mTimeRange"));
            mRoute.setText(savedInstanceState.getString("mRoute"));
            mRouteInformation.setText(savedInstanceState.getString("mRouteInformation"));
            mDescription.setText(savedInstanceState.getString("mDescription"));
            cal.setTime(new Date(selectedDay));
        }

        // Set up the calendar fragment with the helper class
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        mPlanHelper.setup(cal);

        // Get date range ends (reading instances for 1 month)
        long startOfMonth = mPlanHelper.generateLowerMonthRangeEnd(year, month);
        long endOfMonth = mPlanHelper.generateUpperMonthRangeEnd(year, month);

        // Read calender instances
        mPlanHelper.readPlannedTrips(startOfMonth, endOfMonth);
        mPlanHelper.markDays();
        mPlanHelper.generateListener();

        // Load fragment into container
        transaction.replace(R.id.caldroid_fragment, mPlanHelper.getCaldroid());
        transaction.commit();
    }


    /**
     * Save states when phone gets rotated.
     * @param outState States to save.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mCaldroidFragment != null) {
            mCaldroidFragment.saveStatesToKey(outState, "CALDROID_SAVED_STATE");
            outState.putLong("selectedDay", mPlanHelper.getSelectedDate().getTime());
            outState.putString("mTitle", mTitle.getText().toString());
            outState.putString("mTimeRange", mTimeRange.getText().toString());
            outState.putString("mRoute", mRoute.getText().toString());
            outState.putString("mRouteInformation", mRouteInformation.getText().toString());
            outState.putString("mDescription", mDescription.getText().toString());
        }
    }
}
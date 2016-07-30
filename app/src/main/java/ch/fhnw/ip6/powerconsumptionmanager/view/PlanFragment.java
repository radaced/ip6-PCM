package ch.fhnw.ip6.powerconsumptionmanager.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.roomorama.caldroid.CaldroidFragment;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.util.helper.PlanCalendarViewHelper;

/**
 * This Fragment shows the charge plan created by the user, but managed over the standard
 * google calendar.
 */
public class PlanFragment extends Fragment {
    CaldroidFragment mCaldroidFragment;
    PlanCalendarViewHelper mPlanCalendarViewHelper;
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
//        super.onViewCreated(view, savedInstanceState);
//
//        // Initialize the UI elements
//        mTitle = (TextView) view.findViewById(R.id.tvInfoTitle);
//        mTimeRange = (TextView) view.findViewById(R.id.tvInfoTimerange);
//        mRoute = (TextView) view.findViewById(R.id.tvInfoRoute);
//        mRouteInformation = (TextView) view.findViewById(R.id.tvInfoRouteInformation);
//        mDescription = (TextView) view.findViewById(R.id.tvInfoDescription);
//
//        // Objects to set up caldroid
//        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//        mCaldroidFragment = new CaldroidFragment();
//        mPlanCalendarViewHelper = new PlanCalendarViewHelper(mCaldroidFragment, this);
//        Calendar cal = Calendar.getInstance();
//
//        // Check if caldroid fragment already exists (after screen rotation) and set saved data if needed
//        if (savedInstanceState != null) {
//            mCaldroidFragment.restoreStatesFromKey(savedInstanceState, "CALDROID_SAVED_STATE");
//            long selectedDay = savedInstanceState.getLong("selectedDay");
//            if(selectedDay != 0) {
//                mCaldroidFragment.setBackgroundResourceForDate(R.drawable.caldroid_selected_day, new Date(selectedDay));
//                mPlanCalendarViewHelper.setSelectedDate(new Date(selectedDay));
//            }
//            mTitle.setText(savedInstanceState.getString("mTitle"));
//            mTimeRange.setText(savedInstanceState.getString("mTimeRange"));
//            mRoute.setText(savedInstanceState.getString("mRoute"));
//            mRouteInformation.setText(savedInstanceState.getString("mRouteInformation"));
//            mDescription.setText(savedInstanceState.getString("mDescription"));
//            if(!savedInstanceState.getString("mDescription").equals("")) {
//                mDescription.setBackgroundResource(R.color.colorTextViewBackground);
//                mDescription.setMovementMethod(ScrollingMovementMethod.getInstance());
//            }
//            cal.setTime(new Date(selectedDay));
//        }
//
//        // Set up the calendar fragment with the helper class
//        int month = cal.get(Calendar.MONTH);
//        int year = cal.get(Calendar.YEAR);
//        mPlanCalendarViewHelper.setup(cal);
//
//        // Get date range ends (reading instances for 1 month)
//        long startOfMonth = mPlanCalendarViewHelper.getMonthStart(year, month);
//        long endOfMonth = mPlanCalendarViewHelper.getMonthEnd(year, month);
//
//        // Read calender instances
//        mPlanCalendarViewHelper.readPlannedTrips(startOfMonth, endOfMonth);
//        mPlanCalendarViewHelper.markDays();
//        mPlanCalendarViewHelper.generateListener();
//
//        // Load fragment into container
//        transaction.replace(R.id.caldroid_fragment, mPlanCalendarViewHelper.getCaldroid());
//        transaction.commit();
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
            outState.putLong("selectedDay", mPlanCalendarViewHelper.getSelectedDate().getTime());
            outState.putString("mTitle", mTitle.getText().toString());
            outState.putString("mTimeRange", mTimeRange.getText().toString());
            outState.putString("mRoute", mRoute.getText().toString());
            outState.putString("mRouteInformation", mRouteInformation.getText().toString());
            outState.putString("mDescription", mDescription.getText().toString());
        }
    }
}
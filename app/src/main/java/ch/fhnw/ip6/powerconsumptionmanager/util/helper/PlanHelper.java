package ch.fhnw.ip6.powerconsumptionmanager.util.helper;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.model.PlanEntryModel;
import ch.fhnw.ip6.powerconsumptionmanager.model.RouteInformationModel;
import ch.fhnw.ip6.powerconsumptionmanager.network.DataLoader;
import ch.fhnw.ip6.powerconsumptionmanager.network.DataLoaderCallback;
import ch.fhnw.ip6.powerconsumptionmanager.util.CalendarInstanceReader;
import ch.fhnw.ip6.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;
import ch.fhnw.ip6.powerconsumptionmanager.view.PlanFragment;

/**
 * Helper class to handle and modify caldroid
 */
public class PlanHelper implements DataLoaderCallback {
    // The caldroid fragment itself
    private CaldroidFragment mCaldroid;
    // Contexts
    /* TODO: Change to type Context */
    private PlanFragment mContext;
    private PowerConsumptionManagerAppContext mAppContext;
    // Calendar instance to make date operations
    private Calendar mCalendar;
    // Holds the read instances from the calendar.instances table of one month
    private HashMap<Integer, PlanEntryModel> mInstances;
    // The actual selected date in the caldroid fragment
    private Date mSelectedDate = new Date();

    public PlanHelper(CaldroidFragment caldroid, PlanFragment context) {
        mCaldroid = caldroid;
        mContext = context;
        mAppContext = (PowerConsumptionManagerAppContext) mContext.getActivity().getApplicationContext();
        mInstances = new HashMap<>();
        mCalendar = Calendar.getInstance();
    }

    /**
     * Initial settings for the caldroid fragment (when rotated don't set month and year)
     * @param cal Calendar object with the month and year to set
     */
    public void setup(Calendar cal) {
        Bundle args = new Bundle();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        args.putInt(CaldroidFragment.START_DAY_OF_WEEK, CaldroidFragment.MONDAY);
        args.putInt(CaldroidFragment.THEME_RESOURCE, R.style.CustomCaldroidTheme);
        args.putBoolean(CaldroidFragment.SHOW_NAVIGATION_ARROWS, false);
        args.putBoolean(CaldroidFragment.ENABLE_CLICK_ON_DISABLED_DATES, false);
        args.putBoolean(CaldroidFragment.SQUARE_TEXT_VIEW_CELL, false);
        mCaldroid.setArguments(args);
    }

    /**
     * Generates the lower range from the range the calendar.instances table should be read (start of month)
     * @param year Year
     * @param month Month
     * @return First day of month
     */
    public long generateLowerMonthRangeEnd(int year, int month) {
        mCalendar = Calendar.getInstance();
        mCalendar.set(year, month, 1, 0, 0, 0);
        return mCalendar.getTimeInMillis();
    }

    /** Generates the upper range from the range the calendar.instances table should be read (end of month)
     * @param year Year
     * @param month Month
     * @return Last day of month
     */
    public long generateUpperMonthRangeEnd(int year, int month) {
        mCalendar = Calendar.getInstance();
        mCalendar.set(year, month, 1, 23, 59, 59);
        mCalendar.set(year, month, mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
        return mCalendar.getTimeInMillis();
    }

    /**
     * Reads all planned tesla trips between two dates from the calendar.instances table
     * @param lowerRangeEnd Lower end of the dates to read in calendar.instances table
     * @param upperRangeEnd Upper end of the dates to read in calendar.instances table
     */
    public void readPlannedTrips(long lowerRangeEnd, long upperRangeEnd) {
        CalendarInstanceReader cir = new CalendarInstanceReader(mCalendar, mContext.getContext());
        mInstances = cir.readInstancesBetweenTimestamps(lowerRangeEnd, upperRangeEnd);
    }

    /**
     * Mark all dates in the caldroid fragment that have a tesla trip instance
     */
    public void markDays() {
        // Iterate through all read calendar instances
        for (Map.Entry pair : mInstances.entrySet()) {
            PlanEntryModel pem = (PlanEntryModel) pair.getValue();
            mCalendar.setTime(pem.getBegin());
            mCaldroid.setSelectedDate(mCalendar.getTime());
        }

        // Update caldroid fragment
        mCaldroid.refreshView();
    }

    /**
     * Define the listener for the caldroid fragment
     */
    public void generateListener() {
        // Date format masks to display dates
        final SimpleDateFormat titleFormat = new SimpleDateFormat(mContext.getString(R.string.format_caldroid_info_title));
        final SimpleDateFormat timeRangeFormat = new SimpleDateFormat(mContext.getString(R.string.format_caldroid_info_timerange));

        CaldroidListener listener = new CaldroidListener() {
            @Override
            public void onSelectDate(Date date, View view) {
                mCalendar.setTime(date);
                int pressedDay = mCalendar.get(Calendar.DAY_OF_MONTH);

                // Mark selected day with accent color and load data to the instance on that day
                if(mInstances.containsKey(pressedDay) && !mSelectedDate.equals(date)) {
                    // Modify look and feel
                    mCaldroid.setBackgroundResourceForDate(R.drawable.caldroid_selected_day, date);
                    mCaldroid.clearBackgroundResourceForDate(mSelectedDate);
                    mSelectedDate = date;
                    mCaldroid.refreshView();

                    // Display instance data
                    PlanEntryModel pem = mInstances.get(pressedDay);
                    View v = mContext.getView();
                    // Title
                    TextView title = (TextView) v.findViewById(R.id.caldroid_info_title);
                    title.setText(titleFormat.format(pem.getBegin()));
                    // Description
                    TextView description = (TextView) v.findViewById(R.id.caldroid_info_description);
                    if(!pem.getDescription().equals("")) {
                        description.setText(pem.getDescription());
                    } else {
                        description.setText(mContext.getString(R.string.text_information_no_description));
                    }
                    description.setBackgroundResource(R.color.colorTextViewBackground);
                    description.setMovementMethod(ScrollingMovementMethod.getInstance());
                    // Time range of instance
                    TextView timeRange = (TextView) v.findViewById(R.id.caldroid_info_timerange);
                    String time = timeRangeFormat.format(pem.getBegin()) + " - " + timeRangeFormat.format(pem.getEnd());
                    timeRange.setText(time);
                    /*
                     * Display route and load distance and duration to reach destination between two given
                     * locations from the instance
                     */
                    TextView routeOrigDest = (TextView) v.findViewById(R.id.caldroid_info_route);
                    String[] locations = pem.getEventLocation().split("/");
                    String route = "-";
                    if(locations.length == 2 && !"".equals(locations[0]) && !"".equals(locations[1])) {
                        route = locations[0].trim() +
                                       " " +
                                       mContext.getString(R.string.text_route_information_route_to) +
                                       " " +
                                       locations[1].trim();
                        calculateDistance(locations[0].trim(), locations[1].trim());
                    } else {
                        displayRouteInformation(v, mContext.getString(R.string.text_route_information_no_data), "");
                    }
                    routeOrigDest.setText(route);
                }
            }

            @Override
            public void onChangeMonth(int month, int year) {
                /*
                 * Read calendar.instances table with new range and mark days that contain an instance
                 * -1 because caldroid calculates months from 1-12 and Calendar.class does it with 0-11
                 */
                long startMonth = generateLowerMonthRangeEnd(year, month-1);
                long endMonth = generateUpperMonthRangeEnd(year, month-1);
                mInstances.clear();
                readPlannedTrips(startMonth, endMonth);
                markDays();
            }
        };

        mCaldroid.setCaldroidListener(listener);
    }

    public CaldroidFragment getCaldroid() {
        return mCaldroid;
    }

    public Date getSelectedDate() {
        return mSelectedDate;
    }

    public void setSelectedDate(Date mSelectedDate) {
        this.mSelectedDate = mSelectedDate;
    }

    /**** Return point from requests that were called after a day field was pressed in caldroid ****/
    @Override
    public void DataLoaderDidFinish() {
        // Update the text view field for the route information with the loaded data on the UI thread
        mContext.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View view = mContext.getView();
                RouteInformationModel rim = mAppContext.getRouteInformation();

                // Check if a route existed
                if (rim.getDistanceText().equals("")) {
                    displayRouteInformation(view, mContext.getString(R.string.text_route_information_no_route), "");
                } else {
                    displayRouteInformation(
                        view,
                        mContext.getString(R.string.text_route_information_distance) + " " + rim.getDistanceText(),
                        mContext.getString(R.string.text_route_information_duration) + " " + rim.getDurationText()
                    );
                }
            }
        });
    }

    @Override
    public void DataLoaderDidFail() {
        // Update the text view field for the route information with the error message on the UI thread
        mContext.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View view = mContext.getView();
                displayRouteInformation(view, mContext.getString(R.string.text_route_information_error), "");
            }
        });
    }
    /********/

    /**
     * Call google.maps API with the origin and destination location to find out distance and duration
     * of the planned trip
     * @param origin The origin of the route
     * @param destination The destination of the route
     */
    private void calculateDistance(String origin, String destination) {
        DataLoader loader = new DataLoader(mAppContext, this);
        loader.loadRouteInformation(
            mContext.getString(R.string.googleMaps_Api1) +
            "origin=" + origin +
            "&destination=" + destination +
            mContext.getString(R.string.googleMaps_Api2)
        );
    }

    /**
     * Displays the route information (error, no route available or loaded information).
     * @param v View to display the information on
     * @param distance Distance of the route
     * @param duration Duration of the route
     */
    private void displayRouteInformation(View v, String distance, String duration) {
        TextView routeInformation = (TextView) v.findViewById(R.id.caldroid_info_route_information);
        String information;
        if("".equals(duration)) {
            information = distance;
        } else {
            information = distance + " | " + duration;
        }
        routeInformation.setText(information);
    }
}

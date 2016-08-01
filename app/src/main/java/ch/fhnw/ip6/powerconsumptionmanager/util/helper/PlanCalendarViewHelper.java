package ch.fhnw.ip6.powerconsumptionmanager.util.helper;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
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
import ch.fhnw.ip6.powerconsumptionmanager.model.chargeplan.CalendarEntry;
import ch.fhnw.ip6.powerconsumptionmanager.model.chargeplan.RouteInformation;
import ch.fhnw.ip6.powerconsumptionmanager.network.AsyncTaskCallback;
import ch.fhnw.ip6.powerconsumptionmanager.network.GetRouteInformationAsyncTask;
import ch.fhnw.ip6.powerconsumptionmanager.util.CalendarInstanceReader;
import ch.fhnw.ip6.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;

/**
 * Helper class to handle and modify caldroid
 */
public class PlanCalendarViewHelper implements AsyncTaskCallback {
    // The caldroid fragment itself
    private CaldroidFragment mCaldroid;
    // Contexts
    private Context mContext;
    private PowerConsumptionManagerAppContext mAppContext;
    // Calendar instance to make date operations
    private Calendar mCalendar;
    // Holds the read instances from the calendar.instances table of one month
    private HashMap<Integer, CalendarEntry> mInstances;
    // The actual selected date in the caldroid fragment
    private Date mSelectedDate = new Date();

    private TextView mInfoTimerange;
    private TextView mInfoRoute;
    private TextView mInfoRouteInformation;
    private TextView mInfoDescription;

    public PlanCalendarViewHelper(CaldroidFragment caldroid, Context context) {
        mCaldroid = caldroid;
        mContext = context;
        mAppContext = (PowerConsumptionManagerAppContext) mContext.getApplicationContext();
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
    public long getMonthStart(int year, int month) {
        mCalendar = Calendar.getInstance();
        mCalendar.set(year, month, 1, 0, 0, 0);
        return mCalendar.getTimeInMillis();
    }

    /** Generates the upper range from the range the calendar.instances table should be read (end of month)
     * @param year Year
     * @param month Month
     * @return Last day of month
     */
    public long getMonthEnd(int year, int month) {
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
        CalendarInstanceReader cir = new CalendarInstanceReader(mCalendar, mContext);
        mInstances = cir.readInstancesBetweenTimestamps(lowerRangeEnd, upperRangeEnd);
    }

    /**
     * Mark all dates in the caldroid fragment that have a tesla trip instance
     */
    public void markDays() {
        // Iterate through all read calendar instances
        for (Map.Entry pair : mInstances.entrySet()) {
            CalendarEntry pem = (CalendarEntry) pair.getValue();
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
                if(mInstances.containsKey(pressedDay)) {
                    // Modify look and feel
                    mCaldroid.clearBackgroundResourceForDate(mSelectedDate);
                    mCaldroid.setBackgroundResourceForDate(R.drawable.caldroid_selected_day, date);
                    mSelectedDate = date;
                    mCaldroid.refreshView();

                    // Display instance data
                    CalendarEntry pem = mInstances.get(pressedDay);

                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.CustomDialogStyle);
                    builder.setTitle(titleFormat.format(pem.getBegin()));

                    LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                    View planEntryView = layoutInflater.inflate(R.layout.dialog_plan_entry, null);
                    builder.setView(planEntryView);

                    mInfoTimerange = (TextView) planEntryView.findViewById(R.id.tvInfoTimerange);
                    mInfoRoute = (TextView) planEntryView.findViewById(R.id.tvInfoRoute);
                    mInfoRouteInformation = (TextView) planEntryView.findViewById(R.id.tvInfoRouteInformation);
                    mInfoDescription = (TextView) planEntryView.findViewById(R.id.tvInfoDescription);

                    // Fill layout with stored values
                    // Description
                    if(!pem.getDescription().equals("")) {
                        mInfoDescription.setText(pem.getDescription());
                    } else {
                        mInfoDescription.setText(mContext.getString(R.string.text_information_no_description));
                    }
                    mInfoDescription.setMovementMethod(ScrollingMovementMethod.getInstance());
                    // Time range of instance
                    String time = timeRangeFormat.format(pem.getBegin()) + " - " + timeRangeFormat.format(pem.getEnd());
                    mInfoTimerange.setText(time);
                    /*
                     * Display route and load distance and duration to reach destination between two given
                     * locations from the instance
                     */
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
                        displayRouteInformation(mInfoRouteInformation, mContext.getString(R.string.text_route_information_no_data), "");
                    }
                    mInfoRoute.setText(route);

                    builder.setPositiveButton(mContext.getString(R.string.dialog_button_close), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    builder.show();
                }
            }

            @Override
            public void onChangeMonth(int month, int year) {
                /*
                 * Read calendar.instances table with new range and mark days that contain an instance
                 * -1 because caldroid calculates months from 1-12 and Calendar.class does it with 0-11
                 */
                long startMonth = getMonthStart(year, month-1);
                long endMonth = getMonthEnd(year, month-1);
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

    @Override
    public void asyncTaskFinished(boolean result) {
        Handler handler = new android.os.Handler(Looper.getMainLooper());
        if(result) {
           handler.post(new Runnable() {
                @Override
                public void run() {
                    RouteInformation rim = mAppContext.getRouteInformation();

                    // Check if a route existed
                    if (rim.getDistanceText().equals("")) {
                        displayRouteInformation(mInfoRouteInformation, mContext.getString(R.string.text_route_information_no_route), "");
                    } else {
                        displayRouteInformation(
                                mInfoRouteInformation,
                                mContext.getString(R.string.text_route_information_distance) + " " + rim.getDistanceText(),
                                mContext.getString(R.string.text_route_information_duration) + " " + rim.getDurationText()
                        );
                    }
                }
            });
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    displayRouteInformation(mInfoRouteInformation, mContext.getString(R.string.text_route_information_error), "");
                }
            });
        }
    }

    /**
     * Call google.maps API with the origin and destination location to find out distance and duration
     * of the planned trip
     * @param origin The origin of the route
     * @param destination The destination of the route
     */
    private void calculateDistance(String origin, String destination) {
        new GetRouteInformationAsyncTask(
            mAppContext,
            this,
            mContext.getString(R.string.googleMaps_Api1) +
            "origin=" + origin +
            "&destination=" + destination +
            mContext.getString(R.string.googleMaps_Api2)
        ).execute();
    }

    /**
     * Displays the route information (error, no route available or loaded information).
     * @param routeInformation Textview widget to display the route information on
     * @param distance Distance of the route
     * @param duration Duration of the route
     */
    private void displayRouteInformation(TextView routeInformation, String distance, String duration) {
        String information;
        if("".equals(duration)) {
            information = distance;
        } else {
            information = distance + " | " + duration;
        }
        routeInformation.setText(information);
    }
}

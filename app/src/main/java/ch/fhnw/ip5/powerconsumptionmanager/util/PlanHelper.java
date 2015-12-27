package ch.fhnw.ip5.powerconsumptionmanager.util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ch.fhnw.ip5.powerconsumptionmanager.R;
import ch.fhnw.ip5.powerconsumptionmanager.model.PlanEntryModel;
import ch.fhnw.ip5.powerconsumptionmanager.model.RouteInformationModel;
import ch.fhnw.ip5.powerconsumptionmanager.network.DataLoader;
import ch.fhnw.ip5.powerconsumptionmanager.network.DataLoaderCallback;
import ch.fhnw.ip5.powerconsumptionmanager.view.PlanFragment;

/**
 * Helper class to handle and modify caldroid
 */
public class PlanHelper implements DataLoaderCallback {
    // Projection array holding values to read from the instances table
    public static final String[] INSTANCE_FIELDS = new String[] {
            CalendarContract.Instances.TITLE,
            CalendarContract.Instances.EVENT_LOCATION,
            CalendarContract.Instances.DESCRIPTION,
            CalendarContract.Instances.BEGIN,
            CalendarContract.Instances.END
    };

    // Projection array indices
    private static final int INSTANCE_TITLE = 0;
    private static final int INSTANCE_EVENT_LOCATION = 1;
    private static final int INSTANCE_DESCRIPTION = 2;
    private static final int INSTANCE_BEGIN = 3;
    private static final int INSTANCE_END = 4;

    // The caldroid fragment itself
    private CaldroidFragment mCaldroid;
    // Contexts
    private PlanFragment mContext;
    private PowerConsumptionManagerAppContext mAppContext;
    // Calendar instance to make date operations
    private Calendar mCalendar;
    // Holds the read instances from the calendar.instances table
    private HashMap<Integer, PlanEntryModel> mInstances;
    // The actual selected date in the caldroid fragment
    private Date mSelectedDate = new Date();


    public PlanHelper(CaldroidFragment caldroid, PlanFragment context) {
        mCaldroid = caldroid;
        mContext = context;
        mAppContext = (PowerConsumptionManagerAppContext) mContext.getActivity().getApplicationContext();
        mInstances = new HashMap<Integer, PlanEntryModel>();
    }

    // Initial settings for the caldroid fragment
    public void setup() {
        mCalendar = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt(CaldroidFragment.MONTH, mCalendar.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, mCalendar.get(Calendar.YEAR));
        args.putInt(CaldroidFragment.START_DAY_OF_WEEK, CaldroidFragment.MONDAY);
        args.putInt(CaldroidFragment.THEME_RESOURCE, R.style.CustomCaldroidTheme);
        args.putBoolean(CaldroidFragment.SHOW_NAVIGATION_ARROWS, false);
        args.putBoolean(CaldroidFragment.ENABLE_CLICK_ON_DISABLED_DATES, false);
        args.putBoolean(CaldroidFragment.SQUARE_TEXT_VIEW_CELL, false);
        mCaldroid.setArguments(args);
    }

    // Generates the lower range from the range the calendar.instances table should be read (start of month)
    public long generateLowerRangeEnd(int year, int month) {
        mCalendar = Calendar.getInstance();
        mCalendar.set(year, month, 1, 0, 0, 0);
        return mCalendar.getTimeInMillis();
    }

    // Generates the upper range from the range the calendar.instances table should be read (end of month)
    public long generateUpperRangeEnd(int year, int month) {
        mCalendar = Calendar.getInstance();
        mCalendar.set(year, month, 1, 23, 59, 59);
        mCalendar.set(year, month, mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
        return mCalendar.getTimeInMillis();
    }

    // Reads all planned tesla trips from the calendar.instances table
    public void readPlannedTrips(long lowerRangeEnd, long upperRangeEnd) {
        ContentResolver cr = mContext.getActivity().getContentResolver();
        // Condition what entries in the instance table to read
        String selection = "((" + CalendarContract.Instances.BEGIN + " >= ?) AND (" + CalendarContract.Instances.END + " <= ?))";
        // Arguments for the condition (replacing ?)
        String[] selectionArgs = new String[]{String.valueOf(lowerRangeEnd), String.valueOf(upperRangeEnd)};

        // Build the uri
        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(builder, lowerRangeEnd);
        ContentUris.appendId(builder, upperRangeEnd);

        // Submit query
        Cursor cursor = cr.query(builder.build(), INSTANCE_FIELDS, selection, selectionArgs, null);

        // Iterate through results
        while (cursor.moveToNext()) {
            String title = cursor.getString(INSTANCE_TITLE);

            // Check if tesla trip or not
            if(!title.equals(mContext.getString(R.string.instance_title))) {
                continue;
            }

            String eventLocation = cursor.getString(INSTANCE_EVENT_LOCATION);
            String description = cursor.getString(INSTANCE_DESCRIPTION);
            long begin = cursor.getLong(INSTANCE_BEGIN);
            mCalendar.setTimeInMillis(begin);
            int startDay = mCalendar.get(Calendar.DAY_OF_MONTH);
            long end = cursor.getLong(INSTANCE_END);

            // Store read data into hash map
            if(!mInstances.containsKey(startDay)) {
                mInstances.put(startDay, new PlanEntryModel(title, eventLocation, description, new Date(begin), new Date(end)));
            }
        }
    }

    // Mark all dates in the caldroid fragment that have a tesla trip instance
    public void markDays() {
        Iterator iterator = mInstances.entrySet().iterator();

        // Iterate through all read calendar instances
        while(iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            PlanEntryModel pem = (PlanEntryModel) pair.getValue();
            mCalendar.setTime(pem.getBegin());
            mCaldroid.setSelectedDate(mCalendar.getTime());
        }

        // Update caldroid fragment
        mCaldroid.refreshView();
    }

    // Define the listener for the caldroid fragment
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

                    // Display data
                    PlanEntryModel pem = mInstances.get(pressedDay);
                    View v = mContext.getView();
                    TextView title = (TextView) v.findViewById(R.id.caldroid_info_title);
                    TextView description = (TextView) v.findViewById(R.id.caldroid_info_description);
                    TextView timeRange = (TextView) v.findViewById(R.id.caldroid_info_timerange);
                    title.setText(titleFormat.format(pem.getBegin()));
                    description.setText(pem.getDescription());
                    timeRange.setText(timeRangeFormat.format(pem.getBegin()) + " - " + timeRangeFormat.format(pem.getEnd()));

                    // Load distance and duration to reach destination between two given locations from the instance
                    String[] locations = pem.getEventLocation().split("/");
                    if(locations != null && !"".equals(locations[0]) && !"".equals(locations[1])) {
                        calculateDistance(locations[0].trim(), locations[1].trim());
                    } else {
                        TextView routeInformation = (TextView) v.findViewById(R.id.text_route_information);
                        routeInformation.setText(mContext.getString(R.string.text_route_information_no_data));
                    }
                }
            }


            @Override
            public void onChangeMonth(int month, int year) {
                // Read calendar.instances table with new range and mark days that contain an instance
                long startMonth = generateLowerRangeEnd(year, month);
                long endMonth = generateUpperRangeEnd(year, month);
                readPlannedTrips(startMonth, endMonth);
                markDays();
            }
        };

        mCaldroid.setCaldroidListener(listener);
    }

    public CaldroidFragment getCaldroid() {
        return mCaldroid;
    }

    @Override
    public void DataLoaderDidFinish() {
        // Update the text view field for the route information with the loaded data on the UI thread
        mContext.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View view = mContext.getView();
                TextView routeInformation = (TextView) view.findViewById(R.id.text_route_information);
                RouteInformationModel rim = mAppContext.getRouteInformation();
                routeInformation.setText("Distance: " + rim.getDistanceText() + ", Duration: " + rim.getDurationText());
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
                TextView routeInformation = (TextView) view.findViewById(R.id.text_route_information);
                routeInformation.setText(mContext.getString(R.string.text_route_information_error));
            }
        });
    }

    /*
     * Call google.maps API with the origin and destination location to find out distance and duration
     * of the planned trip
     */
    private void calculateDistance(String origin, String destination) {
        DataLoader loader = new DataLoader(mAppContext, this);
        loader.loadDistanceBetweenAddresses(
            mContext.getString(R.string.googleMaps_Api1) +
            "origin=" + origin +
            "&destination=" + destination +
            "&mode=driving&sensor=false"
        );
    }
}

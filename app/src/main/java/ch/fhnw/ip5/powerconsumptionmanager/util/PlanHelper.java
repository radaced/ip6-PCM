package ch.fhnw.ip5.powerconsumptionmanager.util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.Toast;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ch.fhnw.ip5.powerconsumptionmanager.R;
import ch.fhnw.ip5.powerconsumptionmanager.model.PlanEntryModel;
import ch.fhnw.ip5.powerconsumptionmanager.view.PlanFragment;

/**
 * Helperclass to handle and modify caldroid
 */
public class PlanHelper {
    // Projection array holding values to read from the instances table
    public static final String[] INSTANCE_FIELDS = new String[] {
            CalendarContract.Instances.TITLE,
            CalendarContract.Instances.DESCRIPTION,
            CalendarContract.Instances.BEGIN,
            CalendarContract.Instances.END
    };

    // Projection array indices
    private static final int INSTANCE_TITLE = 0;
    private static final int INSTANCE_DESCRIPTION = 1;
    private static final int INSTANCE_BEGIN = 2;
    private static final int INSTANCE_END = 3;

    private CaldroidFragment mCaldroid;
    private PlanFragment mContext;
    private Calendar mCalendar;
    private HashMap<Integer, PlanEntryModel> mInstances;
    private ArrayList<Double> mProcessedViews;

    public PlanHelper(CaldroidFragment caldroid, PlanFragment context) {
        mCaldroid = caldroid;
        mContext = context;
        mInstances = new HashMap<Integer, PlanEntryModel>();
        mProcessedViews = new ArrayList<Double>();
    }

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

    public long generateLowerRangeEnd(int year, int month) {
        mCalendar = Calendar.getInstance();
        mCalendar.set(year, month, 1, 0, 0, 0);
        return mCalendar.getTimeInMillis();
    }

    public long generateUpperRangeEnd(int year, int month) {
        mCalendar = Calendar.getInstance();
        mCalendar.set(year, month, 1, 23, 59, 59);
        mCalendar.set(year, month, mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
        return mCalendar.getTimeInMillis();
    }

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

            if(!title.equals(mContext.getString(R.string.instance_title))) {
                continue;
            }

            String description = cursor.getString(INSTANCE_DESCRIPTION);
            long begin = cursor.getLong(INSTANCE_BEGIN);
            mCalendar.setTimeInMillis(begin);
            int startDay = mCalendar.get(Calendar.DAY_OF_MONTH);
            long end = cursor.getLong(INSTANCE_END);

            if(!mInstances.containsKey(startDay)) {
                mInstances.put(startDay, new PlanEntryModel(title, description, begin, end));
            }
        }
    }

    public void markDays() {
        Iterator iterator = mInstances.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            PlanEntryModel pem = (PlanEntryModel) pair.getValue();
            mCalendar.setTimeInMillis(pem.getBegin());
            mCaldroid.setSelectedDate(mCalendar.getTime());
        }
        mCaldroid.refreshView();
    }

    public void generateListener() {
        final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

        CaldroidListener listener = new CaldroidListener() {

            @Override
            public void onSelectDate(Date date, View view) {
                Toast.makeText(mContext.getActivity().getApplicationContext(), formatter.format(date),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChangeMonth(int month, int year) {
                double viewCode = year / month;
                if(!mProcessedViews.contains(viewCode)) {
                    long startMonth = generateLowerRangeEnd(year, month);
                    long endMonth = generateUpperRangeEnd(year, month);
                    readPlannedTrips(startMonth, endMonth);
                    markDays();
                    mProcessedViews.add(viewCode);
                    Toast.makeText(mContext.getActivity().getApplicationContext(), month + " " + year,
                            Toast.LENGTH_SHORT).show();
                }
            }
        };

        mCaldroid.setCaldroidListener(listener);
    }

    public CaldroidFragment getCaldroid() {
        return mCaldroid;
    }
}

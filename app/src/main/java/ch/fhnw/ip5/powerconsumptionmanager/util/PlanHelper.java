package ch.fhnw.ip5.powerconsumptionmanager.util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;

import com.roomorama.caldroid.CaldroidFragment;

import java.util.Calendar;

import ch.fhnw.ip5.powerconsumptionmanager.R;
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

    public PlanHelper(CaldroidFragment caldroid, PlanFragment context) {
        mCaldroid = caldroid;
        mContext = context;
        mCalendar.getInstance();
    }

    public void setup() {
        Bundle args = new Bundle();
        args.putInt(CaldroidFragment.MONTH, mCalendar.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, mCalendar.get(Calendar.YEAR));
        args.putInt(CaldroidFragment.START_DAY_OF_WEEK, CaldroidFragment.MONDAY);
        args.putInt(CaldroidFragment.THEME_RESOURCE, R.style.CustomCaldroidTheme);
        args.putBoolean(CaldroidFragment.SHOW_NAVIGATION_ARROWS, false);
        args.putBoolean(CaldroidFragment.SQUARE_TEXT_VIEW_CELL, false);
        mCaldroid.setArguments(args);
    }

    public long generateLowerRangeEnd() {
        mCalendar.set(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), 1, 0, 0, 0);
        return mCalendar.getTimeInMillis();
    }

    public long generateUpperRangeEnd() {
        mCalendar.set(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
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
            // Get the field values
            String title = cursor.getString(INSTANCE_TITLE);
        }
    }
}

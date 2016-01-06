package ch.fhnw.ip5.powerconsumptionmanager.util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import ch.fhnw.ip5.powerconsumptionmanager.R;
import ch.fhnw.ip5.powerconsumptionmanager.model.PlanEntryModel;

/**
 * Class to read instances from the calendar.instance table
 */
public class CalendarInstanceReader {
    // Projection array holding values to read from the instances table
    private static final String[] INSTANCE_FIELDS = new String[] {
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

    private Calendar mCalendar;
    private Context mContext;
    private HashMap<Integer, PlanEntryModel> mInstances;

    public CalendarInstanceReader(Calendar calendar, Context context) {
        mCalendar = calendar;
        mContext = context;
        /* TODO SparseIntArray */
        mInstances = new HashMap<Integer, PlanEntryModel>();
    }

    /*
     * Reads all the instances in the calendar.instances table and returns the information according to
     * the projection array in a hashmap where the key is the day on which the instance is
     */
    public HashMap<Integer, PlanEntryModel> readInstancesBetweenTimestamps(long lowerRangeEnd, long upperRangeEnd) {
        ContentResolver cr = mContext.getApplicationContext().getContentResolver();
        // Condition what entries in the instance table to read
        String selection = "((" + CalendarContract.Instances.BEGIN + " >= ?) AND (" + CalendarContract.Instances.END + " <= ?))";
        // Arguments for the condition (replacing ? in selection string)
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

            // Process received information
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
        cursor.close();

        return mInstances;
    }
}
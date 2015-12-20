package ch.fhnw.ip5.powerconsumptionmanager.view;

import android.Manifest;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Instances;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ch.fhnw.ip5.powerconsumptionmanager.R;

/**
 * This Fragment shows the charge plan created by the user.
 */
public class PlanFragment extends Fragment {

    public static PlanFragment newInstance() {
        PlanFragment fragment = new PlanFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_plan, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Cursor cur = null;
        String selection = "((" + CalendarContract.Events.DTSTART
                + " >= ?) AND (" + CalendarContract.Events.DTEND + " <= ?))";
        Time t = new Time();
        t.setToNow();
        String dtStart = Long.toString(t.toMillis(false));
        t.set(59, 59, 23, t.monthDay, t.month, t.year);
        String dtEnd = Long.toString(t.toMillis(false));
        String[] selectionArgs = new String[]{dtStart, dtEnd};

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            // http://developer.android.com/training/permissions/requesting.html
            return;
        }
        cur = getActivity().getContentResolver().query(CalendarContract.Events.CONTENT_URI,
                null, selection, selectionArgs, null);

        if (cur.getCount() > 0) {
            String test = "test";
        }

        /*
        Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI
                .buildUpon();
        ContentUris.appendId(eventsUriBuilder, 1448928000000);
        ContentUris.appendId(eventsUriBuilder, 1451433600000);
        Uri eventsUri = eventsUriBuilder.build();
        Cursor cursor = null;
        String[] proj = new String[]{
                Instances._ID,
                Instances.BEGIN,
                Instances.END,
                Instances.EVENT_ID};
        cursor = getContext().getContentResolver().query(eventsUri, proj, null, null, CalendarContract.Instances.DTSTART + " ASC");

        if (cursor.getCount() > 0) {
            String poop = "bal";
        }
        */

    }
}
package ch.fhnw.ip5.powerconsumptionmanager.view;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Instances;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.roomorama.caldroid.CaldroidFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ch.fhnw.ip5.powerconsumptionmanager.R;

/**
 * This Fragment shows the charge plan created by the user.
 */
public class PlanFragment extends Fragment {

    // Projection array holding values to read from the instances table
    public static final String[] INSTANCE_FIELDS = new String[] {
        Instances.TITLE,
        Instances.DESCRIPTION,
        Instances.BEGIN,
        Instances.END
    };

    // Projection array indices
    private static final int INSTANCE_TITLE = 0;
    private static final int INSTANCE_DESCRIPTION = 1;
    private static final int INSTANCE_BEGIN = 2;
    private static final int INSTANCE_END = 3;

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

        CaldroidFragment caldroidFragment = new CaldroidFragment();
        Bundle args = new Bundle();
        Calendar cal = Calendar.getInstance();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        args.putInt(CaldroidFragment.START_DAY_OF_WEEK, CaldroidFragment.MONDAY);
        args.putInt(CaldroidFragment.THEME_RESOURCE, R.style.CustomCaldroidTheme);
        caldroidFragment.setArguments(args);

        FragmentTransaction t = getFragmentManager().beginTransaction();
        t.replace(R.id.caldroid_fragment, caldroidFragment);
        t.commit();

        // Get date ranges (1 month)
        long startOfMonth = getStartOfMonth(cal);
        long endOfMonth = getEndOfMonth(cal);

        ContentResolver cr = getActivity().getContentResolver();
        // Condition what entries in the instance table to read
        String selection = "((" + Instances.BEGIN + " >= ?) AND (" + Instances.END + " <= ?))";
        // Arguments for the condition (replacing ?)
        String[] selectionArgs = new String[]{String.valueOf(startOfMonth), String.valueOf(endOfMonth)};

        // Build the uri
        Uri.Builder builder = Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(builder, startOfMonth);
        ContentUris.appendId(builder, endOfMonth);

        // Submit query
        Cursor cursor = cr.query(builder.build(), INSTANCE_FIELDS, selection, selectionArgs, null);

        // Iterate through results
        while (cursor.moveToNext()) {
            // Get the field values
            String title = cursor.getString(INSTANCE_TITLE);
        }
    }

    private long getStartOfMonth(Calendar cal) {
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1, 0, 0, 0);
        return cal.getTimeInMillis();
    }

    private long getEndOfMonth(Calendar cal) {
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
        return cal.getTimeInMillis();
    }
}
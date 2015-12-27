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
import ch.fhnw.ip5.powerconsumptionmanager.network.DataLoaderCallback;
import ch.fhnw.ip5.powerconsumptionmanager.util.PlanHelper;
import ch.fhnw.ip5.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;

/**
 * This Fragment shows the charge plan created by the user, but managed over the standard
 * google calendar.
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

        // Set up the calendar fragment with the helper class
        CaldroidFragment caldroidFragment = new CaldroidFragment();
        PlanHelper planHelper = new PlanHelper(caldroidFragment, this);
        planHelper.setup();

        // Get date range ends (reading instances for 1 month)
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        long startOfMonth = planHelper.generateLowerRangeEnd(year, month);
        long endOfMonth = planHelper.generateUpperRangeEnd(year, month);

        // Read calender instances
        planHelper.readPlannedTrips(startOfMonth, endOfMonth);
        planHelper.markDays();
        planHelper.generateListener();

        // Load fragment into container
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.caldroid_fragment, planHelper.getCaldroid());
        transaction.commit();
    }
}
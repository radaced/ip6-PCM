package ch.fhnw.ip6.powerconsumptionmanager.model.settings;

import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.widget.LinearLayout;

import com.roomorama.caldroid.CaldroidFragment;

import java.util.Calendar;

import ch.fhnw.ip6.powerconsumptionmanager.activity.ComponentSettingsActivity;
import ch.fhnw.ip6.powerconsumptionmanager.util.helper.PlanHelper;

public class PCMPlan extends PCMSetting {
    private PlanHelper mPlanHelper;
    private int mFragmentContainerId;

    private CaldroidFragment mCaldroidFragment;

    public PCMPlan(String name) {
        super(name);
        mFragmentContainerId = super.getName().hashCode();
    }

    @Override
    public void inflateLayout(Context context, LinearLayout container) {
        FragmentTransaction transaction = ((ComponentSettingsActivity) context).getSupportFragmentManager().beginTransaction();
        float density = context.getResources().getDisplayMetrics().density;

        mCaldroidFragment = new CaldroidFragment();
        mPlanHelper = new PlanHelper(mCaldroidFragment, (ComponentSettingsActivity) context);

        LinearLayout.LayoutParams llLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        llLayoutParams.setMargins((int) (8 * density), 0, (int) (8 * density), (int) (15 * density));

        LinearLayout llFragmentContainer = new LinearLayout(context);
        llFragmentContainer.setId(mFragmentContainerId);
        llFragmentContainer.setOrientation(LinearLayout.VERTICAL);
        llFragmentContainer.setLayoutParams(llLayoutParams);

        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        mPlanHelper.setup(cal);

        long startOfMonth = mPlanHelper.getMonthStart(year, month);
        long endOfMonth = mPlanHelper.getMonthEnd(year, month);

        mPlanHelper.readPlannedTrips(startOfMonth, endOfMonth);
        mPlanHelper.markDays();
        mPlanHelper.generateListener();

        transaction.replace(mFragmentContainerId, mPlanHelper.getCaldroid()).commit();

        container.addView(llFragmentContainer);
    }

    @Override
    public String generateSaveJson() {
        return null;
    }
}

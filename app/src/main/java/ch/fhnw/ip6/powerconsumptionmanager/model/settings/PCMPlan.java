package ch.fhnw.ip6.powerconsumptionmanager.model.settings;

import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.roomorama.caldroid.CaldroidFragment;

import java.util.Calendar;
import java.util.LinkedHashMap;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.activity.ComponentSettingsActivity;
import ch.fhnw.ip6.powerconsumptionmanager.model.chargeplan.PCMPlanEntry;
import ch.fhnw.ip6.powerconsumptionmanager.util.helper.PlanCalendarViewHelper;

public class PCMPlan extends PCMSetting {
    private PlanCalendarViewHelper mPlanCalendarViewHelper;
    private boolean mUsesGoogleCalendar;
    private int mFragmentContainerId;

    private CaldroidFragment mCaldroidFragment;
    private LinkedHashMap<Integer, PCMPlanEntry> mChargePlanData = new LinkedHashMap<>();
    private int mDayIndex = 0;
    private boolean mIgnoreChange = false;

    public PCMPlan(String name, boolean usesGoogleCalendar) {
        super(name);
        mUsesGoogleCalendar = usesGoogleCalendar;
        mFragmentContainerId = super.getName().hashCode();
        // ID must be positive
        mFragmentContainerId = mFragmentContainerId < 0 ? mFragmentContainerId * (-1) : mFragmentContainerId;
    }

    @Override
    public void inflateLayout(Context context, LinearLayout container) {
        float density = context.getResources().getDisplayMetrics().density;

        LinearLayout.LayoutParams tvLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        tvLayoutParams.setMargins((int) (8 * density), 0, (int) (8 * density), 0);

        TextView tvSettingDescription = new TextView(context);
        tvSettingDescription.setText(super.getName());
        tvSettingDescription.setTextSize(18);
        tvSettingDescription.setTextColor(ContextCompat.getColor(context, R.color.colorTextPrimary));
        tvSettingDescription.setLayoutParams(tvLayoutParams);

        container.addView(tvSettingDescription);

        LinearLayout.LayoutParams llLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        llLayoutParams.setMargins((int) (8 * density), 0, (int) (8 * density), (int) (15 * density));

        if(mUsesGoogleCalendar) {
            loadCalendarView(context, container, llLayoutParams);
        } else {
            loadPCMPlanView(context, container, llLayoutParams);
        }
    }

    @Override
    public String generateSaveJson() {
        return null;
    }

    private void loadCalendarView(Context context, LinearLayout container, LinearLayout.LayoutParams layoutParams) {
        FragmentTransaction transaction = ((ComponentSettingsActivity) context).getSupportFragmentManager().beginTransaction();

        mCaldroidFragment = new CaldroidFragment();
        mPlanCalendarViewHelper = new PlanCalendarViewHelper(mCaldroidFragment, (ComponentSettingsActivity) context);

        LinearLayout llFragmentContainer = new LinearLayout(context);
        llFragmentContainer.setId(mFragmentContainerId);
        llFragmentContainer.setOrientation(LinearLayout.VERTICAL);
        llFragmentContainer.setLayoutParams(layoutParams);

        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        mPlanCalendarViewHelper.setup(cal);

        long startOfMonth = mPlanCalendarViewHelper.getMonthStart(year, month);
        long endOfMonth = mPlanCalendarViewHelper.getMonthEnd(year, month);

        mPlanCalendarViewHelper.readPlannedTrips(startOfMonth, endOfMonth);
        mPlanCalendarViewHelper.markDays();
        mPlanCalendarViewHelper.generateListener();

        transaction.replace(mFragmentContainerId, mPlanCalendarViewHelper.getCaldroid()).commit();
        container.addView(llFragmentContainer);
    }

    private void loadPCMPlanView(Context context, LinearLayout container, LinearLayout.LayoutParams layoutParams) {
        LinearLayout llPCMPlan = new LinearLayout(context);
        llPCMPlan.setOrientation(LinearLayout.HORIZONTAL);
        llPCMPlan.setLayoutParams(layoutParams);

        LinearLayout.LayoutParams npLayoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1
        );
        npLayoutParams.gravity = Gravity.CENTER;

        NumberPicker npWeekdays = new NumberPicker(context);
        npWeekdays.setMinValue(0);
        npWeekdays.setMaxValue(6);
        npWeekdays.setDisplayedValues(context.getResources().getStringArray(R.array.weekdays));
        npWeekdays.setLayoutParams(npLayoutParams);

        llPCMPlan.addView(npWeekdays);

        LinearLayout.LayoutParams llTimePickersLayoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            4
        );

        LinearLayout llTimePickers = new LinearLayout(context);
        llTimePickers.setOrientation(LinearLayout.VERTICAL);
        llTimePickers.setLayoutParams(llTimePickersLayoutParams);

        LinearLayout.LayoutParams llTimeContainers = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );

        LinearLayout.LayoutParams tvLayoutParams = new LinearLayout.LayoutParams(
            100 * (int) context.getResources().getDisplayMetrics().density,
            LinearLayout.LayoutParams.MATCH_PARENT,
            1
        );

        LinearLayout.LayoutParams tpLayoutParams = new LinearLayout.LayoutParams(
            200 * (int) context.getResources().getDisplayMetrics().density,
            LinearLayout.LayoutParams.MATCH_PARENT,
            2
        );

        LinearLayout llDeparture = new LinearLayout(context);
        llDeparture.setOrientation(LinearLayout.HORIZONTAL);
        llDeparture.setLayoutParams(llTimeContainers);

        TextView tvDeparture = new TextView(context);
        tvDeparture.setText(context.getString(R.string.text_departure_time));
        tvDeparture.setTextSize(14);
        tvDeparture.setTextColor(ContextCompat.getColor(context, R.color.colorTextPrimary));
        tvDeparture.setGravity(Gravity.CENTER);
        tvDeparture.setLayoutParams(tvLayoutParams);

        llDeparture.addView(tvDeparture);

        final TimePicker tpDeparture = new TimePicker(context, null, 1);
        tpDeparture.setIs24HourView(true);
        tpDeparture.setCurrentHour(mChargePlanData.get(mDayIndex).getDepartureHour());
        tpDeparture.setCurrentMinute(mChargePlanData.get(mDayIndex).getDepartureMinute());
        tpDeparture.setLayoutParams(tpLayoutParams);

        llDeparture.addView(tpDeparture);

        llTimePickers.addView(llDeparture);

        LinearLayout llArrival = new LinearLayout(context);
        llArrival.setOrientation(LinearLayout.HORIZONTAL);
        llArrival.setLayoutParams(llTimeContainers);

        TextView tvArrival = new TextView(context);
        tvArrival.setText(context.getString(R.string.text_arrival_time));
        tvArrival.setTextSize(14);
        tvArrival.setTextColor(ContextCompat.getColor(context, R.color.colorTextPrimary));
        tvArrival.setGravity(Gravity.CENTER);
        tvArrival.setLayoutParams(tvLayoutParams);

        llArrival.addView(tvArrival);

        final TimePicker tpArrival = new TimePicker(context, null, 1);
        tpArrival.setIs24HourView(true);
        tpArrival.setCurrentHour(mChargePlanData.get(mDayIndex).getArrivalHour());
        tpArrival.setCurrentMinute(mChargePlanData.get(mDayIndex).getArrivalMinute());
        tpArrival.setLayoutParams(tpLayoutParams);

        llArrival.addView(tpArrival);

        llTimePickers.addView(llArrival);

        llPCMPlan.addView(llTimePickers);

        LinearLayout.LayoutParams llKmLayoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1
        );
        llKmLayoutParams.gravity = Gravity.CENTER;

        LinearLayout llKm = new LinearLayout(context);
        llKm.setOrientation(LinearLayout.HORIZONTAL);
        llKm.setLayoutParams(llKmLayoutParams);

        LinearLayout.LayoutParams tvKmLayoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );

        TextView tvKm = new TextView(context);
        tvKm.setText(context.getString(R.string.text_km));
        tvKm.setLayoutParams(tvKmLayoutParams);

        llKm.addView(tvKm);

        LinearLayout.LayoutParams etLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        etLayoutParams.setMargins(15, 0, 0, 0);

        final EditText etKm = new EditText(context);
        etKm.setBackgroundResource(android.R.color.white);
        etKm.setTextColor(ContextCompat.getColor(context.getApplicationContext(), R.color.colorTextPrimaryInverse));
        etKm.setGravity(Gravity.CENTER);
        etKm.setTextSize(14);
        etKm.setText(String.format("%s", mChargePlanData.get(mDayIndex).getKm()));
        etKm.setInputType(InputType.TYPE_CLASS_NUMBER);
        etKm.setFilters( new InputFilter[] { new InputFilter.LengthFilter(4) } );
        etKm.setMinEms(4);
        etKm.setLayoutParams(etLayoutParams);
        etKm.setPadding(0, 0, 0, 0);

        llKm.addView(etKm);

        llPCMPlan.addView(llKm);

        /* Set on-value-change listeners */
        npWeekdays.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mIgnoreChange = true;
                tpDeparture.setCurrentHour(mChargePlanData.get(newVal).getDepartureHour());
                tpDeparture.setCurrentMinute(mChargePlanData.get(newVal).getDepartureMinute());
                tpArrival.setCurrentHour(mChargePlanData.get(newVal).getArrivalHour());
                tpArrival.setCurrentMinute(mChargePlanData.get(newVal).getArrivalMinute());
                etKm.setText(String.format("%s", mChargePlanData.get(newVal).getKm()));
                mIgnoreChange = false;
                mDayIndex = newVal;
            }
        });

        tpDeparture.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                if(!mIgnoreChange) {
                    mChargePlanData.get(mDayIndex).setDepartureHour(hourOfDay);
                    mChargePlanData.get(mDayIndex).setDepartureMinute(minute);
                }
            }
        });

        tpArrival.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                if(!mIgnoreChange) {
                    mChargePlanData.get(mDayIndex).setArrivalHour(hourOfDay);
                    mChargePlanData.get(mDayIndex).setArrivalMinute(minute);
                }
            }
        });

        etKm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!mIgnoreChange) {
                    mChargePlanData.get(mDayIndex).setKm(Integer.valueOf(s.toString()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        container.addView(llPCMPlan);
    }

    public LinkedHashMap<Integer, PCMPlanEntry> getChargePlanData() {
        return mChargePlanData;
    }
}

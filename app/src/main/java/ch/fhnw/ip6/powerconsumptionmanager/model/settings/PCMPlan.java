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
import ch.fhnw.ip6.powerconsumptionmanager.network.SynchronizeChargePlanAsyncTask;
import ch.fhnw.ip6.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;
import ch.fhnw.ip6.powerconsumptionmanager.util.helper.PlanCalendarViewHelper;

/**
 * Represents a charge plan setting from the PCM.
 */
public class PCMPlan extends PCMSetting {
    private boolean mUsesGoogleCalendar;
    private int mFragmentContainerId;
    private LinkedHashMap<Integer, PCMPlanEntry> mChargePlanData = new LinkedHashMap<>();
    private int mDayIndex = 0;
    private boolean mIgnoreChange = false;

    /**
     * Constructor to create a new charge plan setting.
     * @param name Name of the setting.
     * @param usesGoogleCalendar Boolean if the user uses the google calendar to manage the charge plan.
     */
    public PCMPlan(String name, boolean usesGoogleCalendar) {
        super(name);
        mUsesGoogleCalendar = usesGoogleCalendar;
        mFragmentContainerId = super.getName().hashCode();
        // ID must be positive
        mFragmentContainerId = mFragmentContainerId < 0 ? mFragmentContainerId * (-1) : mFragmentContainerId;
    }

    @Override
    public void inflateLayout(Context context, LinearLayout container) throws IllegalArgumentException {
        super.inflateLayout(context, container);

        float density = context.getResources().getDisplayMetrics().density;

        LinearLayout.LayoutParams llLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        llLayoutParams.setMargins(0, 0, 0, (int) (MARGIN_BOTTOM * density));

        // Depending on the users preference to view the calendar load the according layout
        if(mUsesGoogleCalendar) {
            loadCalendarView(context, container, llLayoutParams);
        } else {
            loadPCMPlanView(context, container, llLayoutParams);
        }
    }

    @Override
    public String executeSaveOrGenerateJson(Context context) {
        // Call the synchronize charge plan task depending on the users preference to manage the charge plan
        if(mUsesGoogleCalendar) {
            new SynchronizeChargePlanAsyncTask((PowerConsumptionManagerAppContext) context.getApplicationContext(), null).execute();
        } else {
            new SynchronizeChargePlanAsyncTask((PowerConsumptionManagerAppContext) context.getApplicationContext(), mChargePlanData).execute();
        }

        // JSON is generated and already sent to the PCM in another task
        return "";
    }

    /**
     * Generates the layout for when the user uses the google calendar to manage the charge plan.
     * @param context Context of the widget to create.
     * @param container The layout where the generated view is added to.
     * @param layoutParams Layout parameters for the main layout container.
     */
    private void loadCalendarView(Context context, LinearLayout container, LinearLayout.LayoutParams layoutParams) {
        FragmentTransaction transaction = ((ComponentSettingsActivity) context).getSupportFragmentManager().beginTransaction();

        // New caldroid fragment and helper class instance
        CaldroidFragment mCaldroidFragment = new CaldroidFragment();
        PlanCalendarViewHelper mPlanCalendarViewHelper = new PlanCalendarViewHelper(context, mCaldroidFragment);

        // Format and set the id for the container that holds the caldroid fragment
        LinearLayout llFragmentContainer = new LinearLayout(context);
        llFragmentContainer.setId(mFragmentContainerId);
        llFragmentContainer.setOrientation(LinearLayout.VERTICAL);
        llFragmentContainer.setLayoutParams(layoutParams);

        // Setup the caldroid fragment
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        mPlanCalendarViewHelper.setup(cal);

        long startOfMonth = mPlanCalendarViewHelper.getMonthStart(year, month);
        long endOfMonth = mPlanCalendarViewHelper.getMonthEnd(year, month);

        mPlanCalendarViewHelper.readPlannedTrips(startOfMonth, endOfMonth);
        mPlanCalendarViewHelper.markDays();
        mPlanCalendarViewHelper.generateListener();

        // Attach the fragment to the container
        transaction.replace(mFragmentContainerId, mPlanCalendarViewHelper.getCaldroid()).commit();

        // Add the generated layout to the main layout container
        container.addView(llFragmentContainer);
    }

    /**
     * Generates the layout for when the user is not using the google calendar to manage the charge plan.
     * @param context Context of the widget to create.
     * @param container The layout where the generated view is added to.
     * @param layoutParams Layout parameters for the main layout container.
     */
    private void loadPCMPlanView(Context context, LinearLayout container, LinearLayout.LayoutParams layoutParams) {
        // Horizontal container for UI elements to edit the charge plan (1)
        LinearLayout llPCMPlan = new LinearLayout(context);
        llPCMPlan.setOrientation(LinearLayout.HORIZONTAL);
        llPCMPlan.setLayoutParams(layoutParams);

        // Generate the number picker UI to switch between the different weekdays
        LinearLayout.LayoutParams npLayoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1
        );
        npLayoutParams.gravity = Gravity.CENTER;

        Calendar cal = Calendar.getInstance();
        int tomorrow = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if(tomorrow < 0) {
            tomorrow += 7;
        }
        mDayIndex = tomorrow;

        NumberPicker npWeekdays = new NumberPicker(context);
        npWeekdays.setMinValue(0);
        npWeekdays.setMaxValue(6);
        npWeekdays.setDisplayedValues(context.getResources().getStringArray(R.array.weekdays));
        npWeekdays.setValue(mDayIndex);
        npWeekdays.setLayoutParams(npLayoutParams);

        // Add to horizontal container (1)
        llPCMPlan.addView(npWeekdays);

        LinearLayout.LayoutParams llTimePickersLayoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            4
        );

        // Vertical container for the start and end time of a trip (2)
        LinearLayout llTimePickers = new LinearLayout(context);
        llTimePickers.setOrientation(LinearLayout.VERTICAL);
        llTimePickers.setLayoutParams(llTimePickersLayoutParams);

        // Layout parameters for the UI elements to display start and end time of a trip
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

        // Horizontal layout to display departure label and time picker next to each other (3)
        LinearLayout llDeparture = new LinearLayout(context);
        llDeparture.setOrientation(LinearLayout.HORIZONTAL);
        llDeparture.setLayoutParams(llTimeContainers);

        // Departure label
        TextView tvDeparture = new TextView(context);
        tvDeparture.setText(context.getString(R.string.text_departure_time));
        tvDeparture.setTextSize(14);
        tvDeparture.setTextColor(ContextCompat.getColor(context, R.color.colorTextPrimary));
        tvDeparture.setGravity(Gravity.CENTER);
        tvDeparture.setLayoutParams(tvLayoutParams);

        // Add UI element to (3)
        llDeparture.addView(tvDeparture);

        // Departure timepicker
        final TimePicker tpDeparture = new TimePicker(context, null, 1); // Use this constructor to display the time picker as a spinner!
        tpDeparture.setIs24HourView(true);
        tpDeparture.setCurrentHour(mChargePlanData.get(mDayIndex).getDepartureHour());
        tpDeparture.setCurrentMinute(mChargePlanData.get(mDayIndex).getDepartureMinute());
        tpDeparture.setLayoutParams(tpLayoutParams);

        // Add UI element to (3)
        llDeparture.addView(tpDeparture);

        // Add departure container (3) to (2)
        llTimePickers.addView(llDeparture);

        // Analog for the arrival label and time picker
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

        final TimePicker tpArrival = new TimePicker(context, null, 1); // Use this constructor to display the time picker as a spinner!
        tpArrival.setIs24HourView(true);
        tpArrival.setCurrentHour(mChargePlanData.get(mDayIndex).getArrivalHour());
        tpArrival.setCurrentMinute(mChargePlanData.get(mDayIndex).getArrivalMinute());
        tpArrival.setLayoutParams(tpLayoutParams);

        llArrival.addView(tpArrival);

        llTimePickers.addView(llArrival);

        // Add the layout container to edit the trip times (2) to the horizontal main container (1)
        llPCMPlan.addView(llTimePickers);

        LinearLayout.LayoutParams llKmLayoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1
        );
        llKmLayoutParams.gravity = Gravity.CENTER;

        // Horizontal layout to display the amount of kilometers to drive (4)
        LinearLayout llKm = new LinearLayout(context);
        llKm.setOrientation(LinearLayout.HORIZONTAL);
        llKm.setLayoutParams(llKmLayoutParams);

        LinearLayout.LayoutParams tvKmLayoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );

        // Kilometer label
        TextView tvKm = new TextView(context);
        tvKm.setText(context.getString(R.string.unit_km));
        tvKm.setLayoutParams(tvKmLayoutParams);

        // Add UI element to (4)
        llKm.addView(tvKm);

        LinearLayout.LayoutParams etLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        etLayoutParams.setMargins(15, 0, 0, 0);

        // Edittext UI element to edit the amount of kilometers
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

        // Add UI element to (4)
        llKm.addView(etKm);

        // Add container layout to edit the kilometers to the horizontal main container
        llPCMPlan.addView(llKm);

        /* Set on-value-change listeners for the number picker (weekday), the time pickers (arrival, departure)
         * and the edit text to modify the kilometers
         */
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
                    if(s.toString().equals("")) {
                        mChargePlanData.get(mDayIndex).setKm(0);
                    } else {
                        mChargePlanData.get(mDayIndex).setKm(Integer.valueOf(s.toString()));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Add the generated layout to the main layout container
        container.addView(llPCMPlan);
    }

    /***********************
     * GETTERS AND SETTERS *
     ***********************/
    public LinkedHashMap<Integer, PCMPlanEntry> getChargePlanData() {
        return mChargePlanData;
    }
}

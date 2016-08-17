package ch.fhnw.ip6.powerconsumptionmanager.network;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.model.chargeplan.CalendarEntry;
import ch.fhnw.ip6.powerconsumptionmanager.model.chargeplan.RouteInformation;
import ch.fhnw.ip6.powerconsumptionmanager.model.chargeplan.PCMPlanEntry;
import ch.fhnw.ip6.powerconsumptionmanager.util.CalendarInstanceReader;
import ch.fhnw.ip6.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;
import ch.fhnw.ip6.powerconsumptionmanager.util.RouteProcessor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Background task to build and send the JSON for synchronizing the google calendar with the PCM or save the changed charge plan.
 */
public class SynchronizeChargePlanAsyncTask extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = "SyncChargePlanAsyncTask";
    private static final String[] WEEKDAY_SHORTCUTS = { "Mo", "Di", "Mi", "Do", "Fr", "Sa", "So" };

    private PowerConsumptionManagerAppContext mAppContext;
    private AsyncTaskCallback mCallbackContext;
    private LinkedHashMap<Integer, PCMPlanEntry> mChargePlanData;
    private StringBuilder mJsonString;
    private String[] mWeekToSync = new String[7];

    /**
     * Construct a new synchronization task.
     * @param appContext Application context.
     * @param chargePlanData Linked hash map with the charge plan data of the PCM.
     */
    public SynchronizeChargePlanAsyncTask(PowerConsumptionManagerAppContext appContext, LinkedHashMap<Integer, PCMPlanEntry> chargePlanData) {
        mAppContext = appContext;
        mChargePlanData = chargePlanData;
        mJsonString = new StringBuilder(1000);
    }

    /**
     * Construct a new synchronization task with a callback context.
     * @param appContext Application context.
     * @param callbackContext Context of the callback.
     * @param chargePlanData Linked hash map with the charge plan data of the PCM.
     */
    public SynchronizeChargePlanAsyncTask(
            PowerConsumptionManagerAppContext appContext,
            AsyncTaskCallback callbackContext,
            LinkedHashMap<Integer, PCMPlanEntry> chargePlanData) {
        this(appContext, chargePlanData);
        mCallbackContext = callbackContext;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean success = true;

        if(mChargePlanData == null) {
            Calendar calendar = Calendar.getInstance(Locale.GERMANY);
            // Determine from which day on the sync starts
            int syncStartDay = calendar.get(Calendar.DAY_OF_WEEK) - 2;
            if(syncStartDay < 0) {
                syncStartDay += 7;
            }

            CalendarInstanceReader cir = new CalendarInstanceReader(calendar, mAppContext);

            // Calculate the lower and upper range to request tesla trips to sync (one week range)
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            long lowerRangeEnd = calendar.getTimeInMillis();

            /* Get all the days that are being synchronized (needed because the keys in the hash map are the
             * days where the calendar instance takes place)
             */
            int[] dayKeys = new int[7];
            for (int i = 0; i < 7; i++) {
                dayKeys[i] = calendar.get(Calendar.DAY_OF_MONTH);
                if (i != 6) {
                    calendar.add(Calendar.DAY_OF_WEEK, 1);
                }
            }
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
            long upperRangeEnd = calendar.getTimeInMillis();

            // Get all calendar instances that take place in the current week
            HashMap<Integer, CalendarEntry> instances = cir.readInstancesBetweenTimestamps(lowerRangeEnd, upperRangeEnd);

            String day;
            String weekday;
            String start;
            String end;
            int kilometer = 0;

            // Build the actual JSON that is being sent
            mJsonString.append("[");
            for (int i = 0; i < 7; i++) {
                // Check if a calendar instance exists to a day that needs to be synced
                if (instances.containsKey(dayKeys[i])) {
                    CalendarEntry pem = instances.get(dayKeys[i]);
                    if(pem.getEventLocation() != null) {
                        String[] locations = pem.getEventLocation().split("/");
                        // Check if an event location has been specified
                        if (locations.length == 2 && !"".equals(locations[0]) && !"".equals(locations[1])) {
                            Request request = new Request.Builder()
                                    .url(mAppContext.getString(R.string.googleMaps_Api1) +
                                            "origin=" + locations[0] +
                                            "&destination=" + locations[1] +
                                            mAppContext.getString(R.string.googleMaps_Api2))
                                    .build();

                            try {
                                Response response = mAppContext.getOkHTTPClient().newCall(request).execute();
                                if (!RouteProcessor.processRoutes(response, mAppContext)) {
                                    success = false;
                                }
                            } catch (IOException e) {
                                Log.e(TAG, "Exception while loading routes data for synchronisation.");
                                success = false;
                            }
                        }
                    }

                    // Format the information that need to be sent
                    SimpleDateFormat shortDay = new SimpleDateFormat("EE", Locale.GERMAN);
                    SimpleDateFormat time = new SimpleDateFormat("HH:mm", Locale.GERMAN);

                    weekday = shortDay.format(pem.getBegin()).substring(0, 2);
                    start = time.format(pem.getBegin());
                    end = time.format(pem.getEnd());

                    RouteInformation rim = mAppContext.getRouteInformation();
                    if (rim != null) {
                        if (!rim.getDistanceText().equals("")) {
                            String[] withMeasurement = rim.getDistanceText().split(" ");
                            // Google API returns distances longer than 999 km as 1,000 km => ignore comma
                            String fullNumber = withMeasurement[0].replace(",", "");
                            kilometer = (int) Double.parseDouble(fullNumber);
                        }
                    }

                    // Reset route information
                    mAppContext.setRouteInformation(null);

                    // Build string for one day
                    day = "{" +
                        "\"Wochentag\": \"" + weekday + "\"," +
                        "\"Abfahrtszeit\": \"" + start + "\"," +
                        "\"Kilometer\": " + kilometer + "," +
                        "\"Ankunftszeit\": \"" + end + "\"," +
                        "\"Zusatz km\": 0" +
                        "}";

                    mWeekToSync[getWeekdayNumber(weekday)] = day;
                } else {
                    weekday = WEEKDAY_SHORTCUTS[(syncStartDay + i) % 7];

                    // Build string for day with no data
                    day = "{" +
                        "\"Wochentag\": \"" + weekday + "\"," +
                        "\"Abfahrtszeit\": \"00:00\"," +
                        "\"Kilometer\": 0," +
                        "\"Ankunftszeit\": \"00:00\"," +
                        "\"Zusatz km\": 0" +
                        "}";

                    mWeekToSync[getWeekdayNumber(weekday)] = day;
                }

                // Reset kilometers
                kilometer = 0;
            }

            for(int i = 0; i < mWeekToSync.length; i++) {
                appendDayToJson(mWeekToSync[i], i);
            }

            mJsonString.append("]");
        } else {
            mJsonString.append("[");

            for(int i = 0; i < mChargePlanData.size(); i++) {
                PCMPlanEntry pcmPlanEntry = mChargePlanData.get(i);

                // Build string for one day
                String day = "{" +
                    "\"Wochentag\": \"" + WEEKDAY_SHORTCUTS[i] + "\"," +
                    "\"Abfahrtszeit\": \"" + leftPad2(pcmPlanEntry.getDepartureHour()) + ":" + leftPad2(pcmPlanEntry.getDepartureMinute()) + "\"," +
                    "\"Kilometer\": " + pcmPlanEntry.getKm() + "," +
                    "\"Ankunftszeit\": \"" + leftPad2(pcmPlanEntry.getArrivalHour()) + ":" + leftPad2(pcmPlanEntry.getArrivalMinute()) + "\"," +
                    "\"Zusatz km\": 0" +
                    "}";

                appendDayToJson(day, i);
            }
            mJsonString.append("]");
        }

        // Only save when the building of the JSON was successful
        if(!mJsonString.toString().equals("") && success) {
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON, mJsonString.toString());

            // Make the PUT-request
            Request request = new Request.Builder()
                    .url("http://" + mAppContext.getIPAdress() + ":" + mAppContext.getString(R.string.webservice_putChargePlan))
                    .put(requestBody)
                    .build();

            try {
                // Execute the request
                Response response = mAppContext.getOkHTTPClient().newCall(request).execute();
                success = response.isSuccessful();
            } catch (IOException e) {
                Log.e(TAG, "Exception while saving charge plan data with PUT-request.");
                success = false;
            }
        } else {
            success = false;
        }

        return success;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);

        // Check if the task had a callback and notify accordingly
        if(mCallbackContext != null) {
            mCallbackContext.asyncTaskFinished(result, mAppContext.OP_TYPES[0]);
        } else {
            if(result) {
                Toast.makeText(mAppContext, mAppContext.getString(R.string.toast_sync_ended_success), Toast.LENGTH_SHORT).show();
                // Set no sync is pending after a successful sync
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mAppContext).edit();
                editor.putBoolean("brChargePlanSyncPending", false);
                editor.apply();
            } else {
                Toast.makeText(mAppContext, mAppContext.getString(R.string.toast_sync_ended_error), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Determines the index of a weekday by the weekdays shortcut.
     * @param weekday The shortcut of the weekday.
     * @return The index of the weekday in the week (monday = 0, sunday = 6). -1 when an error happens.
     */
    private int getWeekdayNumber(String weekday) {
        int weekdayNumber;

        // Get the weekday shortcut of the day that has no data to sync
        switch (weekday) {
            case "Mo":
                weekdayNumber = 0;
                break;
            case "Di":
                weekdayNumber = 1;
                break;
            case "Mi":
                weekdayNumber = 2;
                break;
            case "Do":
                weekdayNumber = 3;
                break;
            case "Fr":
                weekdayNumber = 4;
                break;
            case "Sa":
                weekdayNumber = 5;
                break;
            case "So":
                weekdayNumber = 6;
                break;
            default:
                weekdayNumber = -1;
                break;
        }

        return weekdayNumber;
    }

    /**
     * Appends a JSON (charge plan data of one weekday) to the "whole" JSON string.
     * @param jsonPart The JSON to append.
     * @param numberOfDay Number of the day in a week (monday = 0, sunday = 6).
     */
    private void appendDayToJson(String jsonPart, int numberOfDay) {
        mJsonString.append(jsonPart);
        if (numberOfDay != 6) {
            mJsonString.append(",");
        }
    }

    /**
     * Sets a 0 in front of one digit numeric values.
     * @param number Numeric value.
     * @return The numeric value with two digits (e.g. 01, 07, 10, 12).
     */
    private String leftPad2(int number) {
        return String.format("%0" + 2 + "d", number);
    }
}

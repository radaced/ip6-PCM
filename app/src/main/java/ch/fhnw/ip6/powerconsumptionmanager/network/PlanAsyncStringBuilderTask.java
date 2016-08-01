package ch.fhnw.ip6.powerconsumptionmanager.network;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.model.chargeplan.CalendarEntry;
import ch.fhnw.ip6.powerconsumptionmanager.model.RouteInformation;
import ch.fhnw.ip6.powerconsumptionmanager.model.chargeplan.PCMPlanEntry;
import ch.fhnw.ip6.powerconsumptionmanager.util.CalendarInstanceReader;
import ch.fhnw.ip6.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;
import ch.fhnw.ip6.powerconsumptionmanager.util.RouteProcessor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Background task to build the JSON of the charge plan data that needs to be synced
 */
public class PlanAsyncStringBuilderTask extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = "PlanSyncStringBuilder";
    private static final String[] WEEKDAY_SHORTCUTS = { "Mo", "Di", "Mi", "Do", "Fr", "Sa", "So" };

    private PowerConsumptionManagerAppContext mAppContext;
    private LinkedHashMap<Integer, PCMPlanEntry> mChargePlanData;
    private StringBuilder mJsonString;
    private String[] mWeekToSync = new String[7];
    private int mSyncStartDay;

    public PlanAsyncStringBuilderTask(PowerConsumptionManagerAppContext context, LinkedHashMap<Integer, PCMPlanEntry> chargePlanData) {
        mAppContext = context;
        mChargePlanData = chargePlanData;
        mJsonString = new StringBuilder(1000);
    }

    /**
     * Task is executed in background (necessary because no network activity can be run on the UI- or
     * main thread) and generates the string for the synchronization.
     * @param params No params needed
     * @return String for the put-Request
     */
    @Override
    protected Boolean doInBackground(Void... params) {
        boolean success = true;

        if(mChargePlanData == null) {
            Calendar calendar = Calendar.getInstance();
            CalendarInstanceReader cir = new CalendarInstanceReader(calendar, mAppContext);

            // Calculate the lower and upper range to request tesla trips to sync (one week range)
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            mSyncStartDay = calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH) - 1;
            long lowerRangeEnd = calendar.getTimeInMillis();

            /*
             * Get all the days that are being synchronized (needed because the keys in the hashmap are the
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
            int kilometer;

            // Build the actual JSON that is being sent
            mJsonString.append("[");
            for (int i = 0; i < 7; i++) {
                // Check if a calendar instance exists to a day that needs to be synched
                if (instances.containsKey(dayKeys[i])) {
                    CalendarEntry pem = instances.get(dayKeys[i]);
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

                    // Format the information that need to be sent
                    SimpleDateFormat shortDay = new SimpleDateFormat("EE", Locale.GERMAN);
                    SimpleDateFormat time = new SimpleDateFormat("HH:mm", Locale.GERMAN);

                    weekday = shortDay.format(pem.getBegin()).substring(0, 2);
                    start = time.format(pem.getBegin());
                    end = time.format(pem.getEnd());

                    RouteInformation rim = mAppContext.getRouteInformation();
                    if (rim.getDistanceText().equals("")) {
                        kilometer = 0;
                    } else {
                        String[] withMeasurement = rim.getDistanceText().split(" ");
                        kilometer = (int) Double.parseDouble(withMeasurement[0]);
                    }

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
                    weekday = WEEKDAY_SHORTCUTS[(mSyncStartDay + i) % 7];

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



        if(!mJsonString.toString().equals("")) {
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON, mJsonString.toString());
            // Make the put request
            Request request = new Request.Builder()
                    .url("http://" + mAppContext.getIPAdress() + ":" + mAppContext.getString(R.string.webservice_putChargePlan))
                    .put(requestBody)
                    .build();

            try {
                Response response = mAppContext.getOkHTTPClient().newCall(request).execute();
                success = response.isSuccessful();
            } catch (IOException e) {
                Log.e(TAG, "Exception while saving charge plan data with PUT-request.");
            }
        } else {
            success = false;
        }

        return success;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);

        if(result) {
            Toast.makeText(mAppContext, mAppContext.getString(R.string.toast_sync_ended_success), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mAppContext, mAppContext.getString(R.string.toast_sync_ended_error_loading), Toast.LENGTH_SHORT).show();
        }
    }

    private int getWeekdayNumber(String weekday) {
        int weekdayNumber;

        // Get the weekday shortcut of the day that has no data to sync
        if (weekday.equals("Mo")) {
            weekdayNumber = 0;
        } else if (weekday.equals("Di")) {
            weekdayNumber = 1;
        } else if (weekday.equals("Mi")) {
            weekdayNumber = 2;
        } else if (weekday.equals("Do")) {
            weekdayNumber = 3;
        } else if (weekday.equals("Fr")) {
            weekdayNumber = 4;
        } else if (weekday.equals("Sa")) {
            weekdayNumber = 5;
        } else {
            weekdayNumber = 6;
        }

        return weekdayNumber;
    }

    private String getWeekdayShortcut(int weekday) {
        String weekdayShortcut;

        // Get the weekday shortcut of the day that has no data to sync
        if (weekday == 0) {
            weekdayShortcut = "Mo";
        } else if (weekday == 1) {
            weekdayShortcut = "Di";
        } else if (weekday == 2) {
            weekdayShortcut = "Mi";
        } else if (weekday == 3) {
            weekdayShortcut = "Do";
        } else if (weekday == 4) {
            weekdayShortcut = "Fr";
        } else if (weekday == 5) {
            weekdayShortcut = "Sa";
        } else {
            weekdayShortcut = "So";
        }

        return weekdayShortcut;
    }

    private void appendDayToJson(String jsonPart, int numberOfDay) {
        mJsonString.append(jsonPart);
        if (numberOfDay != 6) {
            mJsonString.append(",");
        }
    }

    private String leftPad2(int number) {
        return String.format("%0" + 2 + "d", number);
    }
}

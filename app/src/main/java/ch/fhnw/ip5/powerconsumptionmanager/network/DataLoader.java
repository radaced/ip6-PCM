package ch.fhnw.ip5.powerconsumptionmanager.network;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.webkit.MimeTypeMap;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import ch.fhnw.ip5.powerconsumptionmanager.R;
import ch.fhnw.ip5.powerconsumptionmanager.model.ConsumptionDataModel;
import ch.fhnw.ip5.powerconsumptionmanager.model.PlanEntryModel;
import ch.fhnw.ip5.powerconsumptionmanager.model.RouteInformationModel;
import ch.fhnw.ip5.powerconsumptionmanager.util.PlanHelper;
import ch.fhnw.ip5.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;
import okio.BufferedSink;

/**
 * Loads the different data from the power consumption manager server component or other APIs
 * over asynchronous web requests
 */
public class DataLoader {
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

    private PowerConsumptionManagerAppContext mContext;
    private DataLoaderCallback mCallback;

    public DataLoader(PowerConsumptionManagerAppContext context, DataLoaderCallback callback) {
        this.mContext = context;
        this.mCallback = callback;
    }

    // getData call to get consumption data (24h) of all connected devices
    public void loadConsumptionData(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        OkHttpClient client = new OkHttpClient();
        // Define call and callback
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                mCallback.DataLoaderDidFail();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) {
                    mCallback.DataLoaderDidFail();
                    return;
                }

                // When response was successful ...
                try {
                    JSONArray dataJson = new JSONArray(response.body().string());

                    // ... fill model containers with each device and add the data to application context
                    for(int i = 0; i < dataJson.length(); i++) {
                        // TEST
                        if(i > 3) {
                            continue;
                        }
                        ConsumptionDataModel usageData = new ConsumptionDataModel((JSONObject) dataJson.get(i));
                        mContext.getConsumptionData().add(usageData);
                    }

                    // Directly call the loader for getComponents
                    loadComponents("http://" + mContext.getIPAdress() + ":" + mContext.getString(R.string.webservice_getComponents));
                    mCallback.DataLoaderDidFinish();
                } catch (JSONException e) {
                    e.printStackTrace();
                    mCallback.DataLoaderDidFail();
                }
            }
        });
    }

    // getComponents call to get names of all connected devices
    public void loadComponents(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        OkHttpClient client = new OkHttpClient();
        // Define call and callback
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                mCallback.DataLoaderDidFail();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) {
                    mCallback.DataLoaderDidFail();
                    return;
                }

                // When response was successful ...
                try {
                    JSONArray componentsJson = new JSONArray(response.body().string());

                    // ... get all component names from the response and store them in application context
                    for(int i = 0; i < componentsJson.length(); i++) {
                        // TEST
                        if(i > 3) {
                            continue;
                        }
                        String component = componentsJson.getString(i);
                        mContext.getComponents().add(component);
                    }

                    //mCallback.DataLoaderDidFinish();
                } catch (JSONException e) {
                    e.printStackTrace();
                    mCallback.DataLoaderDidFail();
                }
            }
        });
    }

    // Get distance and duration to reach it between origin and destination
    public void loadRouteInformation(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        OkHttpClient client = new OkHttpClient();
        // Define call and callback
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                mCallback.DataLoaderDidFail();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) {
                    mCallback.DataLoaderDidFail();
                    return;
                }

                if(processRoutes(response)) {
                    mCallback.DataLoaderDidFinish();
                } else {
                    mCallback.DataLoaderDidFail();
                }
            }
        });
    }

    public void synchronizeChargingPlan(String url) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String data = buildDataToSynchronize();

        RequestBody requestBody = RequestBody.create(JSON, data);
        Request request = new Request.Builder()
                .url(url)
                .put(requestBody)
                .build();

        OkHttpClient client = new OkHttpClient();
        // Define call and callback
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                mCallback.DataLoaderDidFail();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) {
                    mCallback.DataLoaderDidFail();
                    return;
                }

                mCallback.DataLoaderDidFinish();
            }
        });
    }

    private String buildDataToSynchronize() {
        boolean error = false;
        HashMap<Integer, PlanEntryModel> instances = new HashMap<Integer, PlanEntryModel>();
        ContentResolver cr = mContext.getContentResolver();
        Calendar calendar = Calendar.getInstance();

        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1, 0, 0, 0);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        calendar.set(Calendar.MILLISECOND, 0);
        long lowerRangeEnd = calendar.getTimeInMillis();
        int startKey = calendar.get(Calendar.DAY_OF_MONTH);

        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        calendar.add(Calendar.DAY_OF_WEEK, 6);
        long upperRangeEnd = calendar.getTimeInMillis();

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
            String title = cursor.getString(INSTANCE_TITLE);

            // Check if tesla trip or not
            if(!title.equals(mContext.getString(R.string.instance_title))) {
                continue;
            }

            String eventLocation = cursor.getString(INSTANCE_EVENT_LOCATION);
            String description = cursor.getString(INSTANCE_DESCRIPTION);
            long begin = cursor.getLong(INSTANCE_BEGIN);
            calendar.setTimeInMillis(begin);
            int startDay = calendar.get(Calendar.DAY_OF_MONTH);
            long end = cursor.getLong(INSTANCE_END);

            // Store read data into hash map
            if(!instances.containsKey(startDay)) {
                instances.put(startDay, new PlanEntryModel(title, eventLocation, description, new Date(begin), new Date(end)));
            }
        }
        cursor.close();

        String day;
        String weekday;
        String start;
        String end;
        int kilometer;

        StringBuilder data = new StringBuilder(1000);
        for(int i = 0; i < 7; i++) {
            if(instances.containsKey(startKey)) {
                PlanEntryModel pem = instances.get(startKey);
                String[] locations = pem.getEventLocation().split("/");
                if(locations != null && !"".equals(locations[0]) && !"".equals(locations[1])) {
                    Request request = new Request.Builder()
                        .url(mContext.getString(R.string.googleMaps_Api1) +
                            "origin=" + locations[0] +
                            "&destination=" + locations[1] +
                            mContext.getString(R.string.googleMaps_Api2))
                        .build();

                    OkHttpClient client = new OkHttpClient();
                    try {
                        Response response = client.newCall(request).execute();
                        processRoutes(response);
                    } catch (IOException e) {
                        error = true;
                    }
                }

                SimpleDateFormat shortDay = new SimpleDateFormat("EE", Locale.GERMAN);
                SimpleDateFormat time = new SimpleDateFormat("HH:mm", Locale.GERMAN);

                weekday = shortDay.format(pem.getBegin());
                start = time.format(pem.getBegin());
                end = time.format(pem.getEnd());

                RouteInformationModel rim = mContext.getRouteInformation();
                if(rim.getDistanceText().equals("")) {
                    kilometer = 0;
                } else {
                    String[] withMeasurement = rim.getDistanceText().split(" ");
                    /* TODO Kommazahl? */
                    kilometer = Integer.parseInt(withMeasurement[0]);
                }

                day = "{" +
                    "\"Wochentag\": \"" + weekday + "\"," +
                    "\"Abfahrtszeit\": \"" + start + "\"," +
                    "\"Kilometer\": " + kilometer + "," +
                    "\"Ankunftszeit\": \"" + end + "\"," +
                    "\"Zusatz km\": 0" +
                    "}";
            } else {
                switch (i) {
                    case 0:  weekday = "Mo"; break;
                    case 1:  weekday = "Di"; break;
                    case 2:  weekday = "Mi"; break;
                    case 3:  weekday = "Do"; break;
                    case 4:  weekday = "Fr"; break;
                    case 5:  weekday = "Sa"; break;
                    case 6:  weekday = "So"; break;
                    default: weekday = "Mo"; break;
                }

                day = "{" +
                    "\"Wochentag\": \"" + weekday + "\"," +
                    "\"Abfahrtszeit\": \"00:00\"," +
                    "\"Kilometer\": 0," +
                    "\"Ankunftszeit\": \"00:00\"," +
                    "\"Zusatz km\": 0" +
                    "}";
            }

            data.append(day);
        }

        return data.toString();
    }

    private boolean processRoutes(Response response) throws IOException {
        boolean success;

        // When response was successful ...
        try {
            JSONObject routeJson = new JSONObject(response.body().string());
            // ... navigate through JSON-tree
            JSONArray routesArray = routeJson.getJSONArray("routes");
            // Check if routes exist
            if(!routesArray.isNull(0)) {
                JSONObject route = routesArray.getJSONObject(0);
                JSONArray legs = route.getJSONArray("legs");
                JSONObject leg = legs.getJSONObject(0);

                // Extract data
                JSONObject distance = leg.getJSONObject("distance");
                String distanceText = distance.getString("text");
                JSONObject duration = leg.getJSONObject("duration");
                String durationText = duration.getString("text");

                // Store in application context
                mContext.setRouteInformation(new RouteInformationModel(durationText, distanceText));
            } else {
                mContext.setRouteInformation(new RouteInformationModel(
                    mContext.getString(R.string.text_route_information_no_route),
                    ""
                ));
            }
            success = true;
        } catch (JSONException e) {
            e.printStackTrace();
            success = false;
        }

        return success;
    }
}

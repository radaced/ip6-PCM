package ch.fhnw.ip5.powerconsumptionmanager.network;

import android.util.Log;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import ch.fhnw.ip5.powerconsumptionmanager.R;
import ch.fhnw.ip5.powerconsumptionmanager.model.ConsumptionDataModel;
import ch.fhnw.ip5.powerconsumptionmanager.model.RouteInformationModel;
import ch.fhnw.ip5.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;

/**
 * Loads the different data from the power consumption manager server component or other APIs
 * over asynchronous web requests with OkHttp
 */
public class DataLoader {
    private static final String TAG = "DataLoader";

    private PowerConsumptionManagerAppContext mAppContext;
    private DataLoaderCallback mCallback;
    private OkHttpClient mClient;

    public DataLoader(PowerConsumptionManagerAppContext context, DataLoaderCallback callback) {
        this.mAppContext = context;
        this.mCallback = callback;
        this.mClient = new OkHttpClient();
    }

    /**
     * getData call to get consumption data (24h) and names of all connected devices
     * @param url Address that needs to be called
     */
    //
    public void loadConsumptionData(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        // Define call and callback
        mClient.newCall(request).enqueue(new Callback() {
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

                // Lists to hold the loaded consumption data and component names
                ArrayList<ConsumptionDataModel> consumptionData = new ArrayList<>();
                ArrayList<String> components = new ArrayList<>();

                try {
                    JSONArray dataJson = new JSONArray(response.body().string());

                    // Fill model containers with each device and add the data to the list
                    for(int i = 0; i < dataJson.length(); i++) {
                        /* TODO lower loaded for testing */
                        if(i > 3) {
                            continue;
                        }
                        ConsumptionDataModel usageData = new ConsumptionDataModel((JSONObject) dataJson.get(i));
                        consumptionData.add(usageData);
                        components.add(usageData.getComponentName());
                    }

                    // Make consumption data and component names available for the application through the application context
                    mAppContext.setConsumptionData(consumptionData);
                    mAppContext.setComponents(components);
                    mCallback.DataLoaderDidFinish();
                } catch (JSONException e) {
                    Log.e(TAG, "JSON exception while processing consumption data.");
                    mCallback.DataLoaderDidFail();
                }
            }
        });
    }

    /**
     * getComponents call to get names of all connected devices
     * @param url Address that needs to be called
     */
    public void loadComponents(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        // Define call and callback
        mClient.newCall(request).enqueue(new Callback() {
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

                // List to hold the components that are connected with the power consumption manager
                ArrayList<String> components = new ArrayList<>();

                try {
                    JSONArray componentsJson = new JSONArray(response.body().string());

                    // Get all component names from the response and store them in application context
                    for(int i = 0; i < componentsJson.length(); i++) {
                        // TEST
                        if(i > 3) {
                            continue;
                        }
                        String component = componentsJson.getString(i);
                        components.add(component);
                    }

                    // Make components data available for the application through the application context
                    mAppContext.setComponents(components);
                    mCallback.DataLoaderDidFinish();
                } catch (JSONException e) {
                    Log.e(TAG, "JSON exception while processing component data.");
                    mCallback.DataLoaderDidFail();
                }
            }
        });
    }


    /**
     * Get distance and duration to reach it between origin and destination
     * @param url Address that needs to be called
     */
    public void loadRouteInformation(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        // Define call and callback
        mClient.newCall(request).enqueue(new Callback() {
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

    /**
     * Synchronize the tesla trips of the current week with the server component
     * @param url Address that needs to be called
     * @throws ExecutionException
     * @throws InterruptedException
     */
    //
    public void synchronizeChargePlan(String url) throws ExecutionException, InterruptedException {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String data = new PlanSyncStringBuilderTask(mAppContext).execute().get();

        if(!data.equals("")) {
            RequestBody requestBody = RequestBody.create(JSON, data);
            // Make the put request
            Request request = new Request.Builder()
                    .url(url)
                    .put(requestBody)
                    .build();

            // Define call and callback
            mClient.newCall(request).enqueue(new Callback() {
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
        } else {
            Toast.makeText(mAppContext.getApplicationContext(), "Sync failed (error building data to sync)", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Processes the route information to two locations and stores the loaded information for further use
     * @param response The response from the OkHttp request
     * @return true when no successful, false when errors occured
     * @throws IOException
     */
    //
    public boolean processRoutes(Response response) throws IOException {
        boolean success = true;
        try {
            JSONObject routeJson = new JSONObject(response.body().string());
            // Navigate through JSON-tree
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
                mAppContext.setRouteInformation(new RouteInformationModel(durationText, distanceText));
            } else {
                mAppContext.setRouteInformation(new RouteInformationModel(
                        mAppContext.getString(R.string.text_route_information_no_route),
                        ""
                ));
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON exception while processing routes data.");
            success = false;
        }

        return success;
    }
}

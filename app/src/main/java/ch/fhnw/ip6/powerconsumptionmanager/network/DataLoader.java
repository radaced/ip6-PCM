package ch.fhnw.ip6.powerconsumptionmanager.network;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.model.ConsumptionChartDataModel;
import ch.fhnw.ip6.powerconsumptionmanager.model.RouteInformation;
import ch.fhnw.ip6.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
     *
     * @param url Address that needs to be called
     */
    public void loadConsumptionData(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        // Define call and callback
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mCallback.DataLoaderDidFail();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    mCallback.DataLoaderDidFail();
                    return;
                }

                // Lists to hold the loaded consumption data and component names
                ArrayList<ConsumptionChartDataModel> consumptionData = new ArrayList<>();
                ArrayList<String> components = new ArrayList<>();

                try {
                    JSONArray dataJson = new JSONArray(response.body().string());

                    // Fill model containers with each device and add the data to the list
                    for (int i = 0; i < dataJson.length(); i++) {
                        ConsumptionChartDataModel usageData = new ConsumptionChartDataModel((JSONObject) dataJson.get(i));
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
}

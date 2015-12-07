package ch.fhnw.ip5.powerconsumptionmanager.network;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ch.fhnw.ip5.powerconsumptionmanager.model.ConsumptionDataModel;
import ch.fhnw.ip5.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;

/**
 * Created by Patrik on 02.12.2015.
 */
public class DataLoader {
    private PowerConsumptionManagerAppContext mContext;
    private DataLoaderCallback mCallback;

    public DataLoader(PowerConsumptionManagerAppContext context, DataLoaderCallback callback) {
        this.mContext = context;
        this.mCallback = callback;
    }

    public void loadConsumptionData(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                mCallback.UsageDataLoaderDidFail();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) {
                    mCallback.UsageDataLoaderDidFail();
                    return;
                }
                try {
                    JSONArray dataJson = new JSONArray(response.body().string());

                    for(int i = 0; i < dataJson.length(); i++) {
                        // TEST
                        if(i > 3) {
                            continue;
                        }
                        ConsumptionDataModel usageData = new ConsumptionDataModel((JSONObject) dataJson.get(i));
                        mContext.getConsumptionData().add(usageData);
                    }

                    mCallback.UsageDataLoaderDidFinish();
                } catch (JSONException e) {
                    e.printStackTrace();
                    mCallback.UsageDataLoaderDidFail();
                }
            }
        });
    }

    public void loadComponents(String url) {

        Request request = new Request.Builder()
                .url(url)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                mCallback.UsageDataLoaderDidFail();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) {
                    mCallback.UsageDataLoaderDidFail();
                    return;
                }
                try {
                    JSONArray dataJson = new JSONArray(response.body().string());

                    for(int i = 0; i < dataJson.length(); i++) {
                        if(i > 3) {
                            continue;
                        }
                        String component = dataJson.getString(i);
                        mContext.getComponents().add(component);
                    }

                    //mCallback.UsageDataLoaderDidFinish();
                } catch (JSONException e) {
                    e.printStackTrace();
                    mCallback.UsageDataLoaderDidFail();
                }
            }
        });
    }
}

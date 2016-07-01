package ch.fhnw.ip6.powerconsumptionmanager.network;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.model.dashboard.CurrentPCMComponentData;
import ch.fhnw.ip6.powerconsumptionmanager.model.dashboard.CurrentPCMData;
import ch.fhnw.ip6.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;

public class GetCurrentPCMDataAsyncTask extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = "GetCurrentDataAsyncTask";

    private PowerConsumptionManagerAppContext mAppContext;
    private AsyncTaskCallback mCallbackContext;
    private String mURL;
    private CurrentPCMData mCurrentPCMData;

    public GetCurrentPCMDataAsyncTask(PowerConsumptionManagerAppContext context, AsyncTaskCallback callbackContext, String url) {
        mAppContext = context;
        mCallbackContext = callbackContext;
        mURL = url;
        mCurrentPCMData = new CurrentPCMData();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean success = true;
        Request currentStatistics = new Request.Builder()
                .url(mURL + mAppContext.getString(R.string.webservice_getCurrentStatistics))
                .build();
        Request currentComponentData = new Request.Builder()
                .url(mURL + mAppContext.getString(R.string.webservice_getCurrentData))
                .build();

        Response response;
        OkHttpClient client = new OkHttpClient();
        try {
            response = client.newCall(currentStatistics).execute();
            if(!handleCurrentStatisticsResponse(response)) {
                success = false;
            }
        } catch (IOException e) {
            Log.e(TAG, "Exception while loading current statistics data for dashboard.");
            success = false;
        }
        try {
            response = client.newCall(currentComponentData).execute();
            if(!handleCurrentComponentDataResponse(response)) {
                success = false;
            }
        } catch (IOException e) {
            Log.e(TAG, "Exception while loading current component data for dashboard.");
            success = false;
        }

        mAppContext.setCurrentPCMData(mCurrentPCMData);

        return success;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        mCallbackContext.asyncTaskFinished(result);
    }

    public boolean handleCurrentStatisticsResponse(Response response) throws IOException {
        boolean success = true;
        try {
            JSONObject dataJson = new JSONObject(response.body().string());
            mCurrentPCMData.setAutarchy(dataJson.getDouble("Autarkie(%)"));
            mCurrentPCMData.setSelfsupply(dataJson.getDouble("Eigenverbrauch(%)"));
            mCurrentPCMData.setConsumption(dataJson.getDouble("Bezug(kW)"));

            int occupationColorCode = dataJson.getInt("Bezug(Farbe)");
            int red = Color.red(occupationColorCode);
            int blue = Color.blue(occupationColorCode);
            int green = Color.green(occupationColorCode);
            mCurrentPCMData.setConsumptionColor(Color.rgb(red, blue, green));
        } catch (JSONException e) {
            Log.e(TAG, "JSON exception while processing current statistics data.");
            success = false;
        }

        return success;
    }

    public boolean handleCurrentComponentDataResponse(Response response) throws IOException {
        boolean success = true;
        try {
            JSONArray dataJson = new JSONArray(response.body().string());

            for(int i = 0; i < dataJson.length(); i++) {
                JSONObject dataJsonEntry = (JSONObject) dataJson.get(i);
                CurrentPCMComponentData ccdm = new CurrentPCMComponentData(dataJsonEntry.getJSONObject("Data"));
                mCurrentPCMData.getCurrentComponentData().put(dataJsonEntry.getString("Name"), ccdm);
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON exception while processing current component data.");
            success = false;
        }

        return success;
    }
}

package ch.fhnw.ip6.powerconsumptionmanager.network;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.model.dashboard.CurrentPCMComponentData;
import ch.fhnw.ip6.powerconsumptionmanager.model.dashboard.CurrentPCMData;
import ch.fhnw.ip6.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
        boolean successStatistics;
        boolean successComponentData;

        Request currentStatistics = new Request.Builder()
                .url(mURL + mAppContext.getString(R.string.webservice_getCurrentStatistics))
                .build();
        Request currentComponentData = new Request.Builder()
                .url(mURL + mAppContext.getString(R.string.webservice_getCurrentData))
                .build();

        Response response;

        try {
            response = mAppContext.getOkHTTPClient().newCall(currentStatistics).execute();
            if(!response.isSuccessful()) {
                Log.e(TAG, "Response for current statistics data not successful.");
                return false;
            }
            successStatistics = handleCurrentStatisticsResponse(response);
        } catch (IOException e) {
            Log.e(TAG, "Exception while loading current statistics data for dashboard.");
            successStatistics = false;
        }

        try {
            response = mAppContext.getOkHTTPClient().newCall(currentComponentData).execute();
            if(!response.isSuccessful()) {
                Log.e(TAG, "Response for current component data not successful.");
                return false;
            }
            successComponentData = handleCurrentComponentDataResponse(response);
        } catch (IOException e) {
            Log.e(TAG, "Exception while loading current component data for dashboard.");
            successComponentData = false;
        }

        mAppContext.setCurrentPCMData(mCurrentPCMData);

        return successStatistics && successComponentData;
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
            int red = occupationColorCode >> 16 & 0xff;
            int green = occupationColorCode >> 8 & 0xff;
            int blue = occupationColorCode & 0xff;
            mCurrentPCMData.setConsumptionColor(Color.rgb(red, green, blue));
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

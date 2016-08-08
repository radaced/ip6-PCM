package ch.fhnw.ip6.powerconsumptionmanager.network;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.model.PCMComponent;
import ch.fhnw.ip6.powerconsumptionmanager.model.PCMData;
import ch.fhnw.ip6.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Async task to load the current data of the PCM (current consumption, self supply, ...).
 */
public class GetCurrentPCMDataAsyncTask extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = "GetCurrentDataAsyncTask";

    private PowerConsumptionManagerAppContext mAppContext;
    private AsyncTaskCallback mCallbackContext;
    private String mURL;
    private PCMData mPCMData;

    /**
     * Constructor to create a new task to request the current data of the PCM.
     * @param appContext Application context.
     * @param callbackContext Context of the callback.
     */
    public GetCurrentPCMDataAsyncTask(PowerConsumptionManagerAppContext appContext, AsyncTaskCallback callbackContext) {
        mAppContext = appContext;
        mCallbackContext = callbackContext;
        mURL = "http://" + mAppContext.getIPAdress() + ":";
        mPCMData = mAppContext.getPCMData();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean successStatistics;
        boolean successComponentData;

        /* The current data is divided into two parts over two separate webservices, the current statistics and the current data of
         * every connected component.
         */
        Request currentStatistics = new Request.Builder()
                .url(mURL + mAppContext.getString(R.string.webservice_getCurrentStatistics))
                .build();
        Request currentComponentData = new Request.Builder()
                .url(mURL + mAppContext.getString(R.string.webservice_getCurrentData))
                .build();

        Response response;

        try {
            // Request the current statistics like consumption, self supply, ... and process the response
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
            // Request the current data of every component (current consumption, costs, ...) and process the response
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

        return successStatistics && successComponentData;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        mCallbackContext.asyncTaskFinished(result, mAppContext.OP_TYPES[0]);
    }

    /**
     * Processes the response of the current statistics request.
     * @param response The response of the current statistics request.
     * @return State if the response could be processed successfully.
     * @throws IOException
     */
    public boolean handleCurrentStatisticsResponse(Response response) throws IOException {
        boolean success = true;
        try {
            // Set the current statistics in the PCMData base structure
            JSONObject dataJson = new JSONObject(response.body().string());
            mPCMData.setAutarchy(dataJson.getDouble("Autarkie(%)"));
            mPCMData.setSelfsupply(dataJson.getDouble("Eigenverbrauch(%)"));
            mPCMData.setConsumption(dataJson.getDouble("Bezug(kW)"));

            // Color of the consumption wave loading view
            int red = dataJson.getInt("SaeulenFarbe(R)");
            int green = dataJson.getInt("SaeulenFarbe(G)");
            int blue = dataJson.getInt("SaeulenFarbe(B)");
            mPCMData.setConsumptionColor(Color.rgb(red, green, blue));

            // The scale of the current consumption statistic
            mPCMData.setMinScaleConsumption(dataJson.getInt("Grenze_unten(kW)"));
            mPCMData.setMaxScaleConsumption(dataJson.getInt("Grenze_oben(kW)"));
        } catch (JSONException e) {
            Log.e(TAG, "JSON exception while processing current statistics data.");
            success = false;
        }

        return success;
    }

    /**
     * Processes the response of the current data of all components request.
     * @param response The response of the current data of all components request.
     * @return State if the response could be processed successfully.
     * @throws IOException
     */
    public boolean handleCurrentComponentDataResponse(Response response) throws IOException {
        boolean success = true;
        try {
            JSONArray dataJson = new JSONArray(response.body().string());

            for(int i = 0; i < dataJson.length(); i++) {
                JSONObject dataJsonEntry = (JSONObject) dataJson.get(i);
                // Get the currently processing component of the connected components map
                PCMComponent component = mPCMData.getComponentData().get(dataJsonEntry.getString("Name"));

                // Fill the current data into the PCMComponent structure
                if(component != null) {
                    component.fillDashboardData(
                        dataJsonEntry.getJSONObject("Data"),
                        dataJsonEntry.getInt("Grenze_unten(kW)"),
                        dataJsonEntry.getInt("Grenze_oben(kW)")
                    );
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON exception while processing current component data.");
            success = false;
        }

        return success;
    }
}

package ch.fhnw.ip6.powerconsumptionmanager.network;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.model.consumptiondata.ConsumptionComponentModel;
import ch.fhnw.ip6.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;
import okhttp3.Request;
import okhttp3.Response;

public class GetConsumptionDataAsyncTask  extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = "GetConsumpDataAsyncTask";

    private PowerConsumptionManagerAppContext mAppContext;
    private AsyncTaskCallback mCallbackContext;
    private String mURL;

    public GetConsumptionDataAsyncTask(PowerConsumptionManagerAppContext context, AsyncTaskCallback callbackContext) {
        mAppContext = context;
        mCallbackContext = callbackContext;
        mURL = "http://" + mAppContext.getIPAdress() + ":" + mAppContext.getString(R.string.webservice_getConsumptionData);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean success;

        Request request = new Request.Builder()
                .url(mURL)
                .build();

        try {
            Response response = mAppContext.getOkHTTPClient().newCall(request).execute();
            if(!response.isSuccessful()) {
                Log.e(TAG, "Response for consumption data not successful.");
                return false;
            }
            success = handleResponse(response);
        } catch (IOException e) {
            Log.e(TAG, "Exception while loading consumption data.");
            success = false;
        }

        return success;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        mCallbackContext.asyncTaskFinished(result);
    }

    public boolean handleResponse(Response response) throws IOException {
        boolean success = true;

        // List to hold the loaded consumption data
        ArrayList<ConsumptionComponentModel> consumptionData = new ArrayList<>();

        try {
            JSONArray dataJson = new JSONArray(response.body().string());

            // Fill model containers with each device and add the data to the list
            for (int i = 0; i < dataJson.length(); i++) {
                ConsumptionComponentModel usageData = new ConsumptionComponentModel((JSONObject) dataJson.get(i));
                consumptionData.add(usageData);
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON exception while processing consumption data.");
            success = false;
        }

        // Make consumption data available for the application through the application context
        mAppContext.setConsumptionData(consumptionData);

        return success;
    }
}

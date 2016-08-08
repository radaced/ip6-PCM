package ch.fhnw.ip6.powerconsumptionmanager.network;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.model.PCMComponent;
import ch.fhnw.ip6.powerconsumptionmanager.model.PCMData;
import ch.fhnw.ip6.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Async task that loads all connected components.
 */
public class GetConnectedComponentsAsyncTask extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = "GetConnCompAsyncTask";

    private PowerConsumptionManagerAppContext mAppContext;
    private AsyncTaskCallback mCallbackContext;
    private String mURL;
    private PCMData mPCMData;

    /**
     * Constructor to create a new async task to load all connected components.
     * @param appContext Application context.
     * @param callbackContext Context of the callback.
     */
    public GetConnectedComponentsAsyncTask(PowerConsumptionManagerAppContext appContext, AsyncTaskCallback callbackContext) {
        mAppContext = appContext;
        mCallbackContext = callbackContext;
        mURL = "http://" + mAppContext.getIPAdress() + ":" + mAppContext.getString(R.string.webservice_getComponents);
        // Create new PCM data object (base structure for PCM data)
        mPCMData = new PCMData();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean success;

        Request request = new Request.Builder()
                .url(mURL)
                .build();

        try {
            // Make the request and process the response
            Response response = mAppContext.getOkHTTPClient().newCall(request).execute();
            if(!response.isSuccessful()) {
                Log.e(TAG, "Response for connected components not successful.");
                return false;
            }
            success = handleResponse(response);
        } catch (IOException e) {
            Log.e(TAG, "Exception while loading connected components.");
            success = false;
        }

        return success;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        // Populate the new PCM data object onto the application context to fill it further with data from the PCM later on
        mAppContext.setPCMData(mPCMData);
        mCallbackContext.asyncTaskFinished(result, mAppContext.OP_TYPES[0]);
        // Directly load the current data of all connected components after this task (initial data)
        new GetCurrentPCMDataAsyncTask(mAppContext, mCallbackContext).execute();
    }

    /**
     * Processes the response of the connected components request.
     * @param response The response of the connected components request.
     * @return State if the response could be processed successfully.
     * @throws IOException
     */
    public boolean handleResponse(Response response) throws IOException {
        boolean success = true;

        try {
            JSONArray componentsJson = new JSONArray(response.body().string());

            // Get all component names from the response and save them as PCMComponent objects in the PCMData base structure
            for(int i = 0; i < componentsJson.length(); i++) {
                String component = componentsJson.getString(i);
                mPCMData.getComponentData().put(component, new PCMComponent(component));
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON exception while processing component data.");
            success = false;
        }

        return success;
    }
}

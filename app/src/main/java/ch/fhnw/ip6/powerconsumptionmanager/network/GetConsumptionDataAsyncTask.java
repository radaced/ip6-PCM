package ch.fhnw.ip6.powerconsumptionmanager.network;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedHashMap;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.model.PCMComponent;
import ch.fhnw.ip6.powerconsumptionmanager.model.ConsumptionData;
import ch.fhnw.ip6.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Async task that loads the consumption data of all components.
 */
public class GetConsumptionDataAsyncTask  extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = "GetConsumpDataAsyncTask";

    private PowerConsumptionManagerAppContext mAppContext;
    private AsyncTaskCallback mCallbackContext;
    private String mURL;

    /**
     * Constructor to create a new async task to load the consumption data.
     * @param appContext Application context.
     * @param callbackContext Context of the callback.
     */
    public GetConsumptionDataAsyncTask(PowerConsumptionManagerAppContext appContext, AsyncTaskCallback callbackContext) {
        mAppContext = appContext;
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
            // Request the consumption data and process the response
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
        mCallbackContext.asyncTaskFinished(result, mAppContext.OP_TYPES[0]);
    }

    /**
     * Processes the response of the consumption data request.
     * @param response The response of the consumption data request.
     * @return State if the response could be processed successfully.
     * @throws IOException
     */
    public boolean handleResponse(Response response) throws IOException {
        boolean success = true;

        // Get the list with all connected components
        LinkedHashMap<String, PCMComponent> componentData = mAppContext.getPCMData().getComponentData();

        try {
            JSONArray dataJson = new JSONArray(response.body().string());

            // Fill the consumption data list of every component with the received consumption data
            for (int i = 0; i < dataJson.length(); i++) {
                JSONObject jsonConsumptionDataPerComponent = (JSONObject) dataJson.get(i);
                JSONArray jsonConsumptionData = jsonConsumptionDataPerComponent.getJSONArray("Data");
                PCMComponent component = componentData.get(jsonConsumptionDataPerComponent.getString("Name"));

                if(component != null) {
                    component.getConsumptionData().clear();
                    for(int j = 0; j < jsonConsumptionData.length(); j++){
                        componentData.get(jsonConsumptionDataPerComponent.getString("Name")).getConsumptionData().add(
                            new ConsumptionData(jsonConsumptionData.getJSONObject(j))
                        );
                    }
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON exception while processing consumption data.");
            success = false;
        }

        return success;
    }
}

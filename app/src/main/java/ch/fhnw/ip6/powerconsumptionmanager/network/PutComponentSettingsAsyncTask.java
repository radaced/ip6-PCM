package ch.fhnw.ip6.powerconsumptionmanager.network;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Async task to save the component settings of a component.
 */
public class PutComponentSettingsAsyncTask extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = "PlanSyncStringBuilder";

    private PowerConsumptionManagerAppContext mAppContext;
    private AsyncTaskCallback mCallbackContext;
    private String mJsonString;
    private String mComponentName;

    /**
     * Constructs a new async task to save the settings of a component.
     * @param appContext Application context.
     * @param callbackContext Context of the callback.
     * @param json The JSON to send via PUT-request.
     * @param component The component of which the settings are being saved.
     */
    public PutComponentSettingsAsyncTask(PowerConsumptionManagerAppContext appContext, AsyncTaskCallback callbackContext, String json, String component) {
        mAppContext = appContext;
        mCallbackContext = callbackContext;
        mJsonString = json;
        mComponentName = component;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean success;

        /* TODO: Saving settings only works for putProgramSettings yet (Zogg Energy Control)*/
        String[] jsonParts = mJsonString.split("\\[\\[ProgramSettings\\]\\]");

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, jsonParts[1].substring(0, jsonParts[1].length()-1));

        // Create the PUT-request
        Request request = new Request.Builder()
                .url("http://" + mAppContext.getIPAdress() + ":" + mAppContext.getString(R.string.webservice_putProgramSettings) + mComponentName)
                .put(requestBody)
                .build();

        try {
            // Execute request
            Response response = mAppContext.getOkHTTPClient().newCall(request).execute();
            success = response.isSuccessful();
        } catch (IOException e) {
            Log.e(TAG, "Exception while saving program settings data with PUT-request.");
            success = false;
        }

        return success;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        mCallbackContext.asyncTaskFinished(result, mAppContext.OP_TYPES[1]);
    }
}

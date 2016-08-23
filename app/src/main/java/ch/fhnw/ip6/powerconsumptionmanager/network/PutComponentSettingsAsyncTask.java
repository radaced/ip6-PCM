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
    private String mNiedertarifJson;
    private String mProgrammEndeJson;
    private String mComponentName;

    /**
     * Constructs a new async task to save the settings of a component.
     * @param appContext Application context.
     * @param callbackContext Context of the callback.
     * @param json The JSON to send via PUT-request.
     * @param component The component of which the settings are being saved.
     */
    public PutComponentSettingsAsyncTask(PowerConsumptionManagerAppContext appContext,
                                         AsyncTaskCallback callbackContext,
                                         String json,
                                         String niedertarifJson,
                                         String programmEndeJson,
                                         String component)
    {
        mAppContext = appContext;
        mCallbackContext = callbackContext;
        mJsonString = json;
        mNiedertarifJson = niedertarifJson;
        mProgrammEndeJson = programmEndeJson;
        mComponentName = component;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean success = true;

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        Request requestComfortSettings = null, requestProgrammEnde = null, requestNiedertarif = null;

        if(!mJsonString.equals("]")) {
            RequestBody requestBody = RequestBody.create(JSON, mJsonString);

            requestComfortSettings = new Request.Builder()
                    .url("http://" + mAppContext.getIPAdress() + ":" + mAppContext.getString(R.string.webservice_putComfortSettings) + mComponentName)
                    .put(requestBody)
                    .build();
        }

        if(!mNiedertarifJson.equals("")) {
            RequestBody requestBodyNiedertarif = RequestBody.create(JSON, mNiedertarifJson);

            requestNiedertarif = new Request.Builder()
                    .url("http://" + mAppContext.getIPAdress() + ":" + mAppContext.getString(R.string.webservice_putNiedertarif) + mComponentName)
                    .put(requestBodyNiedertarif)
                    .build();
        }

        if(!mProgrammEndeJson.equals("")) {
            RequestBody requestBodyProgrammEnde = RequestBody.create(JSON, mProgrammEndeJson);

            requestProgrammEnde = new Request.Builder()
                    .url("http://" + mAppContext.getIPAdress() + ":" + mAppContext.getString(R.string.webservice_putProgrammEnde) + mComponentName)
                    .put(requestBodyProgrammEnde)
                    .build();
        }

        try {
            // Execute request
            Response response;
            if(requestComfortSettings != null) {
                response = mAppContext.getOkHTTPClient().newCall(requestComfortSettings).execute();
                success = response.isSuccessful();
            }
            if(success && requestNiedertarif != null) {
                response = mAppContext.getOkHTTPClient().newCall(requestNiedertarif).execute();
                success = response.isSuccessful();
            }
            if(success && requestProgrammEnde != null) {
                response = mAppContext.getOkHTTPClient().newCall(requestProgrammEnde).execute();
                success = response.isSuccessful();
            }
        } catch (IOException e) {
            Log.e(TAG, "Exception while saving settings data with PUT-request.");
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

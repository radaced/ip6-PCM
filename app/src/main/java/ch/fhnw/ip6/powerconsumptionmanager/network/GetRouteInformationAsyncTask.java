package ch.fhnw.ip6.powerconsumptionmanager.network;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import ch.fhnw.ip6.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;
import ch.fhnw.ip6.powerconsumptionmanager.util.RouteProcessor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Gets the route information to two locations over the google maps API.
 */
public class GetRouteInformationAsyncTask extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = "GetRouteInfoAsyncTask";

    private PowerConsumptionManagerAppContext mAppContext;
    private AsyncTaskCallback mCallbackContext;
    private String mURL;

    /**
     * Constructor to create a new task to request route information to two locations over the google maps API.
     * @param appContext Application context.
     * @param callbackContext Context of the callback.
     * @param url The exact URL to request on the google maps API.
     */
    public GetRouteInformationAsyncTask(PowerConsumptionManagerAppContext appContext, AsyncTaskCallback callbackContext, String url) {
        mAppContext = appContext;
        mCallbackContext = callbackContext;
        mURL = url;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean success;

        Request request = new Request.Builder()
                .url(mURL)
                .build();

        try {
            // Request and process the route information
            Response response = mAppContext.getOkHTTPClient().newCall(request).execute();
            if(!response.isSuccessful()) {
                Log.e(TAG, "Response for route information not successful.");
                return false;
            }
            success = RouteProcessor.processRoutes(response, mAppContext);
        } catch (IOException e) {
            Log.e(TAG, "Exception while loading general statistics data.");
            success = false;
        }

        return success;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        mCallbackContext.asyncTaskFinished(result, mAppContext.OP_TYPES[0]);
    }
}

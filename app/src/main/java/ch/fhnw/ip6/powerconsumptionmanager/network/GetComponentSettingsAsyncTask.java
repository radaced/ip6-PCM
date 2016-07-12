package ch.fhnw.ip6.powerconsumptionmanager.network;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.model.dashboard.PCMData;
import ch.fhnw.ip6.powerconsumptionmanager.model.settings.PCMSlider;
import ch.fhnw.ip6.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Patrik on 12.07.2016.
 */
public class GetComponentSettingsAsyncTask extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = "GetCompSettAsyncTask";

    private PowerConsumptionManagerAppContext mAppContext;
    private Context mContext;
    private AsyncTaskCallback mCallbackContext;
    private String mComponentName;
    private String mURL;
    private ArrayList<String> mConnectedComponents = new ArrayList<>();
    private PCMData mPCMData;

    public GetComponentSettingsAsyncTask(Context context, AsyncTaskCallback callbackContext, String componentName) {
        mAppContext = (PowerConsumptionManagerAppContext) context.getApplicationContext();
        mContext = context;
        mCallbackContext = callbackContext;
        mComponentName = componentName;
        mURL = "http://" + mAppContext.getIPAdress() + ":";
        mPCMData = mAppContext.getPCMData();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean successComfortSettings;
//        boolean successProgramSettings;

        Request comfortSettings = new Request.Builder()
                .url(mURL + mAppContext.getString(R.string.webservice_getComfortSettings) + mComponentName)
                .build();
//        Request programSettings = new Request.Builder()
//                .url(mURL + mAppContext.getString(R.string.webservice_getProgramSettings) + mComponentName)
//                .build();

        Response response;

        try {
            response = mAppContext.getOkHTTPClient().newCall(comfortSettings).execute();
            if(!response.isSuccessful()) {
                Log.e(TAG, "Response for comfort settings of " + mComponentName + " not successful.");
                return false;
            }
            successComfortSettings = handleComfortSettingsResponse(response);
        } catch (IOException e) {
            Log.e(TAG, "Exception while loading comfort settings of " + mComponentName + ".");
            successComfortSettings = false;
        }

//        try {
//            response = mAppContext.getOkHTTPClient().newCall(programSettings).execute();
//            if(!response.isSuccessful()) {
//                Log.e(TAG, "Response for program settings of " + mComponentName + " not successful.");
//                return false;
//            }
//            successProgramSettings = handleProgramSettingsResponse(response);
//        } catch (IOException e) {
//            Log.e(TAG, "Exception while loading program settings of " + mComponentName + ".");
//            successProgramSettings = false;
//        }

        return successComfortSettings; // && successProgramSettings;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        mCallbackContext.asyncTaskFinished(result);
    }

    public boolean handleComfortSettingsResponse(Response response) throws IOException {
        boolean success = true;
        mPCMData.getComponentData().get(mComponentName).getSettings().clear();

        try {
            JSONArray dataJson = new JSONArray(response.body().string());
            for(int i = 0; i < dataJson.length(); i++) {
                JSONObject dataJsonEntry = (JSONObject) dataJson.get(i);
                String settingName = dataJsonEntry.getString("Signal");
                String type = "";

                switch (settingName) {
                    /* TODO: cases should depend on types (json parameter) not on setting-names */
                    case "Speichertemperatur1 (°C)":
                    case "Speichertemperatur2 (°C)":
                    case "Raumtemperatur (°C)":
                        type = "positive_slider";
                        break;
                    case "Raumtemp. Absenk (°C)":
                        type = "negative_slider";
                        break;
                    case "Stellwert (kW)":
                        type = "positive_negative_slider";
                        break;
                    case "Programmende":
                        type = "timer";
                        break;
                    case "Ladeplan":
                        type = "plan";
                        break;
                    default:
                        break;
                }

                switch (type) {
                    case "positive_slider":
                        mPCMData.getComponentData().get(mComponentName).getSettings().add(
                            /* TODO: dynamic min/maxScale and unit */
                            new PCMSlider(
                                mContext,
                                settingName,
                                "°C",
                                0,
                                90,
                                (float) dataJsonEntry.getDouble("Min"),
                                (float) dataJsonEntry.getDouble("Max"),
                                true,
                                false
                            )
                        );
                        break;
                    case "negative_slider":
                    case "positive_negative_slider":
                        mPCMData.getComponentData().get(mComponentName).getSettings().add(
                            /* TODO: dynamic min/maxScale and unit */
                                new PCMSlider(
                                        mContext,
                                        settingName,
                                        "°C",
                                        0,
                                        90,
                                        (float) dataJsonEntry.getDouble("Min"),
                                        (float) dataJsonEntry.getDouble("Max"),
                                        true,
                                        true
                                )
                        );
                        break;
                    case "timer":
                        break;
                    case "plan":
                        break;
                    default:
                        break;
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON exception while processing current statistics data.");
            success = false;
        }

        return success;
    }

//    public boolean handleProgramSettingsResponse(Response response) throws IOException {
//        boolean success = true;
//        try {
//            JSONArray dataJson = new JSONArray(response.body().string());
//
//            switch (dataJson)
//
//            for(int i = 0; i < dataJson.length(); i++) {
//                JSONObject dataJsonEntry = (JSONObject) dataJson.get(i);
//                PCMComponentData ccdm = new PCMComponentData(dataJsonEntry.getString("Name"), dataJsonEntry.getJSONObject("Data"));
//                mPCMData.getComponentData().put(dataJsonEntry.getString("Name"), ccdm);
//            }
//        } catch (JSONException e) {
//            Log.e(TAG, "JSON exception while processing current component data.");
//            success = false;
//        }
//
//        return success;
//    }
}
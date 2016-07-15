package ch.fhnw.ip6.powerconsumptionmanager.network;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.model.PCMData;
import ch.fhnw.ip6.powerconsumptionmanager.model.settings.PCMPlan;
import ch.fhnw.ip6.powerconsumptionmanager.model.settings.PCMSetting;
import ch.fhnw.ip6.powerconsumptionmanager.model.settings.PCMSlider;
import ch.fhnw.ip6.powerconsumptionmanager.model.settings.PCMSwitch;
import ch.fhnw.ip6.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;
import okhttp3.Request;
import okhttp3.Response;

public class GetComponentSettingsAsyncTask extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = "GetCompSettAsyncTask";

    private PowerConsumptionManagerAppContext mAppContext;
    private AsyncTaskCallback mCallbackContext;
    private String mComponentName;
    private String mURL;
    private PCMData mPCMData;

    public GetComponentSettingsAsyncTask(PowerConsumptionManagerAppContext appContext, AsyncTaskCallback callbackContext, String componentName) {
        mAppContext = appContext;
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
        List<PCMSetting> settingList = mPCMData.getComponentData().get(mComponentName).getSettings();

        try {
            JSONArray dataJson = new JSONArray(response.body().string());
            for(int i = 0; i < dataJson.length(); i++) {
                JSONObject dataJsonEntry = (JSONObject) dataJson.get(i);
                String name = "";
                if (dataJsonEntry.has("Signal")) {
                    name = dataJsonEntry.getString("Signal");
                }

                if(dataJsonEntry.has("Typ") && !"".equals(name)) {
                    switch (dataJsonEntry.getString("Typ")) {
                        case "slider":
                            // TODO: store in a key-value pair "unit"
                            //String unit = dataJsonEntry.getString("Signal").split("\\(")[1].split("\\)")[0];
                            settingList.add(
                                new PCMSlider(
                                    name,
                                    "",
                                    (float) dataJsonEntry.getDouble("Grenze_unten"),
                                    (float) dataJsonEntry.getDouble("Grenze_oben"),
                                    (float) dataJsonEntry.getDouble("Min"),
                                    (float) dataJsonEntry.getDouble("Max"),
                                    dataJsonEntry.getBoolean("isRange")
                                )
                            );
                            break;

                        case "plan":
                            settingList.add(new PCMPlan(name));
                            break;

                        case "uhrzeit":
                            break;

                        case "switch": // TODO: not existing yet (over getProgramSettings-Webservice)
                            String textOn = "", textOff = ""; boolean isOn = true;
                            if(dataJsonEntry.has("textOn")) { textOn = dataJsonEntry.getString("textOn"); } // TODO: not existing yet
                            if(dataJsonEntry.has("textOff")) { textOff = dataJsonEntry.getString("textOff"); } // TODO: not existing yet
                            if(dataJsonEntry.has("isOn")) { isOn = dataJsonEntry.getBoolean("isOn"); } // TODO: not existing yet
                            settingList.add(new PCMSwitch(dataJsonEntry.getString("Signal"), textOn, textOff, isOn));
                            break;
                        case "numeric":
                        /* TODO: Receiving faulty data from the webservice (setting "Stellwert (kW)"), should also generate a PCMSlider */
                            break;
                        default:
                            break;
                    }
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
//                PCMComponent ccdm = new PCMComponent(dataJsonEntry.getString("Name"), dataJsonEntry.getJSONObject("Data"));
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
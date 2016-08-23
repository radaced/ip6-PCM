package ch.fhnw.ip6.powerconsumptionmanager.network;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.List;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.model.PCMData;
import ch.fhnw.ip6.powerconsumptionmanager.model.chargeplan.PCMPlanEntry;
import ch.fhnw.ip6.powerconsumptionmanager.model.settings.PCMPlan;
import ch.fhnw.ip6.powerconsumptionmanager.model.settings.PCMSetting;
import ch.fhnw.ip6.powerconsumptionmanager.model.settings.PCMSlider;
import ch.fhnw.ip6.powerconsumptionmanager.model.settings.PCMSwitch;
import ch.fhnw.ip6.powerconsumptionmanager.model.settings.PCMTextInfo;
import ch.fhnw.ip6.powerconsumptionmanager.model.settings.PCMTimer;
import ch.fhnw.ip6.powerconsumptionmanager.util.PowerConsumptionManagerAppContext;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Async task that loads the settings of a component.
 */
public class GetComponentSettingsAsyncTask extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = "GetCompSettAsyncTask";

    private PowerConsumptionManagerAppContext mAppContext;
    private AsyncTaskCallback mCallbackContext;
    private String mComponentName;
    private String mURL;
    private PCMData mPCMData;

    // Fields if more data from other URI's need be requested
    private boolean mRequestChargePlanData;
    private boolean mRequestStatusEmobil;

    /**
     * Constructor to create a new async task to load the settings of a component.
     * @param appContext Application context.
     * @param callbackContext Context of the callback.
     * @param componentName The component name of which the settings need to be loaded.
     */
    public GetComponentSettingsAsyncTask(PowerConsumptionManagerAppContext appContext, AsyncTaskCallback callbackContext, String componentName) {
        mAppContext = appContext;
        mCallbackContext = callbackContext;
        mComponentName = componentName;
        mURL = "http://" + mAppContext.getIPAdress() + ":";
        mPCMData = mAppContext.getPCMData();
        mRequestChargePlanData = false;
        mRequestStatusEmobil = false;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        // Flags that determine if the data could be loaded successfully or not
        boolean successComfortSettings;

        /* Build two requests (one for getting the comfortSettings and one for the programSettings.
         * TODO: Get all settings from one request (Zogg Energy Control)
         */
        Request comfortSettings = new Request.Builder()
            .url(mURL + mAppContext.getString(R.string.webservice_getComfortSettings) + mComponentName)
            .build();
        Request programSettings = new Request.Builder()
                .url(mURL + mAppContext.getString(R.string.webservice_getProgramSettings) + mComponentName)
                .build();

        Response response;

        // Clear the settings so no old data is being displayed later on
        List<PCMSetting> settingList = mPCMData.getComponentData().get(mComponentName).getSettings();
        settingList.clear();

        try {
            // Make the first request for the comfortSettings and process the response
            response = mAppContext.getOkHTTPClient().newCall(comfortSettings).execute();
            if(!response.isSuccessful()) {
                Log.e(TAG, "Response for comfort settings of " + mComponentName + " not successful.");
                return false;
            }
            successComfortSettings = handleComfortSettingsResponse(response, settingList);
        } catch (IOException e) {
            Log.e(TAG, "Exception while loading comfort settings of " + mComponentName + ".");
            successComfortSettings = false;
        }

        // Request the charge plan data if necessary
        if(mRequestChargePlanData && successComfortSettings) {
            Request chargePlanData = new Request.Builder()
                .url(mURL + mAppContext.getString(R.string.webservice_getChargePlan))
                .build();

            try {
                response = mAppContext.getOkHTTPClient().newCall(chargePlanData).execute();
                if(!response.isSuccessful()) {
                    Log.e(TAG, "Response for charge plan data not successful.");
                    return false;
                }
                successComfortSettings = handleChargePlanResponse(response, settingList);
            } catch (IOException e) {
                Log.e(TAG, "Exception while loading charge plan data.");
                successComfortSettings = false;
            }
        }

        // Request the status of the emobil if necessary
        if(mRequestStatusEmobil && successComfortSettings) {
            Request emobilStatus = new Request.Builder()
                    .url(mURL + mAppContext.getString(R.string.webservice_getStatusEmobil))
                    .build();

            try {
                response = mAppContext.getOkHTTPClient().newCall(emobilStatus).execute();
                if(!response.isSuccessful()) {
                    Log.e(TAG, "Response for emobil status not successful.");
                    return false;
                }
                successComfortSettings = handleEmobilStatusResponse(response, settingList);
            } catch (IOException e) {
                Log.e(TAG, "Exception while loading emobil status.");
                successComfortSettings = false;
            }
        }

        return successComfortSettings;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        // Notify the callback context that the task has finished
        mCallbackContext.asyncTaskFinished(result, mAppContext.OP_TYPES[0]);
    }

    /**
     * Processes the response of the comfort settings request.
     * @param response Response of the comfort settings request.
     * @param settingList The list of settings for the selected component.
     * @return State if the response could be processed successfully.
     * @throws IOException
     */
    private boolean handleComfortSettingsResponse(Response response, List<PCMSetting> settingList) throws IOException {
        boolean success = true;

        // TODO: Currently there is a webservice that returns the status of the emobil if one is connected (Zogg Energy Control)
        if(mComponentName.equals("Emobil")) { mRequestStatusEmobil = true; }

        try {
            // Process the JSON from the response
            JSONArray dataJson = new JSONArray(response.body().string());
            for(int i = 0; i < dataJson.length(); i++) {
                JSONObject dataJsonEntry = (JSONObject) dataJson.get(i);
                String name = "";
                // Get the name of the setting
                if (dataJsonEntry.has("Signal")) {
                    name = dataJsonEntry.getString("Signal");
                }

                if(dataJsonEntry.has("Typ") && !"".equals(name)) {
                    // Differentiate by the type what setting/widget needs to be displayed
                    switch (dataJsonEntry.getString("Typ")) {
                        case "slider":
                            /* TODO: Receive unit in a json key-value pair "unit" (Zogg Energy Control)
                             * Throws error now because not all settings have their unit stored in the setting name
                             * String unit = dataJsonEntry.getString("Signal").split("\\(")[1].split("\\)")[0];
                             */
                            // Add a new slider setting to the settings list of this component
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
                            /* Idea: Make instance title a dynamic json-parameter so multiple calendar widgets can display
                             * different instances in one google calendar
                             */
                            /* Add a new plan setting and set flag to request the charge plan data if the user does not manage
                             * the charge plan over his google calendar
                             */
                            settingList.add(new PCMPlan(name, mAppContext.usesGoogleCalendar()));
                            mRequestChargePlanData = !mAppContext.usesGoogleCalendar();
                            break;

                        case "uhrzeit":
                            // TODO: Currently no data (only type) delivered over the webservice (Zogg Energy Control)
                            int hour = 10; int minute = 25;
                            boolean automatic = false;
                            if(dataJsonEntry.has("Max")) {
                                int fullTime = dataJsonEntry.getInt("Max");
                                hour = fullTime / 3600;
                                minute = (fullTime % 3600) / 60;
                            }
                            if(dataJsonEntry.has("isRange")) { automatic = dataJsonEntry.getBoolean("isRange"); }
                            if(dataJsonEntry.has("hour")) { hour = dataJsonEntry.getInt("hour"); }
                            if(dataJsonEntry.has("minute")) { minute = dataJsonEntry.getInt("minute"); }
                            // Add a new timer setting to the settings list
                            settingList.add(new PCMTimer(dataJsonEntry.getString("Signal"), hour, minute, automatic));
                            break;

                        /* TODO: Not dynamically implemented yet (Zogg Energy Control)
                         * Niedertarif is the only setting that uses the switch but the data is not delivered over the comfort
                         * settings webservice but over the program settings webservice. As of now for every component the program
                         * settings are being checked explicitly (handleProgramSettingsResponse) and a switch widget gets added
                         * depending on the received data
                         */
                        case "switch":
                            String textOn = "", textOff = ""; boolean isOn = true;
                            if(dataJsonEntry.has("textOn")) { textOn = dataJsonEntry.getString("textOn"); }
                            if(dataJsonEntry.has("textOff")) { textOff = dataJsonEntry.getString("textOff"); }
                            if(dataJsonEntry.has("Min")) { isOn = dataJsonEntry.getInt("Min") != 0; }
                            if(dataJsonEntry.has("isOn")) { isOn = dataJsonEntry.getBoolean("isOn"); }
                            // Add a new switch setting to the settings list
                            settingList.add(new PCMSwitch(dataJsonEntry.getString("Signal"), textOn, textOff, isOn));
                            break;

                        case "textinfo":
                            // TODO: Not implemented yet (Zogg Energy Control)
                            JSONArray infoTexts = dataJsonEntry.getJSONArray("infoTexts");
                            // Map to store sent description text pairs
                            LinkedHashMap<String, String> descTextPairs = new LinkedHashMap<>();

                            for(int j = 0; j < infoTexts.length(); j++) {
                                JSONObject descTextPair = (JSONObject) infoTexts.get(j);
                                descTextPairs.put(descTextPair.getString("desc"), descTextPair.getString("text"));
                            }

                            // Add the text info "setting" (just displaying information) to the settings list
                            settingList.add(new PCMTextInfo(dataJsonEntry.getString("Signal"), descTextPairs));
                            break;

                        default:
                            break;
                    }
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON exception while processing comfort settings.");
            success = false;
        }

        return success;
    }

    /**
     * Processes the response of the charge plan request.
     * @param response Response of the charge plan request.
     * @param settingList The list of settings for the selected component.
     * @return State if the response could be processed successfully.
     * @throws IOException
     */
    private boolean handleChargePlanResponse(Response response, List<PCMSetting> settingList) throws IOException {
        boolean success = true;
        try {
            JSONArray dataJson = new JSONArray(response.body().string());

            /* TODO: Currently per definition of Zogg Energy Control a component can only have one plan setting
             * Therefore filling all PCMPlan settings with the charge plan data of the PCM works here. This should be
             * improved though
             */
            for(int j = 0; j < settingList.size(); j++) {
                if(settingList.get(j) instanceof PCMPlan) {
                    PCMPlan plan = (PCMPlan) settingList.get(j);
                    for(int i = 0; i < dataJson.length(); i++) {
                        JSONObject dataJsonEntry = (JSONObject) dataJson.get(i);
                        plan.getChargePlanData().put(i, new PCMPlanEntry(dataJsonEntry));
                    }
                }
            }

        } catch (JSONException e) {
            Log.e(TAG, "JSON exception while processing charge plan data.");
            success = false;
        }

        return success;
    }

    /**
     * Processes the response of the emobil status request.
     * @param response Response of the emobil status request.
     * @param settingList The list of settings for the selected component.
     * @return State if the response could be processed successfully.
     * @throws IOException
     */
    public boolean handleEmobilStatusResponse(Response response, List<PCMSetting> settingList) throws IOException {
        boolean success = true;

        try {
            // Get the emobil status...
            JSONObject dataJson = new JSONObject(response.body().string());
            Double chargedEnergy = dataJson.getDouble("Ladeenergie (kWh)");
            Double reachableDistance = dataJson.getDouble("Ladereichweite (km)");
            String plugged = dataJson.getBoolean("Plugged") ?
                mAppContext.getString(R.string.text_plugged_true) :
                mAppContext.getString(R.string.text_plugged_false);

            LinkedHashMap<String, String> descTextPairs = new LinkedHashMap<>();
            DecimalFormat oneDigitAfterCommaFormat = new DecimalFormat("#.#");

            // ... and fill the data into a map with description text pairs
            descTextPairs.put(
                mAppContext.getString(R.string.text_charged_energy),
                oneDigitAfterCommaFormat.format(chargedEnergy) + " " + mAppContext.getString(R.string.unit_kwh)
            );
            descTextPairs.put(
                mAppContext.getString(R.string.text_distance_reachable),
                oneDigitAfterCommaFormat.format(reachableDistance) + " " + mAppContext.getString(R.string.unit_km)
            );
            descTextPairs.put(mAppContext.getString(R.string.text_plugged), plugged);

            // Add a text info "setting" to display the emobil status
            settingList.add(new PCMTextInfo(mAppContext.getString(R.string.text_charge_state_emobil), descTextPairs));
        } catch (JSONException e) {
            Log.e(TAG, "JSON exception while processing program settings.");
            success = false;
        }

        return success;
    }
}
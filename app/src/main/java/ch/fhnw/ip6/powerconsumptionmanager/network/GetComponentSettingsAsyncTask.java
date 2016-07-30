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
    private boolean mRequestChargePlanData;
    private boolean mRequestStatusEmobil;

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
        boolean successComfortSettings;
        boolean successProgramSettings;

        Request comfortSettings = new Request.Builder()
            .url(mURL + mAppContext.getString(R.string.webservice_getComfortSettings) + mComponentName)
            .build();
        Request programSettings = new Request.Builder()
                .url(mURL + mAppContext.getString(R.string.webservice_getProgramSettings) + mComponentName)
                .build();

        Response response;

        mPCMData.getComponentData().get(mComponentName).getSettings().clear();
        List<PCMSetting> settingList = mPCMData.getComponentData().get(mComponentName).getSettings();

        try {
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

        try {
            response = mAppContext.getOkHTTPClient().newCall(programSettings).execute();
            if(!response.isSuccessful()) {
                Log.e(TAG, "Response for program settings of " + mComponentName + " not successful.");
                return false;
            }
            successProgramSettings = handleProgramSettingsResponse(response, settingList);
        } catch (IOException e) {
            Log.e(TAG, "Exception while loading program settings of " + mComponentName + ".");
            successProgramSettings = false;
        }

        if(mRequestChargePlanData && successComfortSettings && successProgramSettings) {
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

        if(mRequestStatusEmobil && successComfortSettings && successProgramSettings) {
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

        return successComfortSettings && successProgramSettings;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        mCallbackContext.asyncTaskFinished(result);
    }

    public boolean handleComfortSettingsResponse(Response response, List<PCMSetting> settingList) throws IOException {
        boolean success = true;

        if(mComponentName.equals("Emobil")) { mRequestStatusEmobil = true; }

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
                            /*
                             * TODO: store in a key-value pair "unit"
                             * throws error now because not all settings have their unit stored in the setting name
                             */
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
                            // Idea: Make instance title a dynamic json-parameter so multiple calendar widgets can display
                            // different instances in one google calendar
                            settingList.add(new PCMPlan(name, mAppContext.usesGoogleCalendar()));
                            mRequestChargePlanData = !mAppContext.usesGoogleCalendar();
                            break;

                        case "uhrzeit":
                            break;

                        case "switch": // TODO: not dynamically implemented by Zogg Energy Control yet
                            String textOn = "", textOff = ""; boolean isOn = true;
                            if(dataJsonEntry.has("textOn")) { textOn = dataJsonEntry.getString("textOn"); }
                            if(dataJsonEntry.has("textOff")) { textOff = dataJsonEntry.getString("textOff"); }
                            if(dataJsonEntry.has("isOn")) { isOn = dataJsonEntry.getBoolean("isOn"); }
                            settingList.add(new PCMSwitch(dataJsonEntry.getString("Signal"), textOn, textOff, isOn));
                            break;

                        case "textinfo": // TODO: not implemented by Zogg Energy Control yet
                            JSONArray infoTexts = dataJsonEntry.getJSONArray("infoTexts");
                            LinkedHashMap<String, String> descTextPairs = new LinkedHashMap<>();

                            for(int j = 0; j < infoTexts.length(); j++) {
                                JSONObject descTextPair = (JSONObject) infoTexts.get(j);
                                descTextPairs.put(descTextPair.getString("desc"), descTextPair.getString("text"));
                            }

                            settingList.add(new PCMTextInfo(dataJsonEntry.getString("Signal"), descTextPairs));
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
            Log.e(TAG, "JSON exception while processing comfort settings.");
            success = false;
        }

        return success;
    }

    public boolean handleChargePlanResponse(Response response, List<PCMSetting> settingList) throws IOException {
        boolean success = true;
        try {
            JSONArray dataJson = new JSONArray(response.body().string());

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

    public boolean handleProgramSettingsResponse(Response response, List<PCMSetting> settingList) throws IOException {
        boolean success = true;

        try {
            JSONObject dataJson = new JSONObject(response.body().string());
            boolean lowerRate = dataJson.getBoolean("Niedertarif");
            settingList.add(new PCMSwitch("Niedertarif", "", "", lowerRate));
        } catch (JSONException e) {
            Log.e(TAG, "JSON exception while processing program settings.");
            success = false;
        }

        return success;
    }

    public boolean handleEmobilStatusResponse(Response response, List<PCMSetting> settingList) throws IOException {
        boolean success = true;

        try {
            JSONObject dataJson = new JSONObject(response.body().string());
            Double chargedEnergy = dataJson.getDouble("Ladeenergie (kWh)");
            Double reachableDistance = dataJson.getDouble("Ladereichweite (km)");
            String plugged = dataJson.getBoolean("Plugged") ?
                mAppContext.getString(R.string.text_plugged_true) :
                mAppContext.getString(R.string.text_plugged_false);

            LinkedHashMap<String, String> descTextPairs = new LinkedHashMap<>();
            DecimalFormat oneDigitAfterCommaFormat = new DecimalFormat("#.#");

            descTextPairs.put(
                mAppContext.getString(R.string.text_charged_energy),
                oneDigitAfterCommaFormat.format(chargedEnergy) + " " + mAppContext.getString(R.string.unit_kwh)
            );
            descTextPairs.put(
                mAppContext.getString(R.string.text_distance_reachable),
                oneDigitAfterCommaFormat.format(reachableDistance) + " " + mAppContext.getString(R.string.unit_km)
            );
            descTextPairs.put(mAppContext.getString(R.string.text_plugged), plugged);

            settingList.add(new PCMTextInfo(mAppContext.getString(R.string.text_charge_state_emobil), descTextPairs));
        } catch (JSONException e) {
            Log.e(TAG, "JSON exception while processing program settings.");
            success = false;
        }

        return success;
    }
}
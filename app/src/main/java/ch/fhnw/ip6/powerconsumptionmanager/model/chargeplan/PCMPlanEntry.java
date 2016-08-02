package ch.fhnw.ip6.powerconsumptionmanager.model.chargeplan;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Holds the necessary information of one day from the PCM charge plan.
 */
public class PCMPlanEntry {
    private int mDepartureHour;
    private int mDepartureMinute;
    private int mArrivalHour;
    private int mArrivalMinute;
    private int mKm;
    private int mAdditionalKm;

    /**
     * Constructor to build a new calendar entry object with the necessary information from the PCM charge plan.
     * @param pcmPlanEntry A JSON object representing the information of one day in the PCM charge plan.
     * @throws JSONException when an error while processing the JSON occurs.
     */
    public PCMPlanEntry(JSONObject pcmPlanEntry) throws JSONException {
        String[] departureTime = pcmPlanEntry.getString("Abfahrtszeit").split(":");
        String[] arrivalTime = pcmPlanEntry.getString("Ankunftszeit").split(":");
        mDepartureHour = Integer.valueOf(departureTime[0]);
        mDepartureMinute = Integer.valueOf(departureTime[1]);
        mArrivalHour = Integer.valueOf(arrivalTime[0]);
        mArrivalMinute = Integer.valueOf(arrivalTime[1]);
        mKm = pcmPlanEntry.getInt("Kilometer");
        mAdditionalKm = pcmPlanEntry.getInt("Zusatz km");
    }

    /***********************
     * GETTERS AND SETTERS *
     ***********************/
    public int getDepartureHour() {
        return mDepartureHour;
    }

    public void setDepartureHour(int mDepartureHour) {
        this.mDepartureHour = mDepartureHour;
    }

    public int getDepartureMinute() {
        return mDepartureMinute;
    }

    public void setDepartureMinute(int mDepartureMinute) {
        this.mDepartureMinute = mDepartureMinute;
    }

    public int getArrivalHour() {
        return mArrivalHour;
    }

    public void setArrivalHour(int mArrivalHour) {
        this.mArrivalHour = mArrivalHour;
    }

    public int getArrivalMinute() {
        return mArrivalMinute;
    }

    public void setArrivalMinute(int mArrivalMinute) {
        this.mArrivalMinute = mArrivalMinute;
    }

    public int getKm() {
        return mKm;
    }

    public void setKm(int mKm) {
        this.mKm = mKm;
    }

    public int getAdditionalKm() {
        return mAdditionalKm;
    }

    public void setAdditionalKm(int mAdditionalKm) {
        this.mAdditionalKm = mAdditionalKm;
    }
}

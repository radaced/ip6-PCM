package ch.fhnw.ip6.powerconsumptionmanager.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.LinkedList;

public class PCMData {
    // Difference in seconds from lab view timestamp (01.01.1904) to unix timestamp (01.01.1970)
    public static final long TIMESTAMP_DIFFERENCE = 2082844800;

    private double mAutarchy;
    private double mSelfsupply;
    private double mConsumption;
    private int mScaleMinConsumption, mScaleMaxConsumption;
    private int mConsumptionColor;

    // Lists for statistics
    private LinkedList<String> mStatisticsDates = new LinkedList<>();
    private LinkedList<Double> mSelfSupplyStatisticsValues = new LinkedList<>();
    private LinkedList<Double> mConsumptionStatisticsValues = new LinkedList<>();
    private LinkedList<Double> mSurplusStatisticsValues = new LinkedList<>();

    // Holds all connected components in a map with the name of the component as the key
    private LinkedHashMap<String, PCMComponent> mPCMComponentData = new LinkedHashMap<>();

    /**
     * Fills the general statistic lists and the statistic dates list with the data that has been loaded.
     * @param name Name of the general statistics list to fill.
     * @param data Statistics data as a JSONArray with the cost statistic per day.
     * @param ignoreStatisticsDates Flag to determine if the dates from when the statistics are need to be stored in the
     *                              mStatisticsDates list. Usually only on the first fillStatistics call necessary.
     * @throws JSONException when an error occurred while processing the JSON.
     */
    public void fillStatistics(String name, JSONArray data, boolean ignoreStatisticsDates) throws JSONException {
        if(!ignoreStatisticsDates) {
            for (int i = 0; i < data.length(); i++) {
                JSONObject stats = (JSONObject) data.get(i);
                mStatisticsDates.add(String.valueOf(stats.getLong("Zeit") - TIMESTAMP_DIFFERENCE));
            }
        }

        LinkedList<Double> statistics = new LinkedList<>();

        for (int i = 0; i < data.length(); i++) {
            JSONObject stats = (JSONObject) data.get(i);
            statistics.add(stats.getDouble("Kosten(CHF)"));
        }

        switch (name) {
            case "Eigenverbrauch":
                mSelfSupplyStatisticsValues = statistics;
                break;
            case "Netzbezug":
                mConsumptionStatisticsValues = statistics;
                break;
            case "Überschuss":
                mSurplusStatisticsValues = statistics;
                break;
            default:
                break;
        }
    }

    /**
     * Clears the statistics of all connected components and all general statistics.
     */
    public void clearStatistics() {
        mStatisticsDates.clear();
        mSelfSupplyStatisticsValues.clear();
        mConsumptionStatisticsValues.clear();
        mSurplusStatisticsValues.clear();

        for (PCMComponent data : mPCMComponentData.values()) {
            data.getStatistics().clear();
        }
    }

    /***********************
     * GETTERS AND SETTERS *
     ***********************/
    public double getAutarchy() {
        return mAutarchy;
    }

    public void setAutarchy(double mAutarchy) {
        this.mAutarchy = mAutarchy;
    }

    public double getSelfsupply() {
        return mSelfsupply;
    }

    public void setSelfsupply(double mSelfsupply) {
        this.mSelfsupply = mSelfsupply;
    }

    public double getConsumption() {
        return mConsumption;
    }

    public void setConsumption(double mConsumption) {
        this.mConsumption = mConsumption;
    }

    public int getMinScaleConsumption() {
        return mScaleMinConsumption;
    }

    public void setMinScaleConsumption(int mScaleMinConsumption) {
        this.mScaleMinConsumption = mScaleMinConsumption;
    }

    public int getMaxScaleConsumption() {
        return mScaleMaxConsumption;
    }

    public void setMaxScaleConsumption(int mScaleMaxConsumption) {
        this.mScaleMaxConsumption = mScaleMaxConsumption;
    }

    public int getConsumptionColor() {
        return mConsumptionColor;
    }

    public void setConsumptionColor(int mConsumptionColor) {
        this.mConsumptionColor = mConsumptionColor;
    }

    public LinkedList<String> getStatisticsDates() {
        return mStatisticsDates;
    }

    public LinkedList<Double> getSelfSupplyStatisticsValues() {
        return mSelfSupplyStatisticsValues;
    }

    public LinkedList<Double> getConsumptionStatisticsValues() {
        return mConsumptionStatisticsValues;
    }

    public LinkedList<Double> getSurplusStatisticsValues() {
        return mSurplusStatisticsValues;
    }

    public LinkedHashMap<String, PCMComponent> getComponentData() {
        return mPCMComponentData;
    }

}

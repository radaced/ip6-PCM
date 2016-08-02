package ch.fhnw.ip6.powerconsumptionmanager.model.chargeplan;

/**
 * Holds the loaded route information to an origin and destination
 */
public class RouteInformation {
    private String mDurationText;
    private String mDistanceText;

    /**
     * Constructor to build route information object.
     * @param durationText Text to display as duration between origin and destination.
     * @param distanceText Text to display as distance between origin and destination.
     */
    public RouteInformation(String durationText, String distanceText) {
        this.mDurationText = durationText;
        this.mDistanceText = distanceText;
    }

    /***********************
     * GETTERS AND SETTERS *
     ***********************/
    public String getDurationText() {
        return mDurationText;
    }

    public String getDistanceText() {
        return mDistanceText;
    }
}

package ch.fhnw.ip5.powerconsumptionmanager.model;

/**
 * Holds the loaded route information to an origin and destination
 */
public class RouteInformationModel {
    private String mDurationText;
    private String mDistanceText;

    public RouteInformationModel(String durationText, String distanceText) {
        this.mDurationText = durationText;
        this.mDistanceText = distanceText;
    }

    public String getDurationText() {
        return mDurationText;
    }

    public String getDistanceText() {
        return mDistanceText;
    }
}

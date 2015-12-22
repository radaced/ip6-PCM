package ch.fhnw.ip5.powerconsumptionmanager.model;

/**
 * Created by Patrik on 22.12.2015.
 */
public class PlanEntryModel {
    private String mTitle;
    private String mDescription;
    private long mBegin;
    private long mEnd;

    public PlanEntryModel( String mTitle, String mDescription, long mBegin, long mEnd) {
        this.mTitle = mTitle;
        this.mDescription = mDescription;
        this.mBegin = mBegin;
        this.mEnd = mEnd;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public long getBegin() {
        return mBegin;
    }

    public long getEnd() {
        return mEnd;
    }
}

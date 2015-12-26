package ch.fhnw.ip5.powerconsumptionmanager.model;

import java.util.Date;

/**
 * Created by Patrik on 22.12.2015.
 */
public class PlanEntryModel {
    private String mTitle;
    private String mDescription;
    private Date mBegin;
    private Date mEnd;

    public PlanEntryModel( String mTitle, String mDescription, Date mBegin, Date mEnd) {
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

    public Date getBegin() {
        return mBegin;
    }

    public Date getEnd() {
        return mEnd;
    }
}

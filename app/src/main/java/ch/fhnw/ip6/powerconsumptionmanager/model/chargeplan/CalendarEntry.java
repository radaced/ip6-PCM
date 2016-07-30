package ch.fhnw.ip6.powerconsumptionmanager.model.chargeplan;

import java.util.Date;

/**
 * Holds the necessary information to a tesla trip instance (from calendar.instance table)
 */
public class CalendarEntry {
    private String mTitle;
    private String mEventLocation;
    private String mDescription;
    private Date mBegin;
    private Date mEnd;

    public CalendarEntry(String title, String eventLocation, String description, Date begin, Date end) {
        this.mTitle = title;
        this.mEventLocation = eventLocation;
        this.mDescription = description;
        this.mBegin = begin;
        this.mEnd = end;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getEventLocation() { return mEventLocation; }

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

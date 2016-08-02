package ch.fhnw.ip6.powerconsumptionmanager.model.chargeplan;

import java.util.Date;

/**
 * Holds the necessary information to an emobil trip instance (from the calendar.instance table (google calendar)).
 */
public class CalendarEntry {
    private String mTitle;
    private String mEventLocation;
    private String mDescription;
    private Date mBegin;
    private Date mEnd;

    /**
     * Constructor to build a new calendar entry object with the necessary information from the google calendar.
     * @param title The title of the calendar instance.
     * @param eventLocation The event location of the calendar instance.
     * @param description The description of the calendar instance.
     * @param begin The exact date when the calendar instance begins.
     * @param end The exact date when the calendar instance ends.
     */
    public CalendarEntry(String title, String eventLocation, String description, Date begin, Date end) {
        this.mTitle = title;
        this.mEventLocation = eventLocation;
        this.mDescription = description;
        this.mBegin = begin;
        this.mEnd = end;
    }

    /***********************
     * GETTERS AND SETTERS *
     ***********************/
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

package com.example.eventcamp;

/**
 * Created by Sunilkumar on 25-11-2016.
 */

public class RetrievingEventDetails {
    String eventTitle,eventDate,eventPoster,eventSubtitle;
    RetrievingEventDetails(){}

    public RetrievingEventDetails(String eventTitle, String eventDate,
                                  String eventPoster, String eventSubtitle) {
        this.eventTitle = eventTitle;
        this.eventDate = eventDate;
        this.eventPoster = eventPoster;
        this.eventSubtitle = eventSubtitle;
    }

    public String getEventTitle() {return eventTitle;}

    public void setEventTitle(String eventTitle) {this.eventTitle = eventTitle;}

    public String getEventDate() {return eventDate;}

    public void setEventDate(String eventDate) {this.eventDate = eventDate;}

    public String getEventPoster() {return eventPoster;}

    public void setEventPoster(String eventPoster) {this.eventPoster = eventPoster;}

    public String getEventSubtitle() {return eventSubtitle;}

    public void setEventSubtitle(String eventSubtitle) {this.eventSubtitle = eventSubtitle;}
}

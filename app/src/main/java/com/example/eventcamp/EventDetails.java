package com.example.eventcamp;

/**
 * Created by Sunilkumar on 13-11-2016.
 */

public class EventDetails {
    public  String eventTitle,eventSubtitle,eventDate,eventVenue,eventDescription,eventPoster,userId;
    public EventDetails(){}
    public EventDetails(String eventTitle, String eventDate,
                        String eventVenue, String eventDescription, String eventPoster,
                        String eventSubtitle, String userId){
        this.eventTitle = eventTitle;
        this.eventSubtitle = eventSubtitle;
        this.eventDate = eventDate;
        this.eventVenue = eventVenue;
        this.eventDescription = eventDescription;
        this.eventPoster = eventPoster;
        this.userId = userId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventVenue() {
        return eventVenue;
    }

    public void setEventVenue(String eventVenue) {
        this.eventVenue = eventVenue;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getEventPoster() {
        return eventPoster;
    }

    public void setEventPoster(String eventPoster) {
        this.eventPoster = eventPoster;
    }

    public String getEventSubtitle() {
        return eventSubtitle;
    }

    public void setEventSubtitle(String eventSubtitle) {
        this.eventSubtitle = eventSubtitle;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

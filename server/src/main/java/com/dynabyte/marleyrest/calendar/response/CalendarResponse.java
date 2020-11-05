package com.dynabyte.marleyrest.calendar.response;

import com.dynabyte.marleyrest.calendar.model.GoogleCredentials;
import com.google.api.services.calendar.model.Event;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CalendarResponse {

    private GoogleCredentials googleCredentials;
    private List<Event> calendarEvents;

}

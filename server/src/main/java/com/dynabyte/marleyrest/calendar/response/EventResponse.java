package com.dynabyte.marleyrest.calendar.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.api.services.calendar.model.Event;
import lombok.Data;

@Data
public class EventResponse {

    private final Event event;
    @JsonProperty(value = "isOngoing")
    private boolean isOngoing = false;
    private int hoursRemaining = 0;
    private int minutesRemaining;

    public EventResponse(Event event) {
        this.event = event;
    }
}

package com.dynabyte.marleyrest.calendar.util;

import com.dynabyte.marleyrest.api.exception.InvalidArgumentException;
import com.dynabyte.marleyrest.api.exception.RequestBodyNotFoundException;
import com.dynabyte.marleyrest.calendar.request.GoogleCalendarAuthenticationRequest;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;


import java.util.List;

import static java.util.Objects.isNull;

public class CalendarUtil {

    public static void validateAuthenticationRequest(GoogleCalendarAuthenticationRequest authenticationRequest){
        if(isNull(authenticationRequest)){
            throw new RequestBodyNotFoundException("Request body missing!");
        }
        if(authenticationRequest.getFaceId() == null) {
            throw new InvalidArgumentException("faceId cannot be null!");
        }
        if(authenticationRequest.getAuthCode() == null){
            throw new InvalidArgumentException("authCode cannot be null!");
        }
    }

    public static boolean isDynabyteEvent(Event event) {
        List<EventAttendee> attendees = event.getAttendees();
        if(isNull(attendees)){
            return false;
        }
        if(hasDynabyteCreator(event)){
            return true;
        }
        for (EventAttendee attendee: attendees) {
            if(isDynabyteAttendee(attendee) || isDynabyteRoom(attendee)){
                return true;
            }
        }
        return false;
    }

    private static boolean hasDynabyteCreator(Event event) {
        return event.getCreator().getEmail().endsWith("@dynabyte.se");
    }

    private static boolean isDynabyteRoom(EventAttendee attendee) {
        return attendee.getDisplayName().contains("Dynabyte") && attendee.isResource();
    }

    private static boolean isDynabyteAttendee(EventAttendee attendee) {
        return attendee.getEmail().endsWith("@dynabyte.se");
    }
}

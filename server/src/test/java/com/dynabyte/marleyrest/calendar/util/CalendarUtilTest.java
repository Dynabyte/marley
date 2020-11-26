package com.dynabyte.marleyrest.calendar.util;

import com.dynabyte.marleyrest.api.exception.InvalidArgumentException;
import com.dynabyte.marleyrest.api.exception.RequestBodyNotFoundException;
import com.dynabyte.marleyrest.calendar.request.GoogleCalendarAuthenticationRequest;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CalendarUtilTest {

    private GoogleCalendarAuthenticationRequest authenticationRequest;

    @BeforeEach
    void setUp() {
        authenticationRequest = new GoogleCalendarAuthenticationRequest();
        authenticationRequest.setFaceId("5fad4b9e1bbac38873e8cdbd");
        authenticationRequest.setAuthCode("aggkdjaknlkjgfn930jotatjoasdflsklf");

    }

    @Test
    void validAuthenticationRequestShouldNotThrowException() {
        CalendarUtil.validateAuthenticationRequest(authenticationRequest);
    }

    @Test
    void nullRequestShouldThrowException(){
        assertThrows(RequestBodyNotFoundException.class, ()->
                CalendarUtil.validateAuthenticationRequest(null));
    }

    @Test
    void nullFaceIdShouldThrowException(){
        authenticationRequest.setFaceId(null);
        assertThrows(InvalidArgumentException.class, ()->
            CalendarUtil.validateAuthenticationRequest(authenticationRequest)
        );
    }

    @Test
    void nullAuthCodeShouldThrowException(){
        authenticationRequest.setAuthCode(null);
        assertThrows(InvalidArgumentException.class, ()->
                CalendarUtil.validateAuthenticationRequest(authenticationRequest));
    }

    @Test
    void eventWithDynabyteCreatorShouldReturnTrue() {
        Event dynabyteCreatorEvent = new Event();
        Event.Creator dynabyteCreator = new Event.Creator();
        dynabyteCreator.setEmail("test@dynabyte.se");
        dynabyteCreatorEvent.setCreator(dynabyteCreator);

        boolean isDynabyteEvent = CalendarUtil.isDynabyteEvent(dynabyteCreatorEvent);

        assertTrue(isDynabyteEvent);
    }

    @Test
    void eventWithDynabyteAttendeeShouldReturnTrue() {
        Event dynabyteAttendeeEvent = new Event();
        Event.Creator creator = new Event.Creator();
        creator.setEmail("name@somesite.com");
        dynabyteAttendeeEvent.setCreator(creator);

        EventAttendee dynabyteAttendee = new EventAttendee();
        dynabyteAttendee.setDisplayName("Name LastName");
        dynabyteAttendee.setEmail("name@dynabyte.se");
        dynabyteAttendeeEvent.setAttendees(List.of(dynabyteAttendee));

        boolean isDynabyteEvent = CalendarUtil.isDynabyteEvent(dynabyteAttendeeEvent);

        assertTrue(isDynabyteEvent);
    }

    @Test
    void eventWithDynabyteRoomShouldReturnTrue() {
        Event dynabyteAttendeeEvent = new Event();
        Event.Creator creator = new Event.Creator();
        creator.setEmail("name@somesite.com");
        dynabyteAttendeeEvent.setCreator(creator);

        EventAttendee attendee = new EventAttendee();
        attendee.setDisplayName("Name LastName");
        attendee.setEmail("name@best-website.se");

        EventAttendee dynabyteRoom = new EventAttendee();
        dynabyteRoom.setEmail("roomGeneratedEmail");
        dynabyteRoom.setDisplayName("Dynabyte Room");
        dynabyteRoom.setResource(true);
        dynabyteAttendeeEvent.setAttendees(List.of(attendee, dynabyteRoom));

        boolean isDynabyteEvent = CalendarUtil.isDynabyteEvent(dynabyteAttendeeEvent);

        assertTrue(isDynabyteEvent);
    }

    @Test
    void nonDynabyteEventShouldReturnFalse() {
        Event event = new Event();
        Event.Creator creator = new Event.Creator();
        creator.setEmail("name@somesite.com");
        event.setCreator(creator);

        EventAttendee attendee = new EventAttendee();
        attendee.setDisplayName("Name LastName");
        attendee.setEmail("name@website.com");
        event.setAttendees(List.of(attendee));

        boolean isDynabyteEvent = CalendarUtil.isDynabyteEvent(event);

        assertFalse(isDynabyteEvent);
    }

    @Test
    void noAttendeesShouldReturnFalse() {
        Event event = new Event();
        Event.Creator creator = new Event.Creator();
        creator.setEmail("name@somesite.com");
        event.setCreator(creator);

        boolean isDynabyteEvent = CalendarUtil.isDynabyteEvent(event);

        assertFalse(isDynabyteEvent);
    }

    @Test
    void resourceWithoutDisplayNameCannotCountAsDynabyteRoom(){
        Event dynabyteAttendeeEvent = new Event();
        Event.Creator creator = new Event.Creator();
        creator.setEmail("name@somesite.com");
        dynabyteAttendeeEvent.setCreator(creator);

        EventAttendee attendee = new EventAttendee();
        attendee.setDisplayName("Name LastName");
        attendee.setEmail("name@best-website.se");

        EventAttendee dynabyteRoom = new EventAttendee();
        dynabyteRoom.setEmail("roomGeneratedEmail");
        dynabyteRoom.setResource(true);
        dynabyteAttendeeEvent.setAttendees(List.of(attendee, dynabyteRoom));

        boolean isDynabyteEvent = CalendarUtil.isDynabyteEvent(dynabyteAttendeeEvent);

        assertFalse(isDynabyteEvent);
    }
}
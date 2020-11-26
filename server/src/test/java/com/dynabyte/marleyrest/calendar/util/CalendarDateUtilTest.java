package com.dynabyte.marleyrest.calendar.util;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;


class CalendarDateUtilTest {

    @BeforeAll
    static void setUp() {
        Clock fixedClock = Clock.fixed(Instant.parse("2020-01-01T10:10:10.00Z"),
                ZoneId.systemDefault());
        CalendarDateUtil.setClock(fixedClock);
    }

    @Test
    void isEventTodayShouldBeTrueForTodayWithDifferentTime() {
        EventDateTime eventDateTime = new EventDateTime();
        eventDateTime.setDateTime(new DateTime("2020-01-01T20:15:10.00Z"));

        Event fakeTodayEvent = new Event();
        fakeTodayEvent.setStart(eventDateTime);

        assertTrue(CalendarDateUtil.isEventToday(fakeTodayEvent));
    }

    @Test
    void isEventTodayShouldBeFalseForTomorrow() {
        EventDateTime eventDateTime = new EventDateTime();
        eventDateTime.setDateTime(new DateTime("2020-01-02T10:10:10.00Z"));

        Event fakeTodayEvent = new Event();
        fakeTodayEvent.setStart(eventDateTime);

        assertFalse(CalendarDateUtil.isEventToday(fakeTodayEvent));
    }

    @Test
    void isEventTodayShouldBeFalseForPastDate() {
        EventDateTime eventDateTime = new EventDateTime();
        eventDateTime.setDateTime(new DateTime("2019-12-30T10:10:10.00Z"));

        Event fakeTodayEvent = new Event();
        fakeTodayEvent.setStart(eventDateTime);

        assertFalse(CalendarDateUtil.isEventToday(fakeTodayEvent));
    }

    @Test
    void getMinutesRemainingPositiveShouldRoundUp() {
        Event event = new Event();
        EventDateTime eventDateTime = new EventDateTime();

        eventDateTime.setDateTime(new DateTime("2020-01-01T10:10:10.00Z"));
        event.setStart(eventDateTime);
        assertEquals(0, CalendarDateUtil.getMinutesRemaining(event));

        eventDateTime.setDateTime(new DateTime("2020-01-01T10:19:19.50Z"));
        event.setStart(eventDateTime);
        assertEquals(10, CalendarDateUtil.getMinutesRemaining(event));


        eventDateTime.setDateTime(new DateTime("2020-01-01T11:19:19.50Z"));
        event.setStart(eventDateTime);
        assertEquals(70, CalendarDateUtil.getMinutesRemaining(event));
    }

    @Test
    void getMinutesRemainingNegativeShouldRoundDown() {
        Event event = new Event();
        EventDateTime eventDateTime = new EventDateTime();

        eventDateTime.setDateTime(new DateTime("2020-01-01T10:09:29.00Z"));
        event.setStart(eventDateTime);
        assertEquals(-1, CalendarDateUtil.getMinutesRemaining(event));

        eventDateTime.setDateTime(new DateTime("2020-01-01T09:55:19.50Z"));
        event.setStart(eventDateTime);
        assertEquals(-15, CalendarDateUtil.getMinutesRemaining(event));


        eventDateTime.setDateTime(new DateTime("2020-01-01T08:09:19.50Z"));
        event.setStart(eventDateTime);
        assertEquals(-121, CalendarDateUtil.getMinutesRemaining(event));
    }

    @AfterAll
    static void tearDown(){
        CalendarDateUtil.setClock(Clock.systemDefaultZone());
    }
}
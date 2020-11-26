package com.dynabyte.marleyrest.calendar.util;

import com.google.api.services.calendar.model.Event;
import lombok.Setter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.util.Date;


public class CalendarDateUtil {

    @Setter //Setter available to change the clock for testing purposes
    private static Clock clock = Clock.systemDefaultZone();

    public static boolean isEventToday(Event event){
        Date eventDateWithTime = new Date(event.getStart().getDateTime().getValue());
        Date eventDate = removeTime(eventDateWithTime);
        Date currentDateWithTime = Date.from(clock.instant());
        Date currentDate = removeTime(currentDateWithTime);

        //If the event date is today, return true, else false
        return eventDate.compareTo(currentDate) == 0;
    }

    //TODO is this correct clock behavior? Round up for minutes left, round down for minutes passed (negative value)
    public static int getMinutesRemaining(Event event){
        long eventSystemTime = event.getStart().getDateTime().getValue();
        long remainingSeconds = (eventSystemTime - clock.millis())/1000;
        double minutesRemaining = remainingSeconds/60.0;
        if (minutesRemaining >=0){
            return (int) Math.ceil(minutesRemaining);
        }
        else{
            return (int) Math.floor(minutesRemaining);
        }
    }

    private static Date removeTime(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date dateWithoutTime = null;
        try {
            dateWithoutTime = sdf.parse(sdf.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateWithoutTime;
    }
}

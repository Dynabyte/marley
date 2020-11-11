package com.dynabyte.marleyrest.calendar.util;

import com.google.api.services.calendar.model.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CalendarDateUtil {

    public static boolean isEventToday(Event event){
        Date eventDate = removeTime(new Date(event.getStart().getDateTime().getValue()));
        Date currentDate = removeTime(new Date(System.currentTimeMillis()));

        //If the event date is today, return true, else false
        return eventDate.compareTo(currentDate) == 0;
    }

    public static int getMinutesRemaining(Event event){
        long eventSystemTime = event.getStart().getDateTime().getValue();
        long remainingSeconds = (eventSystemTime - System.currentTimeMillis())/1000;
        return (int) remainingSeconds/60;
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

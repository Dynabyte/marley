package com.dynabyte.marleyrest.calendar.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    public static Date removeTime(Date date){
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

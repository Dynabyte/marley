package com.dynabyte.marleyrest.calendar.response;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Getter
public class GoogleCredentials {

    private final String clientId;
    private final String googleCalendarApiKey;

    public GoogleCredentials(String clientId) {
        this.clientId = clientId;
        googleCalendarApiKey = System.getenv("GOOGLE_CALENDAR_API_KEY");
    }

}

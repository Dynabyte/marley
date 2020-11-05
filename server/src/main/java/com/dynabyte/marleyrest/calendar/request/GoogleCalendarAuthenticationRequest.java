package com.dynabyte.marleyrest.calendar.request;

import lombok.Data;

@Data
public class GoogleCalendarAuthenticationRequest {

    private String faceId;
    private String authCode;

}

package com.dynabyte.marleyrest.calendar.util;

import com.dynabyte.marleyrest.api.exception.InvalidArgumentException;
import com.dynabyte.marleyrest.api.exception.RequestBodyNotFoundException;
import com.dynabyte.marleyrest.calendar.model.GoogleTokens;
import com.dynabyte.marleyrest.calendar.request.GoogleCalendarAuthenticationRequest;

import java.util.Objects;

import static java.util.Objects.isNull;

public class CalendarRequestUtil {

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
}
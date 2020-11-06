package com.dynabyte.marleyrest.calendar.controller;

import com.dynabyte.marleyrest.calendar.model.GoogleTokens;
import com.dynabyte.marleyrest.calendar.request.GoogleCalendarAuthenticationRequest;
import com.dynabyte.marleyrest.calendar.response.CalendarResponse;
import com.dynabyte.marleyrest.calendar.service.CalendarService;
import com.dynabyte.marleyrest.calendar.service.GoogleTokensService;
import com.dynabyte.marleyrest.calendar.util.CalendarRequestUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
public class CalendarController {

    private final CalendarService calendarService;
    private final GoogleTokensService googleTokensService;

    @Autowired
    public CalendarController(CalendarService calendarService, GoogleTokensService googleTokensService) {
        this.calendarService = calendarService;
        this.googleTokensService = googleTokensService;
    }


    @GetMapping("/calendar/{id}")
    public ResponseEntity<CalendarResponse> getCalendarEvents(@PathVariable String id){

        CalendarResponse calendarResponse = calendarService.getCalendarEvents(id);

        return ResponseEntity.ok(calendarResponse);
    }

    @PostMapping("/calendar/tokens")
    public HttpStatus saveGoogleTokens(@RequestBody GoogleCalendarAuthenticationRequest authenticationRequest){

        CalendarRequestUtil.validateAuthenticationRequest(authenticationRequest);
        GoogleTokenResponse tokenResponse = calendarService.getTokensFromGoogle(authenticationRequest.getAuthCode());
        Long expirationSystemTime = System.currentTimeMillis() + tokenResponse.getExpiresInSeconds()*1000;
        //TODO save via personService or googleTokensService? Check that everything saves correctly in both tables
        googleTokensService.save(new GoogleTokens(authenticationRequest.getFaceId(), tokenResponse.getAccessToken(), tokenResponse.getRefreshToken(), expirationSystemTime));

        return HttpStatus.OK;
    }
}

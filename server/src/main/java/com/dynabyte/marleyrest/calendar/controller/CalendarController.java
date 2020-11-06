package com.dynabyte.marleyrest.calendar.controller;

import com.dynabyte.marleyrest.calendar.model.GoogleTokens;
import com.dynabyte.marleyrest.calendar.request.GoogleCalendarAuthenticationRequest;
import com.dynabyte.marleyrest.calendar.response.CalendarResponse;
import com.dynabyte.marleyrest.calendar.service.CalendarService;
import com.dynabyte.marleyrest.calendar.service.GoogleTokensService;
import com.dynabyte.marleyrest.calendar.util.CalendarRequestUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
public class CalendarController {
    private final Logger LOGGER = LoggerFactory.getLogger(CalendarController.class);

    private final CalendarService calendarService;
    private final GoogleTokensService googleTokensService;

    @Autowired
    public CalendarController(CalendarService calendarService, GoogleTokensService googleTokensService) {
        this.calendarService = calendarService;
        this.googleTokensService = googleTokensService;
    }


    @GetMapping("/calendar/{faceId}")
    public ResponseEntity<CalendarResponse> getCalendarEvents(@PathVariable String faceId){
        LOGGER.info("Calendar request received");
        CalendarResponse calendarResponse = calendarService.getCalendarEventsOrCredentials(faceId);
        LOGGER.info("Calendar request successful");
        return ResponseEntity.ok(calendarResponse);
    }

    @PostMapping("/calendar/tokens")
    public HttpStatus saveGoogleTokens(@RequestBody GoogleCalendarAuthenticationRequest authenticationRequest){
        LOGGER.info("Request to save tokens received");
        CalendarRequestUtil.validateAuthenticationRequest(authenticationRequest);
        GoogleTokenResponse tokenResponse = calendarService.getTokensFromGoogle(authenticationRequest.getAuthCode());
        Long expirationSystemTime = System.currentTimeMillis() + tokenResponse.getExpiresInSeconds()*1000;
        //TODO save via personService or googleTokensService? Check that everything saves correctly in both tables
        googleTokensService.save(new GoogleTokens(authenticationRequest.getFaceId(), tokenResponse.getAccessToken(), tokenResponse.getRefreshToken(), expirationSystemTime));
        LOGGER.info("Request successful. Tokens have been saved to database");
        return HttpStatus.OK;
    }

    @DeleteMapping("/calendar/tokens/{faceId}")
    public HttpStatus deleteGoogleTokens(@PathVariable String faceId){
        googleTokensService.deleteById(faceId);
        return HttpStatus.OK;
    }
}

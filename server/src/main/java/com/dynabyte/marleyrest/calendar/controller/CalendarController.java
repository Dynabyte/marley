package com.dynabyte.marleyrest.calendar.controller;

import com.dynabyte.marleyrest.calendar.model.GoogleTokens;
import com.dynabyte.marleyrest.calendar.request.GoogleCalendarAuthenticationRequest;
import com.dynabyte.marleyrest.calendar.response.EventResponse;
import com.dynabyte.marleyrest.calendar.response.GoogleCredentials;
import com.dynabyte.marleyrest.calendar.service.CalendarService;
import com.dynabyte.marleyrest.calendar.service.GoogleTokensService;
import com.dynabyte.marleyrest.calendar.util.CalendarUtil;
import com.dynabyte.marleyrest.personrecognition.service.PersonService;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("calendar/")
public class CalendarController {
    private final Logger LOGGER = LoggerFactory.getLogger(CalendarController.class);

    private final CalendarService calendarService;
    private final GoogleTokensService googleTokensService;
    private final PersonService personService;

    @GetMapping("{faceId}")
    public ResponseEntity<EventResponse> getCalendarEvent(@PathVariable String faceId) {
        LOGGER.info("Calendar request received");
        EventResponse calendarEvent = calendarService.getCalendarEvent(faceId);
        LOGGER.info("Calendar request successful");
        return ResponseEntity.ok(calendarEvent);
    }

    @GetMapping("credentials")
    public ResponseEntity<GoogleCredentials> getCalendarCredentials() {
        return ResponseEntity.ok(calendarService.getCredentials());
    }


    @PostMapping("tokens")
    public HttpStatus saveGoogleTokens(@RequestBody GoogleCalendarAuthenticationRequest authenticationRequest) {
        LOGGER.info("Request to save tokens received");
        CalendarUtil.validateAuthenticationRequest(authenticationRequest);
        personService.validatePersonExists(authenticationRequest.getFaceId());

        GoogleTokenResponse tokenResponse = calendarService.getTokensFromGoogle(authenticationRequest.getAuthCode());
        Long expirationSystemTime = System.currentTimeMillis() + tokenResponse.getExpiresInSeconds() * 1000;
        googleTokensService.save(new GoogleTokens(authenticationRequest.getFaceId(), tokenResponse.getAccessToken(), tokenResponse.getRefreshToken(), expirationSystemTime));
        LOGGER.info("Request successful. Tokens have been saved to database");
        return HttpStatus.OK;
    }

    @DeleteMapping("tokens/{faceId}")
    public HttpStatus deleteGoogleTokens(@PathVariable String faceId) {
        googleTokensService.deleteById(faceId);
        return HttpStatus.OK;
    }
}

package com.dynabyte.marleyrest.calendar.service;

import com.dynabyte.marleyrest.calendar.exception.GoogleAPIException;
import com.dynabyte.marleyrest.calendar.exception.GoogleCredentialsMissingException;
import com.dynabyte.marleyrest.calendar.exception.GoogleTokensMissingException;
import com.dynabyte.marleyrest.calendar.model.GoogleTokens;
import com.dynabyte.marleyrest.calendar.response.EventResponse;
import com.dynabyte.marleyrest.calendar.response.GoogleCredentials;
import com.dynabyte.marleyrest.calendar.util.CalendarDateUtil;
import com.dynabyte.marleyrest.calendar.util.CalendarUtil;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalendarService {
    private final Logger LOGGER = LoggerFactory.getLogger(CalendarService.class);

    private static final String APPLICATION_NAME = "Marley Person Recognition";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String CREDENTIALS_FILE_PATH = "/google-credentials.json";

    private final GoogleTokensService googleTokensService;
    private final GoogleClientSecrets clientSecrets = getGoogleClientSecretsFromJSON();

    /**
     * Retrieves client id and Google API key which frontend needs in order to request calendar permission
     * @return GoogleCredentials object to identify application with Google
     */
    public GoogleCredentials getCredentials() {
        return new GoogleCredentials(clientSecrets.getDetails().getClientId());
    }

    /**
     * Makes a request to Google to get tokens for to a particular user that has granted permission to view calendar
     * @param authCode authentication code received from the frontend after a user has granted calendar access
     * @return GoogleTokenResponse including access token and refresh token
     */
    public GoogleTokenResponse getTokensFromGoogle(String authCode){
        LOGGER.info("Obtaining tokens from Google using authorization code");
        try {
            return new GoogleAuthorizationCodeTokenRequest(
                            new NetHttpTransport(),
                            JSON_FACTORY,
                            "https://oauth2.googleapis.com/token",
                            clientSecrets.getDetails().getClientId(),
                            clientSecrets.getDetails().getClientSecret(),
                            authCode,
                            "http://localhost:3000")  // Specify the same redirect URI that you use with your web app.
                                            // If you don't have a web version of your app, you can specify an empty string.
                            .execute();
        } catch (IOException e) {
            e.printStackTrace();
            throw new GoogleAPIException("Could not obtain tokens from Google Calendar API");
        }
    }

    /**
     * Get upcoming calendar event for a particular faceId
     * @param faceId the faceId to get a calendar event for
     * @return next upcoming calendar event from Google Calendar
     */
    public EventResponse getCalendarEvent(String faceId) {
        Optional<GoogleTokens> googleTokensOptional = googleTokensService.findGoogleTokensById(faceId);
        if(googleTokensOptional.isPresent()){
            try {
                return getCalendarEventFromGoogle(googleTokensOptional.get());
            } catch (IOException e) {
                e.printStackTrace();
                throw new GoogleAPIException("Could not get events from Google Calendar API");
            }
        }
        throw new GoogleTokensMissingException("Google Tokens not found in database for faceId: " + faceId);
    }

    /**
     * Makes a request to Google Calendar API to get the next calendar event. The access token will be used if it has
     * not expired. If the token has expired then a new access token will be requested and saved to the database before
     * the calendar request is made.
     * @param tokens access token, refresh token and token expiration time from the database
     * @return the next upcoming calendar event
     * @throws IOException throws IOException if something goes wrong in a request to Google
     */
    private EventResponse getCalendarEventFromGoogle(GoogleTokens tokens) throws IOException {
        long remainingMilliseconds = tokens.getExpirationSystemTime() - System.currentTimeMillis();
        LOGGER.info("Token expires in (ms): " + remainingMilliseconds);
        if(remainingMilliseconds < 0){
            LOGGER.info("Access token expired. Requesting new token");
            GoogleTokenResponse tokenResponse = refreshAccessToken(tokens.getRefreshToken());
            tokens.setAccessToken(tokenResponse.getAccessToken());
            tokens.setExpirationSystemTime(System.currentTimeMillis() + tokenResponse.getExpiresInSeconds()*1000);
            googleTokensService.save(tokens);
            LOGGER.info("New access token saved to database");
        }
        Calendar calendar = new Calendar.Builder(new NetHttpTransport(), JSON_FACTORY, new GoogleCredential().setAccessToken(tokens.getAccessToken()))
                .setApplicationName(APPLICATION_NAME)
                .build();

        //Get upcoming events from the primary calendar. Ongoing event also counts
        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = calendar.events().list("primary")
                .setMaxResults(10)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        //Filter to only include today's events that are Dynabyte events
        List<Event> todayEvents = events.getItems().stream()
                .filter(CalendarDateUtil::isEventToday)
                .filter(CalendarUtil::isDynabyteEvent)
                .collect(Collectors.toList());

        if (todayEvents.isEmpty()) {
            return null;
        }

        Event event = todayEvents.get(0); //Get the next event as single Event object

        System.out.println(event.getLocation());
        EventResponse eventResponse = new EventResponse(event);
        int minutesRemaining = CalendarDateUtil.getMinutesRemaining(event);
        if (minutesRemaining <= -60) {
            //Since the meeting started over an hour ago, the next meeting will be shown instead, if it's for today
            if (todayEvents.size() > 1) {
                Event nextEvent = todayEvents.get(1);
                System.out.println(nextEvent.getLocation());
                eventResponse = new EventResponse(nextEvent);
                minutesRemaining = CalendarDateUtil.getMinutesRemaining(nextEvent);
            } else return null; //The are no more meetings for today
        }

        if (minutesRemaining >= 60) {
            eventResponse.setHoursRemaining(minutesRemaining / 60);
            minutesRemaining %= 60;
        } else if (minutesRemaining < 0) {
            eventResponse.setOngoing(true);
            }
            eventResponse.setMinutesRemaining(minutesRemaining);

            System.out.println("Hours: " + eventResponse.getHoursRemaining());
            System.out.println("Minutes: " + eventResponse.getMinutesRemaining());
           return eventResponse;
        }

    /**
     * Makes a request to Google to acquire a new access token using existing refresh token
     * @param refreshToken refresh token related to a particular faceId
     * @return GoogleTokenResponse including a new limited time access token that gives access to calendar data
     * @throws IOException Google API may throw IOException if something goes wrong
     */
    private GoogleTokenResponse refreshAccessToken(String refreshToken) throws IOException {
            return new GoogleRefreshTokenRequest(
                            new NetHttpTransport(),
                            JSON_FACTORY,
                            refreshToken,
                            clientSecrets.getDetails().getClientId(),
                            clientSecrets.getDetails().getClientSecret())
                    .execute();
    }

    /**
     * Loads GoogleClientSecrets from json file
     * @return GoogleClientSecrets object including credentials to make requests to Google API
     */
    private static GoogleClientSecrets getGoogleClientSecretsFromJSON() {
        GoogleClientSecrets clientSecrets;
        try {
            InputStream in = CalendarService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
            clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        } catch (IOException e) {
            throw new GoogleCredentialsMissingException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        return clientSecrets;
    }
}

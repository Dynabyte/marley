package com.dynabyte.marleyrest.calendar.service;

import com.dynabyte.marleyrest.calendar.exception.GoogleAPIException;
import com.dynabyte.marleyrest.calendar.response.CalendarResponse;
import com.dynabyte.marleyrest.calendar.model.GoogleCredentials;
import com.dynabyte.marleyrest.calendar.exception.GoogleCredentialsMissingException;
import com.dynabyte.marleyrest.calendar.model.GoogleTokens;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;

@Service
public class CalendarService {
    private final Logger LOGGER = LoggerFactory.getLogger(CalendarService.class);

    private static final String APPLICATION_NAME = "Marley Person Recognition";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String CREDENTIALS_FILE_PATH = "/google-credentials.json";

    private final GoogleTokensService googleTokensService;
    private final GoogleClientSecrets clientSecrets = getGoogleClientSecrets();

    @Autowired
    public CalendarService(GoogleTokensService googleTokensService) {
        this.googleTokensService = googleTokensService;
    }

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

    public CalendarResponse getCalendarEventsOrCredentials(String faceId) {
        CalendarResponse calendarResponse = new CalendarResponse();

        Optional<GoogleTokens> googleTokensOptional = googleTokensService.findGoogleTokensById(faceId);
        googleTokensOptional.ifPresentOrElse(googleTokens -> {
            try {
                calendarResponse.setCalendarEvents(getCalendarEventsFromGoogle(googleTokens));
                LOGGER.info("Calendar events received from Google");
            } catch (IOException e) {
                e.printStackTrace();
                throw new GoogleAPIException("Could not get events from Google Calendar API");
            }
        }, ()-> {
            calendarResponse.setGoogleCredentials(
                    new GoogleCredentials(clientSecrets.getDetails().getClientId()));
            LOGGER .info("No tokens found for faceId. Sending app credentials");
        });

        return calendarResponse;
    }

    private List<Event> getCalendarEventsFromGoogle(GoogleTokens tokens) throws IOException {
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

        //List the next 10 events from the primary calendar.
        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = calendar.events().list("primary")
                .setMaxResults(10)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        return events.getItems();
    }

    private GoogleTokenResponse refreshAccessToken(String refreshToken) throws IOException {
            return new GoogleRefreshTokenRequest(
                            new NetHttpTransport(),
                            JSON_FACTORY,
                            refreshToken,
                            clientSecrets.getDetails().getClientId(),
                            clientSecrets.getDetails().getClientSecret())
                    .execute();
    }

    private static GoogleClientSecrets getGoogleClientSecrets() {
        // Load client secrets.
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

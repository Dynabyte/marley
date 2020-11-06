package com.dynabyte.marleyrest.calendar.service;

import com.dynabyte.marleyrest.calendar.exception.GoogleAPIException;
import com.dynabyte.marleyrest.calendar.response.CalendarResponse;
import com.dynabyte.marleyrest.calendar.model.GoogleCredentials;
import com.dynabyte.marleyrest.calendar.exception.GoogleCredentialsMissingException;
import com.dynabyte.marleyrest.calendar.model.GoogleTokens;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class CalendarService {
    private static final String APPLICATION_NAME = "Marley Person Recognition";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/google-credentials.json";

    private final GoogleTokensService googleTokensService;
    private final GoogleClientSecrets clientSecrets = getGoogleClientSecrets();

    @Autowired
    public CalendarService(GoogleTokensService googleTokensService) {
        this.googleTokensService = googleTokensService;
    }

    public GoogleTokenResponse getTokensFromGoogle(String authCode){
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

    public CalendarResponse getCalendarEvents(String faceId) {
        CalendarResponse calendarResponse = new CalendarResponse();

        Optional<GoogleTokens> googleTokensOptional = googleTokensService.findGoogleTokensById(faceId);
        googleTokensOptional.ifPresentOrElse(googleTokens -> {
            try {
                calendarResponse.setCalendarEvents(getCalendarEventsFromGoogle(googleTokens));
            } catch (IOException e) {
                e.printStackTrace();
                throw new GoogleAPIException("Could not get events from Google Calendar API");
            }
        }, ()-> calendarResponse.setGoogleCredentials(
                new GoogleCredentials(clientSecrets.getDetails().getClientId())));

        return calendarResponse;
    }

    private List<Event> getCalendarEventsFromGoogle(GoogleTokens tokens) throws IOException {
        if(System.currentTimeMillis() > tokens.getExpirationSystemTime()){
            GoogleTokenResponse googleTokenResponse = refreshAccessToken(tokens.getRefreshToken());
            tokens.setAccessToken(googleTokenResponse.getAccessToken());
            tokens.setExpirationSystemTime(System.currentTimeMillis() + googleTokenResponse.getExpiresInSeconds()*1000);
            googleTokensService.save(tokens);
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

    private NetHttpTransport initializeNetHttpTransport(){
        NetHttpTransport netHttpTransport = null;
        try {
            netHttpTransport = GoogleNetHttpTransport.newTrustedTransport();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
        return netHttpTransport;
    }
}

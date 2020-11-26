package com.dynabyte.marleyrest.calendar.service;

import com.dynabyte.marleyrest.calendar.model.GoogleTokens;
import com.dynabyte.marleyrest.calendar.response.GoogleCredentials;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ResourceUtils;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CalendarServiceTest {

    @Mock
    private GoogleTokensService googleTokensService;

    @InjectMocks CalendarService calendarService;

    private GoogleTokens googleTokens;
    private String faceId;

    @BeforeEach
    void setUp() {
        faceId = "5fad4b9e1bbac38873e8cdbd";
        try {
            ObjectMapper mapper = new ObjectMapper();
            File file = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "google-tokens.json");
            googleTokens = mapper.readValue(file, GoogleTokens.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getCredentials() throws Exception {
        //This test generates a warning while setting environment variable for the test, but it works
        EnvironmentVariables environmentVariables = new EnvironmentVariables("GOOGLE_CALENDAR_API_KEY", "api-key");

        environmentVariables.execute(()-> {
            GoogleCredentials credentials = calendarService.getCredentials();
            assertTrue(credentials.getClientId().endsWith(".apps.googleusercontent.com"));
            assertEquals("api-key", credentials.getGoogleCalendarApiKey());
        });
    }
}
package com.dynabyte.marleyrest.calendar.service;

import com.dynabyte.marleyrest.calendar.model.GoogleTokens;
import com.dynabyte.marleyrest.calendar.repository.GoogleTokensRepository;
import com.dynabyte.marleyrest.personrecognition.request.ImageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoogleTokensServiceTest {

    @Mock
    private GoogleTokensRepository googleTokensRepository;

    @InjectMocks
    private GoogleTokensService googleTokensService;

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
    void save() {
        googleTokensService.save(googleTokens);
        verify(googleTokensRepository).save(googleTokens);
    }

    @Test
    void findGoogleTokensById() {
        when(googleTokensRepository.findById(faceId)).thenReturn(Optional.of(googleTokens));

        Optional<GoogleTokens> googleTokensOptional = googleTokensService.findGoogleTokensById(faceId);

        assertEquals(googleTokens, googleTokensOptional.get());

        verify(googleTokensRepository).findById(faceId);
    }

    @Test
    void deleteById() {
        googleTokensService.deleteById(faceId);
        verify(googleTokensRepository).deleteById(faceId);
    }
}
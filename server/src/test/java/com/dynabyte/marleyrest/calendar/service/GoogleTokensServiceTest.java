package com.dynabyte.marleyrest.calendar.service;

import com.dynabyte.marleyrest.calendar.repository.GoogleTokensRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GoogleTokensServiceTest {

    @Mock
    private GoogleTokensRepository googleTokensRepository;

    @InjectMocks
    private GoogleTokensService googleTokensService;

    @BeforeEach
    void setUp() {
        //TODO load google-tokens.json
    }

    @Test
    void save() {
    }

    @Test
    void findGoogleTokensById() {
    }

    @Test
    void deleteById() {
    }
}
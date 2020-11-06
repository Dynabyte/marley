package com.dynabyte.marleyrest.calendar.service;

import com.dynabyte.marleyrest.calendar.model.GoogleTokens;
import com.dynabyte.marleyrest.calendar.repository.GoogleTokensRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GoogleTokensService {

    private final GoogleTokensRepository googleTokensRepository;

    @Autowired
    public GoogleTokensService(GoogleTokensRepository googleTokensRepository) {
        this.googleTokensRepository = googleTokensRepository;
    }

    public void save(GoogleTokens googleTokens){
        googleTokensRepository.save(googleTokens);
    }

    public Optional<GoogleTokens> findGoogleTokensById(String faceId){
        return googleTokensRepository.findById(faceId);
    }

    public void deleteById(String faceId){
        googleTokensRepository.deleteById(faceId);
    }
}

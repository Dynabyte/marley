package com.dynabyte.marleyjavarestapi.facerecognition.to.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

/**
 * Response object to frontend for prediction requests. Includes information about the person if a the python system
 * predicts the image contains the face of a known person and includes boolean data about the facial detection.
 */
@Data
public class ClientPredictionResponse {

    //TODO include person object instead of just name?

    private UUID id;
    private String name;
    @JsonProperty(value = "isFace")
    private boolean isFace;
    @JsonProperty(value = "isKnownFace")
    private boolean isKnownFace;

    public ClientPredictionResponse(UUID id, String name, boolean isFace, boolean isKnownFace) {
        this.id = id;
        this.name = name;
        this.isFace = isFace;
        this.isKnownFace = isKnownFace;
    }
}
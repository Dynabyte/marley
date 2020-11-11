package com.dynabyte.marleyrest.prediction.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Response object to client for prediction requests. Includes information about the person if found in the python api.
 * Predicts faceId and name if the image contains the face of a known person and includes boolean data about the facial detection.
 */
@Data
public class ClientPredictionResponse {

    private String id;
    private String name;
    @JsonProperty(value = "isFace")
    private boolean isFace;
    @JsonProperty(value = "isKnownFace")
    private boolean isKnownFace;
    private boolean hasAllowedCalendar;

    public ClientPredictionResponse(String id, String name, boolean isFace, boolean isKnownFace, boolean hasAllowedCalendar) {
        this.id = id;
        this.name = name;
        this.isFace = isFace;
        this.isKnownFace = isKnownFace;
        this.hasAllowedCalendar = hasAllowedCalendar;
    }
}
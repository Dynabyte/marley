package com.dynabyte.marleyrest.prediction.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * Response object to client for prediction requests. Includes information about the person if found in the python api.
 * Predicts faceId and name if the image contains the face of a known person and includes boolean data about the facial detection.
 */
@Data
@AllArgsConstructor
public class ClientPredictionResponse {

    private String id;
    private String name;
    @JsonProperty(value = "isFace")
    private boolean isFace;
    @JsonProperty(value = "isKnownFace")
    private boolean isKnownFace;
   @Getter(AccessLevel.NONE)
   @JsonProperty("hasAllowedCalendar")
    private boolean hasAllowedCalendar;

    public boolean hasAllowedCalendar() {
        return hasAllowedCalendar;
    }
}
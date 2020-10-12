package com.dynabyte.marleyjavarestapi.facerecognition.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FaceRecognitionResult {

    @JsonProperty
    private boolean isFace;
    private String faceId;

    public FaceRecognitionResult(boolean isFace, String faceId) {
        this.isFace = isFace;
        this.faceId = faceId;
    }
}

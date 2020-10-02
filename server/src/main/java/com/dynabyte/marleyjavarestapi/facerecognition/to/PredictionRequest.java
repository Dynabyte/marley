package com.dynabyte.marleyjavarestapi.facerecognition.to;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a request for a prediction of a base64 encoded image.
 */
@Data
public class PredictionRequest {

    private String image;
}

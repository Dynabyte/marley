package com.dynabyte.marleyjavarestapi.facerecognition.to.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Represents a request for a prediction of a base64 encoded image.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PredictionRequest extends ImageRequest{

}

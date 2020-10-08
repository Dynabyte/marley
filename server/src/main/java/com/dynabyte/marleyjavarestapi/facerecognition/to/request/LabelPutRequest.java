package com.dynabyte.marleyjavarestapi.facerecognition.to.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * Includes an image in base64 format and a faceId to add the encoded image to as a reference
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class LabelPutRequest extends ImageRequest{

    String faceId;

    public LabelPutRequest(@NotNull(message = "image must be included as a base64 string") String image) {
        super(image);
    }

    public LabelPutRequest(@NotNull(message = "image must be included as a base64 string") String image, String faceId) {
        super(image);
        this.faceId = faceId;
    }

}

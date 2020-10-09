package com.dynabyte.marleyjavarestapi.facerecognition.to.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Includes an image in base64 format and a faceId to add the encoded image to as a reference
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class LabelPutRequest extends ImageRequest{

    String faceId;

    public LabelPutRequest(String image, String faceId) {
        super(image);
        this.faceId = faceId;
    }

}

package com.dynabyte.marleyrest.registration.request;

import lombok.Data;

/**
 * Request to python face recognition api
 * Includes an image in base64 format and a faceId to add the encoded image to as a reference
 */
@Data
public class LabelPutRequest {

    String image;
    String faceId;

    public LabelPutRequest(String image, String faceId) {
        this.image = image;
        this.faceId = faceId;
    }

}

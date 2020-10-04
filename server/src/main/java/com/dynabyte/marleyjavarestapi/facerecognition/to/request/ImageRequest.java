package com.dynabyte.marleyjavarestapi.facerecognition.to.request;

import lombok.Data;

/**
 * Superclass for requests from frontend involving an image in base64 format
 */
@Data
public abstract class ImageRequest {

    private String image;

}

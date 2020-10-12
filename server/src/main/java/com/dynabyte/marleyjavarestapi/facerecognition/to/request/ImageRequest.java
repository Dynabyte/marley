package com.dynabyte.marleyjavarestapi.facerecognition.to.request;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request including an image in base64 format
 */
@Data
@NoArgsConstructor
public class ImageRequest {

    private String image;

    public ImageRequest(String image) {
        this.image = image;
    }
}

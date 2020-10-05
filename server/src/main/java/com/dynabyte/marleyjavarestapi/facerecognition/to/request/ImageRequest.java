package com.dynabyte.marleyjavarestapi.facerecognition.to.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * Superclass for requests from frontend involving an image in base64 format
 */
@Data
@NoArgsConstructor
public abstract class ImageRequest {

    @NotNull(message = "image must be included as a base64 string")
    private String image;

    public ImageRequest(@NotNull(message = "image must be included as a base64 string") String image) {
        this.image = image;
    }
}

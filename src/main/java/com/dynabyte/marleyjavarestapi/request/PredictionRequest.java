package com.dynabyte.marleyjavarestapi.request;

/**
 * Represents a request for a prediction of a base64 encoded image.
 */
public class PredictionRequest {

    private String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

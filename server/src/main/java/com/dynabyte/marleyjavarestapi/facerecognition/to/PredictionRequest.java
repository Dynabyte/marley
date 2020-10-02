package com.dynabyte.marleyjavarestapi.facerecognition.to;

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

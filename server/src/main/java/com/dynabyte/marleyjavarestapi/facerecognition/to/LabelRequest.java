package com.dynabyte.marleyjavarestapi.facerecognition.to;

public class LabelRequest {

    private String name;
    private String image;

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

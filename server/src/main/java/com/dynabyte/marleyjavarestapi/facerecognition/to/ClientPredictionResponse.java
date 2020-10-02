package com.dynabyte.marleyjavarestapi.facerecognition.to;

import java.util.UUID;

/**
 * Response object for prediction requests. Includes information about the person if a the python system predicts the
 * image contains the face of a known person and includes boolean data about the facial detection.
 */
public class ClientPredictionResponse {

    //TODO include person object instead of just name?

    private UUID id;
    private String name;
    private boolean isFace;
    private boolean isKnownFace;

    public ClientPredictionResponse(UUID id, String name, boolean isFace, boolean isKnownFace) {
        this.id = id;
        this.name = name;
        this.isFace = isFace;
        this.isKnownFace = isKnownFace;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getIsFace() {
        return isFace;
    }

    public void setIsFace(boolean face) {
        isFace = face;
    }

    public boolean getIsKnownFace() {
        return isKnownFace;
    }

    public void setIsKnownFace(boolean knownFace) {
        isKnownFace = knownFace;
    }
}
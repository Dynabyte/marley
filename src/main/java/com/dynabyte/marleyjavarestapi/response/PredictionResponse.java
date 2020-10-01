package com.dynabyte.marleyjavarestapi.response;

import java.util.UUID;

/**
 * Response object for prediction requests. Includes information about the person if a the python system predicts the
 * image contains the face of a known person and includes boolean data about the facial detection.
 */
public class PredictionResponse {

    private UUID id;
    private String name;
    private boolean isFace;
    private boolean isKnownFace;
    private boolean isConfidentDetection;

    public PredictionResponse(UUID id, String name, boolean isFace, boolean isKnownFace, boolean isConfidentDetection) {
        this.id = id;
        this.name = name;
        this.isFace = isFace;
        this.isKnownFace = isKnownFace;
        this.isConfidentDetection = isConfidentDetection;
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

    public boolean isFace() {
        return isFace;
    }

    public void setFace(boolean face) {
        isFace = face;
    }

    public boolean isKnownFace() {
        return isKnownFace;
    }

    public void setKnownFace(boolean knownFace) {
        isKnownFace = knownFace;
    }

    public boolean isConfidentDetection() {
        return isConfidentDetection;
    }

    public void setConfidentDetection(boolean confidentDetection) {
        isConfidentDetection = confidentDetection;
    }
}


/*
HTTP 200 OK/HTTP 5xx/4xx
json
{
	"id":(UUID, nullable),
	"name":(string, nullable),
	"isKnownFace":(boolean),
	"isFace":(boolean),
	"isConfident":(boolean),
}

 */
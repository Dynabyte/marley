package com.dynabyte.marleyjavarestapi.facerecognition.to;


public class PythonResponse {

    private boolean isFace;
    private String faceId;

    public boolean getIsFace() {
        return isFace;
    }

    public String getFaceId() {
        return faceId;
    }


    @Override
    public String toString() {
        return "PythonResponse{" +
                "getIsFace=" + isFace +
                ", faceId='" + faceId + '\'' +
                '}';
    }
}

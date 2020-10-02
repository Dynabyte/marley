package com.dynabyte.marleyjavarestapi.facerecognition.to;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PythonResponse {

    @JsonProperty
    private boolean isFace;
    private String faceId;

}

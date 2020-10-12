package com.dynabyte.marleyjavarestapi.facerecognition.to.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Response from python rest api which will have faceId == null if no match is found. If a face is detected then
 * isFace == true. A face can be detected without getting a match but a match cannot be found without detecting a face.
 */
@Data
public class PythonResponse {

    private String faceId;

}

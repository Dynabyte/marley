package com.dynabyte.marleyrest.prediction.response;

import lombok.Data;

/**
 * Response from the face recognition API including a faceId that may be null if face is not known
 */
@Data
public class FaceRecognitionResponse {

    private String faceId;

}

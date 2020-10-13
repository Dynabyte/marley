package com.dynabyte.marleyrest.prediction.service;

import com.dynabyte.marleyrest.prediction.request.ImageRequest;
import com.dynabyte.marleyrest.registration.request.LabelPutRequest;
import com.dynabyte.marleyrest.prediction.response.FaceRecognitionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Face Recognition Service sends requests to the python rest api for face recognition and delivers a response.
 */
@Service
public class FaceRecognitionService {

    private final Logger LOGGER = LoggerFactory.getLogger(FaceRecognitionService.class);
    private final String faceRecognitionURL = "http://localhost:5000/face-recognition/";
    private final RestTemplate restTemplate = new RestTemplate();



    /**
     * Sends a prediction request to find out whether an image includes a face and if it is recognized as a known person
     * @param imageRequest includes an image to test
     * @return response object including whether a face is detected and includes a faceID if a known person is detected
     */
    public FaceRecognitionResponse predict(ImageRequest imageRequest){
        LOGGER.debug("Sending prediction request to face recognition API");
        return restTemplate.postForObject(faceRecognitionURL + "predict", imageRequest, FaceRecognitionResponse.class);
    }



    /**
     * Sends a labeling request to save a the encoding of an image for a new person to the database
     * @param imageRequest includes an image in base64 format
     * @return response object including whether a face is detected and includes a faceID if a known person is detected
     */
    public FaceRecognitionResponse postLabel(ImageRequest imageRequest){
        LOGGER.debug("Sending label post request to face recognition API");
        return restTemplate.postForObject(faceRecognitionURL + "label", imageRequest, FaceRecognitionResponse.class);
    }

    /**
     * Sends a labeling request to add an encoding of an additional image for a existing face to the database
     * @param labelPutRequest includes an image in base64 format
     */
    public void putLabel(LabelPutRequest labelPutRequest) {
        LOGGER.debug("Sending label put request to face recognition API");
        restTemplate.put(faceRecognitionURL + "label/" + labelPutRequest.getFaceId(), labelPutRequest);
    }
}

package com.dynabyte.marleyjavarestapi.facerecognition.service;

import com.dynabyte.marleyjavarestapi.facerecognition.to.request.ImageRequest;
import com.dynabyte.marleyjavarestapi.facerecognition.to.request.LabelPutRequest;
import com.dynabyte.marleyjavarestapi.facerecognition.to.response.PythonResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


/**
 * Face Recognition Service sends requests to the python rest api for face recognition and delivers a response.
 */
@Service
public class FaceRecognitionService {

    private final String faceRecognitionURL = "http://localhost:5000/";
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Sends a prediction request to find out whether an image includes a face and if it is recognized as a known person
     * @param imageRequest includes an image to test
     * @return response object including whether a face is detected and includes a faceID if a known person is detected
     */
    public PythonResponse predict(ImageRequest imageRequest){
        return restTemplate.postForObject(faceRecognitionURL + "predict", imageRequest, PythonResponse.class);
    }

    /**
     * Sends a labeling request to save a the encoding of an image for a new person to the database
     * @param imageRequest includes an image in base64 format
     * @return response object including whether a face is detected and includes a faceID if a known person is detected
     */
    public PythonResponse postLabel(ImageRequest imageRequest){
        return  restTemplate.postForObject(faceRecognitionURL + "label", imageRequest, PythonResponse.class);
    }

    public void putLabel(LabelPutRequest labelPutRequest) {
        restTemplate.put(faceRecognitionURL + "label", labelPutRequest);
    }
}

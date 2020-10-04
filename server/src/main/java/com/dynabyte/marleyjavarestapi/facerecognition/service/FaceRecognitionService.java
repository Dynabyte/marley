package com.dynabyte.marleyjavarestapi.facerecognition.service;

import com.dynabyte.marleyjavarestapi.facerecognition.service.interfaces.IFaceRecognitionService;
import com.dynabyte.marleyjavarestapi.facerecognition.to.request.LabelRequest;
import com.dynabyte.marleyjavarestapi.facerecognition.to.request.PredictionRequest;
import com.dynabyte.marleyjavarestapi.facerecognition.to.response.PythonResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


/**
 * Face Recognition Service sends requests to the python rest api for face recognition and delivers a response.
 */
@Service
public class FaceRecognitionService implements IFaceRecognitionService {

    private final String faceRecognitionURL = "http://localhost:5000/";
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Sends a prediction request to find out whether an image includes a face and if it is recognized as a known person
     * @param predictionRequest includes an image to test
     * @return response object including whether a face is detected and includes a faceID if a known person is detected
     */
    public PythonResponse predict(PredictionRequest predictionRequest){
        return restTemplate.postForObject(faceRecognitionURL + "predict", predictionRequest, PythonResponse.class);
    }

    /**
     * Sends a labeling request to save a the encoding of an image for a new person to the database
     * @param labelRequest includes an image and a
     * @return response object including whether a face is detected and includes a faceID if a known person is detected
     */
    public PythonResponse label(LabelRequest labelRequest){
        return  restTemplate.postForObject(faceRecognitionURL + "label", labelRequest, PythonResponse.class);
    }
}

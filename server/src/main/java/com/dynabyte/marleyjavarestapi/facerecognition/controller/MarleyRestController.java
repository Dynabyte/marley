package com.dynabyte.marleyjavarestapi.facerecognition.controller;

import com.dynabyte.marleyjavarestapi.facerecognition.service.FaceRecognitionService;
import com.dynabyte.marleyjavarestapi.facerecognition.to.LabelRequest;
import com.dynabyte.marleyjavarestapi.facerecognition.to.PredictionRequest;
import com.dynabyte.marleyjavarestapi.facerecognition.to.ClientPredictionResponse;
import com.dynabyte.marleyjavarestapi.facerecognition.to.PythonResponse;
import com.dynabyte.marleyjavarestapi.facerecognition.utility.Validation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Controller for the Marley rest api that is the communication hub for all requests.
 */
@RestController
public class MarleyRestController {

    private final FaceRecognitionService faceRecognitionService = new FaceRecognitionService();

    //TODO integrate into official marley project on github

    /**
     * Handles image prediction requests where the user can send an image and the python system will predict whether that
     * image contains a face of any known person and if it contains a face at all. If the person is known then the response
     * will also include that persons UUID and other information stored in a database.
     *
     * @param predictionRequest A request object that must contain an image in base64 format
     * @return ResponseEntity object with the results of the image prediction
     */
    @PostMapping("/predict")
    public ResponseEntity<ClientPredictionResponse> predict(@RequestBody PredictionRequest predictionRequest){

        Validation.validateRequest(predictionRequest);

        PythonResponse pythonResponse = faceRecognitionService.predict(predictionRequest);
        System.out.println(pythonResponse);

        ClientPredictionResponse mockDataPredictionResponse =
                new ClientPredictionResponse(null, "Unknown", pythonResponse.getIsFace(), false);

        if(pythonResponse.getFaceId() == null){
            return ResponseEntity.ok(mockDataPredictionResponse);
        }

        //TODO connect to and check PostgreSQL database for which name to display
        //Mocking the database
        if(pythonResponse.getFaceId().equals("5f75cdb6d2b163f57228a203")){
            mockDataPredictionResponse.setId(UUID.randomUUID());
            mockDataPredictionResponse.setName("Daniel");
            mockDataPredictionResponse.setIsKnownFace(true);
        }
        else if(pythonResponse.getFaceId().equals("5f75f4f1f3aa57396704136a")){
            mockDataPredictionResponse.setId(UUID.randomUUID());
            mockDataPredictionResponse.setName("Mr Bean");
            mockDataPredictionResponse.setIsKnownFace(true);
        }

        return ResponseEntity.ok(mockDataPredictionResponse);
    }

    //TODO make label method for put and for multiple images?
    @PostMapping("/label")
    public HttpStatus label(@RequestBody LabelRequest labelRequest){
        Validation.validateRequest(labelRequest);
        PythonResponse pythonResponse = faceRecognitionService.label(labelRequest);
        //TODO Save name and mongoDB id to database

        return HttpStatus.OK;
    }
}
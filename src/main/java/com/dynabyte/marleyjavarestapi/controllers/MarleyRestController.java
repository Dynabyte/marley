package com.dynabyte.marleyjavarestapi.controllers;

import com.dynabyte.marleyjavarestapi.exception.ImageEncodingException;
import com.dynabyte.marleyjavarestapi.exception.MissingArgumentException;
import com.dynabyte.marleyjavarestapi.request.PredictionRequest;
import com.dynabyte.marleyjavarestapi.response.PredictionResponse;
import com.dynabyte.marleyjavarestapi.utility.Validation;
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

    /**
     * Handles image prediction requests where the user can send an image and the python system will predict whether that
     * image contains a face of any known person and if it contains a face at all. If the person is known then the response
     * will also include that persons UUID and other information stored in a database.
     *
     * @param predictionRequest A request object that must contain an image in base64 format
     * @return ResponseEntity object with the results of the image prediction
     */
    @PostMapping("/predict")
    public ResponseEntity<PredictionResponse> predict(@RequestBody PredictionRequest predictionRequest){

        if(predictionRequest.getImage() == null){
            throw new MissingArgumentException("image cannot be null. Must be an image in base64 format");
        }
        //TODO custom error message handling for image == null and Bad Request
        //TODO integrate into official marley project on github

        if (!Validation.isBase64Encoded(predictionRequest.getImage())){
            throw new ImageEncodingException("Image is not in base64 format!");
        }

        PredictionResponse mockDataPredictionResponse =
                new PredictionResponse(UUID.randomUUID(), "Marley", true, true, true);

        return ResponseEntity.ok(mockDataPredictionResponse);
    }
}



/*
Förslag:

Request
POST
json
{
	"image":[base64 encoded binary data](string)
}

->
Predict Api
->

Response
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
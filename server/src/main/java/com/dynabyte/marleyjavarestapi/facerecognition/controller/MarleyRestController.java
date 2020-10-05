package com.dynabyte.marleyjavarestapi.facerecognition.controller;

import com.dynabyte.marleyjavarestapi.facerecognition.model.Person;
import com.dynabyte.marleyjavarestapi.facerecognition.service.interfaces.IFaceRecognitionService;
import com.dynabyte.marleyjavarestapi.facerecognition.service.interfaces.IPersonService;
import com.dynabyte.marleyjavarestapi.facerecognition.to.request.*;
import com.dynabyte.marleyjavarestapi.facerecognition.to.response.ClientPredictionResponse;
import com.dynabyte.marleyjavarestapi.facerecognition.to.response.PythonResponse;
import com.dynabyte.marleyjavarestapi.facerecognition.utility.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for the Marley rest api that is the communication hub for all requests.
 */
@RestController
public class MarleyRestController {

    private final IFaceRecognitionService faceRecognitionService;
    private final IPersonService personService;

    @Autowired
    public MarleyRestController(IFaceRecognitionService faceRecognitionService, IPersonService personService) {
        this.faceRecognitionService = faceRecognitionService;
        this.personService = personService;
    }

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

        Validation.validateImageRequest(predictionRequest);

        PythonResponse pythonResponse = faceRecognitionService.predict(predictionRequest);
        System.out.println(pythonResponse);

        ClientPredictionResponse predictionResponse =
                new ClientPredictionResponse("Unknown", pythonResponse.isFace(), false);

        if(pythonResponse.getFaceId() == null){
            return ResponseEntity.ok(predictionResponse);
        }

        personService.findById(pythonResponse.getFaceId()).ifPresent(person -> {
            predictionResponse.setKnownFace(true);
            predictionResponse.setName(person.getName());
        });

        //TODO throw exception perhaps if a faceID is in the predictionResponse but not found in DB?

        return ResponseEntity.ok(predictionResponse);
    }

    @PostMapping("/register")
    public HttpStatus register(@RequestBody RegistrationRequest registrationRequest){
        Validation.validateRegistrationRequest(registrationRequest);

        //TODO verify that everything works
        registerPersonWithMultipleImages(registrationRequest);
        //TODO Log report of how many images succeeded

        return HttpStatus.OK;
    }

    private void registerPersonWithMultipleImages(RegistrationRequest registrationRequest) {
        String faceId = null;
        List<String> images = registrationRequest.getImages();
        int startIndex = 0;

        //Go through each base64 image until one can be encoded and added to the database correctly
        for (String image : images){
            startIndex++;
            try {
                PythonResponse pythonResponse = faceRecognitionService.postLabel(new LabelRequest(image));
                if (pythonResponse.getFaceId() != null){
                    faceId = pythonResponse.getFaceId();
                    personService.save(new Person(faceId, registrationRequest.getName()));
                    break;
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        //TODO check if person already exists in the python database using predict?
        //TODO throw exception if no image at all can be added to the database
        System.out.println("Start index: " + startIndex);

        //Add all remaining image encodings to the same faceId that was generated earlier
        for (int i = startIndex; i < images.size(); i++) {
            try {
                faceRecognitionService.putLabel(new LabelRequest(images.get(i), faceId));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    //TODO remake label method to have more fitting names and only save to DB if valid faceId
    @PostMapping("/label")
    public HttpStatus registerWithSingleImage(@RequestBody SingleImageRegistrationRequest singleImageRegistrationRequest){
        Validation.validateImageRequest(singleImageRegistrationRequest);
        PythonResponse pythonResponse = faceRecognitionService.postLabel(singleImageRegistrationRequest);
        personService.save(new Person(pythonResponse.getFaceId(), singleImageRegistrationRequest.getName()));
        return HttpStatus.OK;
    }
}
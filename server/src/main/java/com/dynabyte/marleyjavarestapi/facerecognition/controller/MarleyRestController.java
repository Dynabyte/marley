package com.dynabyte.marleyjavarestapi.facerecognition.controller;

import com.dynabyte.marleyjavarestapi.facerecognition.exception.MissingPersonInDbException;
import com.dynabyte.marleyjavarestapi.facerecognition.exception.PersonAlreadyInDbException;
import com.dynabyte.marleyjavarestapi.facerecognition.exception.RegistrationException;
import com.dynabyte.marleyjavarestapi.facerecognition.model.Person;
import com.dynabyte.marleyjavarestapi.facerecognition.service.FaceRecognitionService;
import com.dynabyte.marleyjavarestapi.facerecognition.service.PersonService;
import com.dynabyte.marleyjavarestapi.facerecognition.to.request.*;
import com.dynabyte.marleyjavarestapi.facerecognition.to.response.ClientPredictionResponse;
import com.dynabyte.marleyjavarestapi.facerecognition.to.response.PythonResponse;
import com.dynabyte.marleyjavarestapi.facerecognition.utility.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;

/**
 * Controller for the Marley rest api that is the communication hub for all requests.
 */
@RestController
public class MarleyRestController {

    private final Logger LOGGER = LoggerFactory.getLogger(MarleyRestController.class);
    private final FaceRecognitionService faceRecognitionService;
    private final PersonService personService;

    @Autowired
    public MarleyRestController(FaceRecognitionService faceRecognitionService, PersonService personService) {
        this.faceRecognitionService = faceRecognitionService;
        this.personService = personService;
    }

    //TODO integrate into official marley project on github

    /**
     * Handles image prediction requests where the user can send an image and the python system will predict whether that
     * image contains a face of any known person and if it contains a face at all. If the person is known then the response
     * will also include that persons UUID and other information stored in a database.
     *
     * @param imageRequest A request object that must contain an image in base64 format
     * @return ResponseEntity object with the results of the image prediction
     */
    @PostMapping("/predict")
    public ResponseEntity<ClientPredictionResponse> predict(@RequestBody ImageRequest imageRequest){
        LOGGER.info("Predict request initiated");

        Validation.validateImageRequest(imageRequest);

        PythonResponse pythonResponse = faceRecognitionService.predict(imageRequest);
        LOGGER.info("Received response: " + pythonResponse);

        ClientPredictionResponse clientPredictionResponse =
                new ClientPredictionResponse("Unknown", pythonResponse.isFace(), false);

        if(pythonResponse.getFaceId() == null){
            LOGGER.info("Successful prediction: " + clientPredictionResponse);
            return ResponseEntity.ok(clientPredictionResponse);
        }

        personService.findById(pythonResponse.getFaceId()).ifPresentOrElse(person -> {
            clientPredictionResponse.setKnownFace(true);
            clientPredictionResponse.setName(person.getName());
        }, () -> {
            String warningMessage = "Found faceId but no matching person in the database";
            LOGGER.warn(warningMessage);
            throw new MissingPersonInDbException(warningMessage);
        });

        LOGGER.info("Successful prediction: " + clientPredictionResponse);
        return ResponseEntity.ok(clientPredictionResponse);
    }

    @PostMapping("/register")
    public HttpStatus register(@RequestBody RegistrationRequest registrationRequest){
        LOGGER.info("Registration request initiated");
        Validation.validateRegistrationRequest(registrationRequest);
        LOGGER.info("Registration request validated");

        //TODO verify that everything works
        registerPersonWithMultipleImages(registrationRequest);
        //TODO Log report of how many images succeeded

        LOGGER.info("Registration request successful");
        return HttpStatus.OK;
    }

    @PostMapping("/label")
    public HttpStatus registerWithSingleImage(@RequestBody SingleImageRegistrationRequest singleImageRegistrationRequest){
        LOGGER.info("Registration request for single image initiated");
        Validation.validateSingleImageRegistrationRequest(singleImageRegistrationRequest);
        verifyThatPersonIsNotInDbAlready(singleImageRegistrationRequest.getImage());
        PythonResponse pythonResponse = faceRecognitionService.postLabel(singleImageRegistrationRequest);
        personService.save(new Person(pythonResponse.getFaceId(), singleImageRegistrationRequest.getName()));
        LOGGER.info("Registration complete. Person saved to database");
        return HttpStatus.OK;
    }

    private void registerPersonWithMultipleImages(RegistrationRequest registrationRequest) {
        LOGGER.info("Registering images...");
        String faceId = null;
        int registeredImagesCount = 0;

        boolean isRegisteredPersonInDb = false;
        //Go through each base64 image until one can be encoded and added to the database correctly, then add the rest to the same faceId
        for (String image : registrationRequest.getImages()){
            try {
                if(!isRegisteredPersonInDb){
                    verifyThatPersonIsNotInDbAlready(image);
                    PythonResponse labelResponse = faceRecognitionService.postLabel(new LabelPutRequest(image));
                    if (labelResponse.getFaceId() != null){
                        faceId = labelResponse.getFaceId();
                        personService.save(new Person(faceId, registrationRequest.getName()));
                        isRegisteredPersonInDb = true;
                        registeredImagesCount++;
                        LOGGER.info("Posted Image. Registered images: " + registeredImagesCount);
                    }
                }
                else {
                    //TODO exception handling or check return value faceId != null somehow
                    faceRecognitionService.putLabel(new LabelPutRequest(image, faceId));
                    registeredImagesCount++;
                    LOGGER.info("Added Image. Registered images: " + registeredImagesCount);
                }
            } catch (HttpServerErrorException e){
                LOGGER.warn("Something went wrong in Face Recognition API");
                e.printStackTrace();
            }
        }
        if(registeredImagesCount == 0){
            throw new RegistrationException("Could not register any images or save person to Db");
        }
        LOGGER.info("Registration Complete. Total registered images: " + registeredImagesCount);
    }

    private void verifyThatPersonIsNotInDbAlready(String image) {
        LOGGER.info("Verifying that person is not already in database");

        PythonResponse predictResponse = faceRecognitionService.predict(new ImageRequest(image));
        final String faceId = predictResponse.getFaceId();
        if(faceId != null){
            personService.findById(faceId).ifPresent(person -> {
                String warningMessage = "Cannot register a person who is already in the database";
                LOGGER.warn(warningMessage);
                throw new PersonAlreadyInDbException(warningMessage);
            });
        }
    }
}
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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;

/**
 * Controller for the Marley rest api that is the communication hub for all requests.
 */
@CrossOrigin(origins = {"http://localhost:3000"})
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

    /**
     * Handles image prediction requests where the user can send an image and the python system will predict whether that
     * image contains a face of any known person and if it contains a face at all. If the person is known then the response
     * will also include information about that person stored in a SQL database.
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

    /**
     * Registers a person in the SQL database and requests face recognition service to store face encodings from the
     * images in the request. A person will only be registered if that person is not already registered and has at least
     * one image including a face
     *
     * @param registrationRequest Request including name and a list of base64 image strings
     * @return HttpStatus.OK
     */
    @PostMapping("/register")
    public HttpStatus register(@RequestBody RegistrationRequest registrationRequest){
        LOGGER.info("Registration request initiated");
        Validation.validateRegistrationRequest(registrationRequest);
        LOGGER.info("Registration request validated");

        registerPersonWithMultipleImages(registrationRequest);

        LOGGER.info("Registration request successful");
        return HttpStatus.OK;
    }

    /**
     * Loops through the images in the request until a face is found and a person can be added to the database. After
     * that the loop continues and more image encodings are added to the same faceId.
     *
     * @param registrationRequest The request object accepted in the register endpoint
     */
    private void registerPersonWithMultipleImages(RegistrationRequest registrationRequest) {
        LOGGER.info("Registering images...");
        String faceId = null;
        int registeredImagesCount = 0;

        boolean isRegisteredPersonInDb = false;
        //Go through each base64 image until one can be encoded and added to the database correctly, then add the rest to the same faceId
        for (String image : registrationRequest.getImages()){
            try {
                if(!isRegisteredPersonInDb){
                    verifyImageHasFaceAndPersonIsNotInDbAlready(image);
                    PythonResponse labelResponse = faceRecognitionService.postLabel(new ImageRequest(image));
                    if (labelResponse.getFaceId() != null){
                        faceId = labelResponse.getFaceId();
                        personService.save(new Person(faceId, registrationRequest.getName()));
                        isRegisteredPersonInDb = true;
                        registeredImagesCount++;
                        LOGGER.info("Posted Image. Registered images: " + registeredImagesCount);
                    }
                }
                else {
                    //TODO check return value faceId != null somehow? Could verify face and not in db depending on performance
                    faceRecognitionService.putLabel(new LabelPutRequest(image, faceId));
                    registeredImagesCount++;
                    LOGGER.info("Added Image. Registered images: " + registeredImagesCount);
                }
            } catch (HttpServerErrorException e){
                LOGGER.warn("Image not registered. Something went wrong in Face Recognition API");
                e.printStackTrace();
            } catch (RegistrationException e){
                LOGGER.warn("Image not registered. No face found in image");
            }
        }
        if(!isRegisteredPersonInDb){
            String warningMessage = "Could not register any images or save person to Db";
            LOGGER.warn(warningMessage);
            throw new RegistrationException(warningMessage);
        }
        LOGGER.info("Registration Complete. Total registered images: " + registeredImagesCount);
    }

    /**
     * Verifies that the image has a face and that the faceId is not already present in the SQL database
     *
     * @param image An image in base64 format
     */
    private void verifyImageHasFaceAndPersonIsNotInDbAlready(String image) { //TODO split into two methods?
        LOGGER.info("Verifying that person is not already in database");

        PythonResponse predictResponse = faceRecognitionService.predict(new ImageRequest(image));
        final String faceId = predictResponse.getFaceId();
        if(!predictResponse.isFace()){
            String warningMessage = "No face found, cannot get face encoding from image";
            LOGGER.warn(warningMessage);
            throw new RegistrationException(warningMessage);
        }
        if(faceId != null){
            LOGGER.info("Found face in face recognition database: " + faceId);
            personService.findById(faceId).ifPresent(person -> {
                String warningMessage = "Cannot register a person who is already in the database";
                LOGGER.warn(warningMessage);
                throw new PersonAlreadyInDbException(warningMessage);
            });
        }
    }
}
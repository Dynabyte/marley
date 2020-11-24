package com.dynabyte.marleyrest.api.controller;

import com.dynabyte.marleyrest.deletion.DeleteUseCase;
import com.dynabyte.marleyrest.prediction.PredictionUseCase;
import com.dynabyte.marleyrest.registration.RegistrationUseCase;
import com.dynabyte.marleyrest.registration.request.RegistrationRequest;
import com.dynabyte.marleyrest.personrecognition.request.ImageRequest;
import com.dynabyte.marleyrest.prediction.response.ClientPredictionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Rest controller for the Marley rest api that is the communication hub for all Marley related requests.
 */
@CrossOrigin //TODO make app more secure by only accepting correct origin?
@RestController
public class MarleyRestController {

    private final Logger LOGGER = LoggerFactory.getLogger(MarleyRestController.class);
    private final PredictionUseCase predictionUseCase;
    private final RegistrationUseCase registrationUseCase;
    private final DeleteUseCase deleteUseCase;

    @Autowired
    public MarleyRestController(PredictionUseCase predictionUseCase, RegistrationUseCase registrationUseCase, DeleteUseCase deleteUseCase) {
        this.predictionUseCase = predictionUseCase;
        this.registrationUseCase = registrationUseCase;
        this.deleteUseCase = deleteUseCase;
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
    public ResponseEntity<ClientPredictionResponse> predict(@RequestBody ImageRequest imageRequest) {

        ClientPredictionResponse clientPredictionResponse = predictionUseCase.execute(imageRequest);

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
    public ResponseEntity<String> register(@RequestBody RegistrationRequest registrationRequest) {

        String faceId = registrationUseCase.execute(registrationRequest);

        LOGGER.info("Registration request successful");
        return ResponseEntity.ok(faceId);
    }

    @DeleteMapping("/delete/{id}")
    public HttpStatus delete(@PathVariable String id){

        deleteUseCase.execute(id);

        LOGGER.info("Person with faceId " + id + " deleted");
        return HttpStatus.OK;
    }
}
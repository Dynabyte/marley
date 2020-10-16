package com.dynabyte.marleyrest.registration;

import com.dynabyte.marleyrest.api.util.RequestUtil;
import com.dynabyte.marleyrest.prediction.PredictionUseCase;
import com.dynabyte.marleyrest.personrecognition.request.ImageRequest;
import com.dynabyte.marleyrest.personrecognition.response.FaceRecognitionResponse;
import com.dynabyte.marleyrest.personrecognition.service.FaceRecognitionService;
import com.dynabyte.marleyrest.registration.exception.MissingPersonInDbException;
import com.dynabyte.marleyrest.registration.exception.PersonAlreadyInDbException;
import com.dynabyte.marleyrest.registration.exception.RegistrationException;
import com.dynabyte.marleyrest.personrecognition.model.Person;
import com.dynabyte.marleyrest.registration.request.LabelPutRequest;
import com.dynabyte.marleyrest.registration.request.RegistrationRequest;
import com.dynabyte.marleyrest.personrecognition.service.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;

/**
 * Service class for executing the registration use case
 */
@Service
public class RegistrationUseCase {

    private final Logger LOGGER = LoggerFactory.getLogger(RegistrationUseCase.class);
    private final PersonService personService;
    private final FaceRecognitionService faceRecognitionService;

    @Autowired
    public RegistrationUseCase(PersonService personService, FaceRecognitionService faceRecognitionService) {
        this.personService = personService;
        this.faceRecognitionService = faceRecognitionService;
    }

    /**
     * Executes the use case of registering a person in the person database and saving face encodings from a list of
     * images to the face recognition API. The face encodings and the person will have a matching faceId.
     *
     * @param registrationRequest a request with a name and a list of base64 image strings
     */
    public void execute(RegistrationRequest registrationRequest) {
        LOGGER.info("Registration request initiated");
        registrationRequest = RequestUtil.validateAndPrepareRegistrationRequest(registrationRequest);
        LOGGER.debug("Registration request validated");

        registerPersonWithMultipleImages(registrationRequest);
    }

    /**
     * Loops through the images in the request until a face is found and a person can be added to the database, which
     * could be just 1 image. After that the loop continues and more image encodings are added to the same faceId.
     * A successful registration adds face encodings to the face recognition API database as well as a person to the
     * person database with a matching faceId.
     * Exceptions will be thrown if there's a mismatch between the face recognition API database and the person database
     * or if all images failed so not a single one could be registered.
     *
     * @param registrationRequest The request object accepted in the register endpoint
     */
    private void registerPersonWithMultipleImages(RegistrationRequest registrationRequest) {
        LOGGER.info("Registering images...");
        String registeredFaceId = null;
        int registeredImagesCount = 0;
        final String name = registrationRequest.getName();
        final List<String> images = registrationRequest.getImages();

        boolean isRegisteredPersonInDb = false;
        //Go through each base64 image until one can be encoded and added to the database correctly, then add the rest to the same faceId
        for (String image : images) {
            try {
                if (!isRegisteredPersonInDb) {
                    verifyPersonIsNotRegisteredAlready(image);

                    registeredFaceId = faceRecognitionService.postLabel(new ImageRequest(image));
                    personService.save(new Person(registeredFaceId, name));
                    isRegisteredPersonInDb = true;
                    registeredImagesCount++;
                    LOGGER.debug("Posted Image. Registered images: " + registeredImagesCount);
                } else {
                    //TODO predict again to verify it's still the same person? Would perhaps be more secure but would decrease performance
                    faceRecognitionService.putLabel(new LabelPutRequest(image, registeredFaceId));
                    registeredImagesCount++;
                    LOGGER.debug("Added Image. Registered images: " + registeredImagesCount);
                }
            } catch (RestClientResponseException e) {
                if (e.getRawStatusCode() == 409) {
                    LOGGER.info("No face found, cannot get face encoding from image. Skipping an image");
                } else {
                    LOGGER.info("Cannot register image. Something went wrong with Face Recognition API.");
                }
            }
        }
        if (!isRegisteredPersonInDb) {
            throw new RegistrationException("Could not register any images or save person to Db");
        }
        LOGGER.info("Registration Complete. Total registered images: " + registeredImagesCount + " out of " + images.size());
    }

    /**
     * Verifies that a person is not already in a database by making a prediction. If faceId != null then a face is
     * recognized in the face recognition API and an exception will be thrown.
     *
     * @param image the image to be face predicted for verification
     */
    private void verifyPersonIsNotRegisteredAlready(String image) {
        String predictionFaceId = faceRecognitionService.predict(new ImageRequest(image));

        if (predictionFaceId != null) {
            personService.findById(predictionFaceId).ifPresentOrElse(person -> {
                LOGGER.info("Found face in face recognition database: " + predictionFaceId);
                throw new PersonAlreadyInDbException("Cannot register a person who is already in the database");
            }, () -> {
                throw new MissingPersonInDbException("Found faceId in face recognition API but no matching person in the database");
            });
        }
    }
}

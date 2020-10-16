package com.dynabyte.marleyrest.prediction;

import com.dynabyte.marleyrest.api.util.RequestUtil;
import com.dynabyte.marleyrest.prediction.exception.FaceRecognitionException;
import com.dynabyte.marleyrest.personrecognition.request.ImageRequest;
import com.dynabyte.marleyrest.prediction.response.ClientPredictionResponse;
import com.dynabyte.marleyrest.personrecognition.response.FaceRecognitionResponse;
import com.dynabyte.marleyrest.personrecognition.service.FaceRecognitionService;
import com.dynabyte.marleyrest.registration.exception.MissingPersonInDbException;
import com.dynabyte.marleyrest.personrecognition.service.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;

/**
 * Service class for executing the prediction use case
 */
@Service
public class PredictionUseCase {

    private final Logger LOGGER = LoggerFactory.getLogger(PredictionUseCase.class);
    private final FaceRecognitionService faceRecognitionService;
    private final PersonService personService;

    @Autowired
    public PredictionUseCase(FaceRecognitionService faceRecognitionService, PersonService personService) {
        this.faceRecognitionService = faceRecognitionService;
        this.personService = personService;
    }

    /**
     * Predicts whether an image has a face and whether that face is recognized in the database
     *
     * @param imageRequest a request with an image as a base64 image string
     * @return ClientPredictionResponse object
     */
    public ClientPredictionResponse execute(ImageRequest imageRequest) {
        LOGGER.info("Predict request initiated");
        imageRequest = RequestUtil.validateAndPreparePredictionRequest(imageRequest);

        String faceId;
        try {
            faceId = faceRecognitionService.predict(imageRequest);
        } catch (RestClientResponseException e) {
            if (e.getRawStatusCode() == 409) {
                return new ClientPredictionResponse("Unknown", false, false);
            }
            throw new FaceRecognitionException("Something went wrong with the face recognition API", HttpStatus.valueOf(e.getRawStatusCode()));
        }

        ClientPredictionResponse clientPredictionResponse = new ClientPredictionResponse("Unknown", true, false);

        if (faceId == null) {
            return clientPredictionResponse;
        }

        personService.findById(faceId).ifPresentOrElse(person -> {
            clientPredictionResponse.setName(person.getName());
            clientPredictionResponse.setKnownFace(true);
        }, () -> {
            throw new MissingPersonInDbException("Found faceId in face recognition API but no matching person in the database");
        });
        return clientPredictionResponse;
    }
}

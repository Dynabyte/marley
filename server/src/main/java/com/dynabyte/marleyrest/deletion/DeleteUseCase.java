package com.dynabyte.marleyrest.deletion;

import com.dynabyte.marleyrest.deletion.exception.IdNotFoundException;
import com.dynabyte.marleyrest.personrecognition.service.FaceRecognitionService;
import com.dynabyte.marleyrest.personrecognition.service.PersonService;
import com.dynabyte.marleyrest.prediction.exception.FaceRecognitionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;

/**
 * Service class for executing the deletion use case
 */
@Service
public class DeleteUseCase {

    private final Logger LOGGER = LoggerFactory.getLogger(DeleteUseCase.class);
    private final FaceRecognitionService faceRecognitionService;
    private final PersonService personService;

    @Autowired
    public DeleteUseCase(FaceRecognitionService faceRecognitionService, PersonService personService) {
        this.faceRecognitionService = faceRecognitionService;
        this.personService = personService;
    }

    /**
     * Deletes a person from both the face database and person database.
     * If a face with the id provided is not found then an exception is thrown
     * @param faceId the faceId for the person and face to be deleted
     */
    public void execute(String faceId) {
        deletePerson(faceId);
        deleteFace(faceId);
    }

    /**
     * Deletes a person from the local database
     * @param faceId the id of the person to be deleted
     */
    private void deletePerson(String faceId) {
        try {
            personService.deleteById(faceId);
            LOGGER.debug("Deleted person with faceId: " + faceId);
        } catch (EmptyResultDataAccessException e) {
            LOGGER.info("No person found with id: " + faceId);
        }
    }

    /**
     * Deletes face from the face recognition database
     * Throws exception if the faceId cannot be found or anything else goes wrong
     * @param faceId the faceId of the face to be deleted
     */
    private void deleteFace(String faceId) {
        try {
            faceRecognitionService.delete(faceId);
            LOGGER.debug("Deleted face with id: " + faceId);
        } catch (RestClientResponseException e) {
            if (e.getRawStatusCode() == 404) {
                throw new IdNotFoundException("Could not delete! Id not found: " + faceId);
            } else {
                throw new FaceRecognitionException("Something went wrong with the face recognition API", HttpStatus.valueOf(e.getRawStatusCode()));
            }
        }
    }

}

package com.dynabyte.marleyrest.deletion;

import com.dynabyte.marleyrest.deletion.exception.IdNotFoundException;
import com.dynabyte.marleyrest.personrecognition.service.FaceRecognitionService;
import com.dynabyte.marleyrest.personrecognition.service.PersonService;
import com.dynabyte.marleyrest.prediction.exception.FaceRecognitionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.client.RestClientResponseException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteUseCaseTest {

    @Mock
    FaceRecognitionService faceRecognitionService;

    @Mock
    PersonService personService;

    @InjectMocks
    DeleteUseCase deleteUseCase;

    private String faceId;

    @BeforeEach
    void setUp() {
        faceId = "5fad4b9e1bbac38873e8cdbd";
    }

    @Test
    void shouldDeleteUsingBothServices() {
        deleteUseCase.execute(faceId);

        verify(personService).deleteById(faceId);
        verify(faceRecognitionService).delete(faceId);
    }

    @Test
    void shouldDeleteFromFaceRecognitionEvenWhenPersonNotFound(){
        doThrow(EmptyResultDataAccessException.class).when(personService).deleteById(faceId);

        deleteUseCase.execute(faceId);

        verify(personService).deleteById(faceId);
        verify(faceRecognitionService).delete(faceId);
    }

    @Test
    void noFaceIdFoundShouldThrowException(){
        RestClientResponseException restClientResponseException =
                new RestClientResponseException("Error message", 404, "NOT_FOUND", null, null, null);
        doThrow(restClientResponseException).when(faceRecognitionService).delete(faceId);

        assertThrows(IdNotFoundException.class, ()-> deleteUseCase.execute(faceId));

        verify(personService).deleteById(faceId);
        verify(faceRecognitionService).delete(faceId);
    }

    @Test
    void whenFaceRecognitionNotWorkingShouldThrowException(){
        RestClientResponseException restClientResponseException =
                new RestClientResponseException("Error message", 500, "INTERNAL_SERVER_ERROR", null, null, null);
        doThrow(restClientResponseException).when(faceRecognitionService).delete(faceId);

        assertThrows(FaceRecognitionException.class, ()-> deleteUseCase.execute(faceId));
        verify(personService).deleteById(faceId);
        verify(faceRecognitionService).delete(faceId);
    }
}
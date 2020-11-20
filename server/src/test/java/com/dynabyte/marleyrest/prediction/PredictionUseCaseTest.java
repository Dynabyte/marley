package com.dynabyte.marleyrest.prediction;

import com.dynabyte.marleyrest.personrecognition.model.Person;
import com.dynabyte.marleyrest.personrecognition.request.ImageRequest;
import com.dynabyte.marleyrest.personrecognition.service.FaceRecognitionService;
import com.dynabyte.marleyrest.personrecognition.service.PersonService;
import com.dynabyte.marleyrest.prediction.exception.FaceRecognitionException;
import com.dynabyte.marleyrest.prediction.response.ClientPredictionResponse;
import com.dynabyte.marleyrest.registration.exception.MissingPersonInDbException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestClientResponseException;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PredictionUseCaseTest {

    @Mock
    FaceRecognitionService faceRecognitionService;

    @Mock
    PersonService personService;

    @InjectMocks
    PredictionUseCase predictionUseCase;

    private ImageRequest imageRequest;
    private ImageRequest predictRequest;
    private String faceId;

    @BeforeEach
    void setUp() {
        faceId = "5fad4b9e1bbac38873e8cdbd";
        try {
            ObjectMapper mapper = new ObjectMapper();
            File file = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "valid-image-request.json");
            imageRequest = mapper.readValue(file, ImageRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        predictRequest = new ImageRequest(
                imageRequest.getImage()
                        .replace("data:image/jpeg;base64,", ""));
    }

    @Test
    void whenFaceIsRecognizedKnownPersonShouldBeReturned() {
        Person knownPerson = new Person(faceId, "Name");
        ClientPredictionResponse expectedResponse = new ClientPredictionResponse(faceId, "Name", true, true);

        when(faceRecognitionService.predict(predictRequest)).thenReturn(faceId);
        when(personService.findById(faceId)).thenReturn(Optional.of(knownPerson));

        ClientPredictionResponse response = predictionUseCase.execute(imageRequest);

        assertEquals(expectedResponse.getId(), response.getId());
        assertEquals(expectedResponse.getName(), response.getName());
        assertEquals(expectedResponse.isFace(), response.isFace());
        assertEquals(expectedResponse.isKnownFace(), response.isKnownFace());
        verify(faceRecognitionService).predict(predictRequest);
        verify(personService).findById(faceId);
    }

    @Test
    void whenUnknownFaceThenUnknownPersonShouldBeReturned(){
        ClientPredictionResponse expectedResponse = new ClientPredictionResponse(null, "Unknown", true, false);

        when(faceRecognitionService.predict(predictRequest)).thenReturn(null);

        ClientPredictionResponse response = predictionUseCase.execute(imageRequest);

        assertEquals(expectedResponse.getId(), response.getId());
        assertEquals(expectedResponse.getName(), response.getName());
        assertEquals(expectedResponse.isFace(), response.isFace());
        assertEquals(expectedResponse.isKnownFace(), response.isKnownFace());
        verify(faceRecognitionService).predict(predictRequest);
        verify(personService, times(0)).findById(anyString());
    }

    @Test
    void whenFaceNotFoundThenIsFaceShouldBeFalse(){
        ClientPredictionResponse expectedResponse = new ClientPredictionResponse(null, "Unknown", false, false);
        RestClientResponseException notFoundException = new RestClientResponseException("Error message", 409, "NOT_FOUND", null, null, null);

        when(faceRecognitionService.predict(predictRequest)).thenThrow(notFoundException);

        ClientPredictionResponse response = predictionUseCase.execute(imageRequest);

        assertEquals(expectedResponse.getId(), response.getId());
        assertEquals(expectedResponse.getName(), response.getName());
        assertEquals(expectedResponse.isFace(), response.isFace());
        assertEquals(expectedResponse.isKnownFace(), response.isKnownFace());
        verify(faceRecognitionService).predict(predictRequest);
        verify(personService, times(0)).findById(anyString());
    }

    @Test
    void whenFaceRecognitionNotWorkingExceptionShouldBeThrown(){
        RestClientResponseException clientResponseException = new RestClientResponseException("Error message", 500, "INTERNAL_SERVER_ERROR", null, null, null);

        when(faceRecognitionService.predict(predictRequest)).thenThrow(clientResponseException);

        try {
            predictionUseCase.execute(imageRequest);
            fail("Should throw FaceRecognitionException");
        } catch (FaceRecognitionException e) {
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, e.getHttpStatus());
        }

        verify(faceRecognitionService).predict(predictRequest);
        verify(personService, times(0)).findById(anyString());
    }

    @Test
    void whenDatabaseMissingFaceIdThenExceptionShouldBeThrown(){
        Optional<Person> emptyOptional = Optional.empty();

        when(faceRecognitionService.predict(predictRequest)).thenReturn(faceId);
        when(personService.findById(faceId)).thenReturn(emptyOptional);

        assertThrows(MissingPersonInDbException.class, ()-> predictionUseCase.execute(imageRequest));
    }
}
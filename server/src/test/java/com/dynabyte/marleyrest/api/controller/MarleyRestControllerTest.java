package com.dynabyte.marleyrest.api.controller;

import com.dynabyte.marleyrest.api.exception.ApiExceptionHandler;
import com.dynabyte.marleyrest.api.exception.ImageEncodingException;
import com.dynabyte.marleyrest.api.exception.InvalidArgumentException;
import com.dynabyte.marleyrest.deletion.DeleteUseCase;
import com.dynabyte.marleyrest.deletion.exception.IdNotFoundException;
import com.dynabyte.marleyrest.personrecognition.request.ImageRequest;
import com.dynabyte.marleyrest.prediction.PredictionUseCase;
import com.dynabyte.marleyrest.prediction.exception.FaceRecognitionException;
import com.dynabyte.marleyrest.prediction.response.ClientPredictionResponse;
import com.dynabyte.marleyrest.registration.RegistrationUseCase;
import com.dynabyte.marleyrest.registration.exception.MissingPersonInDbException;
import com.dynabyte.marleyrest.registration.exception.PersonAlreadyInDbException;
import com.dynabyte.marleyrest.registration.exception.RegistrationException;
import com.dynabyte.marleyrest.registration.request.RegistrationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.ResourceAccessException;

import java.io.File;
import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MarleyRestControllerTest {

    @Mock
    private PredictionUseCase predictionUseCase;

    @Mock
    private RegistrationUseCase registrationUseCase;

    @Mock
    private DeleteUseCase deleteUseCase;

    @InjectMocks
    private MarleyRestController marleyRestController;

    private String faceId;
    private RegistrationRequest registrationRequest;
    private ImageRequest imageRequest;
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        faceId = "5fad4b9e1bbac38873e8cdbd";
        mockMvc = MockMvcBuilders.standaloneSetup(marleyRestController)
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
        try {
            ObjectMapper mapper = new ObjectMapper();
            File file = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "valid-registration-request-jpeg.json");
            registrationRequest = mapper.readValue(file, RegistrationRequest.class);
            file = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "valid-image-request.json");
            imageRequest = mapper.readValue(file, ImageRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void predictKnownFaceShouldGiveKnownPersonResponse() throws Exception {
        ClientPredictionResponse knownPersonResponse = new ClientPredictionResponse(faceId, "Valid Name", true, true);
        when(predictionUseCase.execute(imageRequest)).thenReturn(knownPersonResponse);

        mockMvc.perform(
                post("/predict")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(imageRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id").value(knownPersonResponse.getId()))
                .andExpect(jsonPath("name").value(knownPersonResponse.getName()))
                .andExpect(jsonPath("isFace").value(knownPersonResponse.isFace()))
                .andExpect(jsonPath("isKnownFace").value(knownPersonResponse.isKnownFace()));

        verify(predictionUseCase).execute(imageRequest);
    }

    @Test
    void predictUnknownFaceShouldGiveUnknownPersonResponse() throws Exception {
        ClientPredictionResponse knownPersonResponse = new ClientPredictionResponse(null, "Unknown", true, false);
        when(predictionUseCase.execute(imageRequest)).thenReturn(knownPersonResponse);

        mockMvc.perform(
                post("/predict")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(imageRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id").value(knownPersonResponse.getId()))
                .andExpect(jsonPath("name").value(knownPersonResponse.getName()))
                .andExpect(jsonPath("isFace").value(knownPersonResponse.isFace()))
                .andExpect(jsonPath("isKnownFace").value(knownPersonResponse.isKnownFace()));

        verify(predictionUseCase).execute(imageRequest);
    }

    @Test
    void predictNoFaceShouldGiveNoFaceResponse() throws Exception {
        ClientPredictionResponse knownPersonResponse = new ClientPredictionResponse(null, "Unknown", false, false);
        when(predictionUseCase.execute(imageRequest)).thenReturn(knownPersonResponse);

        mockMvc.perform(
                post("/predict")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(imageRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id").value(knownPersonResponse.getId()))
                .andExpect(jsonPath("name").value(knownPersonResponse.getName()))
                .andExpect(jsonPath("isFace").value(knownPersonResponse.isFace()))
                .andExpect(jsonPath("isKnownFace").value(knownPersonResponse.isKnownFace()));

        verify(predictionUseCase).execute(imageRequest);
    }

    @Test
    void registerValidRequestShouldHaveStatusOK() throws Exception {
        mockMvc.perform(
                post("/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(registrationRequest)))
                .andExpect(status().isOk());

        verify(registrationUseCase).execute(registrationRequest);
    }

    @Test
    void deleteShouldHaveStatusOk() throws Exception {
        mockMvc.perform(
                delete("/delete/{id}", faceId))
                .andExpect(status().isOk());

        verify(deleteUseCase).execute(faceId);
    }

    @Test
    void predictWithoutRequestBodyShouldThrowException() throws Exception {
        mockMvc.perform(
                post("/predict"))
                .andExpect(status().isBadRequest());
        verify(predictionUseCase, never()).execute(any());
    }

    @Test
    void registerWithoutRequestBodyShouldThrowException() throws Exception {
        mockMvc.perform(
                post("/register"))
                .andExpect(status().isBadRequest());

        verify(registrationUseCase, never()).execute(any());
    }

    @Test
    void predictWithInvalidImageShouldBeBadRequest() throws Exception {
        when(predictionUseCase.execute(imageRequest))
                .thenThrow(new ImageEncodingException("Image is not in base64 format!"));

        mockMvc.perform(
                post("/predict")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(imageRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("exceptionClass").value(ImageEncodingException.class.getSimpleName()))
                .andExpect(jsonPath("message").value("Image is not in base64 format!"))
                .andExpect(jsonPath("httpStatus").value("BAD_REQUEST"))
                .andExpect(jsonPath("timestamp").isNotEmpty());

        verify(predictionUseCase).execute(imageRequest);
    }

    @Test
    void registerWithEmptyNameShouldBeBadRequest() throws Exception {
        RegistrationRequest noNameRequest = new RegistrationRequest("", registrationRequest.getImages());
        doThrow(new InvalidArgumentException("name cannot be null!"))
                .when(registrationUseCase).execute(noNameRequest);

        mockMvc.perform(
                post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(noNameRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("exceptionClass").value(InvalidArgumentException.class.getSimpleName()))
                .andExpect(jsonPath("message").value("name cannot be null!"))
                .andExpect(jsonPath("httpStatus").value("BAD_REQUEST"))
                .andExpect(jsonPath("timestamp").isNotEmpty());

        verify(registrationUseCase).execute(noNameRequest);
    }

    @Test
    void faceRecognitionErrorPredictionRequestsShouldHaveAppropriateErrorCode() throws Exception {
        String errorMessage = "Something went wrong with the face recognition API";
        HttpStatus expectedHttpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        when(predictionUseCase.execute(imageRequest))
                .thenThrow(new FaceRecognitionException(errorMessage, expectedHttpStatus));

        mockMvc.perform(
                post("/predict")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(imageRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("exceptionClass").value(FaceRecognitionException.class.getSimpleName()))
                .andExpect(jsonPath("message").value(errorMessage))
                .andExpect(jsonPath("httpStatus").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("timestamp").isNotEmpty());

        verify(predictionUseCase).execute(imageRequest);


        doThrow(new FaceRecognitionException(errorMessage, expectedHttpStatus))
            .when(registrationUseCase).execute(registrationRequest);

        mockMvc.perform(
                post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(registrationRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("exceptionClass").value(FaceRecognitionException.class.getSimpleName()))
                .andExpect(jsonPath("message").value(errorMessage))
                .andExpect(jsonPath("httpStatus").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("timestamp").isNotEmpty());

        verify(registrationUseCase).execute(registrationRequest);
    }

    @Test
    void missingPersonPredictionRequestShouldBeNotAcceptable() throws Exception {
        String errorMessage = "Found faceId in face recognition API but no matching person in the database";

        when(predictionUseCase.execute(imageRequest))
                .thenThrow(new MissingPersonInDbException(errorMessage));

        mockMvc.perform(
                post("/predict")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(imageRequest)))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("exceptionClass").value(MissingPersonInDbException.class.getSimpleName()))
                .andExpect(jsonPath("message").value(errorMessage))
                .andExpect(jsonPath("httpStatus").value("NOT_ACCEPTABLE"))
                .andExpect(jsonPath("timestamp").isNotEmpty());

        verify(predictionUseCase).execute(imageRequest);


        doThrow(new MissingPersonInDbException(errorMessage))
            .when(registrationUseCase).execute(registrationRequest);

        mockMvc.perform(
                post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(registrationRequest)))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("exceptionClass").value(MissingPersonInDbException.class.getSimpleName()))
                .andExpect(jsonPath("message").value(errorMessage))
                .andExpect(jsonPath("httpStatus").value("NOT_ACCEPTABLE"))
                .andExpect(jsonPath("timestamp").isNotEmpty());

        verify(registrationUseCase).execute(registrationRequest);
    }

    @Test
    void personAlreadyInDbShouldBeNotAcceptable() throws Exception {
        String errorMessage = "Found faceId in face recognition API but no matching person in the database";
        doThrow(new PersonAlreadyInDbException(errorMessage))
                .when(registrationUseCase).execute(registrationRequest);

        mockMvc.perform(
                post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(registrationRequest)))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("exceptionClass").value(PersonAlreadyInDbException.class.getSimpleName()))
                .andExpect(jsonPath("message").value(errorMessage))
                .andExpect(jsonPath("httpStatus").value("NOT_ACCEPTABLE"))
                .andExpect(jsonPath("timestamp").isNotEmpty());

        verify(registrationUseCase).execute(registrationRequest);
    }

    @Test
    void registrationExceptionShouldBeNotAcceptable() throws Exception {
        String errorMessage = "Could not register any images or save person to Db";
        doThrow(new RegistrationException(errorMessage))
                .when(registrationUseCase).execute(registrationRequest);

        mockMvc.perform(
                post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(registrationRequest)))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("exceptionClass").value(RegistrationException.class.getSimpleName()))
                .andExpect(jsonPath("message").value(errorMessage))
                .andExpect(jsonPath("httpStatus").value("NOT_ACCEPTABLE"))
                .andExpect(jsonPath("timestamp").isNotEmpty());

        verify(registrationUseCase).execute(registrationRequest);
    }

    @Test
    void idNotFoundDeleteRequestShouldNotBeNotAcceptable() throws Exception {
        String errorMessage = "Could not delete! Id not found: " + faceId;
        doThrow(new IdNotFoundException(errorMessage))
                .when(deleteUseCase).execute(faceId);

        mockMvc.perform(
                delete("/delete/{faceId}", faceId))
                    .andExpect(status().isNotAcceptable())
                    .andExpect(jsonPath("exceptionClass").value(IdNotFoundException.class.getSimpleName()))
                    .andExpect(jsonPath("message").value(errorMessage))
                    .andExpect(jsonPath("httpStatus").value("NOT_ACCEPTABLE"))
                    .andExpect(jsonPath("timestamp").isNotEmpty());
    }

    @Test
    void resourceAccessExceptionShouldBeServiceUnavailable() throws Exception {
        String errorMessage = "Face recognition service unavailable";
        ResourceAccessException resourceAccessException =
                new ResourceAccessException(errorMessage);

        doThrow(resourceAccessException)
                .when(registrationUseCase).execute(registrationRequest);

        mockMvc.perform(
                post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(registrationRequest)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("exceptionClass").value(ResourceAccessException.class.getSimpleName()))
                .andExpect(jsonPath("message").value(errorMessage))
                .andExpect(jsonPath("httpStatus").value("SERVICE_UNAVAILABLE"))
                .andExpect(jsonPath("timestamp").isNotEmpty());

    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
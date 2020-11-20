package com.dynabyte.marleyrest.api.util;

import com.dynabyte.marleyrest.api.exception.ImageEncodingException;
import com.dynabyte.marleyrest.api.exception.InvalidArgumentException;
import com.dynabyte.marleyrest.personrecognition.request.ImageRequest;
import com.dynabyte.marleyrest.registration.request.RegistrationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class RequestUtilTest {

    private static RegistrationRequest validJpegRegistrationRequest;
    private static RegistrationRequest validPngRegistrationRequest;
    private static RegistrationRequest oneInvalidImageRegistrationRequest;

    @BeforeAll
    static void initializeRequestsFromJson(){
        ObjectMapper mapper = new ObjectMapper();
        try {
            File file = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "valid-registration-request-jpeg.json");
            validJpegRegistrationRequest = mapper.readValue(file, RegistrationRequest.class);

            file = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "valid-registration-request-png.json");
            validPngRegistrationRequest = mapper.readValue(file, RegistrationRequest.class);

            file = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "invalid-image-registration-request.json");
            oneInvalidImageRegistrationRequest = mapper.readValue(file, RegistrationRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void nullNameShouldThrowInvalidArgumentException(){
        RegistrationRequest nullNameRegistrationRequest = new RegistrationRequest();
        nullNameRegistrationRequest.setImages(validJpegRegistrationRequest.getImages());
        assertThrows(
                InvalidArgumentException.class,
                () -> RequestUtil.validateAndPrepareRegistrationRequest(nullNameRegistrationRequest)
        );
    }

    @Test
    void emptyNameShouldThrowInvalidArgumentException(){
        RegistrationRequest emptyNameRegistrationRequest = new RegistrationRequest();
        emptyNameRegistrationRequest.setName("");
        emptyNameRegistrationRequest.setImages(validJpegRegistrationRequest.getImages());
        assertThrows(
                InvalidArgumentException.class,
                () -> RequestUtil.validateAndPrepareRegistrationRequest(emptyNameRegistrationRequest)
        );
    }

    @Test
    void nullImageShouldThrowInvalidArgumentException(){
        ImageRequest imageRequest = new ImageRequest(null);

        assertThrows(
                InvalidArgumentException.class,
                ()-> RequestUtil.validateAndPreparePredictionRequest(imageRequest)
        );
    }

    @Test
    void nullImagesListShouldThrowInvalidArgumentException(){
        RegistrationRequest nullImagesRegistrationRequest = new RegistrationRequest();
        nullImagesRegistrationRequest.setName("Valid Name");
        assertThrows(
                InvalidArgumentException.class,
                () -> RequestUtil.validateAndPrepareRegistrationRequest(nullImagesRegistrationRequest)
        );
    }
    @Test
    void emptyImagesListShouldThrowInvalidArgumentException(){
        RegistrationRequest emptyImagesRegistrationRequest = new RegistrationRequest();
        emptyImagesRegistrationRequest.setName("Valid Name");
        emptyImagesRegistrationRequest.setImages(Collections.emptyList());
        assertThrows(
                InvalidArgumentException.class,
                () -> RequestUtil.validateAndPrepareRegistrationRequest(emptyImagesRegistrationRequest)
        );
    }

    @Test
    void validRegistrationRequestShouldBeValid(){
        RequestUtil.validateAndPrepareRegistrationRequest(validJpegRegistrationRequest);
        RequestUtil.validateAndPrepareRegistrationRequest(validPngRegistrationRequest);
    }

    @Test
    void validPredictionRequestShouldBeValid(){
        String validBase64Image = validJpegRegistrationRequest.getImages().get(0);
        ImageRequest validImageRequest = new ImageRequest(validBase64Image);
        ImageRequest validatedImageRequest = RequestUtil.validateAndPreparePredictionRequest(validImageRequest);
        assertTrue(validatedImageRequest.getImage().startsWith("/9j/4QAwRXhpZgAASUkqAAgAAAABAJiCAgAMAAAAGgAAAAAAAABSb3kgUm9jaGxpbgAAAP"));
    }

    @Test
    void invalidImageRegistrationShouldThrowException(){
        assertThrows(
                ImageEncodingException.class,
                () -> RequestUtil.validateAndPrepareRegistrationRequest(oneInvalidImageRegistrationRequest)
        );
    }

}
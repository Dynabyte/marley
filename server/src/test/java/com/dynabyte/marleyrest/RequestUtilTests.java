package com.dynabyte.marleyrest;

import com.dynabyte.marleyrest.api.exception.InvalidArgumentException;
import com.dynabyte.marleyrest.personrecognition.request.ImageRequest;
import com.dynabyte.marleyrest.registration.request.RegistrationRequest;
import com.dynabyte.marleyrest.api.util.RequestUtil;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;


public class RequestUtilTests {

    //TODO import from file valid images
    private final List<String> images = Arrays.asList("image1", "image2");

    @Test
    void nullNameShouldThrowInvalidArgumentException(){
        RegistrationRequest noNameRegistrationRequest = new RegistrationRequest();
        noNameRegistrationRequest.setImages(images);
        assertThrows(
                InvalidArgumentException.class,
                () -> RequestUtil.validateAndPrepareRegistrationRequest(noNameRegistrationRequest)
        );
    }

    @Test
    void emptyNameShouldThrowInvalidArgumentException(){
        RegistrationRequest noNameRegistrationRequest = new RegistrationRequest();
        noNameRegistrationRequest.setImages(images);
        assertThrows(
                InvalidArgumentException.class,
                () -> RequestUtil.validateAndPrepareRegistrationRequest(noNameRegistrationRequest)
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
        RegistrationRequest noImagesRegistrationRequest = new RegistrationRequest();
        noImagesRegistrationRequest.setName("Valid Name");
        assertThrows(
                InvalidArgumentException.class,
                () -> RequestUtil.validateAndPrepareRegistrationRequest(noImagesRegistrationRequest)
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
}

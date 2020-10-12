package com.dynabyte.marleyjavarestapi;

import com.dynabyte.marleyjavarestapi.facerecognition.exception.InvalidArgumentException;
import com.dynabyte.marleyjavarestapi.facerecognition.to.request.ImageRequest;
import com.dynabyte.marleyjavarestapi.facerecognition.to.request.RegistrationRequest;
import com.dynabyte.marleyjavarestapi.facerecognition.utility.Validation;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;


public class ValidationTests {

    //TODO import from file valid images
    private final List<String> images = Arrays.asList("image1", "image2");

    @Test
    void nullNameShouldThrowInvalidArgumentException(){
        RegistrationRequest noNameRegistrationRequest = new RegistrationRequest();
        noNameRegistrationRequest.setImages(images);
        assertThrows(
                InvalidArgumentException.class,
                () -> Validation.validateRegistrationRequest(noNameRegistrationRequest)
        );
    }

    @Test
    void emptyNameShouldThrowInvalidArgumentException(){
        RegistrationRequest noNameRegistrationRequest = new RegistrationRequest();
        noNameRegistrationRequest.setImages(images);
        assertThrows(
                InvalidArgumentException.class,
                () -> Validation.validateRegistrationRequest(noNameRegistrationRequest)
        );
    }

    @Test
    void nullImageShouldThrowInvalidArgumentException(){
        ImageRequest imageRequest = new ImageRequest(null);

        assertThrows(
                InvalidArgumentException.class,
                ()-> Validation.validateImageRequest(imageRequest)
        );
    }

    @Test
    void nullImagesListShouldThrowInvalidArgumentException(){
        RegistrationRequest noImagesRegistrationRequest = new RegistrationRequest();
        noImagesRegistrationRequest.setName("Valid Name");
        assertThrows(
                InvalidArgumentException.class,
                () -> Validation.validateRegistrationRequest(noImagesRegistrationRequest)
        );
    }
    @Test
    void emptyImagesListShouldThrowInvalidArgumentException(){
        RegistrationRequest emptyImagesRegistrationRequest = new RegistrationRequest();
        emptyImagesRegistrationRequest.setName("Valid Name");
        emptyImagesRegistrationRequest.setImages(Collections.emptyList());
        assertThrows(
                InvalidArgumentException.class,
                () -> Validation.validateRegistrationRequest(emptyImagesRegistrationRequest)
        );
    }
}

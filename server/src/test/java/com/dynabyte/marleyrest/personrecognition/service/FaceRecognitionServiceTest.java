package com.dynabyte.marleyrest.personrecognition.service;

import com.dynabyte.marleyrest.personrecognition.request.ImageRequest;
import com.dynabyte.marleyrest.personrecognition.response.FaceRecognitionResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
class FaceRecognitionServiceTest {

    @Mock
    RestTemplate restTemplate;

    @InjectMocks
    FaceRecognitionService faceRecognitionService;

    private static String faceId;
    private static ImageRequest imageRequest;
    private static String faceRecognitionURL;
    private static FaceRecognitionResponse faceRecognitionResponse;

//    @BeforeClass
//    public static void beforeClass() {
//        faceRecognitionURL = "http://localhost:5000/face-recognition/";
//        System.getenv().put("FACE_REC_URL", faceRecognitionURL);
//    }
//
//    // Optionally:
//    @AfterClass
//    public static void afterClass() {
//        System.getenv().remove("FACE_REC_URL");
//    }

    @BeforeAll
    static void setUp() {
        faceId = "5fad4b9e1bbac38873e8cdbd";
        faceRecognitionResponse = new FaceRecognitionResponse();
        faceRecognitionResponse.setFaceId(faceId);
        try {
            ObjectMapper mapper = new ObjectMapper();
            File file = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "valid-image-request.json");
            imageRequest = mapper.readValue(file, ImageRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    void setProperties(){

    }

    @Test
    void predict() {
        //TODO inject @Value and test with proper value
        when(restTemplate.postForObject(null + "predict", imageRequest, FaceRecognitionResponse.class))
                .thenReturn(faceRecognitionResponse);
        String predictedFaceId = faceRecognitionService.predict(imageRequest);
        assertEquals(faceId, predictedFaceId);
//        verify(restTemplate).postForEntity(faceRecognitionURL + "predict", imageRequest, FaceRecognitionResponse.class);
    }

    @Test
    void postLabel() {
    }

    @Test
    void putLabel() {
    }

    @Test
    void delete() {
    }
}
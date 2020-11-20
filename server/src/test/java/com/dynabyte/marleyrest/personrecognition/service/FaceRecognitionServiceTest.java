package com.dynabyte.marleyrest.personrecognition.service;

import com.dynabyte.marleyrest.personrecognition.request.ImageRequest;
import com.dynabyte.marleyrest.personrecognition.response.FaceRecognitionResponse;
import com.dynabyte.marleyrest.registration.request.LabelPutRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @BeforeAll
    static void setUp() {
        faceId = "5fad4b9e1bbac38873e8cdbd";
        faceRecognitionURL = "http://localhost:5000/face-recognition/";
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
        ReflectionTestUtils.setField(faceRecognitionService, "faceRecognitionURL", faceRecognitionURL);
    }

    @Test
    void predict() {
        when(restTemplate.postForObject(faceRecognitionURL + "predict", imageRequest, FaceRecognitionResponse.class))
                .thenReturn(faceRecognitionResponse);
        String predictedFaceId = faceRecognitionService.predict(imageRequest);
        assertEquals(faceId, predictedFaceId);
//        verify(restTemplate).postForEntity(faceRecognitionURL + "predict", imageRequest, FaceRecognitionResponse.class);
    }

    @Test
    void postLabel() {
        when(restTemplate.postForObject(faceRecognitionURL + "label", imageRequest, FaceRecognitionResponse.class))
                .thenReturn(faceRecognitionResponse);
        String labeledFaceId = faceRecognitionService.postLabel(imageRequest);
        assertEquals(faceId, labeledFaceId);
    }

    @Test
    void putLabel() {
        LabelPutRequest labelPutRequest = new LabelPutRequest(imageRequest.getImage(), faceId);
        faceRecognitionService.putLabel(labelPutRequest);
        verify(restTemplate).put(faceRecognitionURL + "label/" + labelPutRequest.getFaceId(), labelPutRequest);
    }

    @Test
    void delete() {
        faceRecognitionService.delete(faceId);
        verify(restTemplate).delete(faceRecognitionURL + "delete/" + faceId);
    }
}
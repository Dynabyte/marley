package com.dynabyte.marleyrest.registration;

import com.dynabyte.marleyrest.personrecognition.model.Person;
import com.dynabyte.marleyrest.personrecognition.request.ImageRequest;
import com.dynabyte.marleyrest.personrecognition.service.FaceRecognitionService;
import com.dynabyte.marleyrest.personrecognition.service.PersonService;
import com.dynabyte.marleyrest.registration.request.LabelPutRequest;
import com.dynabyte.marleyrest.registration.request.RegistrationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationUseCaseTest {

    @Mock
    FaceRecognitionService faceRecognitionService;

    @Mock
    PersonService personService;

    @InjectMocks
    RegistrationUseCase registrationUseCase;

    private String faceId;
    private RegistrationRequest registrationRequest;
    private String image1;
    private String image2;
    private String image3;
    private ImageRequest imageRequest1;
    private ImageRequest imageRequest2;
    private ImageRequest imageRequest3;

    @BeforeEach
    void setUp() {
        faceId = "5fad4b9e1bbac38873e8cdbd";
        try {
            ObjectMapper mapper = new ObjectMapper();
            File file = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "valid-registration-request-jpeg.json");
            registrationRequest = mapper.readValue(file, RegistrationRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        image1 = getTrimmedImage(registrationRequest.getImages().get(0));
        image2 = getTrimmedImage(registrationRequest.getImages().get(1));
        image3 = getTrimmedImage(registrationRequest.getImages().get(2));
        imageRequest1 = new ImageRequest(image1);
        imageRequest2 = new ImageRequest(image2);
        imageRequest3 = new ImageRequest(image3);
    }

    private String getTrimmedImage(String image){
        String base64DescriptionTag = "data:image/jpeg;base64,";
        return image.replace(base64DescriptionTag, "");
    }

    @Test
    void validRequestShouldRegisterFaceAndPerson() {
        when(faceRecognitionService.predict(imageRequest1)).thenReturn(null);
        when(faceRecognitionService.postLabel(imageRequest1)).thenReturn(faceId);

        registrationUseCase.execute(registrationRequest);

        verify(faceRecognitionService).predict(imageRequest1);
        verify(faceRecognitionService, never()).predict(imageRequest2);
        verify(faceRecognitionService, never()).predict(imageRequest3);

        verify(faceRecognitionService).postLabel(imageRequest1);
        verify(faceRecognitionService, never()).postLabel(imageRequest2);
        verify(faceRecognitionService, never()).postLabel(imageRequest3);

        verify(faceRecognitionService, never()).putLabel(new LabelPutRequest(image1, anyString()));
        verify(faceRecognitionService).putLabel(new LabelPutRequest(image2, faceId));
        verify(faceRecognitionService).putLabel(new LabelPutRequest(image3, faceId));

        verify(personService, never()).findById(anyString());
        verify(personService).save(new Person(faceId, registrationRequest.getName()));
    }

    //TODO verify various error cases
}
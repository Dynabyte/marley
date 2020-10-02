package com.dynabyte.marleyjavarestapi.facerecognition.service;

import com.dynabyte.marleyjavarestapi.facerecognition.to.LabelRequest;
import com.dynabyte.marleyjavarestapi.facerecognition.to.PredictionRequest;
import com.dynabyte.marleyjavarestapi.facerecognition.to.PythonResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FaceRecognitionService {

    private final String faceRecognitionURL = "http://localhost:5000/";
    private final RestTemplate restTemplate = new RestTemplate();
    HttpHeaders httpHeaders;

//    public FaceRecognitionService() {
//        // build restTemplate
////        this.restTemplate = new RestTemplate();
////        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
////        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
////        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
////        messageConverters.add(converter);
////        this.restTemplate.setMessageConverters(messageConverters);
//
//        // create headers
////        httpHeaders = new HttpHeaders();
////        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
////        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//    }

    public PythonResponse predict(PredictionRequest predictionRequest){
        return restTemplate.postForObject(faceRecognitionURL + "predict", predictionRequest, PythonResponse.class);
    }

    public PythonResponse label(LabelRequest labelRequest){
        return  restTemplate.postForObject(faceRecognitionURL + "label", labelRequest, PythonResponse.class);
    }
}

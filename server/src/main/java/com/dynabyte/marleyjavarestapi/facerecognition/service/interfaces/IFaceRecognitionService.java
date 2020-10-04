package com.dynabyte.marleyjavarestapi.facerecognition.service.interfaces;

import com.dynabyte.marleyjavarestapi.facerecognition.to.request.LabelRequest;
import com.dynabyte.marleyjavarestapi.facerecognition.to.request.PredictionRequest;
import com.dynabyte.marleyjavarestapi.facerecognition.to.response.PythonResponse;

public interface IFaceRecognitionService {

    PythonResponse predict(PredictionRequest predictionRequest);

    PythonResponse label(LabelRequest labelRequest);
}

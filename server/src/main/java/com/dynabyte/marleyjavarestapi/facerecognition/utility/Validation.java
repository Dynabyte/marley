package com.dynabyte.marleyjavarestapi.facerecognition.utility;

import com.dynabyte.marleyjavarestapi.facerecognition.to.LabelRequest;
import com.dynabyte.marleyjavarestapi.facerecognition.to.PredictionRequest;
import com.dynabyte.marleyjavarestapi.facerecognition.exception.ImageEncodingException;
import com.dynabyte.marleyjavarestapi.facerecognition.exception.MissingArgumentException;

import java.util.Base64;

/**
 * Utility class for validating various data using static void/boolean methods. May include helper sub methods.
 */
public class Validation {

    public static void validateRequest(PredictionRequest predictionRequest){
        validateImageNotNull(predictionRequest.getImage());

        if(predictionRequest.getImage().startsWith("data:image/jpeg;base64,")){
            predictionRequest.setImage(predictionRequest.getImage().substring(23));
        }
        validateBase64Image(predictionRequest.getImage());
    }

    public static void validateRequest(LabelRequest labelRequest){
        validateImageNotNull(labelRequest.getImage());

        if(labelRequest.getImage().startsWith("data:image/jpeg;base64,")){
            labelRequest.setImage(labelRequest.getImage().substring(23));
        }
        validateBase64Image(labelRequest.getImage());
    }

    private static void validateImageNotNull(String base64Image){
        if(base64Image == null){
            throw new MissingArgumentException("image cannot be null. Must be an image in base64 format");
        }
    }

    private static void validateBase64Image(String base64Image){
        if (!isBase64Encoded(base64Image)){
            throw new ImageEncodingException("Image is not in base64 format!");
        }
    }



    /**
     * Returns true if input is a base64 encoded string. Not very accurate as some strings can match that of an
     * encoded strings and can therefore be decoded but it needs to have a length divisible by 4 and pass a decoding
     * check. Additionally a limit of at least 1000 characters is enforced to prevent short strings from being accepted.
     * @param input the string to be checked for base64 encoding
     * @return true if base64 encoded string, false otherwise
     */
    public static boolean isBase64Encoded(String input) {

        /*  Since the decoder test can sometimes return true even for regular strings, short strings are dismissed and
            encoded string length will always be divisible by 4, which further improves accuracy a bit
         */
        if(input.length()%4 != 0 || input.length() < 1000){
        return false;
    }

    Base64.Decoder decoder = Base64.getDecoder();

    try {
        decoder.decode(input);
        return true;
    } catch (IllegalArgumentException e) {
        return false;
    }
}
}

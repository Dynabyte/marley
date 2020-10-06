package com.dynabyte.marleyjavarestapi.facerecognition.utility;

import com.dynabyte.marleyjavarestapi.facerecognition.exception.ImageEncodingException;
import com.dynabyte.marleyjavarestapi.facerecognition.exception.MissingArgumentException;
import com.dynabyte.marleyjavarestapi.facerecognition.to.request.ImageRequest;
import com.dynabyte.marleyjavarestapi.facerecognition.to.request.RegistrationRequest;

import java.util.Base64;

/**
 * Utility class for validating various data using static void/boolean methods. Includes helper sub methods.
 */
public class Validation {

    public static void validateRegistrationRequest(RegistrationRequest registrationRequest){
        //TODO validate name or is that handled with @Size?
       if(registrationRequest.getName() == null){
           throw new MissingArgumentException("name cannot be null!");
       }
       if(registrationRequest.getImages() == null){
           throw new MissingArgumentException("images missing! Must be included in base64format");
       }
       registrationRequest.getImages().forEach(Validation::validateImage);
    }

    public static void validateImageRequest(ImageRequest imageRequest){
        imageRequest.setImage(validateImage(imageRequest.getImage()));
    }

    public static String validateImage(String base64Image){
        validateImageNotNull(base64Image);
        base64Image = base64Image.replace("data:image/jpeg;base64,", "");
        validateIsBase64(base64Image);
        return base64Image;
    }

    private static void validateImageNotNull(String base64Image){
        if(base64Image == null){
            throw new MissingArgumentException("image cannot be null. Must be an image in base64 format");
        }
    }

    private static void validateIsBase64(String base64Image){
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

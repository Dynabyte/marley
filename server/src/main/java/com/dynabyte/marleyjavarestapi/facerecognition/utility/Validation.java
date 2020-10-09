package com.dynabyte.marleyjavarestapi.facerecognition.utility;

import ch.qos.logback.core.joran.action.AppenderRefAction;
import com.dynabyte.marleyjavarestapi.facerecognition.exception.ImageEncodingException;
import com.dynabyte.marleyjavarestapi.facerecognition.exception.MissingArgumentException;
import com.dynabyte.marleyjavarestapi.facerecognition.exception.RegistrationException;
import com.dynabyte.marleyjavarestapi.facerecognition.to.request.ImageRequest;
import com.dynabyte.marleyjavarestapi.facerecognition.to.request.RegistrationRequest;
import com.dynabyte.marleyjavarestapi.facerecognition.to.request.SingleImageRegistrationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for validating various data using static void/boolean methods. Includes helper sub methods.
 */
public class Validation {

    private static final Logger LOGGER = LoggerFactory.getLogger(Validation.class);

    public static void validateRegistrationRequest(RegistrationRequest registrationRequest){
        //TODO validate name or is that handled with @Size?
        LOGGER.info("Validating registration request");

        validateName(registrationRequest.getName());
        validateImagesNotNull(registrationRequest.getImages());

        List<String> validatedBase64Images = registrationRequest.getImages()
                .stream()
                .map(Validation::validateImage)
                .collect(Collectors.toList());
        registrationRequest.setImages(validatedBase64Images);

        LOGGER.info("Registration request validated");
    }

    public static void validateSingleImageRegistrationRequest(SingleImageRegistrationRequest request){
        validateName(request.getName());
        validateImageRequest(request);
    }

    public static void validateImageRequest(ImageRequest imageRequest){
        LOGGER.info("Validating image request");
        imageRequest.setImage(validateImage(imageRequest.getImage()));
        LOGGER.info("Image request validated");
    }

    public static String validateImage(String base64Image){
        LOGGER.info("Validating image");
        validateImageNotNull(base64Image);
        base64Image = base64Image.replace("data:image/jpeg;base64,", "");
        validateIsBase64(base64Image);
        LOGGER.info("Image validated. Starts with: " + base64Image.substring(0, 25));
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
    private static boolean isBase64Encoded(String input) {
        LOGGER.info("Checking if image is base64 encoded");

        /*  Since the decoder test can sometimes return true even for regular strings, short strings are dismissed and
            encoded string length will always be divisible by 4, which further improves accuracy a bit
         */
        if(input.length()%4 != 0 || input.length() < 1000){
            return false;
        }

        Base64.Decoder decoder = Base64.getDecoder();

        try {
            decoder.decode(input);
            LOGGER.info("Image is in base64 format");
            return true;
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Image is not in base64 format");
            return false;
        }
    }

    //TODO switch to javax.validation instead?
    private static void validateName(String name) {
        LOGGER.info("Validating name");
        if(name == null){
            String warningMessage = "name cannot be null!";
            LOGGER.warn(warningMessage);
            throw new MissingArgumentException(warningMessage);
        }
        if(name.length() < 1 || name.length() > 50){
            String warningMessage = "name must be between 1 and 50 characters!";
            LOGGER.warn(warningMessage);
            throw new RegistrationException(warningMessage);
        }
        LOGGER.info("Name validated");
    }

    private static void validateImagesNotNull(List<String> images) {
        LOGGER.info("Validating images not null");
        if(images == null || images.isEmpty()){
            String warningMessage = "images missing! Must be included in base64format";
            LOGGER.warn(warningMessage);
            throw new MissingArgumentException(warningMessage);
        }
        LOGGER.info("Images validated as not null");
    }
}

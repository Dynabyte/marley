package com.dynabyte.marleyjavarestapi.facerecognition.utility;

import com.dynabyte.marleyjavarestapi.facerecognition.exception.ImageEncodingException;
import com.dynabyte.marleyjavarestapi.facerecognition.exception.InvalidArgumentException;
import com.dynabyte.marleyjavarestapi.facerecognition.to.request.ImageRequest;
import com.dynabyte.marleyjavarestapi.facerecognition.to.request.RegistrationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for validating various requests and other relevant data. Includes submethods for clarity.
 * Exceptions are thrown if a input is determined to be invalid.
 */
public class Validation {

    private static final Logger LOGGER = LoggerFactory.getLogger(Validation.class);


    /**
     * Validates image request used for predicting an image
     * @param imageRequest Request containing a single base64 image string
     */
    public static void validateImageRequest(ImageRequest imageRequest){
        LOGGER.info("Validating image request");
        validateImageNotNull(imageRequest.getImage());
        imageRequest.setImage(validateImageAndRemoveDescriptorTag(imageRequest.getImage()));
        LOGGER.info("Image request validated");
    }

    /**
     * Validates a request to register a new person and trims potential descriptor tags in the images list
     * @param registrationRequest The request to be validated
     */
    public static void validateRegistrationRequest(RegistrationRequest registrationRequest){
        LOGGER.info("Validating registration request");

        validateName(registrationRequest.getName());
        validateImagesNotNullOrEmpty(registrationRequest.getImages());

        List<String> validatedBase64Images = validateImagesAndRemoveDescriptorTags(registrationRequest.getImages());
        registrationRequest.setImages(validatedBase64Images);

        LOGGER.info("Registration request validated");
    }

    /**
     * Validates a list of base64 image strings and removes descriptor tag if present. Throws error if not valid
     * @param images List of base 64 image strings
     * @return Validated list of base64 image strings after removing descriptor tags
     */
    private static List<String> validateImagesAndRemoveDescriptorTags(List<String> images) {
        return images
                .stream()
                .map(Validation::validateImageAndRemoveDescriptorTag)
                .collect(Collectors.toList());
    }


    /**
     * Validates name to not be null and to fit size requirements
     * @param name String to be validated
     */
    private static void validateName(String name) {
        LOGGER.info("Validating name");
        if(name == null){
            String warningMessage = "name cannot be null!";
            LOGGER.warn(warningMessage);
            throw new InvalidArgumentException(warningMessage);
        }
        if(name.length() < 1 || name.length() > 50){
            String warningMessage = "name must be between 1 and 50 characters!";
            LOGGER.warn(warningMessage);
            throw new InvalidArgumentException(warningMessage);
        }
        LOGGER.info("Name validated");
    }

    /**
     * Validates that a list of strings is not null
     * @param images The list of strings to be validated
     */
    private static void validateImagesNotNullOrEmpty(List<String> images) {
        LOGGER.info("Validating images not null or empty");
        if(images == null || images.isEmpty()){
            String warningMessage = "images array missing or empty! Must be included in base64format";
            LOGGER.warn(warningMessage);
            throw new InvalidArgumentException(warningMessage);
        }
        LOGGER.info("Images validated as not null or empty");
    }


    //TODO split in two methods or keep current method?
    /**
     * Validates a single base64 image string and removes descriptor tag if present
     * @param base64Image Base64 image string to be validated
     * @return Validated base64 image string without descriptor tag
     */
    private static String validateImageAndRemoveDescriptorTag(String base64Image){
        LOGGER.info("Validating image");
        validateImageNotNull(base64Image);
        base64Image = removeBase64DescriptorTag(base64Image);
        validateIsBase64(base64Image);
        LOGGER.info("Image validated. Starts with: " + base64Image.substring(0, 25));
        return base64Image;
    }

    /**
     * Validates that base64 image string is not null
     * @param base64Image Base64 image string to checked for null
     */
    private static void validateImageNotNull(String base64Image){
        if(base64Image == null){
            String warningMessage = "image cannot be null. Must be an image in base64 format";
            LOGGER.warn(warningMessage);
            throw new InvalidArgumentException(warningMessage);
        }
    }

    /**
     * Removes descriptor tag for png or jpeg if present
     * @param base64Image Base64 image string to remove descriptor tag from
     * @return Base64 image string without descriptor tag
     */
    private static String removeBase64DescriptorTag(String base64Image) {
        LOGGER.info("Removing image descriptor tag if present");
        return base64Image
                .replace("data:image/jpeg;base64,", "")
                .replace("data:image/png;base64,", "");
    }

    /**
     * Validates whether a string can be decoded from base64
     * @param base64Image Base64 image string to be validated
     */
    private static void validateIsBase64(String base64Image){
        LOGGER.info("Checking if image is base64 encoded");
        if (!isBase64Encoded(base64Image)){
            String warningMessage = "Image is not in base64 format!";
            LOGGER.warn(warningMessage);
            throw new ImageEncodingException(warningMessage);
        }
        LOGGER.info("Image is in base64 format");
    }

    /**
     * Returns true if input is a base64 encoded string. Not very accurate as some strings can match that of an
     * encoded strings and can therefore be decoded but it needs to have a length divisible by 4 and pass a decoding
     * check. Additionally a limit of at least 1000 characters is enforced to prevent short strings from being accepted.
     * @param input the string to be checked for base64 encoding
     * @return true if base64 encoded string, false otherwise
     */
    private static boolean isBase64Encoded(String input) {

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

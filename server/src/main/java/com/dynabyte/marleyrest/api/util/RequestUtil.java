package com.dynabyte.marleyrest.api.util;

import com.dynabyte.marleyrest.api.exception.ImageEncodingException;
import com.dynabyte.marleyrest.api.exception.InvalidArgumentException;
import com.dynabyte.marleyrest.prediction.request.ImageRequest;
import com.dynabyte.marleyrest.registration.request.RegistrationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for validating requests received in the rest api. Includes private submethods.
 * Exceptions are thrown if a request is determined to be invalid.
 */
public class RequestUtil {

    /**
     * Validates image request used for predicting an image
     * @param imageRequest Request containing a single base64 image string
     */
    public static ImageRequest validateAndPreparePredictionRequest(ImageRequest imageRequest){
        ImageUtil.validateImageNotNull(imageRequest.getImage());
        return new ImageRequest(ImageUtil.validateImageAndRemoveDescriptorTag(imageRequest.getImage()));

    }

    /**
     * Validates a request to register a new person and removes potential descriptor tags in the images list
     * @param registrationRequest The request to be validated
     */
    public static RegistrationRequest validateAndPrepareRegistrationRequest(RegistrationRequest registrationRequest){
        validateName(registrationRequest.getName());
        validateImagesNotNullOrEmpty(registrationRequest.getImages());
        List<String> base64Images = validateImagesAndRemoveDescriptorTags(registrationRequest.getImages());
        return new RegistrationRequest(registrationRequest.getName(), base64Images);
    }

    /**
     * Validates that a list of strings is not null
     * @param images The list of strings to be validated
     */
    private static void validateImagesNotNullOrEmpty(List<String> images) {
        if(images == null || images.isEmpty()){
            throw new InvalidArgumentException("images array missing or empty! Must be included in base64format");
        }
    }

    /**
     * Validates a list of base64 image strings and removes descriptor tag if present. Throws error if not valid
     * @param images List of base 64 image strings
     * @return Validated list of base64 image strings after removing descriptor tags
     */
    private static List<String> validateImagesAndRemoveDescriptorTags(List<String> images) {
        return images
                .stream()
                .map(ImageUtil::validateImageAndRemoveDescriptorTag)
                .collect(Collectors.toList());
    }


    /**
     * Validates name to not be null and to fit size requirements
     * @param name String to be validated
     */
    private static void validateName(String name) {
        if(name == null){
            throw new InvalidArgumentException("name cannot be null!");
        }
        if(name.length() < 1 || name.length() > 50){
            throw new InvalidArgumentException("name must be between 1 and 50 characters!");
        }
    }
}

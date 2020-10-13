package com.dynabyte.marleyrest.api.util;

import com.dynabyte.marleyrest.api.exception.ImageEncodingException;
import com.dynabyte.marleyrest.api.exception.InvalidArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;

/**
 * Utility class for validating and preparing strings as base64 images. Includes private submethods.
 * Exceptions are thrown if a string is determined to be invalid as a base64 image.
 */
public class ImageUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageUtil.class);

    /**
     * Validates a string as a base64 image and removes descriptor tag if present
     * @param input string to be validated as base64 image
     * @return Validated base64 image string without descriptor tag
     */
    static String validateImageAndRemoveDescriptorTag(String input){
        validateImageNotNull(input);
        String base64Image = removeBase64DescriptorTag(input);
        validateIsBase64(base64Image);
        LOGGER.debug("Image validated. Starts with: " + base64Image.substring(0, 25));
        return base64Image;
    }

    /**
     * Validates that base64 image string is not null
     * @param base64Image Base64 image string to checked for null
     */
    static void validateImageNotNull(String base64Image){
        if(base64Image == null){
            throw new InvalidArgumentException("image cannot be null. Must be an image in base64 format");
        }
    }

    /**
     * Removes descriptor tag for png or jpeg if present
     * @param base64Image Base64 image string to remove descriptor tag from
     * @return Base64 image string without descriptor tag
     */
    private static String removeBase64DescriptorTag(String base64Image) {
        return base64Image
                .replace("data:image/jpeg;base64,", "")
                .replace("data:image/png;base64,", "");
    }

    /**
     * Validates whether a string can be decoded from base64
     * @param input string to be validated as base64 image
     */
    private static void validateIsBase64(String input){
        if (!isBase64Encoded(input)){
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

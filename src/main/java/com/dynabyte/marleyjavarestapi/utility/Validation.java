package com.dynabyte.marleyjavarestapi.utility;

import java.util.Base64;

/**
 * Utility class for validating various data using static boolean methods. May include helper sub methods.
 */
public class Validation {

    /**
     * Returns true if input is a base64 encoded string. Might not be 100% accurate as some strings can match that of an
     * encoded strings but it needs to have a length divisible by 4 and pass a decoding check.
     * @param input the string to be checked for base64 encoding
     * @return true if base64 encoded string, false otherwise
     */
    public static boolean isBase64Encoded(String input) {

    if(input.length()%4 != 0){
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

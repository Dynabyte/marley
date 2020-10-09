package com.dynabyte.marleyjavarestapi.facerecognition.to.request;

import lombok.Data;
import java.util.List;

/**
 * Request from client to register a person including name and a list of base64 images
 */
@Data
public class RegistrationRequest {

    //@Size(min = 1, max = 50, message = "name must be between 1-50 characters long")
    private String name;
    private List<String> images;

}

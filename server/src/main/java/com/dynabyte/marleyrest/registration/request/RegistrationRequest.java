package com.dynabyte.marleyrest.registration.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request from client to register a person including name and a list of base64 images
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {

    private String name;
    private List<String> images;

}

package com.dynabyte.marleyjavarestapi.facerecognition.to.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class RegistrationRequest {

    @Size(min = 1, max = 50, message = "name must be between 1-50 characters long")
    private String name;
    @NotNull(message = "images must be in an array of base64 image strings")
    private List<String> images;
}

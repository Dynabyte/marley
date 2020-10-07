package com.dynabyte.marleyjavarestapi.facerecognition.to.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Request to register a Person using a single image instead of many
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SingleImageRegistrationRequest extends ImageRequest {

    private String name;

}

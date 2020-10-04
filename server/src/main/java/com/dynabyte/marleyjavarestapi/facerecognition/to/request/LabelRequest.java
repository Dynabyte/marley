package com.dynabyte.marleyjavarestapi.facerecognition.to.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Request to label a person with a name using an image that will be saved as a reference encoding and labeled based
 * on the faceId given in a pythonResponse
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class LabelRequest extends ImageRequest {

    private String name;

}

package com.dynabyte.marleyjavarestapi.facerecognition.to.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SingleImageRegistrationRequest extends ImageRequest {

    private String name;

}

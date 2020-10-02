package com.dynabyte.marleyjavarestapi.facerecognition.to;

import lombok.Data;

@Data
public class LabelRequest {

    private String name;
    private String image;
}

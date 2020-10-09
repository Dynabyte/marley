package com.dynabyte.marleyjavarestapi.facerecognition.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Entity containing personal information for people who register with the Marley face recognition service
 */
@Entity
@Data
@NoArgsConstructor
public class Person implements Serializable {

    @Id
    private String faceId;
    private String name;

    public Person(String faceId, String name) {
        this.faceId = faceId;
        this.name = name;
    }
}

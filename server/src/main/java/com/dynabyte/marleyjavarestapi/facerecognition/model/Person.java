package com.dynabyte.marleyjavarestapi.facerecognition.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Size;
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
    @Size(min = 1, max = 50)
    private String name;

    public Person(String faceId, @Size(min = 1, max = 50) String name) {
        this.faceId = faceId;
        this.name = name;
    }
}

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

    //TODO currently javax.validation is not in use! Decide whether to use that or Validation.validateName method (current)
    @Id
    private String faceId;
    @Size(min = 1, max = 50, message = "name must be between 1-50 characters long")
    private String name;

    public Person(String faceId, @Size(min = 1, max = 50) String name) {
        this.faceId = faceId;
        this.name = name;
    }
}

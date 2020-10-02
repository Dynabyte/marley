package com.dynabyte.marleyjavarestapi.facerecognition.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
public class Person implements Serializable {

    @Id
    private String id;
    @Size(min = 1, max = 50)
    private String name;

    public Person(String id, @Size(min = 1, max = 50) String name) {
        this.id = id;
        this.name = name;
    }
}

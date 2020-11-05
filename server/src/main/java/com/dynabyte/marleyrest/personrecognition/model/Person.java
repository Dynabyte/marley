package com.dynabyte.marleyrest.personrecognition.model;

import com.dynabyte.marleyrest.calendar.model.GoogleTokens;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
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
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL) //TODO check that deleteRequest removes googleTokens as well
    @JoinColumn(name = "face_id")
    private GoogleTokens googleTokens;

    public Person(String faceId, String name) {
        this.faceId = faceId;
        this.name = name;
    }
}

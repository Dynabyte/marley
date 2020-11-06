package com.dynabyte.marleyrest.calendar.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
public class GoogleTokens {

    @Id
    private String faceId;
    private String accessToken;
    private String refreshToken;
    private Long expirationSystemTime;

    public GoogleTokens(String faceId, String accessToken, String refreshToken, Long expirationSystemTime) {
        this.faceId = faceId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expirationSystemTime = expirationSystemTime;
    }
}

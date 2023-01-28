package org.seng302.models.requests;

import lombok.Data;

/**
 * A simple DTO which holds the user id from a request.
 */
@Data
public class UserIdRequest {

    private long userId;

    public UserIdRequest(){}

    public UserIdRequest(long userId) {
        this.userId = userId;
    }

}

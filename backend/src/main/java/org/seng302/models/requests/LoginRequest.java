package org.seng302.models.requests;

import lombok.Data;

/**
 * DTO that holds info of a login request.
 */
@Data
public class LoginRequest {

    private String email;
    private String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}

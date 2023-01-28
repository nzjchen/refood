package org.seng302.models.requests;

import lombok.Data;
import org.seng302.models.Address;

/**
 * DTO class that holds a new user registration info.
 */
public class NewUserRequest extends UserRequest {

    public NewUserRequest(String firstName, String middleName, String lastName, String nickname, String bio, String email, String dateOfBirth, String phoneNumber, Address homeAddress, String password) {
        super(firstName, middleName, lastName, nickname, bio, email, dateOfBirth, phoneNumber, homeAddress, password);
    }
}

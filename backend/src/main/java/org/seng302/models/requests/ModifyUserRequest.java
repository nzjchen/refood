package org.seng302.models.requests;

import lombok.Getter;
import org.seng302.models.Address;

/**
 * DTO class that holds a modified user info.
 */
@Getter
public class ModifyUserRequest extends UserRequest {

    private String newPassword;

    public ModifyUserRequest(String firstName, String middleName, String lastName, String nickname, String bio, String email, String dateOfBirth, String phoneNumber, Address homeAddress, String password) {
        super(firstName, middleName, lastName, nickname, bio, email, dateOfBirth, phoneNumber, homeAddress, password);
    }
}

package org.seng302.models.requests;

import lombok.Data;
import org.seng302.models.Address;

@Data
public abstract class UserRequest {
    private String firstName;
    private String middleName;
    private String lastName;
    private String nickname;
    private String bio;
    private String email;
    private String dateOfBirth;
    private String phoneNumber;

    private Address homeAddress;
    private String password;

    public UserRequest(String firstName, String middleName, String lastName, String nickname, String bio, String email, String dateOfBirth, String phoneNumber, Address homeAddress, String password) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.nickname = nickname;
        this.bio = bio;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.homeAddress = homeAddress;
        this.password = password;
    }
}

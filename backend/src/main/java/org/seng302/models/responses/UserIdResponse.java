package org.seng302.models.responses;

import lombok.Data;
import org.seng302.models.Role;
import org.seng302.models.User;

/**
 * A simple DTO which holds the user id & role to transfer to frontend.
 */
@Data
public class UserIdResponse {

    private Long userId;
    private Role role;

    public UserIdResponse(User user) {
        this.userId = user.getId();
        this.role = user.getRole();
    }

}

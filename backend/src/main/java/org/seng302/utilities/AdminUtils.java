package org.seng302.utilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seng302.models.Role;
import org.seng302.models.User;
import org.seng302.models.Address;
import org.seng302.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Utilities that assist in DGAA/GAA admin actions can be found here.
 */
@Component
public class AdminUtils {

    private static final Logger logger = LogManager.getLogger(AdminUtils.class.getName());

    @Value("${DGAA_EMAIL}")
    private String DGAA_EMAIL;
    @Value("${DGAA_PASSWORD}")
    private String DGAA_PASSWORD;

    /**
     * Check if a DGAA is present in the database. If not, create one and log the error.
     * @param userRepository the repository to access the user table.
     * @throws NoSuchAlgorithmException if a new DGAA user is being made and password goes wrong.
     */
    public void checkForDefaultGlobalAdmin(UserRepository userRepository) throws NoSuchAlgorithmException {
        List<User> dgaaUsers = userRepository.findAllByRole(Role.DGAA);

        if (dgaaUsers.isEmpty()) {
            Address dgaaAddr = new Address(null, null, null, null, null, "New Zealand", "8041");
            User dgaa = new User("Default", "Global Admin", dgaaAddr, DGAA_EMAIL, DGAA_PASSWORD, Role.DGAA);
            userRepository.save(dgaa);
            logger.error("A DGAA was not found in the user database, and a new DGAA user has been created and inserted.");
        }

    }


}

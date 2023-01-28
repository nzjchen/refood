package org.seng302.utilities;

import org.seng302.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.security.NoSuchAlgorithmException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class that deals with scheduling/periodic checking of the existence of DGAA user in the system.
 */
@Component
public class SchedAdminCheck {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AdminUtils adminUtils;

    private static final Logger logger = LogManager.getLogger(SchedAdminCheck.class.getName());

    /**
     * Checks for a DGAA at a fixed rate after an initial delay.
     * 30000 = 30 seconds, after 60000 = 60 seconds initial delay.
     * @throws NoSuchAlgorithmException when a DGAA is made, and password hashing goes wrong.
     */
    @Scheduled(fixedDelay = 30000, initialDelay = 60000)
    public void fixedRateSched() throws NoSuchAlgorithmException {
        logger.info("Checking for DGAA");
        adminUtils.checkForDefaultGlobalAdmin(userRepository);
    }
}
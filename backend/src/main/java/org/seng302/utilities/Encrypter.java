package org.seng302.utilities;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class that hashes passwords into a hex string
 */
public final class Encrypter {
    private Encrypter() {
    }

    /**
     * This method takes a string as input and converts it into the hex string of the hashes input
     * @param inputString The string to be hashed
     */
    public static String hashString(String inputString) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(inputString.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }
}
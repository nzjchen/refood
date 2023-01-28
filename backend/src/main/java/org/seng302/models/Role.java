package org.seng302.models;

public enum Role {
    DGAA,
    GAA,
    USER;

    /**
     * Checks if a role is of a global application admin.
     * @param role input role given.
     * @return true if it is DGAA/GAA, false otherwise.
     */
    public static boolean isGlobalApplicationAdmin(Role role) {
        return role == DGAA || role == GAA;
    }

}

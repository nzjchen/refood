package org.seng302.models;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * View status of a notification
 */
public enum ViewStatus {
    IMPORTANT("Important"),
    READ("Read"),
    UNREAD("Unread");

    private final String label;

    ViewStatus(String label) {
        this.label = label;
    }

    @Override
    @JsonValue
    public String toString() {
        return this.label;
    }
}

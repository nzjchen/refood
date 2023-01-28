package org.seng302.models.requests;


import lombok.Data;

/**
 * Simple DTO class that contains the info of a new message.
 */
@Data
public class NewMessageRequest {
    private long cardId;
    private String description;

    public NewMessageRequest(long cardId, String description) {
        this.cardId = cardId;
        this.description = description;
    }
}

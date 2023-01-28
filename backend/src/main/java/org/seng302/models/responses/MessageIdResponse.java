package org.seng302.models.responses;

import lombok.Data;

/**
 * Simple DTO class that holds the id of a marketplace community card
 */
@Data
public class MessageIdResponse {

    private long messageId;

    public MessageIdResponse(long messageId) {
        this.messageId = messageId;
    }
}

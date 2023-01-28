package org.seng302.models.responses;

import lombok.Data;

/**
 * Simple DTO class that holds the id of a marketplace community card
 */
@Data
public class CardIdResponse {

    private long cardId;

    public CardIdResponse(long cardId) {
        this.cardId = cardId;
    }
}

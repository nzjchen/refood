package org.seng302.models.requests;

import org.seng302.models.*;

import lombok.Data;

/**
 * Simple DTO class that contains the info of a new community marketplace card.
 */
@Data
public class NewCardRequest {

    private long creatorId;
    private MarketplaceSection section;

    private String title;
    private String description;
    private String keywords;

    public NewCardRequest(long creatorId, String title, String description, String keywords, MarketplaceSection section) {
        this.title = title;
        this.description = description;
        this.keywords = keywords;
        this.creatorId = creatorId;
        this.section = section;
    }
}

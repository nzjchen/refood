package org.seng302.models;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Id class that represents the composite key of a listing like.
 */
@Embeddable
public class ListingLikeId implements Serializable {

    @Column(name = "user_id")
    private long userId;

    @Column(name = "listing_id")
    private long listingId;

    /**
     * Empty constructor for JPA use.
     */
    public ListingLikeId() {
        //Empty constructor for JPA use.
    }

}

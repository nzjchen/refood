package org.seng302.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;

/**
 * Entity that represents a single like between a user and a business sale listing.
 * Key is a composite key comprised of the user id and listing id.
 * @see ListingLikeId
 */
@Data
@Entity
@IdClass(ListingLikeId.class)
public class ListingLike {

    @Id
    @JsonIgnore
    private long userId;

    @Id
    @JsonIgnore
    private long listingId;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "listing_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Listing listing;

    /**
     * Basic constructor to create a new like given a user and listing entity.
     * @param user the entity that is liking the listing.
     * @param listing the entity that is being liked by the user.
     */
    public ListingLike(User user, Listing listing) {
        this.user = user;
        this.listing = listing;
        this.userId = user.getId();
        this.listingId = listing.getId();
    }

    public ListingLike() {}
}
